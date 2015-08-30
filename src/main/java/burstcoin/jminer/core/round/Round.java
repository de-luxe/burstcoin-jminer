/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 by luxe - https://github.com/de-luxe -  BURST-LUXE-RED2-G6JW-H4HG5
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

package burstcoin.jminer.core.round;

import burstcoin.jminer.core.CoreProperties;
import burstcoin.jminer.core.checker.Checker;
import burstcoin.jminer.core.checker.event.CheckerDevResultEvent;
import burstcoin.jminer.core.checker.event.CheckerResultEvent;
import burstcoin.jminer.core.network.Network;
import burstcoin.jminer.core.network.event.NetworkDevResultConfirmedEvent;
import burstcoin.jminer.core.network.event.NetworkResultConfirmedEvent;
import burstcoin.jminer.core.network.event.NetworkResultErrorEvent;
import burstcoin.jminer.core.network.event.NetworkStateChangeEvent;
import burstcoin.jminer.core.network.model.DevPoolResult;
import burstcoin.jminer.core.reader.Reader;
import burstcoin.jminer.core.reader.data.Plots;
import burstcoin.jminer.core.reader.event.ReaderProgressChangedEvent;
import burstcoin.jminer.core.round.event.RoundFinishedEvent;
import burstcoin.jminer.core.round.event.RoundSingleResultEvent;
import burstcoin.jminer.core.round.event.RoundSingleResultSkippedEvent;
import burstcoin.jminer.core.round.event.RoundStartedEvent;
import burstcoin.jminer.core.round.task.RoundFireEventTask;
import fr.cryptohash.Shabal256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import pocminer.generate.MiningPlot;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The type Round.
 */
