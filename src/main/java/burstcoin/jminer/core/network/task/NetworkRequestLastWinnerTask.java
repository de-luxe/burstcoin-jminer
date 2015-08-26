package burstcoin.jminer.core.network.task;

import burstcoin.jminer.core.network.event.NetworkLastWinnerEvent;
import burstcoin.jminer.core.network.model.Block;
import burstcoin.jminer.core.network.model.BlockchainStatus;
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
public class NetworkRequestLastWinnerTask
  implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(NetworkRequestLastWinnerTask.class);

  @Autowired
  private HttpClient httpClient;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ApplicationEventPublisher publisher;

  // data
  private long blockNumber; // updated on new round
  private String server;
  private long connectionTimeout;
  private int winnerRetriesOnAsync;
  private long winnerRetryIntervalInMs;

  public void init(String server, long blockNumber, long connectionTimeout, int winnerRetriesOnAsync, long winnerRetryIntervalInMs)
  {
    this.server = server;
    this.blockNumber = blockNumber;
    this.connectionTimeout = connectionTimeout;

    this.winnerRetriesOnAsync = winnerRetriesOnAsync;
    this.winnerRetryIntervalInMs = winnerRetryIntervalInMs;
  }

  @Override
  public void run()
  {
    Block lastBlock = getLastBlock();
    if(lastBlock != null)
    {
      publisher.publishEvent(new NetworkLastWinnerEvent(this, lastBlock.getHeight(), lastBlock.getGeneratorRS()));
    }
    else
    {
      LOG.info("      last winner 'N/A', walletServer out of sync.");
//      publisher.publishEvent(new NetworkLastWinnerEvent(this, blockNumber-1, "N/A"));
    }
  }

  private Block getLastBlock()
  {
    Block lastBlock = null;
    BlockchainStatus blockChainStatus = getBlockChainStatus();
    int retries = 0;
    if(blockChainStatus != null)
    {
      lastBlock = getBlock(blockChainStatus.getLastBlock());
      if(lastBlock != null)
      {
        while(blockNumber - 1 /*from pool*/ != lastBlock.getHeight()  /* from walletServer*/ && retries < winnerRetriesOnAsync)
        {
          retries++;

          if(retries == winnerRetriesOnAsync)
          {
            LOG.info("lastBlock from walletServer outdated, last retry in " + winnerRetryIntervalInMs + "ms");
          }
          else
          {
            LOG.info("lastBlock from walletServer outdated, retry-" + retries + " in " + winnerRetryIntervalInMs + "ms");
          }

          try
          {
            Thread.sleep(winnerRetryIntervalInMs);
          }
          catch(InterruptedException e)
          {
            e.printStackTrace();
          }
          blockChainStatus = getBlockChainStatus();
          lastBlock = getBlock(blockChainStatus.getLastBlock());
        }
      }
    }

    return retries > 0 && winnerRetriesOnAsync == retries ? null : lastBlock;
  }

  private BlockchainStatus getBlockChainStatus()
  {
    BlockchainStatus blockchainStatus = null;
    try
    {
      ContentResponse response = httpClient.newRequest(server + "/burst?requestType=getBlockchainStatus")
        .timeout(connectionTimeout, TimeUnit.MILLISECONDS)
        .send();

      blockchainStatus = objectMapper.readValue(response.getContentAsString(), BlockchainStatus.class);

    }
    catch(Exception e)
    {
      LOG.warn("Error: Failed to 'getBlockchainStatus'");
    }
    return blockchainStatus;
  }

  private Block getBlock(String blockId)
  {
    Block block = null;
    try
    {
      ContentResponse response = httpClient.newRequest(server + "/burst?requestType=getBlock&block=" + blockId)
        .timeout(connectionTimeout, TimeUnit.MILLISECONDS)
        .send();

      String contentAsString = response.getContentAsString();

      block = objectMapper.readValue(contentAsString, Block.class);
    }
    catch(Exception e)
    {
      LOG.warn("Error: Failed to 'getBlock'");
    }
    return block;
  }
}
