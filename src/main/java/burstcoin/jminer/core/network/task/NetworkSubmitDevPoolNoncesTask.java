/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 by luxe - https://github.com/de-luxe - BURST-LUXE-ZDVD-CX3E-3SM58
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

/**
 * The type Network submit dev pool nonces task.
 */
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

  /**
   * Init void.
   *
   * @param blockNumber the block number
   * @param numericAccountId the numeric account id
   * @param poolServer the pool server
   * @param connectionTimeout the connection timeout
   * @param devPoolResults the dev pool results
   */
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
        LOG.error("Error: could not commit nonces to devPool: " + submitResult);
      }
    }
    catch(Exception e)
    {
      LOG.error("Error: Failed to submit nonce to devPool: " + e.getMessage());
    }
  }
}
