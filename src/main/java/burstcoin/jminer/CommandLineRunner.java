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

package burstcoin.jminer;

import burstcoin.jminer.core.CoreProperties;
import burstcoin.jminer.core.network.Network;
import burstcoin.jminer.core.network.event.NetworkDevResultConfirmedEvent;
import burstcoin.jminer.core.network.event.NetworkLastWinnerEvent;
import burstcoin.jminer.core.network.event.NetworkResultConfirmedEvent;
import burstcoin.jminer.core.network.event.NetworkResultErrorEvent;
import burstcoin.jminer.core.network.event.NetworkStateChangeEvent;
import burstcoin.jminer.core.network.model.DevPoolResult;
import burstcoin.jminer.core.reader.event.ReaderCorruptFileEvent;
import burstcoin.jminer.core.reader.event.ReaderDriveFinishEvent;
import burstcoin.jminer.core.reader.event.ReaderDriveInterruptedEvent;
import burstcoin.jminer.core.reader.event.ReaderProgressChangedEvent;
import burstcoin.jminer.core.round.event.RoundFinishedEvent;
import burstcoin.jminer.core.round.event.RoundSingleResultEvent;
import burstcoin.jminer.core.round.event.RoundSingleResultSkippedEvent;
import burstcoin.jminer.core.round.event.RoundStartedEvent;
import burstcoin.jminer.core.round.event.RoundStoppedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.math.MathContext;

@SpringBootApplication
public class CommandLineRunner
{
  private static final Logger LOG = LoggerFactory.getLogger(CommandLineRunner.class);

  private static final int NUMBER_OF_PROGRESS_LOGS_PER_ROUND = CoreProperties.getReadProgressPerRound();

  private static final int SIZE_DIVISOR = CoreProperties.isByteUnitDecimal() ? 1000 : 1024;
  private static final String T_UNIT = CoreProperties.isByteUnitDecimal() ? "TB" : "TiB";
  private static final String G_UNIT = CoreProperties.isByteUnitDecimal() ? "GB" : "GiB";
  private static final String M_UNIT = CoreProperties.isByteUnitDecimal() ? "MB" : "MiB";

  private static long blockNumber;
  private static int progressLogStep;
  private static long previousRemainingCapacity = 0;
  private static long previousElapsedTime = 0;

