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

package burstcoin.jminer.core.network.event;

import burstcoin.jminer.core.network.model.DevPoolResult;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * The type Network dev result confirmed event.
 */
public class NetworkDevResultConfirmedEvent
  extends ApplicationEvent
{
  private long blockNumber;

  private String response;
  private List<DevPoolResult> devPoolResults;

  /**
   * Instantiates a new Network dev result confirmed event.
   *
   * @param blockNumber the block number
   * @param response the response
   * @param devPoolResults the dev pool results
   */
  public NetworkDevResultConfirmedEvent(long blockNumber, String response, List<DevPoolResult> devPoolResults)
  {
    super(blockNumber);
    this.blockNumber = blockNumber;
    this.response = response;

    this.devPoolResults = devPoolResults;
  }

  /**
   * Gets block number.
   *
   * @return the block number
   */
  public long getBlockNumber()
  {
    return blockNumber;
  }

  /**
   * Gets response.
   *
   * @return the response
   */
  public String getResponse()
  {
    return response;
  }

  /**
   * Gets dev pool results.
   *
   * @return the dev pool results
   */
  public List<DevPoolResult> getDevPoolResults()
  {
    return devPoolResults;
  }
}

