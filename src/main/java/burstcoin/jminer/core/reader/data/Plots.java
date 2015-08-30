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

package burstcoin.jminer.core.reader.data;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * The type Plots.
 */
public class Plots
{
  private static final Logger LOGGER = LoggerFactory.getLogger(Plots.class);

  private Collection<PlotDrive> plotDrives;
  private Map<Long, Long> chunkPartStartNonces;

  /**
   * Instantiates a new Plots.
   *
   * @param plotPaths the plot paths
   * @param numericAccountId the numeric account id
   * @param chunkPartNonces the chunk part nonces
   */
  public Plots(List<String> plotPaths, String numericAccountId, long chunkPartNonces)
  {
    plotDrives = new HashSet<>();
    chunkPartStartNonces = new HashMap<>();
    Map<String, Collection<Path>> plotFilesLookup = collectPlotFiles(plotPaths, numericAccountId);
    for(Map.Entry<String, Collection<Path>> entry : plotFilesLookup.entrySet())
    {
      PlotDrive plotDrive = new PlotDrive(entry.getKey(), entry.getValue(), chunkPartNonces);
      plotDrives.add(plotDrive);

      int expectedSize = chunkPartStartNonces.size() + plotDrive.collectChunkPartStartNonces().size();
      chunkPartStartNonces.putAll(plotDrive.collectChunkPartStartNonces());
      if(expectedSize != chunkPartStartNonces.size())
      {
        LOGGER.error("possible duplicate/overlapping polt-file on drive '" + plotDrive.getDirectory()
                     + "' please use 'https://bchain.info/BURST/tools/overlap' to check your plots.");
      }
    }
  }

  /**
   * Gets plot drives.
   *
   * @return the plot drives
   */
  public Collection<PlotDrive> getPlotDrives()
  {
    return plotDrives;
  }

  private static Map<String, Collection<Path>> collectPlotFiles(List<String> plotDirectories, String numericAccountId)
  {
    Map<String, Collection<Path>> plotFilesLookup = new HashMap<>();
    for(String plotDirectory : plotDirectories)
    {
      Path folderPath = Paths.get(plotDirectory);
      try (DirectoryStream<Path> plotFilesStream = Files.newDirectoryStream(folderPath))
      {
        List<Path> plotFilePaths = new ArrayList<>();
        for(Path plotFilePath : plotFilesStream)
        {
          if(plotFilePath.toString().contains(numericAccountId))
          {
            plotFilePaths.add(plotFilePath);
          }
        }
        plotFilesLookup.put(plotDirectory, plotFilePaths);
      }
      catch(IOException | DirectoryIteratorException e)
      {
        LOGGER.error(e.getMessage());
      }
    }
    return plotFilesLookup;
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
    for(PlotDrive plotDrive : plotDrives)
    {
      size += plotDrive.getSize();
    }
    return size;
  }

  /**
   * Print plot files.
   */
  public void printPlotFiles()
  {
    for(PlotDrive plotDrive : getPlotDrives())
    {
      for(PlotFile plotFile : plotDrive.getPlotFiles())
      {
        System.out.println(plotFile.getFilePath());
      }
    }
  }

  /**
   * Gets plot file by plot file start nonce.
   *
   * @param plotFileStartNonce the plot file start nonce
   * @return the plot file by plot file start nonce
   */
  public PlotFile getPlotFileByPlotFileStartNonce(long plotFileStartNonce)
  {
    for(PlotDrive plotDrive : getPlotDrives())
    {
      for(PlotFile plotFile : plotDrive.getPlotFiles())
      {
        if(plotFile.getFilename().contains(String.valueOf(plotFileStartNonce)))
        {
          return plotFile;
        }
      }
    }
    return null;
  }

  /**
   * Gets chunk part start nonces.
   *
   * @return the chunk part start nonces
   */
  public Map<Long, Long> getChunkPartStartNonces()
  {
    return chunkPartStartNonces;
  }

  /**
   * Gets plot file by chunk part start nonce.
   *
   * @param chunkPartStartNonce the chunk part start nonce
   * @return the plot file by chunk part start nonce
   */
  public PlotFile getPlotFileByChunkPartStartNonce(long chunkPartStartNonce)
  {
    for(PlotDrive plotDrive : getPlotDrives())
    {
      for(PlotFile plotFile : plotDrive.getPlotFiles())
      {
        if(chunkPartStartNonces.containsKey(chunkPartStartNonce))
        {
          return plotFile;
        }
      }
    }
    return null;
  }
}
