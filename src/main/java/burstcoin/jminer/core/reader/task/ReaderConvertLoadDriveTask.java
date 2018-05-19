/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 by luxe - https://github.com/de-luxe - BURST-LUXE-RED2-G6JW-H4HG5
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

package burstcoin.jminer.core.reader.task;

import burstcoin.jminer.core.CoreProperties;
import burstcoin.jminer.core.reader.Reader;
import burstcoin.jminer.core.reader.data.PlotDrive;
import burstcoin.jminer.core.reader.data.PlotFile;
import burstcoin.jminer.core.reader.event.ReaderDriveFinishEvent;
import burstcoin.jminer.core.reader.event.ReaderDriveInterruptedEvent;
import burstcoin.jminer.core.reader.event.ReaderLoadedPartEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pocminer.generate.MiningPlot;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;

/**
 * This reader can handle POC2 plotfiles as long as POC1 is active (tested)
 * and POC1 plotfiles if POC2 is active (todo Not tested yet).
 * To archive compatibility, twice the amount of data needs to be read. Also memory and cpu usage is higher.
 *
 * Executed once for every block ... reads scoops of drive plots
 */
@Component
@Scope("prototype")
public class ReaderConvertLoadDriveTask
  implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(ReaderConvertLoadDriveTask.class);

  private final ApplicationEventPublisher publisher;

  private byte[] generationSignature;
  private PlotDrive plotDrive;
  private int scoopNumber;
  private long blockNumber;
  private boolean showDriveInfo;

  @Autowired
  public ReaderConvertLoadDriveTask(ApplicationEventPublisher publisher)
  {
    this.publisher = publisher;
  }

  public void init(int scoopNumber, long blockNumber, byte[] generationSignature, PlotDrive plotDrive)
  {
    this.scoopNumber = scoopNumber;
    this.blockNumber = blockNumber;
    this.generationSignature = generationSignature;
    this.plotDrive = plotDrive;

    showDriveInfo = CoreProperties.isShowDriveInfo();
  }

  @Override
  public void run()
  {
    long startTime = showDriveInfo ? new Date().getTime() : 0;
    Iterator<PlotFile> iterator = plotDrive.getPlotFiles().iterator();
    boolean interrupted = false;
    while(iterator.hasNext() && !interrupted)
    {
      PlotFile plotPathInfo = iterator.next();
      if(plotPathInfo.getStaggeramt() % plotPathInfo.getNumberOfParts() > 0)
      {
        LOG.warn("staggeramt " + plotPathInfo.getStaggeramt() + " can not be devided by " + plotPathInfo.getNumberOfParts());
        // fallback ... could lead to problems on optimized plot-files
        plotPathInfo.setNumberOfParts(1);
      }
      interrupted = load(plotPathInfo);
    }

    if(showDriveInfo)
    {
      if(interrupted)
      {
        // ui-event
        publisher.publishEvent(new ReaderDriveInterruptedEvent(blockNumber, plotDrive.getDirectory()));
      }
      else
      {
        // ui event
        publisher.publishEvent(new ReaderDriveFinishEvent(plotDrive.getDirectory(), plotDrive.getSize(), new Date().getTime() - startTime, blockNumber));
      }
    }
  }

  private boolean load(PlotFile plotFile)
  {
    // using two channels to avoid positioning manually
    try (SeekableByteChannel sbc1 = Files.newByteChannel(plotFile.getFilePath(), EnumSet.of(StandardOpenOption.READ));
         SeekableByteChannel sbc2 = Files.newByteChannel(plotFile.getFilePath(), EnumSet.of(StandardOpenOption.READ)))
    {
      long partSize = plotFile.getStaggeramt() / plotFile.getNumberOfParts();
      long currentScoopPosition1 = scoopNumber * plotFile.getStaggeramt() * MiningPlot.SCOOP_SIZE;
      int partBufferSize = (int) (partSize * MiningPlot.SCOOP_SIZE);
      ByteBuffer partBuffer1 = ByteBuffer.allocate(partBufferSize);

      long currentScoopPosition2 = (4095 - scoopNumber) * plotFile.getStaggeramt() * MiningPlot.SCOOP_SIZE;
      ByteBuffer partBuffer2 = ByteBuffer.allocate(partBufferSize);

      // optimized plotFiles only have one chunk!
      for(int chunkNumber = 0; chunkNumber < plotFile.getNumberOfChunks(); chunkNumber++)
      {
        long currentChunkPosition = chunkNumber * plotFile.getStaggeramt() * MiningPlot.PLOT_SIZE;
        sbc1.position(currentScoopPosition1 + currentChunkPosition);
        sbc2.position(currentScoopPosition2 + currentChunkPosition);
        for(int partNumber = 0; partNumber < plotFile.getNumberOfParts(); partNumber++)
        {
          sbc1.read(partBuffer1);
          sbc2.read(partBuffer2);

          if(Reader.blockNumber != blockNumber || !Arrays.equals(Reader.generationSignature, generationSignature))
          {
            LOG.trace("loadDriveThread stopped!");
            partBuffer1.clear();
            partBuffer2.clear();
            sbc1.close();
            sbc2.close();
            return true;
          }
          else
          {
            byte[] scoops1 = partBuffer1.array();
            byte[] scoops2 = partBuffer2.array();

            partBuffer1.clear();
            partBuffer2.clear();

            // copy every other 32 bytes from scoops2 to scoops1
            for(int pos = 0; pos < partSize; pos++)
            {
              int copyPosition = pos * MiningPlot.SCOOP_SIZE + MiningPlot.HASH_SIZE;
              System.arraycopy(scoops2, copyPosition, scoops1, copyPosition, MiningPlot.HASH_SIZE);
            }
            BigInteger chunkPartStartNonce = plotFile.getStartnonce().add(BigInteger.valueOf(chunkNumber * plotFile.getStaggeramt() + partNumber * partSize));
            publisher.publishEvent(new ReaderLoadedPartEvent(blockNumber, generationSignature, scoops1, chunkPartStartNonce, plotFile.getFilePath().toString()));
          }
        }
      }
      sbc1.close();
      sbc2.close();
    }
    catch(NoSuchFileException exception)
    {
      LOG.error("File not found ... please restart to rescan plot-files, maybe set rescan to 'true': " + exception.getMessage());
    }
    catch(ClosedByInterruptException e)
    {
      // we reach this, if we do not wait for task on shutdown - ByteChannel closed by thread interruption
      LOG.trace("reader stopped cause of new block ...");
    }
    catch(IOException e)
    {
      LOG.error("IOException in: " + plotFile.getFilePath().toString() + " -> " + e.getMessage());
    }
    return false;
  }
}
