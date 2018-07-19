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
import burstcoin.jminer.core.network.event.NetworkBlocksEvent;
import burstcoin.jminer.core.network.model.Blocks;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.InputStreamResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")
public class NetworkRequestAccountBlocksTask
  implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(NetworkRequestLastWinnerTask.class);

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final ApplicationEventPublisher publisher;

  // data
  private String accountId;
  private String server;

  @Autowired
  public NetworkRequestAccountBlocksTask(HttpClient httpClient, ObjectMapper objectMapper, ApplicationEventPublisher publisher)
  {
    this.httpClient = httpClient;
    this.objectMapper = objectMapper;
    this.publisher = publisher;
  }

  public void init(String accountId, String server)
  {
    this.accountId = accountId;
    this.server = server;
  }

  @Override
  public void run()
  {
    Blocks accountBlocks = getAccountBlocks(accountId);
    publisher.publishEvent(new NetworkBlocksEvent(accountBlocks));
  }

  private Blocks getAccountBlocks(String accountId)
  {
    LOG.info("Requesting mined blocks from wallet ...");
    Blocks blocks = null;
    try
    {
      long connectionTimeout = CoreProperties.getConnectionTimeout() * 3;
      InputStreamResponseListener listener = new InputStreamResponseListener();

      Request request = httpClient.newRequest(server + "/burst");
      request.param("requestType", "getAccountBlocks");
      request.param("account", accountId);
      request.timeout(connectionTimeout, TimeUnit.MILLISECONDS);
      request.send(listener);
      Response response = listener.get(connectionTimeout, TimeUnit.MILLISECONDS);

      // Look at the response
      if(response.getStatus() == 200)
      {
        // Use try-with-resources to close input stream.
        try (InputStream responseContent = listener.getInputStream())
        {
          blocks = objectMapper.readValue(responseContent, Blocks.class);

          LOG.info("Total mined blocks: '" + blocks.getBlocks().size() + "', received in '" + blocks.getRequestProcessingTime() + "' ms");
        }
        catch(Exception e)
        {
          LOG.error("Failed to receive account blocks.", e);
        }
      }
    }
    catch(Exception e)
    {
      LOG.warn("Error: Failed to 'getAccountBlocks': " + e.getMessage());
    }
    return blocks;
  }
}
