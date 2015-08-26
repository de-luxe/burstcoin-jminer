package burstcoin.jminer.core.network.event;

import burstcoin.jminer.core.network.model.DevPoolResult;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class NetworkDevResultConfirmedEvent
  extends ApplicationEvent
{
  private long blockNumber;

  private String response;
  private List<DevPoolResult> devPoolResults;

  public NetworkDevResultConfirmedEvent(long blockNumber, String response, List<DevPoolResult> devPoolResults)
  {
    super(blockNumber);
    this.blockNumber = blockNumber;
    this.response = response;

    this.devPoolResults = devPoolResults;
  }

  public long getBlockNumber()
  {
    return blockNumber;
  }

  public String getResponse()
  {
    return response;
  }

  public List<DevPoolResult> getDevPoolResults()
  {
    return devPoolResults;
  }
}

