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

package burstcoin.jminer.core.checker.event;

import burstcoin.jminer.core.network.model.DevPoolResult;

import java.util.List;

/**
 * fired on devPoolOpt instead of CheckerResultEvent
 */
public class CheckerDevResultEvent
{
  private long chunkPartStartNonce;
  private long blockNumber;

  private List<DevPoolResult> devPoolResults;

  /**
   * Instantiates a new Checker dev result event.
   *
   * @param blockNumber the block number
   * @param chunkPartStartNonce the chunk part start nonce
   * @param devPoolResults the dev pool results
   */
  public CheckerDevResultEvent(long blockNumber, long chunkPartStartNonce, List<DevPoolResult> devPoolResults)
  {
    this.chunkPartStartNonce = chunkPartStartNonce;

    this.blockNumber = blockNumber;
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
   * Gets chunk part start nonce.
   *
   * @return the chunk part start nonce
   */
  public long getChunkPartStartNonce()
  {
    return chunkPartStartNonce;
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

  /**
   * Has results.
   *
   * @return the boolean
   */
  public boolean hasResults()
  {
    return devPoolResults != null && !devPoolResults.isEmpty();
  }
}
