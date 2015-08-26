package burstcoin.jminer.core.checker.event;

import java.math.BigInteger;

/**
 * fired if chunk-part checked
 */
public class CheckerResultEvent
{
  private long chunkPartStartNonce;

  private long blockNumber;
  private long nonce;
  private BigInteger result;

  public CheckerResultEvent(long blockNumber, long chunkPartStartNonce, long nonce, BigInteger result)
  {
    this.chunkPartStartNonce = chunkPartStartNonce;
    this.blockNumber = blockNumber;

    this.nonce = nonce;
    this.result = result;
  }

  public long getBlockNumber()
  {
    return blockNumber;
  }

  public long getNonce()
  {
    return nonce;
  }

  public BigInteger getResult()
  {
    return result;
  }

  public long getChunkPartStartNonce()
  {
    return chunkPartStartNonce;
  }
}
