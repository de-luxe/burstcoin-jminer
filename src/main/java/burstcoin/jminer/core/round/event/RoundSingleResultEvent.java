package burstcoin.jminer.core.round.event;

import burstcoin.jminer.core.round.Round;
import org.springframework.context.ApplicationEvent;

/**
 * fired on new best deadline below targetDeadline (not confirmed)
 */
public class RoundSingleResultEvent
  extends ApplicationEvent
{
  private long blockNumber;
  private long nonce;
  private long chunkPartStartNonce;

  private long calculatedDeadline;
  private boolean poolMining;

  public RoundSingleResultEvent(Round source, long blockNumber, long nonce, long chunkPartStartNonce, long calculatedDeadline, boolean poolMining)
  {
    super(source);

    this.nonce = nonce;
    this.blockNumber = blockNumber;
    this.chunkPartStartNonce = chunkPartStartNonce;
    this.calculatedDeadline = calculatedDeadline;
    this.poolMining = poolMining;
  }

  public long getBlockNumber()
  {
    return blockNumber;
  }

  public long getNonce()
  {
    return nonce;
  }

  public long getChunkPartStartNonce()
  {
    return chunkPartStartNonce;
  }

  public long getCalculatedDeadline()
  {
    return calculatedDeadline;
  }

  public boolean isPoolMining()
  {
    return poolMining;
  }
}
