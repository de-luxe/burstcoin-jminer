/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 by luxe - https://github.com/de-luxe -  BURST-LUXE-RED2-G6JW-H4HG5
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

import java.math.BigInteger;

/**
 * fired if chunk-part checked
 */
public class CheckerResultEvent
{
  private long chunkPartStartNonce;

  private long blockNumber;
  private long nonce;
  private BigInteger result;

  /**
   * Instantiates a new Checker result event.
   *
   * @param blockNumber the block number
   * @param chunkPartStartNonce the chunk part start nonce
   * @param nonce the nonce
   * @param result the result
   */
  public CheckerResultEvent(long blockNumber, long chunkPartStartNonce, long nonce, BigInteger result)
  {
    this.chunkPartStartNonce = chunkPartStartNonce;
    this.blockNumber = blockNumber;

    this.nonce = nonce;
    this.result = result;
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
   * Gets nonce.
   *
   * @return the nonce
   */
  public long getNonce()
  {
    return nonce;
  }

  /**
   * Gets result.
   *
   * @return the result
   */
  public BigInteger getResult()
  {
    return result;
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
}
