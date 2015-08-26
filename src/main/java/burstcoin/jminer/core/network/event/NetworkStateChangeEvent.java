package burstcoin.jminer.core.network.event;

import org.springframework.context.ApplicationEvent;

/* fired if a new block has started */
public class NetworkStateChangeEvent
  extends ApplicationEvent
{
  public interface Handler
  {
    void handleMessage(NetworkStateChangeEvent event);
  }

  private long blockNumber;
  private byte[] generationSignature;
  private long baseTarget;

  // targetDeadline provided via property or server in miningInfo (depends on solo/pool mining)
  private long targetDeadline;

  public NetworkStateChangeEvent(long blockNumber, long baseTarget, byte[] generationSignature, long targetDeadline)
  {
    super(blockNumber);

    this.blockNumber = blockNumber;
    this.baseTarget = baseTarget;
    this.generationSignature = generationSignature;
    this.targetDeadline = targetDeadline;
  }

  public long getBlockNumber()
  {
    return blockNumber;
  }

  public byte[] getGenerationSignature()
  {
    return generationSignature;
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
