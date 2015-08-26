package burstcoin.jminer.core.network.event;

import org.springframework.context.ApplicationEvent;

/**
 * fired if last winner was found
 */
public class NetworkLastWinnerEvent
  extends ApplicationEvent
{
  private long lastBlockNumber;
  private String winner;

  public NetworkLastWinnerEvent(Object source, long lastBlockNumber, String winner)
  {
    super(source);
    this.lastBlockNumber = lastBlockNumber;
    this.winner = winner;
  }

  public long getLastBlockNumber()
  {
    return lastBlockNumber;
  }

  public String getWinner()
  {
    return winner;
  }
}
