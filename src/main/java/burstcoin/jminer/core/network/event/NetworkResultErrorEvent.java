package burstcoin.jminer.core.network.event;

import org.springframework.context.ApplicationEvent;

/**
 * fired if server response deadline does not match calculated deadline.
 */
public class NetworkResultErrorEvent
  extends ApplicationEvent
{
  public interface Handler
  {
    void handleMessage(NetworkResultErrorEvent event);
  }

  private long blockNumber;
  private long nonce;

  private long calculatedDeadline;
  private long strangeDeadline;

  private long chunkPartStartNonce;

  public NetworkResultErrorEvent(long blockNumber, long nonce, long calculatedDeadline, long strangeDeadline, long chunkPartStartNonce)
  {
    super(blockNumber);

    this.blockNumber = blockNumber;
    this.nonce = nonce;
    this.calculatedDeadline = calculatedDeadline;
    this.strangeDeadline = strangeDeadline;

    this.chunkPartStartNonce = chunkPartStartNonce;

  }

  public long getChunkPartStartNonce()
  {
    return chunkPartStartNonce;
  }

  public long getBlockNumber()
  {
    return blockNumber;
  }

  public long getNonce()
  {
    return nonce;
  }

  public long getCalculatedDeadline()
  {
    return calculatedDeadline;
  }

  public long getStrangeDeadline()
  {
    return strangeDeadline;
  }
}
