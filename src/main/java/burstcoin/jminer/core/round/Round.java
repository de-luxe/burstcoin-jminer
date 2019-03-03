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

package burstcoin.jminer.core.round;

import burstcoin.jminer.core.CoreProperties;
import burstcoin.jminer.core.checker.Checker;
import burstcoin.jminer.core.checker.event.CheckerResultEvent;
import burstcoin.jminer.core.network.Network;
import burstcoin.jminer.core.network.event.NetworkQualityChangeEvent;
import burstcoin.jminer.core.network.event.NetworkResultConfirmedEvent;
import burstcoin.jminer.core.network.event.NetworkResultErrorEvent;
import burstcoin.jminer.core.network.event.NetworkStateChangeEvent;
import burstcoin.jminer.core.reader.Reader;
import burstcoin.jminer.core.reader.data.Plots;
import burstcoin.jminer.core.round.event.RoundFinishedEvent;
import burstcoin.jminer.core.round.event.RoundGenSigAlreadyMinedEvent;
import burstcoin.jminer.core.round.event.RoundGenSigUpdatedEvent;
import burstcoin.jminer.core.round.event.RoundSingleResultEvent;
import burstcoin.jminer.core.round.event.RoundSingleResultSkippedEvent;
import burstcoin.jminer.core.round.event.RoundStartedEvent;
import fr.cryptohash.Shabal256;
import nxt.util.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pocminer.generate.MiningPlot;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The type Round.
 */
@Component
@Scope("singleton")
public class Round
{
  private static final Logger LOG = LoggerFactory.getLogger(Round.class);

  private final Reader reader;
  private final Checker checker;
  private final Network network;
  private final ApplicationEventPublisher publisher;

  private boolean poolMining;
  private long targetDeadline;

  private Timer timer;
  private long blockNumber;
  private long finishedBlockNumber;
  private long baseTarget;
  private Date roundStartDate;

  private BigInteger lowest;
  private long bestCommittedDeadline;

  // cache for next lowest
  private CheckerResultEvent queuedEvent;
  private BigInteger lowestCommitted;

  private Set<BigInteger> runningChunkPartStartNonces;
  private Plots plots;
  private byte[] generationSignature;

  // generationSignature
  private Set<String> finishedLookup;

  private long networkSuccessCount;
  private long networkFailCount;

  @Autowired
  public Round(Reader reader, Checker checker, Network network, ApplicationEventPublisher publisher)
  {
    this.reader = reader;
    this.checker = checker;
    this.network = network;
    this.publisher = publisher;

    finishedLookup = new HashSet<>();
  }

  @PostConstruct
  protected void postConstruct()
  {
    this.poolMining = CoreProperties.isPoolMining();
    timer = new Timer();
  }

