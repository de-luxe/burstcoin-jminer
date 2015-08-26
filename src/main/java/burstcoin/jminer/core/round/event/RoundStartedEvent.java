package burstcoin.jminer.core.round.event;


import org.springframework.context.ApplicationEvent;

/**
 * fired after mining started
 */
public class RoundStartedEvent
  extends ApplicationEvent
{
  private long scoopNumber;
  private long capacity;
  private long targetDeadline;
  private long baseTarget;
  private long blockNumber;

  public RoundStartedEvent(long blockNumber, long scoopNumber, long capacity, long targetDeadline, long baseTarget)
  {
    super(blockNumber);

    this.blockNumber = blockNumber;
    this.scoopNumber = scoopNumber;
    this.capacity = capacity;
    this.targetDeadline = targetDeadline;
    this.baseTarget = baseTarget;
  }

  public long getCapacity()
  {
    return capacity;
  }

  public long getBlockNumber()
  {
    return blockNumber;
  }

  public long getScoopNumber()
  {
    return scoopNumber;
  }

  public long getTargetDeadline()
  {
    return targetDeadline;
  }

  public long getBaseTarget()
  {
    return baseTarget;
  }
}
