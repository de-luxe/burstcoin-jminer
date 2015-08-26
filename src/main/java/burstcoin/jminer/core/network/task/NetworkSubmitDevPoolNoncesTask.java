package burstcoin.jminer.core.network.task;

import burstcoin.jminer.core.network.event.NetworkDevResultConfirmedEvent;
import burstcoin.jminer.core.network.model.DevPoolResult;
import nxt.util.Convert;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")
public class NetworkSubmitDevPoolNoncesTask
  implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(NetworkSubmitPoolNonceTask.class);

  @Autowired
  private ApplicationEventPublisher publisher;

  @Autowired
  private HttpClient httpClient;

  private long connectionTimeout;

  private String poolServer;
  private String numericAccountId;

  private long blockNumber;
  private List<DevPoolResult> devPoolResults;

  public void init(long blockNumber, String numericAccountId, String poolServer, long connectionTimeout, List<DevPoolResult> devPoolResults)
  {
    this.connectionTimeout = connectionTimeout;
    this.poolServer = poolServer;
    this.numericAccountId = numericAccountId;
    this.blockNumber = blockNumber;
    this.devPoolResults = devPoolResults;
  }

  @Override
  public void run()
  {
    try
    {
      String data = "";
      for(DevPoolResult devPoolResult : devPoolResults)
      {
        data += numericAccountId + ":" + Convert.toUnsignedLong(devPoolResult.getNonce()) + ":" + blockNumber + "\n";
      }

      ContentResponse response = httpClient.POST(poolServer + "/pool/submitWork")
        .content(new StringContentProvider(data))
        .timeout(connectionTimeout, TimeUnit.MILLISECONDS)
        .send();

      String submitResult = response.getContentAsString();
      if(submitResult != null && submitResult.contains("Received"))
      {
        // success
        publisher.publishEvent(new NetworkDevResultConfirmedEvent(blockNumber, submitResult, devPoolResults));
      }
      else
      {
        LOG.error("Error: could not commit nonces to devPool: "+ submitResult);
      }
    }
    catch(Exception e)
    {
      LOG.error("Error: Failed to submit nonce to devPool: " + e.getMessage());
    }
  }
}
