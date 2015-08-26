package burstcoin.jminer.core.checker.event;

import burstcoin.jminer.core.network.model.DevPoolResult;

import java.util.List;

/**
 * fired on devPoolOpt instead of CheckerResultEvent
 */
public class CheckerDevResultEvent
{
  private long chunkPartStartNonce;
  private long blockNumber;

  private List<DevPoolResult> devPoolResults;

  public CheckerDevResultEvent(long blockNumber, long chunkPartStartNonce, List<DevPoolResult> devPoolResults)
  {
    this.chunkPartStartNonce = chunkPartStartNonce;

    this.blockNumber = blockNumber;
    this.devPoolResults = devPoolResults;
  }

  public long getBlockNumber()
  {
    return blockNumber;
  }

  public long getChunkPartStartNonce()
  {
    return chunkPartStartNonce;
  }

  public List<DevPoolResult> getDevPoolResults()
  {
    return devPoolResults;
  }

  public boolean hasResults()
  {
    return devPoolResults != null && !devPoolResults.isEmpty();
  }
}
