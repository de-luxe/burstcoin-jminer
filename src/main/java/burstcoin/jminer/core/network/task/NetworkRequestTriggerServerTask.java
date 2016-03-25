/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 by luxe - https://github.com/de-luxe - BURST-LUXE-RED2-G6JW-H4HG5
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package burstcoin.jminer.core.network.task;

import burstcoin.jminer.core.network.model.BlockchainStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * task for solo mining, to ensure that even if wallet ui not open in browser, server does not stuck (helped in my case ... does not hurt).
 */
@Component
@Scope("prototype")
public class NetworkRequestTriggerServerTask
  implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(NetworkRequestTriggerServerTask.class);

  @Autowired
  private HttpClient httpClient;

  @Autowired
  private ObjectMapper objectMapper;

  // data
  private String numericAccountId;
  private String server;
  private long connectionTimeout;

  public void init(String server, String numericAccountId,  long connectionTimeout)
  {
    this.server = server;
    this.numericAccountId = numericAccountId;
    this.connectionTimeout = connectionTimeout;
  }

  @Override
  public void run()
  {
    // imitate requests done by wallet
    getBlockChainStatus();
    getUnconfirmedTransactions(numericAccountId);
    LOG.trace("wallet server triggered!");
  }

  private BlockchainStatus getBlockChainStatus()
  {
    BlockchainStatus blockchainStatus = null;
    try
    {
      ContentResponse response = httpClient.newRequest(server + "/burst?requestType=getBlockchainStatus&random=" + new Random().nextFloat())
        .timeout(connectionTimeout, TimeUnit.MILLISECONDS)
        .send();

      blockchainStatus = objectMapper.readValue(response.getContentAsString(), BlockchainStatus.class);
    }
    catch(Exception e)
    {
      LOG.warn("Error: Failed to 'getBlockchainStatus' from 'soloServer' to trigger server.");
    }
    return blockchainStatus;
  }

  private void getUnconfirmedTransactions(String numericAccountId)
  {
    try
    {
      ContentResponse response = httpClient
        .newRequest(server + "/burst?requestType=getUnconfirmedTransactions&accountId=" + numericAccountId + "&random=" + new Random().nextFloat())
        .timeout(connectionTimeout, TimeUnit.MILLISECONDS)
        .send();

      String contentAsString = response.getContentAsString();

      LOG.trace(contentAsString);
    }
    catch(Exception e)
    {
      LOG.warn("Error: Failed to 'getUnconfirmedTransactions' to trigger server.");
    }
  }
}
