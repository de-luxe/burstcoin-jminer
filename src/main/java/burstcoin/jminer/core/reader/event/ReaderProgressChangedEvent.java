package burstcoin.jminer.core.reader.event;

import burstcoin.jminer.core.reader.Reader;
import org.springframework.context.ApplicationEvent;

public class ReaderProgressChangedEvent
  extends ApplicationEvent
{
  private long blockNumber;
  private long capacity;
  private long remainingCapacity;
  private long elapsedTime;

  public ReaderProgressChangedEvent(Reader source, long blockNumber, long capacity, long remainingCapacity, long elapsedTime)
  {
    super(source);

    this.blockNumber = blockNumber;
    this.capacity = capacity;
    this.remainingCapacity = remainingCapacity;
    this.elapsedTime = elapsedTime;
  }

  public long getBlockNumber()
  {
    return blockNumber;
  }

  public long getRemainingCapacity()
  {
    return remainingCapacity;
  }

  public long getCapacity()
  {
    return capacity;
  }

  public long getElapsedTime()
  {
    return elapsedTime;
  }
}