  public static void main(String[] args)
  {
    LOG.info("start the engines ...");
    ConfigurableApplicationContext context = SpringApplication.run(CommandLineRunner.class);

    context.addApplicationListener(new ApplicationListener<RoundFinishedEvent>()
    {
      @Override
      public void onApplicationEvent(RoundFinishedEvent event)
      {
        previousRemainingCapacity = 0;
        previousElapsedTime = 0;

        long s = event.getRoundTime() / 1000;
        long ms = event.getRoundTime() % 1000;

        String bestDeadline = Long.MAX_VALUE == event.getBestCommittedDeadline() ? "N/A" : String.valueOf(event.getBestCommittedDeadline());
        LOG.info("FINISH block '" + event.getBlockNumber() + "', "
                 + "best deadline '" + bestDeadline + "', "
                 + "round time '" + s + "s " + ms + "ms'");
      }
    });

    context.addApplicationListener(new ApplicationListener<RoundStoppedEvent>()
    {
      @Override
      public void onApplicationEvent(RoundStoppedEvent event)
      {
        previousRemainingCapacity = 0;
        previousElapsedTime = 0;

        long s = event.getElapsedTime() / 1000;
        long ms = event.getElapsedTime() % 1000;

        BigDecimal totalCapacity = new BigDecimal(event.getCapacity());
        BigDecimal factor = BigDecimal.ONE.divide(totalCapacity, MathContext.DECIMAL32);
        BigDecimal progress = factor.multiply(new BigDecimal(event.getCapacity() - event.getRemainingCapacity()));
        int percentage = (int) Math.ceil(progress.doubleValue() * 100);
        percentage = percentage > 100 ? 100 : percentage;

        String bestDeadline = Long.MAX_VALUE == event.getBestCommittedDeadline() ? "N/A" : String.valueOf(event.getBestCommittedDeadline());
        LOG.info("STOP block '" + event.getBlockNumber() + "', " + String.valueOf(percentage) + "% done, "
                 + "best deadline '" + bestDeadline + "', "
                 + "elapsed time '" + s + "s " + ms + "ms'");
      }
    });

    context.addApplicationListener(new ApplicationListener<NetworkLastWinnerEvent>()
    {
      @Override
      public void onApplicationEvent(NetworkLastWinnerEvent event)
      {
        if(blockNumber - 1 == event.getLastBlockNumber())
        {
          LOG.info("      winner block '" + event.getLastBlockNumber() + "', '" + event.getWinner() + "'");
        }
        else
        {
          LOG.error("Error: NetworkLastWinnerEvent for block: " + event.getLastBlockNumber() + " is outdated!");
        }
      }
    });

    context.addApplicationListener(new ApplicationListener<NetworkStateChangeEvent>()
    {
      @Override
      public void onApplicationEvent(NetworkStateChangeEvent event)
      {
        blockNumber = event.getBlockNumber();
      }
    });

    context.addApplicationListener(new ApplicationListener<RoundStartedEvent>()
    {
      @Override
      public void onApplicationEvent(RoundStartedEvent event)
      {
        progressLogStep = NUMBER_OF_PROGRESS_LOGS_PER_ROUND;

        LOG.info("-------------------------------------------------------");
        LOG.info("START block '" + event.getBlockNumber() + "', "
                 + "scoopNumber '" + event.getScoopNumber() + "', "
                 + "capacity '" + event.getCapacity() / SIZE_DIVISOR / SIZE_DIVISOR / SIZE_DIVISOR + " " + G_UNIT + "'"
                );
        String target = event.getTargetDeadline() == Long.MAX_VALUE ? "N/A" : String.valueOf(event.getTargetDeadline());
        LOG.info("      targetDeadline '" + target + "', " + "baseTarget '" + String.valueOf(event.getBaseTarget()) + "'");
      }
    });

    context.addApplicationListener(new ApplicationListener<ReaderProgressChangedEvent>()
    {
      @Override
      public void onApplicationEvent(ReaderProgressChangedEvent event)
      {
        long logStepCapacity = event.getCapacity() / NUMBER_OF_PROGRESS_LOGS_PER_ROUND;

        if(event.getRemainingCapacity() < logStepCapacity * progressLogStep || event.getRemainingCapacity() == 0)
        {
          progressLogStep--;

          // trigger garbage collection on every progress step
          System.gc();

          BigDecimal totalCapacity = new BigDecimal(event.getCapacity());
          BigDecimal factor = BigDecimal.ONE.divide(totalCapacity, MathContext.DECIMAL32);
          BigDecimal progress = factor.multiply(new BigDecimal(event.getCapacity() - event.getRemainingCapacity()));
          int percentage = (int) Math.ceil(progress.doubleValue() * 100);
          percentage = percentage > 100 ? 100 : percentage;

          // calculate capacity
          long effMBPerSec = 0;
          if(previousRemainingCapacity > 0)
          {
            long effDoneBytes = previousRemainingCapacity - event.getRemainingCapacity();

            // calculate current reading speed (since last info)
            long effBytesPerMs = (effDoneBytes / 4096) / (event.getElapsedTime() - previousElapsedTime);
            effMBPerSec = (effBytesPerMs * 1000) / SIZE_DIVISOR / SIZE_DIVISOR;
          }

          // calculate capacity
          long doneBytes = event.getCapacity() - event.getRemainingCapacity();
          long doneTB = doneBytes / SIZE_DIVISOR / SIZE_DIVISOR / SIZE_DIVISOR / SIZE_DIVISOR;
          long doneGB = doneBytes / SIZE_DIVISOR / SIZE_DIVISOR / SIZE_DIVISOR % SIZE_DIVISOR;

          // calculate reading speed (average)
          long averageBytesPerMs = (doneBytes / 4096) / event.getElapsedTime();
          long averageMBPerSec = (averageBytesPerMs * 1000) / SIZE_DIVISOR / SIZE_DIVISOR;

          previousRemainingCapacity = event.getRemainingCapacity();
          previousElapsedTime = event.getElapsedTime();

          LOG.info(
            String.valueOf(percentage) + "% done (" + doneTB + T_UNIT + " " + doneGB + G_UNIT + "), avg.'" + averageMBPerSec + " " + M_UNIT + "/s'" +
            (effMBPerSec > 0 ? ", eff.'" + effMBPerSec + " " + M_UNIT + "/s'" : ""));
        }
      }
    });

    context.addApplicationListener(new ApplicationListener<RoundSingleResultEvent>()
    {
      @Override
      public void onApplicationEvent(RoundSingleResultEvent event)
      {
        LOG.info("dl '" + event.getCalculatedDeadline() + "' send (" + (event.isPoolMining() ? "pool" : "solo") + ")");
      }
    });

    context.addApplicationListener(new ApplicationListener<RoundSingleResultSkippedEvent>()
    {
      @Override
      public void onApplicationEvent(RoundSingleResultSkippedEvent event)
      {
        LOG.info("dl '" + event.getCalculatedDeadline() + "' > '" + event.getTargetDeadline() + "' skipped");
      }
    });

    context.addApplicationListener(new ApplicationListener<NetworkResultConfirmedEvent>()
    {
      @Override
      public void onApplicationEvent(NetworkResultConfirmedEvent event)
      {
        LOG.info("dl '" + event.getDeadline() + "' confirmed!  [ " + getDeadlineTime(event.getDeadline()) + " ]");
      }
    });

    context.addApplicationListener(new ApplicationListener<NetworkDevResultConfirmedEvent>()
    {
      @Override
      public void onApplicationEvent(NetworkDevResultConfirmedEvent event)
      {
        LOG.info("devPool response '" + event.getResponse() + "', block '" + event.getBlockNumber() + "'");
        for(DevPoolResult devPoolResult : event.getDevPoolResults())
        {
          LOG.info(
            "dl '" + devPoolResult.getCalculatedDeadline() + "' successful committed!  [ " + getDeadlineTime(devPoolResult.getCalculatedDeadline()) + " ]");
        }
      }
    });

    context.addApplicationListener(new ApplicationListener<NetworkResultErrorEvent>()
    {
      @Override
      public void onApplicationEvent(NetworkResultErrorEvent event)
      {
        LOG.info("strange dl result '" + event.getStrangeDeadline() + "', calculated '" + event.getCalculatedDeadline() + "' "
                 + "block '" + event.getBlockNumber() + "' nonce '" + event.getNonce() + "'");
      }
    });

    context.addApplicationListener(new ApplicationListener<ReaderCorruptFileEvent>()
    {
      @Override
      public void onApplicationEvent(ReaderCorruptFileEvent event)
      {
        LOG.info("strange dl source '" + event.getFilePath() + "' (try replotting!?)");
        LOG.info("strange dl file chunks '" + event.getNumberOfChunks() + "', "
                 + "parts per chunk '" + event.getNumberOfParts() + "', "
                 + "block '" + event.getBlockNumber() + "'");
      }
    });

    context.addApplicationListener(new ApplicationListener<ReaderDriveFinishEvent>()
    {
      @Override
      public void onApplicationEvent(ReaderDriveFinishEvent event)
      {
        if(blockNumber == event.getBlockNumber())
        {
          // calculate capacity
          long doneBytes = event.getSize();
          long doneTB = doneBytes / SIZE_DIVISOR / SIZE_DIVISOR / SIZE_DIVISOR / SIZE_DIVISOR;
          long doneGB = doneBytes / SIZE_DIVISOR / SIZE_DIVISOR / SIZE_DIVISOR % SIZE_DIVISOR;

          long s = event.getTime() / 1000;
          long ms = event.getTime() % 1000;

          LOG.info("read '" + event.getDirectory() + "' (" + doneTB + T_UNIT + " " + doneGB + G_UNIT + ") in '" + s + "s " + ms + "ms'");
        }
      }
    });

    context.addApplicationListener(new ApplicationListener<ReaderDriveInterruptedEvent>()
    {
      @Override
      public void onApplicationEvent(ReaderDriveInterruptedEvent event)
      {
        LOG.info("stopped '" + event.getDirectory() + "' for block '" + event.getBlockNumber() + "'.");
      }
    });

    LOG.info("");
    LOG.info("                            Burstcoin (BURST)");
    LOG.info("            __         __   GPU assisted PoC-Miner");
    LOG.info("           |__| _____ |__| ____   ___________ ");
    LOG.info("   version |  |/     \\|  |/    \\_/ __ \\_  __ \\");
    LOG.info("     0.4.3 |  |  Y Y  \\  |   |  \\  ___/|  | \\/");
    LOG.info("       /\\__|  |__|_|  /__|___|  /\\___  >__| ");
    LOG.info("       \\______|     \\/        \\/     \\/");
    LOG.info("      mining engine: BURST-LUXE-RED2-G6JW-H4HG5");
    LOG.info("     openCL checker: BURST-QHCJ-9HB5-PTGC-5Q8J9");

    Network network = context.getBean(Network.class);
    network.startMining();
  }

  private static String getDeadlineTime(Long calculatedDeadline)
  {
    long sec = calculatedDeadline;
    long min = sec / 60;
    sec = sec % 60;
    long hours = min / 60;
    min = min % 60;
    long days = hours / 24;
    hours = hours % 24;
    return days + "d " + hours + "h " + min + "m " + sec + "s";
  }
}
