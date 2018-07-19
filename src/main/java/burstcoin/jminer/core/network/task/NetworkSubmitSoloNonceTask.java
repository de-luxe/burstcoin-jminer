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

import burstcoin.jminer.core.CoreProperties;
import burstcoin.jminer.core.network.event.NetworkResultConfirmedEvent;
import burstcoin.jminer.core.network.event.NetworkResultErrorEvent;
import burstcoin.jminer.core.network.model.SubmitResultResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The type Network submit solo nonce task.
 */
@Component
@Scope("prototype")
public class NetworkSubmitSoloNonceTask
  implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(NetworkSubmitSoloNonceTask.class);

  private final ApplicationEventPublisher publisher;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  private BigInteger nonce;
  private long blockNumber;
  private BigInteger chunkPartStartNonce;
  private long calculatedDeadline;
  private BigInteger result;
  private byte[] generationSignature;

  @Autowired
  public NetworkSubmitSoloNonceTask(ApplicationEventPublisher publisher, HttpClient httpClient, ObjectMapper objectMapper)
  {
    this.publisher = publisher;
    this.httpClient = httpClient;
    this.objectMapper = objectMapper;
  }

  public void init(long blockNumber, byte[] generationSignature, BigInteger nonce, BigInteger chunkPartStartNonce, long calculatedDeadline, BigInteger result)
  {
    this.generationSignature = generationSignature;
    this.nonce = nonce;
    this.blockNumber = blockNumber;
    this.chunkPartStartNonce = chunkPartStartNonce;
    this.calculatedDeadline = calculatedDeadline;
    this.result = result;
  }

  @Override
  public void run()
  {
    try
    {
      ContentResponse response = httpClient.POST(CoreProperties.getSoloServer() + "/burst")
        .param("requestType", "submitNonce")
        .param("secretPhrase", CoreProperties.getPassPhrase())
        .param("nonce", nonce.toString())
        .timeout(CoreProperties.getConnectionTimeout(), TimeUnit.MILLISECONDS)
        .send();

      SubmitResultResponse result = objectMapper.readValue(response.getContentAsString(), SubmitResultResponse.class);

      if(result.getResult().equals("success"))
      {
        if(calculatedDeadline == result.getDeadline())
        {
          publisher
            .publishEvent(new NetworkResultConfirmedEvent(blockNumber, generationSignature, result.getDeadline(), nonce, chunkPartStartNonce, this.result));
        }
        else
        {
          publisher.publishEvent(
            new NetworkResultErrorEvent(blockNumber, generationSignature, nonce, calculatedDeadline, result.getDeadline(), chunkPartStartNonce, this.result));
        }
      }
      else
      {
        LOG.warn("Error: Submit solo nonce not successful: " + response.getContentAsString());
      }
    }
    catch(TimeoutException timeoutException)
    {
      LOG.warn("Unable to commit solo nonce, caused by connectionTimeout, currently '"
               + (CoreProperties.getConnectionTimeout() / 1000) + " sec.' try increasing it!");
    }
    catch(Exception e)
    {
      LOG.warn("Error: Failed to submit solo nonce: " + e.getMessage());
    }
  }
}