  private void initNewRound(Plots plots)
  {
    networkFailCount = 0;
    networkSuccessCount = 0;
    runningChunkPartStartNonces = new HashSet<>(plots.getChunkPartStartNonces().keySet());
    roundStartDate = new Date();
    lowest = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);
    lowestCommitted = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);
    queuedEvent = null;
    bestCommittedDeadline = Long.MAX_VALUE;
  }

  @EventListener
  public void handleMessage(NetworkStateChangeEvent event)
  {
    synchronized(reader)
    {
      boolean blockHeightIncreased = blockNumber < event.getBlockNumber();
      boolean generationSignatureChanged = generationSignature != null && !Arrays.equals(event.getGenerationSignature(), generationSignature);
      boolean restart = false;

      boolean alreadyMined = finishedLookup.contains(Convert.toHexString(event.getGenerationSignature()));
      if(alreadyMined && CoreProperties.isUpdateMiningInfo())
      {
        publisher.publishEvent(new RoundGenSigAlreadyMinedEvent(event.getBlockNumber(), event.getGenerationSignature()));
      }

      if(!blockHeightIncreased && (!alreadyMined && CoreProperties.isUpdateMiningInfo() && generationSignatureChanged))
      {
        restart = true;
        // generationSignature for block updated
        if(finishedBlockNumber == blockNumber)
        {
          finishedBlockNumber--;
        }
        // ui event
        publisher.publishEvent(new RoundGenSigUpdatedEvent(blockNumber, generationSignature));
      }

      long previousBlockNumber = event.getBlockNumber();
      if(blockHeightIncreased)
      {
        previousBlockNumber = blockNumber;
        Round.this.blockNumber = event.getBlockNumber();
      }

      if(blockHeightIncreased || (!alreadyMined && CoreProperties.isUpdateMiningInfo() && generationSignatureChanged))
      {
        Round.this.baseTarget = event.getBaseTarget();
        Round.this.targetDeadline = event.getTargetDeadline();

        long lastBestCommittedDeadline = bestCommittedDeadline;

        plots = reader.getPlots();
        int networkQuality = getNetworkQuality();
        initNewRound(plots);

        // reconfigure checker
        generationSignature = event.getGenerationSignature();
        if(CoreProperties.isUseOpenCl())
        {
          checker.reconfigure(blockNumber, generationSignature);
        }

        // start reader
        int scoopNumber = calcScoopNumber(event.getBlockNumber(), event.getGenerationSignature());
        reader.read(previousBlockNumber, blockNumber, generationSignature, scoopNumber, lastBestCommittedDeadline, networkQuality);

        // ui event
        publisher.publishEvent(new RoundStartedEvent(restart, blockNumber, scoopNumber, plots.getSize(), targetDeadline, baseTarget, generationSignature));

        network.checkLastWinner(blockNumber);
      }
    }
  }

  @EventListener
  public void handleMessage(CheckerResultEvent event)
  {
    synchronized(reader)
    {
      if(isCurrentRound(event.getBlockNumber(), event.getGenerationSignature()))
      {
        BigInteger nonce = event.getChunkPartStartNonce().add(BigInteger.valueOf(event.getLowestNonce()));
        BigInteger result = calculateResult(event.getScoops(), generationSignature, event.getLowestNonce());
        event.setResult(result);

        BigInteger deadline = result.divide(BigInteger.valueOf(baseTarget));
        long calculatedDeadline = deadline.longValue();

        if(result.compareTo(lowest) < 0)
        {
          lowest = result;
          if(calculatedDeadline < targetDeadline)
          {
            network.commitResult(blockNumber, calculatedDeadline, nonce, event.getChunkPartStartNonce(), plots.getSize(), result, event.getPlotFilePath());

            // ui event
            publisher.publishEvent(new RoundSingleResultEvent(event.getBlockNumber(), nonce, event.getChunkPartStartNonce(), calculatedDeadline,
                                                              poolMining));
          }
          else
          {
            // ui event
            if(CoreProperties.isShowSkippedDeadlines())
            {
              publisher.publishEvent(new RoundSingleResultSkippedEvent(event.getBlockNumber(), nonce, event.getChunkPartStartNonce(), calculatedDeadline,
                                                                       targetDeadline, poolMining));
            }
            // chunkPartStartNonce finished
            runningChunkPartStartNonces.remove(event.getChunkPartStartNonce());
            triggerFinishRoundEvent(event.getBlockNumber());
          }
        }
        // remember next lowest in case that lowest fails to commit
        else if(calculatedDeadline < targetDeadline
                && result.compareTo(lowestCommitted) < 0
                && (queuedEvent == null || result.compareTo(queuedEvent.getResult()) < 0))
        {
          if(queuedEvent != null)
          {
            // remove previous queued
            runningChunkPartStartNonces.remove(queuedEvent.getChunkPartStartNonce());
          }
          LOG.info("dl '" + calculatedDeadline + "' queued");
          queuedEvent = event;

          triggerFinishRoundEvent(event.getBlockNumber());
        }
        else
        {
          // chunkPartStartNonce finished
          runningChunkPartStartNonces.remove(event.getChunkPartStartNonce());
          triggerFinishRoundEvent(event.getBlockNumber());
        }
      }
      else
      {
        LOG.trace("event for previous block ...");
      }
    }
  }

  @EventListener
  public void handleMessage(NetworkResultConfirmedEvent event)
  {
    if(isCurrentRound(event.getBlockNumber(), event.getGenerationSignature()))
    {
      // if result if lower than lowestCommitted, update lowestCommitted
      if(event.getResult() != null && event.getResult().compareTo(lowestCommitted) < 0)
      {
        lowestCommitted = event.getResult();

        // if queuedLowest exist and is higher than lowestCommitted, remove queuedLowest
        if(queuedEvent != null && lowestCommitted.compareTo(queuedEvent.getResult()) < 0)
        {
          BigInteger dl = queuedEvent.getResult().divide(BigInteger.valueOf(baseTarget));
          LOG.debug("dl '" + dl + "' removed from queue");

          runningChunkPartStartNonces.remove(queuedEvent.getChunkPartStartNonce());
          queuedEvent = null;
        }
      }

      runningChunkPartStartNonces.remove(event.getChunkPartStartNonce());

      if(bestCommittedDeadline > event.getDeadline())
      {
        bestCommittedDeadline = event.getDeadline();
      }
      triggerFinishRoundEvent(event.getBlockNumber());
    }
  }

  @EventListener
  public void handleMessage(NetworkResultErrorEvent event)
  {
    if(isCurrentRound(event.getBlockNumber(), event.getGenerationSignature()))
    {
      // reset lowest to lowestCommitted, as it does not commit successful.
      lowest = lowestCommitted;
      // in case that queued result is lower than committedLowest, commit queued again.
      if(queuedEvent != null && lowestCommitted.compareTo(queuedEvent.getResult()) < 0)
      {
        LOG.info("commit queued dl ...");
        handleMessage(queuedEvent);

        queuedEvent = null;
      }

      runningChunkPartStartNonces.remove(event.getChunkPartStartNonce());
      triggerFinishRoundEvent(event.getBlockNumber());
    }
  }

  @EventListener
  public void handleMessage(NetworkQualityChangeEvent event)
  {
    if(event.isSuccess())
    {
      networkSuccessCount++;
    }
    else
    {
      networkFailCount++;
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
      // commit queued if exists ... and it is the only remaining in runningChunkPartStartNonces
      else if(queuedEvent != null && runningChunkPartStartNonces.size() == 1 && runningChunkPartStartNonces.contains(queuedEvent.getChunkPartStartNonce()))
      {
        handleMessage(queuedEvent);
        queuedEvent = null;
      }
    }
  }

  private void onRoundFinish(long blockNumber)
  {
    finishedBlockNumber = blockNumber;

    // remember finished genSig, to prevent mining it again
    finishedLookup.add(Convert.toHexString(generationSignature));

    long elapsedRoundTime = new Date().getTime() - roundStartDate.getTime();
    int networkQuality = getNetworkQuality();
    timer.schedule(new TimerTask()
    {
      @Override
      public void run()
      {
        publisher.publishEvent(new RoundFinishedEvent(blockNumber, bestCommittedDeadline, elapsedRoundTime, networkQuality));
      }
    }, 250); // fire deferred

    triggerCleanup();
  }

  private int getNetworkQuality()
  {
    BigDecimal factor = BigDecimal.ONE.divide(new BigDecimal(networkSuccessCount + networkFailCount + 1), MathContext.DECIMAL32);
    BigDecimal progress = factor.multiply(new BigDecimal(networkSuccessCount + 1));
    int percentage = (int) Math.ceil(progress.doubleValue() * 100);
    return percentage > 100 ? 100 : percentage;
  }

  private void triggerCleanup()
  {
    TimerTask cleanupTask = new TimerTask()
    {
      @Override
      public void run()
      {
        if(!reader.cleanupReaderPool())
        {
          triggerCleanup();
        }
        else
        {
          System.gc();
        }
      }
    };

    try
    {
      timer.schedule(cleanupTask, 1000);
    }
    catch(IllegalStateException e)
    {
      LOG.error("cleanup task already scheduled ...");
    }
  }

  private boolean isCurrentRound(long currentBlockNumber, byte[] currentGenerationSignature)
  {
    return blockNumber == currentBlockNumber
           && Arrays.equals(generationSignature, currentGenerationSignature);
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

  private static BigInteger calculateResult(byte[] scoops, byte[] generationSignature, int nonce)
  {
    Shabal256 md = new Shabal256();
    md.reset();
    md.update(generationSignature);
    md.update(scoops, nonce * MiningPlot.SCOOP_SIZE, MiningPlot.SCOOP_SIZE);
    byte[] hash = md.digest();
    return new BigInteger(1, new byte[]{hash[7], hash[6], hash[5], hash[4], hash[3], hash[2], hash[1], hash[0]});
  }
}
