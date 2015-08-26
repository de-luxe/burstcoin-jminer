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

public class Plots
{
  private static final Logger LOGGER = LoggerFactory.getLogger(Plots.class);

  private Collection<PlotDrive> plotDrives;
  private Map<Long, Long> chunkPartStartNonces;

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
        LOGGER.error("possible duplicate/overlapping polt-file on drive '"+plotDrive.getDirectory()+"' please use 'https://bchain.info/BURST/tools/overlap' to check your plots.");
      }
    }
  }

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
        LOGGER.error(e.getLocalizedMessage());
      }
    }
    return plotFilesLookup;
  }

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

  public Map<Long, Long> getChunkPartStartNonces()
  {
    return chunkPartStartNonces;
  }

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
