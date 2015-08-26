package burstcoin.jminer.core.round.event;

import org.springframework.context.ApplicationEvent;

public class RoundFinishedEvent
  extends ApplicationEvent
{
  private long blockNumber;
  private long bestCommittedDeadline;
  private long roundTime;

  public RoundFinishedEvent(long blockNumber, long bestCommittedDeadline, long roundTime)
  {
    super(blockNumber);
    this.blockNumber = blockNumber;
    this.bestCommittedDeadline = bestCommittedDeadline;
    this.roundTime = roundTime;
  }

  public long getBlockNumber()
  {
    return blockNumber;
  }

  public long getBestCommittedDeadline()
  {
    return bestCommittedDeadline;
  }

  public long getRoundTime()
  {
    return roundTime;
  }
}
