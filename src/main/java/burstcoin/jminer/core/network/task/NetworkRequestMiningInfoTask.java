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

import burstcoin.jminer.core.network.event.NetworkStateChangeEvent;
import burstcoin.jminer.core.network.model.MiningInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import nxt.util.Convert;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.EOFException;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The type Network request mining info task.
 */
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

  private byte[] generationSignature;
  private long blockNumber;
  private String server;

  private boolean poolMining;
  private long connectionTimeout;
  private long defaultTargetDeadline;

  public void init(String server, long blockNumber, byte[] generationSignature, boolean poolMining, long connectionTimeout,
                   long defaultTargetDeadline)
  {
    this.server = server;
    this.generationSignature = generationSignature;
    this.blockNumber = blockNumber;
    this.poolMining = poolMining;
    this.connectionTimeout = connectionTimeout;

    this.defaultTargetDeadline = defaultTargetDeadline;
  }

  @Override
  public void run()
  {
    LOG.trace("start check network state");

    MiningInfoResponse result;
    try
    {
      ContentResponse response = httpClient.newRequest(server + "/burst?requestType=getMiningInfo")
        .timeout(connectionTimeout, TimeUnit.MILLISECONDS)
        .send();

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
        else
        {
          LOG.trace("not publish NetworkStateChangeEvent ... '" + blockNumber + " <= " + this.blockNumber + "'");
        }
      }
      else
      {
        LOG.warn("Unable to parse mining info: " + response.getContentAsString());
      }
    }
    catch(TimeoutException timeoutException)
    {
      LOG.warn("Unable to get mining info from wallet, caused by connectionTimeout, currently '" + (connectionTimeout / 1000) + " sec.' try increasing it!");
    }
    catch(Exception e)
    {
      if(e instanceof ConnectException)
      {
        LOG.warn("Unable to get mining info from wallet due ConnectException.");
        LOG.debug("Unable to get mining info from wallet due ConnectException:" + e.getMessage(), e);
      }
      else if(e instanceof EOFException)
      {
        LOG.warn("Unable to get mining info from wallet due EOFException.");
        LOG.debug("Unable to get mining info from wallet due EOFException:" + e.getMessage(), e);
      }
      else
      {
        LOG.warn("Unable to get mining info from wallet.");
        LOG.debug("Unable to get mining info from wallet: " + e.getMessage(), e);
      }
    }
  }
}
