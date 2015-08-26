package burstcoin.jminer.core.network.model;


public class DevPoolResult
{
  private long blockNumber;
  private long calculatedDeadline;
  private long nonce;
  private long chunkPartStartNonce;

  public DevPoolResult(long blockNumber, long calculatedDeadline, long nonce, long chunkPartStartNonce)
  {
    this.blockNumber = blockNumber;
    this.calculatedDeadline = calculatedDeadline;
    this.nonce = nonce;
    this.chunkPartStartNonce = chunkPartStartNonce;
  }

  public long getBlockNumber()
  {
    return blockNumber;
  }

  public long getCalculatedDeadline()
  {
    return calculatedDeadline;
  }

  public long getNonce()
  {
    return nonce;
  }

  public long getChunkPartStartNonce()
  {
    return chunkPartStartNonce;
  }
}
