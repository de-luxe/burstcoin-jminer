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
import burstcoin.jminer.core.network.event.NetworkBlocksEvent;
import burstcoin.jminer.core.network.event.NetworkResultErrorEvent;
import burstcoin.jminer.core.network.model.Block;
import burstcoin.jminer.core.network.task.NetworkRequestAccountBlocksTask;
import burstcoin.jminer.core.reader.data.PlotDrive;
import burstcoin.jminer.core.reader.data.PlotFile;
import burstcoin.jminer.core.reader.data.Plots;
import burstcoin.jminer.core.reader.data.PocVersion;
import burstcoin.jminer.core.reader.event.ReaderCorruptFileEvent;
import burstcoin.jminer.core.reader.event.ReaderLoadedPartEvent;
import burstcoin.jminer.core.reader.event.ReaderProgressChangedEvent;
import burstcoin.jminer.core.reader.task.ReaderConvertLoadDriveTask;
import burstcoin.jminer.core.reader.task.ReaderLoadDriveTask;
import burstcoin.jminer.core.round.event.RoundStoppedEvent;
import nxt.crypto.Crypto;
import nxt.util.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The type Reader.
 */
@Component
@Scope("singleton")
public class Reader
{
  private static final Logger LOG = LoggerFactory.getLogger(Reader.class);

  private final ApplicationContext context;
  private final ThreadPoolTaskExecutor readerPool;
  private final SimpleAsyncTaskExecutor networkPool;

  // config
  private String numericAccountId;
  private List<String> directories;
  private long chunkPartNonces;
  private boolean scanPathsEveryRound;

  // data
  public static volatile long blockNumber;
  public static volatile byte[] generationSignature;
  private Plots plots;

  private Map<BigInteger, Long> realCapacityLookup;
  private long realRemainingCapacity;
  private long realCapacity;

  private Map<BigInteger, Long> capacityLookup;
  private long remainingCapacity;
  private long capacity;

  private long readerStartTime;
  private int readerThreads;

  @Autowired
  public Reader(ApplicationContext context, @Qualifier(value = "readerPool") ThreadPoolTaskExecutor readerPool,
                @Qualifier(value = "networkPool") SimpleAsyncTaskExecutor networkPool)
  {
    this.context = context;
    this.readerPool = readerPool;
    this.networkPool = networkPool;
  }

  @PostConstruct
  protected void postConstruct()
  {
    String numericAccountId;
    if(CoreProperties.isPoolMining())
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
    realCapacityLookup = new HashMap<>();

    if(CoreProperties.isListPlotFiles())
    {
      // find winner of lastBlock on new round, if server available
      String server = !CoreProperties.isPoolMining() ? CoreProperties.getSoloServer() : CoreProperties.getWalletServer();
      if(!StringUtils.isEmpty(server))
      {
        NetworkRequestAccountBlocksTask networkRequestAccountBlocksTask = context.getBean(NetworkRequestAccountBlocksTask.class);
        networkRequestAccountBlocksTask.init(numericAccountId, server);
        networkPool.execute(networkRequestAccountBlocksTask);
      }
      else
      {
        getPlots().printPlotFiles();
      }
    }
  }

  /* starts reader (once per block) */
  public void read(long previousBlockNumber, long blockNumber, byte[] generationSignature, int scoopNumber, long lastBestCommittedDeadline, int networkQuality)
  {
    Reader.blockNumber = blockNumber;
    Reader.generationSignature = generationSignature;

    // ensure plots are initialized
    plots = plots == null ? getPlots() : plots;

    if(readerPool.getActiveCount() > 0)
    {
      long elapsedTime = new Date().getTime() - readerStartTime;
      context.publishEvent(new RoundStoppedEvent(previousBlockNumber, lastBestCommittedDeadline, capacity, remainingCapacity, elapsedTime, networkQuality));
    }

    // update reader thread count
    int poolSize = readerThreads <= 0 ? directories.size() : readerThreads;
    readerPool.setCorePoolSize(poolSize);
    readerPool.setMaxPoolSize(poolSize);

    // we use the startnonce of loaded (startnonce+chunk+part) as unique job identifier
    capacityLookup.clear();
    capacityLookup.putAll(plots.getChunkPartStartNonces());

    realCapacityLookup.clear();
    realCapacity = 0;
    for(BigInteger chunkPartNonces : capacityLookup.keySet())
    {
      PlotFile plotFile = plots.getPlotFileByChunkPartStartNonce(chunkPartNonces);
      long realChunkPartNoncesCapacity = isCompatibleWithCurrentPoc(blockNumber, plotFile.getPocVersion())
                   ? capacityLookup.get(chunkPartNonces)
                   : 2 * capacityLookup.get(chunkPartNonces);
      realCapacityLookup.put(chunkPartNonces, realChunkPartNoncesCapacity);
      realCapacity+=realChunkPartNoncesCapacity;
    }

    remainingCapacity = plots.getSize();
    capacity = plots.getSize();

    realRemainingCapacity = realCapacity;
    readerStartTime = new Date().getTime();

    // order by slowest and biggest drives first
    List<PlotDrive> orderedPlotDrives = new ArrayList<>(plots.getPlotDrives());
    orderedPlotDrives.removeIf(plotDrive -> plotDrive.getDrivePocVersion() == null);
    orderedPlotDrives.sort((o1, o2) -> Long.compare(o2.getSize(), o1.getSize())); // order by size
    orderedPlotDrives.sort(Comparator.comparing(o -> isCompatibleWithCurrentPoc(blockNumber, o.getDrivePocVersion()))); // order by poc version

    for(PlotDrive plotDrive : orderedPlotDrives)
    {
      PocVersion drivePocVersion = plotDrive.getDrivePocVersion();
      if(drivePocVersion == null)
      {
        LOG.warn("Skipped '" + plotDrive.getDirectory()
                 + "', different POC versions on one drive is not supported! (Workaround: put them in different directories and add them to 'plotFilePaths')");
      }
      else
      {
        if(isCompatibleWithCurrentPoc(blockNumber, drivePocVersion))
        {
          ReaderLoadDriveTask readerLoadDriveTask = context.getBean(ReaderLoadDriveTask.class);
          readerLoadDriveTask.init(scoopNumber, blockNumber, generationSignature, plotDrive);
          readerPool.execute(readerLoadDriveTask);
        }
        else
        {
          ReaderConvertLoadDriveTask readerConvertLoadDriveTask = context.getBean(ReaderConvertLoadDriveTask.class);
          readerConvertLoadDriveTask.init(scoopNumber, blockNumber, generationSignature, plotDrive);
          readerPool.execute(readerConvertLoadDriveTask);
        }
      }
    }
  }

