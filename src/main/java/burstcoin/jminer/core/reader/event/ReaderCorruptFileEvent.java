package burstcoin.jminer.core.reader.event;

import burstcoin.jminer.core.reader.Reader;
import org.springframework.context.ApplicationEvent;

/**
 * fired on NetworkResultErrorEvent, if a calculated deadline was not matching server result, to deliver affected plot file
 */
public class ReaderCorruptFileEvent
  extends ApplicationEvent
{
  private long blockNumber;
  private String filePath;
  private long numberOfChunks;
  private int numberOfParts;

  public ReaderCorruptFileEvent(Reader source, long blockNumber, String filePath, long numberOfChunks, int numberOfParts)
  {
    super(source);
    this.blockNumber = blockNumber;

    this.filePath = filePath;
    this.numberOfChunks = numberOfChunks;
    this.numberOfParts = numberOfParts;
  }

  public long getBlockNumber()
  {
    return blockNumber;
  }

  public String getFilePath()
  {
    return filePath;
  }

  public long getNumberOfChunks()
  {
    return numberOfChunks;
  }

  public int getNumberOfParts()
  {
    return numberOfParts;
  }
}
