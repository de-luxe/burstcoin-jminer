/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 by luxe - https://github.com/de-luxe - BURST-LUXE-RED2-G6JW-H4HG5
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package burstcoin.jminer.core.reader;


import burstcoin.jminer.core.CoreProperties;
import burstcoin.jminer.core.network.event.NetworkResultErrorEvent;
import burstcoin.jminer.core.reader.data.PlotDrive;
import burstcoin.jminer.core.reader.data.PlotFile;
import burstcoin.jminer.core.reader.data.Plots;
import burstcoin.jminer.core.reader.event.ReaderCorruptFileEvent;
import burstcoin.jminer.core.reader.event.ReaderLoadedPartEvent;
import burstcoin.jminer.core.reader.event.ReaderProgressChangedEvent;
import burstcoin.jminer.core.reader.event.ReaderStoppedEvent;
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
 * The type Reader.
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
  public static volatile long blockNumber;
  private Plots plots;
  private Map<Long, Long> capacityLookup;
  private long remainingCapacity;
  private long capacity;
  private long readerStartTime;
  private int readerThreads;

  /**
   * Post construct.
   */
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

    directories = CoreProperties.getPlotPaths();
    chunkPartNonces = CoreProperties.getChunkPartNonces();
    scanPathsEveryRound = CoreProperties.isScanPathsEveryRound();
    readerThreads = CoreProperties.getReaderThreads();
    capacityLookup = new HashMap<>();

    if(CoreProperties.isListPlotFiles())
    {
      getPlots().printPlotFiles();
    }
  }

  /**
   * starts reader (once per block)
   *
   * @param blockNumber the block number
   * @param scoopNumber the scoop number
   */
  public void read(long previousBlockNumber, long blockNumber, int scoopNumber, long lastBestCommittedDeadline)
  {
    Reader.blockNumber = blockNumber;

    // ensure plots are initialized
    plots = plots == null ? getPlots() : plots;

    if(readerPool.getActiveCount() > 0)
    {
      long elapsedTime = new Date().getTime() - readerStartTime;
      publisher.publishEvent(new ReaderStoppedEvent(previousBlockNumber, capacity, remainingCapacity, elapsedTime, lastBestCommittedDeadline));
    }

    // update reader thread count
    int poolSize = readerThreads <= 0 ? directories.size() : readerThreads;
    readerPool.setCorePoolSize(poolSize);
    readerPool.setMaxPoolSize(poolSize);

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
  }

  public Plots getPlots()
  {
    if(scanPathsEveryRound || plots == null)
    {
      plots = new Plots(directories, numericAccountId, chunkPartNonces);
    }
    return plots;
  }

  public boolean cleanupReaderPool()
  {
    // if no read thread running, pool will be increased on next round
    if(readerPool.getActiveCount() == 0)
    {
      readerPool.setCorePoolSize(1);
      readerPool.setMaxPoolSize(1);
      LOG.debug("cleanup was successful ...");
      return true;
    }
    LOG.debug("cleanup skipped ... retry in 1s");
    return false;
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
        LOG.error("Error: ReaderPartLoadedEvent for unknown chunkPartStartNonce: '" + event.getChunkPartStartNonce() + "'!"
                  + " Please check for plot-file duplicate or overlapping plots e.g. use https://bchain.info/BURST/tools/overlap");
      }
    }
    else
    {
      LOG.trace("update reader progress skipped ... old block ...");
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
