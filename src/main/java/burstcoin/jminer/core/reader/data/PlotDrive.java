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

package burstcoin.jminer.core.reader.data;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * The type Plot drive.
 */
public class PlotDrive
{
  private static final Logger LOG = LoggerFactory.getLogger(PlotDrive.class);

  private Collection<PlotFile> plotFiles;
  private String directory;

  /**
   * Instantiates a new Plot drive.
   *
   * @param directory the directory
   * @param plotFilePaths the plot file paths
   * @param chunkPartNonces the chunk part nonces
   */
  public PlotDrive(String directory, Collection<Path> plotFilePaths, Long chunkPartNonces)
  {
    this.directory = directory;

    plotFiles = new HashSet<>();
    for(Path path : plotFilePaths)
    {
      PlotFile plotFile = new PlotFile(path, chunkPartNonces);
      plotFiles.add(plotFile);

      if(plotFile.getStaggeramt() % plotFile.getNumberOfParts() != 0)
      {
        LOG.warn("could not calculate valid numberOfParts: " + plotFile.getFilePath());
      }
    }
  }

  /**
   * Gets plot files.
   *
   * @return the plot files
   */
  public Collection<PlotFile> getPlotFiles()
  {
    return plotFiles;
  }

  /**
   * Gets directory.
   *
   * @return the directory
   */
  public String getDirectory()
  {
    return directory;
  }

  /**
   * Collect chunk part start nonces.
   *
   * @return the map
   */
  public Map<Long, Long> collectChunkPartStartNonces()
  {
    Map<Long, Long> chunkPartStartNonces = new HashMap<>();
    for(PlotFile plotFile : plotFiles)
    {
      int expectedSize = chunkPartStartNonces.size() + plotFile.getChunkPartStartNonces().size();
      chunkPartStartNonces.putAll(plotFile.getChunkPartStartNonces());
      if(expectedSize != chunkPartStartNonces.size())
      {
        LOG.error("possible overlapping plot-file '" + plotFile.getFilePath() + "' please use 'https://bchain.info/BURST/tools/overlap' to check your plots.");
      }
    }
    return chunkPartStartNonces;
  }

  /**
   * Gets size.
   *
   * @return the size
   */
// returns total number of bytes of all plotFiles
  public long getSize()
  {
    long size = 0;
    for(PlotFile plotFile : plotFiles)
    {
      size += plotFile.getSize();
    }
    return size;
  }
}
