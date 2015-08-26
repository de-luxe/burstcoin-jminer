package burstcoin.jminer.core.network.task;

import burstcoin.jminer.core.network.event.NetworkResultConfirmedEvent;
import burstcoin.jminer.core.network.event.NetworkResultErrorEvent;
import burstcoin.jminer.core.network.model.SubmitResultResponse;
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
public class NetworkSubmitSoloNonceTask
  implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(NetworkSubmitSoloNonceTask.class);

  @Autowired
  private ApplicationEventPublisher publisher;

  @Autowired
  private HttpClient httpClient;

  @Autowired
  private ObjectMapper objectMapper;

  private String soloServer;
  private String passPhrase;
  private long nonce;

  private long blockNumber;
  private long chunkPartStartNonce;
  private long calculatedDeadline;
  private long connectionTimeout;

  public void init(long blockNumber, String passPhrase, String soloServer, long connectionTimeout, long nonce, long chunkPartStartNonce, long calculatedDeadline)
  {
    this.connectionTimeout = connectionTimeout;

    this.soloServer = soloServer;
    this.passPhrase = passPhrase;
    this.nonce = nonce;

    this.blockNumber = blockNumber;
    this.chunkPartStartNonce = chunkPartStartNonce;
    this.calculatedDeadline = calculatedDeadline;
  }

  @Override
  public void run()
  {
    try
    {
      ContentResponse response = httpClient.POST(soloServer + "/burst")
        .param("requestType", "submitNonce")
        .param("secretPhrase", passPhrase)
        .param("nonce", Convert.toUnsignedLong(nonce))
        .timeout(connectionTimeout, TimeUnit.MILLISECONDS)
        .send();

      SubmitResultResponse result = objectMapper.readValue(response.getContentAsString(), SubmitResultResponse.class);

      if(result.getResult().equals("success"))
      {
        if(calculatedDeadline == result.getDeadline())
        {
          publisher.publishEvent(new NetworkResultConfirmedEvent(blockNumber, result.getDeadline(), nonce, chunkPartStartNonce));
        }
        else
        {
          publisher.publishEvent(new NetworkResultErrorEvent(blockNumber, nonce, calculatedDeadline, result.getDeadline(), chunkPartStartNonce));
        }
      }
      else
      {
        LOG.warn("Error: Submit solo nonce not successful: " + response.getContentAsString());
      }
    }
    catch(Exception e)
    {
      LOG.warn("Error: Failed to submit solo nonce: " + e.getLocalizedMessage());
    }
  }
}
