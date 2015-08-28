/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 by luxe - https://github.com/de-luxe - BURST-LUXE-ZDVD-CX3E-3SM58
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


import burstcoin.jminer.core.reader.data.PlotDrive;
import burstcoin.jminer.core.reader.data.PlotFile;
import burstcoin.jminer.core.reader.event.ReaderLoadedPartEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pocminer.generate.MiningPlot;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;


/**
 * The type Reader load drive task.
 */
/* executed once for every block ... reads scoops of drive plots */
@Component
@Scope("prototype")
public class ReaderLoadDriveTask
  implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(ReaderLoadDriveTask.class);

  @Autowired
  private ApplicationEventPublisher publisher;

  private PlotDrive plotDrive;
  private int scoopNumber;
  private long blockNumber;

  /**
   * Init void.
   *
   * @param scoopNumber the scoop number
   * @param blockNumber the block number
   * @param plotDrive the plot drive
   */
  public void init(int scoopNumber, long blockNumber, PlotDrive plotDrive)
  {
    this.scoopNumber = scoopNumber;
    this.blockNumber = blockNumber;

    this.plotDrive = plotDrive;
  }

  @Override
  public void run()
  {
    for(PlotFile plotPathInfo : plotDrive.getPlotFiles())
    {
      if(plotPathInfo.getStaggeramt() % plotPathInfo.getNumberOfParts() > 0)
      {
        LOG.warn("staggeramt " + plotPathInfo.getStaggeramt() + " can not be devided by " + plotPathInfo.getNumberOfParts());
        // fallback ... could lead to problems on optimized plot-files
        plotPathInfo.setNumberOfParts(1);
      }
      load(plotPathInfo);
    }
  }

  private void load(PlotFile plotFile)
  {
    try (SeekableByteChannel sbc = Files.newByteChannel(plotFile.getFilePath(), EnumSet.of(StandardOpenOption.READ)))
    {
      long currentScoopPosition = scoopNumber * plotFile.getStaggeramt() * MiningPlot.SCOOP_SIZE;

      long partSize = plotFile.getStaggeramt() / plotFile.getNumberOfParts();
      ByteBuffer partBuffer = ByteBuffer.allocate((int) (partSize * MiningPlot.SCOOP_SIZE));
      // optimized plotFiles only have one chunk!
      for(int chunkNumber = 0; chunkNumber < plotFile.getNumberOfChunks(); chunkNumber++)
      {
        long currentChunkPosition = chunkNumber * plotFile.getStaggeramt() * MiningPlot.PLOT_SIZE;
        sbc.position(currentScoopPosition + currentChunkPosition);
        for(int partNumber = 0; partNumber < plotFile.getNumberOfParts(); partNumber++)
        {
          sbc.read(partBuffer);

          if(Thread.interrupted())
          {
            LOG.debug("loadDriveThread interrupted!");
            // make sure to stop reading and clean up
            chunkNumber += plotFile.getNumberOfChunks();
            partNumber += plotFile.getNumberOfParts();
          }
          else
          {
            long chunkPartStartNonce = plotFile.getStartnonce() + (chunkNumber * plotFile.getStaggeramt()) + (partNumber * partSize);
            final byte[] scoops = partBuffer.array();
            publisher.publishEvent(new ReaderLoadedPartEvent(blockNumber, scoops, chunkPartStartNonce));
          }
          partBuffer.clear();
        }
      }
      sbc.close();
    }
    catch(NoSuchFileException exception)
    {
      LOG.debug("File not found ... please restart to rescan plot-files, maybe set rescan to 'true': " + exception.getMessage());
    }
    catch(ClosedByInterruptException e)
    {
      LOG.debug("reader stopped cause of new block ...");
    }
    catch(IOException e)
    {
      LOG.error("IOException: " + e.getMessage());
    }
  }
}
