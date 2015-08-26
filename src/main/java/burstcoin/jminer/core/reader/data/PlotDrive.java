package burstcoin.jminer.core.reader.data;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PlotDrive
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PlotDrive.class);

  private Collection<PlotFile> plotFiles;
  private String directory;

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
        LOGGER.warn("could not calculate valid numberOfParts: " + plotFile.getFilePath());
      }
    }
  }

  public Collection<PlotFile> getPlotFiles()
  {
    return plotFiles;
  }

  public String getDirectory()
  {
    return directory;
  }

  public Map<Long, Long> collectChunkPartStartNonces()
  {
    Map<Long, Long> chunkPartStartNonces = new HashMap<>();
    for(PlotFile plotFile : plotFiles)
    {
      int expectedSize = chunkPartStartNonces.size() + plotFile.getChunkPartStartNonces().size();
      chunkPartStartNonces.putAll(plotFile.getChunkPartStartNonces());
      if(expectedSize != chunkPartStartNonces.size())
      {
        LOGGER
          .error("possible overlapping plot-file '" + plotFile.getFilePath() + "' please use 'https://bchain.info/BURST/tools/overlap' to check your plots.");
      }
    }
    return chunkPartStartNonces;
  }

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
