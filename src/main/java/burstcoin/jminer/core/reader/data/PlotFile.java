package burstcoin.jminer.core.reader.data;

import nxt.util.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pocminer.generate.MiningPlot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PlotFile
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PlotFile.class);

  // key -> size
  private Map<Long, Long> chunkPartStartNonces;

  private Path filePath;
  private Long chunkPartNonces;
  private int numberOfParts;
  private long numberOfChunks;

  private String filename;
  private long address;
  private long startnonce;
  private long plots;
  private long staggeramt;

  private long size;

  public PlotFile(Path filePath, Long chunkPartNonces)
  {
    this.filePath = filePath;
    this.chunkPartNonces = chunkPartNonces;
    this.filename = filePath.getFileName().toString();
    String[] parts = filename.split("_");
    this.address = Convert.parseUnsignedLong(parts[0]);
    this.startnonce = Long.valueOf(parts[1]);
    this.plots = Long.valueOf(parts[2]);
    staggeramt = Long.valueOf(parts[3]);
    this.numberOfParts = calculateNumberOfParts(staggeramt);
    this.numberOfChunks = plots / staggeramt;

    chunkPartStartNonces = new HashMap<>();

    size = numberOfChunks * staggeramt * MiningPlot.PLOT_SIZE;
    long chunkPartSize = size / numberOfChunks / numberOfParts;
    for(int chunkNumber = 0; chunkNumber < numberOfChunks; chunkNumber++)
    {
      for(int partNumber = 0; partNumber < numberOfParts; partNumber++)
      {
        // register a unique key for identification
        long chunkPartStartNonce = startnonce + chunkNumber * staggeramt + partNumber * (staggeramt / numberOfParts);
        Long key = chunkPartStartNonces.put(chunkPartStartNonce, chunkPartSize);
        if(key != null)
        {
            LOGGER.error("possible overlapping plot-file '"+ filePath +"' please use 'https://bchain.info/BURST/tools/overlap' to check your plots.");
        }
      }
    }
  }

  private long getSize(Path filePath)
  {
    long size = 1;
    try
    {
      size = Files.size(filePath);
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    return size;
  }

  public long getSize()
  {
    return size;
  }

  public Path getFilePath()
  {
    return filePath;
  }

  public String getFilename()
  {
    return filename;
  }

  public long getAddress()
  {
    return address;
  }

  public long getStartnonce()
  {
    return startnonce;
  }

  public long getPlots()
  {
    return plots;
  }

  public long getStaggeramt()
  {
    return staggeramt;
  }

  public long getNumberOfChunks()
  {
    return numberOfChunks;
  }

  public int getNumberOfParts()
  {
    return numberOfParts;
  }

  public void setNumberOfParts(int numberOfParts)
  {
    this.numberOfParts = numberOfParts;
  }

  public Map<Long, Long> getChunkPartStartNonces()
  {
    return chunkPartStartNonces;
  }

  // splitting into parts is not needed, but it seams to improve speed and enables us
  // to have steps of nearly same size
  private int calculateNumberOfParts(long staggeramt)
  {
    long targetNoncesPerPart = chunkPartNonces != null ? chunkPartNonces : 320000; // 640000 works fine

    // calculate numberOfParts based on target
    int suggestedNumberOfParts = (int) (staggeramt / targetNoncesPerPart) + 1;

    // ensure stagger is dividable by numberOfParts, if not adjust numberOfParts
    while(staggeramt % suggestedNumberOfParts != 0)
    {
      suggestedNumberOfParts += 1;
    }
    return suggestedNumberOfParts;
  }
}