@Component
@Scope("singleton")
public class Round
  implements NetworkStateChangeEvent.Handler, NetworkResultErrorEvent.Handler
{
  private static final Logger LOG = LoggerFactory.getLogger(Round.class);

  @Autowired
  private ApplicationContext context;

  @Autowired
  private ThreadPoolTaskExecutor roundPool;

  private final Reader reader;
  private final Checker checker;
  private final Network network;

  private boolean poolMining;
  private long targetDeadline;

  private Timer timer;
  private long blockNumber;
  private long finishedBlockNumber;
  private long baseTarget;
  private Date roundStartDate;

  private BigInteger lowest;
  private long bestCommittedDeadline;

  // dev pool
  private boolean devPool;
  private List<DevPoolResult> devPoolResults;
  private int devPoolCommitsThisRound;
  private int devPoolCommitsPerRound;// one additional on finish round

  private Set<Long> runningChunkPartStartNonces;

  /**
   * Instantiates a new Round.
   *
   * @param reader the reader
   * @param checker the checker
   * @param network the network
   */
  @Autowired
  public Round(Reader reader, Checker checker, Network network)
  {
    this.reader = reader;
    this.checker = checker;
    this.network = network;
  }

  /**
   * Post construct.
   */
  @PostConstruct
  protected void postConstruct()
  {
    this.poolMining = CoreProperties.isPoolMining();
    this.devPool = CoreProperties.isDevPool();
    this.devPoolCommitsPerRound = CoreProperties.getDevPoolCommitsPerRound();

    timer = new Timer();

    initNewRound();
  }

  private void initNewRound()
  {
    roundStartDate = new Date();
    lowest = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);
    runningChunkPartStartNonces = new HashSet<>();
    devPoolResults = new ArrayList<>();
    bestCommittedDeadline = Long.MAX_VALUE;
    devPoolCommitsThisRound = devPoolCommitsPerRound;
  }

  @EventListener
  public void handleMessage(NetworkStateChangeEvent event)
  {
    if(blockNumber < event.getBlockNumber())
    {
      this.blockNumber = event.getBlockNumber();
      this.baseTarget = event.getBaseTarget();
      this.targetDeadline = event.getTargetDeadline();

      initNewRound();

      // reconfigure checker
      checker.reconfigure(blockNumber, baseTarget, targetDeadline, event.getGenerationSignature());

      // start reader
      int scoopNumber = calcScoopNumber(event.getBlockNumber(), event.getGenerationSignature());
      Plots plots = reader.read(blockNumber, scoopNumber);

      runningChunkPartStartNonces.clear();
      runningChunkPartStartNonces.addAll(plots.getChunkPartStartNonces().keySet());

      // ui event
      fireEvent(new RoundStartedEvent(blockNumber, scoopNumber, plots.getSize(), targetDeadline, baseTarget));

      timer.schedule(new TimerTask()
      {
        @Override
        public void run()
        {
          network.checkLastWinner(blockNumber);
        }
      }, 0); // deferred
    }
  }

  /**
   * Handle message.
   *
   * @param event the event
   */
  @EventListener
  public void handleMessage(CheckerResultEvent event)
  {
    if(blockNumber == event.getBlockNumber())
    {
      // check new lowest result
      if(event.getResult() != null)
      {
        long nonce = event.getNonce();
        BigInteger deadline = event.getResult().divide(BigInteger.valueOf(baseTarget));
        long calculatedDeadline = deadline.longValue();

        if(devPool)
        {
          if(calculatedDeadline < targetDeadline)
          {
            // remember for next triggered commit
            devPoolResults.add(new DevPoolResult(event.getBlockNumber(), calculatedDeadline, event.getNonce(), event.getChunkPartStartNonce()));
          }
          else
          {
            // todo there will be a lot of skipped, makes no sense, cause lowest not used here, remove or make adjustable by setting.
//            publisher.publishEvent(new RoundSingleResultSkippedEvent(this, event.getBlockNumber(), nonce, event.getChunkPartStartNonce(), calculatedDeadline,
//                                                                     targetDeadline, poolMining));
            runningChunkPartStartNonces.remove(event.getChunkPartStartNonce());
            triggerFinishRoundEvent(event.getBlockNumber());
          }
        }
        else
        {
          if(event.getResult().compareTo(lowest) < 0)
          {
            lowest = event.getResult();
            if(calculatedDeadline < targetDeadline)
            {
              network.checkResult(blockNumber, calculatedDeadline, nonce, event.getChunkPartStartNonce());

              // ui event
              fireEvent(new RoundSingleResultEvent(this, event.getBlockNumber(), nonce, event.getChunkPartStartNonce(), calculatedDeadline, poolMining));
            }
            else
            {
              // ui event
              fireEvent(new RoundSingleResultSkippedEvent(this, event.getBlockNumber(), nonce, event.getChunkPartStartNonce(), calculatedDeadline,
                                                          targetDeadline, poolMining));
              // chunkPartStartNonce finished
              runningChunkPartStartNonces.remove(event.getChunkPartStartNonce());
              triggerFinishRoundEvent(event.getBlockNumber());
            }
          }
          else
          {
            // chunkPartStartNonce finished
            runningChunkPartStartNonces.remove(event.getChunkPartStartNonce());
            triggerFinishRoundEvent(event.getBlockNumber());
          }
        }
      }
      else
      {
        LOG.error("CheckerResultEvent result == null");
      }
    }
    else
    {
      LOG.debug("event for previous block ...");
    }
  }

  /**
   * Handle message.
   *
   * @param event the event
   */
  @EventListener
  public void handleMessage(CheckerDevResultEvent event)
  {
    if(blockNumber == event.getBlockNumber())
    {
      if(event.hasResults())
      {
        // remember for next triggered commit
        devPoolResults.addAll(event.getDevPoolResults());
      }
      else
      {
        runningChunkPartStartNonces.remove(event.getChunkPartStartNonce());
        triggerFinishRoundEvent(event.getBlockNumber());
      }
    }
    else
    {
      LOG.debug("event for previous block ...");
    }
  }

  /**
   * triggers commit devPool nonces if needed, there will be 'numberOfDevPoolCommitsPerRound'
   *
   * @param event read progress
   */
  @EventListener
  public void handleMessage(ReaderProgressChangedEvent event)
  {
    if(devPool && blockNumber == event.getBlockNumber())
    {
      if(devPoolCommitsPerRound == devPoolCommitsThisRound)
      {
        devPoolCommitsThisRound--;
      }
      else if(event.getRemainingCapacity() < (event.getCapacity() / devPoolCommitsPerRound) * devPoolCommitsThisRound)
      {
        devPoolCommitsThisRound--;
        LOG.debug("trigger dev commit by progress #" + (devPoolCommitsPerRound - devPoolCommitsThisRound) + " this round.");
        if(!devPoolResults.isEmpty())
        {
          commitDevPoolNonces(event.getBlockNumber());
        }
        else
        {
          LOG.info("no shares to commit to dev pool ...");
        }
      }
    }
  }

  /**
   * Handle message.
   *
   * @param event the event
   */
  @EventListener
  public void handleMessage(NetworkResultConfirmedEvent event)
  {
    if(blockNumber == event.getBlockNumber())
    {
      runningChunkPartStartNonces.remove(event.getChunkPartStartNonce());
      bestCommittedDeadline = event.getDeadline();
      triggerFinishRoundEvent(event.getBlockNumber());
    }
  }

  /**
   * Handle message.
   *
   * @param event the event
   */
  @EventListener
  public void handleMessage(NetworkDevResultConfirmedEvent event)
  {
    if(blockNumber == event.getBlockNumber())
    {
      for(DevPoolResult devPoolResult : event.getDevPoolResults())
      {
        if(!runningChunkPartStartNonces.remove(devPoolResult.getChunkPartStartNonce()))
        {
          LOG.error("unknown chunkPartStartNonce in devResult." + devPoolResult.getChunkPartStartNonce());
        }
        if(devPoolResult.getCalculatedDeadline() < bestCommittedDeadline)
        {
          // this is not really a committed just the calculated, cause devPool does not provide deadlines
          // therefore no chance to validate a commit.
          bestCommittedDeadline = devPoolResult.getCalculatedDeadline();
        }
      }
      triggerFinishRoundEvent(event.getBlockNumber());
    }
  }

  @Override
  @EventListener
  public void handleMessage(NetworkResultErrorEvent event)
  {
    if(blockNumber == event.getBlockNumber())
    {
      runningChunkPartStartNonces.remove(event.getChunkPartStartNonce());
      triggerFinishRoundEvent(event.getBlockNumber());
    }
  }

  private void triggerFinishRoundEvent(long blockNumber)
  {
    if(finishedBlockNumber < blockNumber)
    {
      if(runningChunkPartStartNonces.isEmpty())
      {
        onRoundFinish(blockNumber);
      }
      // commit last devPool results
      else if(devPool && runningChunkPartStartNonces.size() == devPoolResults.size())
      {
        commitDevPoolNonces(blockNumber);
      }
    }
  }

  private void onRoundFinish(long blockNumber)
  {
    finishedBlockNumber = blockNumber;
    long elapsedRoundTime = new Date().getTime() - roundStartDate.getTime();
    triggerGarbageCollection();
    timer.schedule(new TimerTask()
    {
      @Override
      public void run()
      {
        fireEvent(new RoundFinishedEvent(blockNumber, bestCommittedDeadline, elapsedRoundTime));
      }
    }, 5); // fire deferred
  }

  private void commitDevPoolNonces(long blockNumber)
  {
    network.checkDevResult(blockNumber, new ArrayList<>(devPoolResults));
    devPoolResults.clear();

    // todo ui event
    LOG.info("shares, committed to devPool ...");
  }

  // not needed, just to force java to free memory (depending on gc used)
  private void triggerGarbageCollection()
  {
    timer.schedule(new TimerTask()
    {
      @Override
      public void run()
      {
        LOG.debug("trigger garbage collection ... ");
        System.gc();
      }
    }, 1500);
  }

  /**
   * Stop timer.
   */
  public void stopTimer()
  {
    timer.cancel();
  }

  private <EVENT extends ApplicationEvent> void fireEvent(EVENT event)
  {
    RoundFireEventTask roundFireEventTask = context.getBean(RoundFireEventTask.class);
    roundFireEventTask.init(event);
    roundPool.execute(roundFireEventTask);
  }

  private static int calcScoopNumber(long blockNumber, byte[] generationSignature)
  {
    if(blockNumber > 0 && generationSignature != null)
    {
      ByteBuffer buf = ByteBuffer.allocate(32 + 8);
      buf.put(generationSignature);
      buf.putLong(blockNumber);

      // generate new scoop number
      Shabal256 md = new Shabal256();
      md.update(buf.array());

      BigInteger hashnum = new BigInteger(1, md.digest());
      return hashnum.mod(BigInteger.valueOf(MiningPlot.SCOOPS_PER_PLOT)).intValue();
    }
    return 0;
  }
}
