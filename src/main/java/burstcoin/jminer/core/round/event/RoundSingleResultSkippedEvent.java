package burstcoin.jminer.core.round.event;

import burstcoin.jminer.core.round.Round;
import org.springframework.context.ApplicationEvent;

/**
 * fired if deadline is skipped cause of targetDeadline
 */
public class RoundSingleResultSkippedEvent
  extends ApplicationEvent
{
  private long blockNumber;
  private long nonce;
  private long chunkPartStartNonce;

  private long calculatedDeadline;
  private long targetDeadline;
  private boolean poolMining;

  public RoundSingleResultSkippedEvent(Round source, long blockNumber, long nonce, long chunkPartStartNonce, long calculatedDeadline, long targetDeadline,
                                       boolean poolMining)
  {
    super(source);

    this.nonce = nonce;
    this.blockNumber = blockNumber;
    this.chunkPartStartNonce = chunkPartStartNonce;

    this.calculatedDeadline = calculatedDeadline;
    this.targetDeadline = targetDeadline;
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

  public long getTargetDeadline()
  {
    return targetDeadline;
  }

  public boolean isPoolMining()
  {
    return poolMining;
  }
}
