package burstcoin.jminer.core.reader.event;


public class ReaderLoadedPartEvent
{
  public interface Handler
  {
    void handleMessage(ReaderLoadedPartEvent event);
  }

  private long chunkPartStartNonce;
  private long blockNumber;

  private byte[] scoops;

  public ReaderLoadedPartEvent(long blockNumber, byte[] scoops, long chunkPartStartNonce)
  {
    this.chunkPartStartNonce = chunkPartStartNonce;
    this.blockNumber = blockNumber;
    this.scoops = scoops;
  }

  public long getBlockNumber()
  {
    return blockNumber;
  }

  public byte[] getScoops()
  {
    return scoops;
  }

  public long getChunkPartStartNonce()
  {
    return chunkPartStartNonce;
  }


}
