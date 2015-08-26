package burstcoin.jminer.core.network.event;


import org.springframework.context.ApplicationEvent;

/**
 * fired if calculatedDeadline was confirmed by server
 */
public class NetworkResultConfirmedEvent
  extends ApplicationEvent
{
  private long blockNumber;
  private long deadline;
  private long nonce;

  private long chunkPartStartNonce;

  public NetworkResultConfirmedEvent(long blockNumber, long deadline, long nonce, long chunkPartStartNonce)
  {
    super(blockNumber);

    this.blockNumber = blockNumber;
    this.deadline = deadline;
    this.nonce = nonce;

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

  public long getDeadline()
  {
    return deadline;
  }

  public long getNonce()
  {
    return nonce;
  }
}
