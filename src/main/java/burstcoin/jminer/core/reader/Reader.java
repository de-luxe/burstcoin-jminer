package burstcoin.jminer.core.reader;


import burstcoin.jminer.core.CoreProperties;
import burstcoin.jminer.core.network.event.NetworkResultErrorEvent;
import burstcoin.jminer.core.reader.data.PlotDrive;
import burstcoin.jminer.core.reader.data.PlotFile;
import burstcoin.jminer.core.reader.data.Plots;
import burstcoin.jminer.core.reader.event.ReaderCorruptFileEvent;
import burstcoin.jminer.core.reader.event.ReaderLoadedPartEvent;
import burstcoin.jminer.core.reader.event.ReaderProgressChangedEvent;
import burstcoin.jminer.core.reader.task.ReaderLoadDriveTask;
import nxt.crypto.Crypto;
import nxt.util.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Component
@Scope("singleton")
public class Reader
  implements ReaderLoadedPartEvent.Handler, NetworkResultErrorEvent.Handler
{
  private static final Logger LOG = LoggerFactory.getLogger(Reader.class);

  @Autowired
  private ApplicationContext context;

  @Autowired
  private ApplicationEventPublisher publisher;

  @Autowired
  @Qualifier(value = "readerPool")
  private ThreadPoolTaskExecutor readerPool;

  // config
  private String numericAccountId;
  private List<String> directories;
  private long chunkPartNonces;
  private boolean scanPathsEveryRound;

  // data
  private long blockNumber;
  private Plots plots;
  private Map<Long, Long> capacityLookup;
  private long remainingCapacity;
  private long capacity;
  private long readerStartTime;

  @PostConstruct
  protected void postConstruct()
  {
    Boolean poolMining = CoreProperties.isPoolMining();

    String numericAccountId;
    if(poolMining)
    {
      numericAccountId = CoreProperties.getNumericAccountId();
    }
    else
    {
      // calculate numericAccountId
      byte[] publicKey = Crypto.getPublicKey(CoreProperties.getPassPhrase());
      byte[] publicKeyHash = Crypto.sha256().digest(publicKey);
      long accountId = Convert.fullHashToId(publicKeyHash);
      numericAccountId = Convert.toUnsignedLong(accountId);
    }

    if(!StringUtils.isEmpty(numericAccountId))
    {
      this.numericAccountId = numericAccountId;
    }
    else
    {
      LOG.error("init reader failed!");
    }

    this.directories = CoreProperties.getPlotPaths();
    this.chunkPartNonces = CoreProperties.getChunkPartNonces();
    this.scanPathsEveryRound = CoreProperties.isScanPathsEveryRound();
    capacityLookup = new HashMap<>();

    if(!scanPathsEveryRound)
    {
      plots = new Plots(directories, numericAccountId, chunkPartNonces);
    }
  }

  /**
   * starts reader (once per block)
   *
   * @return chunkPartStartNonces that will be read and their capacity
   */
  public Plots read(long blockNumber, int scoopNumber)
  {
    this.blockNumber = blockNumber;

    // re-scan drives each round
    if(scanPathsEveryRound)
    {
      plots = new Plots(directories, numericAccountId, chunkPartNonces);
    }

    if(readerPool.getActiveCount() > 0)
    {
      readerPool.shutdown();
      readerPool.initialize();
    }

    // update reader thread count
    readerPool.setCorePoolSize(directories.size());
    readerPool.setMaxPoolSize(directories.size());

    // we use the startnonce of loaded (startnonce+chunk+part) as unique job identifier
    capacityLookup.clear();
    capacityLookup.putAll(plots.getChunkPartStartNonces());

    remainingCapacity = plots.getSize();
    capacity = plots.getSize();

    readerStartTime = new Date().getTime();

    for(PlotDrive plotDrive : plots.getPlotDrives())
    {
      ReaderLoadDriveTask readerLoadDriveTask = context.getBean(ReaderLoadDriveTask.class);
      readerLoadDriveTask.init(scoopNumber, blockNumber, plotDrive);
      readerPool.execute(readerLoadDriveTask);
    }
    return plots;
  }

  @Override
  @EventListener
  public void handleMessage(ReaderLoadedPartEvent event)
  {
    if(blockNumber == event.getBlockNumber())
    {
      // update progress
      Long removedCapacity = capacityLookup.remove(event.getChunkPartStartNonce());
      if(removedCapacity != null)
      {
        remainingCapacity -= removedCapacity;
        long elapsedTime = new Date().getTime() - readerStartTime;
        publisher.publishEvent(new ReaderProgressChangedEvent(this, event.getBlockNumber(), capacity, remainingCapacity, elapsedTime));
      }
      else
      {
        LOG.error("Error: ReaderPartLoadedEvent for unknown chunkPartStartNonce!");
      }
    }
    else
    {
      LOG.debug("update reader progress skipped ... old block ...");
    }
  }

  @Override
  @EventListener
  public void handleMessage(NetworkResultErrorEvent event)
  {
    if(blockNumber == event.getBlockNumber())
    {
      // find maybe corrupt plot-file
      PlotFile plotFile = plots.getPlotFileByChunkPartStartNonce(event.getChunkPartStartNonce());
      if(plotFile != null)
      {
        // plotFile.toString is just objId
        publisher.publishEvent(new ReaderCorruptFileEvent(this, event.getBlockNumber(), plotFile.getFilePath().toString(), plotFile.getNumberOfChunks(),
                                                          plotFile.getNumberOfParts()));
      }
    }
  }
}
