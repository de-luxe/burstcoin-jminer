package burstcoin.jminer.core.network.task;

import burstcoin.jminer.core.network.event.NetworkStateChangeEvent;
import burstcoin.jminer.core.network.model.MiningInfoResponse;
import nxt.util.Convert;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")
public class NetworkRequestMiningInfoTask
  implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(NetworkRequestMiningInfoTask.class);

  @Autowired
  private HttpClient httpClient;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ApplicationEventPublisher publisher;

  private long blockNumber;
  private String server;

  private boolean poolMining;
  private long connectionTimeout;
  private long defaultTargetDeadline;
  private boolean devV2Pool;

  public void init(String server, long blockNumber, boolean poolMining, long connectionTimeout, long defaultTargetDeadline, boolean devV2Pool)
  {
    this.server = server;
    this.blockNumber = blockNumber;
    this.poolMining = poolMining;
    this.connectionTimeout = connectionTimeout;

    this.defaultTargetDeadline = defaultTargetDeadline;
    this.devV2Pool = devV2Pool;
  }

  @Override
  public void run()
  {
    MiningInfoResponse result;
    try
    {
      ContentResponse response;
      if(devV2Pool)
      {
        response = httpClient.newRequest(server + "/pool/getMiningInfo")
          .timeout(connectionTimeout, TimeUnit.MILLISECONDS)
          .send();
      }
      else
      {
       response = httpClient.newRequest(server + "/burst?requestType=getMiningInfo")
          .timeout(connectionTimeout, TimeUnit.MILLISECONDS)
          .send();
      }

      result = objectMapper.readValue(response.getContentAsString(), MiningInfoResponse.class);

      if(result != null)
      {
        long blockNumber = Convert.parseUnsignedLong(result.getHeight());

        if(blockNumber > this.blockNumber)
        {
          byte[] generationSignature = Convert.parseHexString(result.getGenerationSignature());
          long baseTarget = Convert.parseUnsignedLong(result.getBaseTarget());

          // ensure default is not 0
          defaultTargetDeadline = defaultTargetDeadline > 0 ? defaultTargetDeadline : Long.MAX_VALUE;
          long targetDeadline = poolMining ? result.getTargetDeadline() > 0 ? result.getTargetDeadline() : defaultTargetDeadline : defaultTargetDeadline;

          publisher.publishEvent(new NetworkStateChangeEvent(blockNumber, baseTarget, generationSignature, targetDeadline));
        }
      }
      else
      {
        LOG.warn("Unable to parse mining info: " + response.getContentAsString());
      }
    }
    catch(Exception e)
    {
      LOG.warn("Unable to get mining info from wallet: " + e.getLocalizedMessage());
    }
  }
}