  private Boolean isCompatibleWithCurrentPoc(long blockNumber, PocVersion drivePocVersion)
  {
    switch(drivePocVersion)
    {
      case POC_2:
        return blockNumber >= CoreProperties.getPoc2ActivationBlockHeight();
      case POC_1:
      default:
        return blockNumber < CoreProperties.getPoc2ActivationBlockHeight();
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
      LOG.trace("cleanup was successful ...");
      return true;
    }
    LOG.trace("cleanup skipped ... retry in 1s");
    return false;
  }

  @EventListener
  public void handleMessage(ReaderLoadedPartEvent event)
  {
    if(blockNumber == event.getBlockNumber() && Arrays.equals(event.getGenerationSignature(), generationSignature))
    {
      // update progress
      Long removedCapacity = capacityLookup.remove(event.getChunkPartStartNonce());
      Long realRemovedCapacity = realCapacityLookup.remove(event.getChunkPartStartNonce());
      if(removedCapacity != null)
      {
        remainingCapacity -= removedCapacity;
        realRemainingCapacity -= realRemovedCapacity;
        long elapsedTime = new Date().getTime() - readerStartTime;
        context.publishEvent(new ReaderProgressChangedEvent(this, event.getBlockNumber(), capacity, remainingCapacity,
                                                            realCapacity, realRemainingCapacity, elapsedTime));
      }
      else
      {
        LOG.error("Error: ReaderPartLoadedEvent for unknown chunkPartStartNonce: '" + event.getChunkPartStartNonce() + "'!"
                  + " Please check for plot-file duplicate or overlapping plots.");
      }
    }
    else
    {
      LOG.trace("update reader progress skipped ... old block ...");
    }
  }

  @EventListener
  public void handleMessage(NetworkResultErrorEvent event)
  {
    if(blockNumber == event.getBlockNumber() && Arrays.equals(event.getGenerationSignature(), generationSignature))
    {
      // find maybe corrupt plot-file
      PlotFile plotFile = plots.getPlotFileByChunkPartStartNonce(event.getChunkPartStartNonce());
      if(plotFile != null)
      {
        // plotFile.toString is just objId
        context.publishEvent(new ReaderCorruptFileEvent(this, event.getBlockNumber(), plotFile.getFilePath().toString(), plotFile.getNumberOfChunks(),
                                                        plotFile.getNumberOfParts()));
      }
    }
  }

  @EventListener
  public void handleMessage(NetworkBlocksEvent event)
  {
    if(event.getBlocks() != null && !event.getBlocks().getBlocks().isEmpty())
    {
      Map<String, List<Block>> foundBlocksLookup = new HashMap<>();
      Map<String, Long> numberOfChunksLookup = new HashMap<>();
      Set<Block> unassignedBlocks = new HashSet<>(event.getBlocks().getBlocks());

      for(PlotDrive plotDrive : getPlots().getPlotDrives())
      {
        for(PlotFile plotFile : plotDrive.getPlotFiles())
        {
          // number of chunks
          numberOfChunksLookup.put(plotFile.getFilePath().toString(), plotFile.getPlots() / plotFile.getStaggeramt());

          // found blocks
          foundBlocksLookup.put(plotFile.getFilePath().toString(), new ArrayList<>());
          Set<Block> assignedBlocks = new HashSet<>();
          for(Block unassignedBlock : unassignedBlocks)
          {
            BigInteger nonce = new BigInteger(unassignedBlock.getNonce());
            BigInteger end = plotFile.getStartnonce().add(BigInteger.valueOf(plotFile.getPlots()));
            // check if nonce is within plotfile
            if(plotFile.getStartnonce().compareTo(nonce) < 0 && end.compareTo(nonce) >= 0)
            {
              foundBlocksLookup.get(plotFile.getFilePath().toString()).add(unassignedBlock);
              assignedBlocks.add(unassignedBlock);
            }
          }
          unassignedBlocks.removeAll(assignedBlocks);
        }
      }

      List<String> rows = new ArrayList<>();
      for(Map.Entry<String, List<Block>> entry : foundBlocksLookup.entrySet())
      {
        String seekOut = numberOfChunksLookup.get(entry.getKey()).equals(1L) ? ", OPTIMIZED!" : ", chunks '" + numberOfChunksLookup.get(entry.getKey()) + "'.";
        if(entry.getValue().isEmpty())
        {
          rows.add("'" + entry.getKey() + "', blocks 'N/A'" + seekOut);
        }
        else
        {
          rows.add("'" + entry.getKey() + "', blocks '" + entry.getValue().size() + "'" + seekOut);
        }
      }

      Collections.sort(rows);
      for(String row : rows)
      {
        System.out.println(row);
      }
    }
  }
}
