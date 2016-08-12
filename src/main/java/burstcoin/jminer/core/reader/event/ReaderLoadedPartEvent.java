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

package burstcoin.jminer.core.reader.event;


import java.math.BigInteger;

/**
 * The type Reader loaded part event.
 */
public class ReaderLoadedPartEvent
{
  /**
   * The interface Handler.
   */
  public interface Handler
  {
    /**
     * Handle message.
     *
     * @param event the event
     */
    void handleMessage(ReaderLoadedPartEvent event);
  }

  private BigInteger chunkPartStartNonce;
  private long blockNumber;

  private byte[] scoops;

  /**
   * Instantiates a new Reader loaded part event.
   *
   * @param blockNumber the block number
   * @param scoops the scoops
   * @param chunkPartStartNonce the chunk part start nonce
   */
  public ReaderLoadedPartEvent(long blockNumber, byte[] scoops, BigInteger chunkPartStartNonce)
  {
    this.chunkPartStartNonce = chunkPartStartNonce;
    this.blockNumber = blockNumber;
    this.scoops = scoops;
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
   * Get scoops.
   *
   * @return the byte [ ]
   */
  public byte[] getScoops()
  {
    return scoops;
  }

  /**
   * Gets chunk part start nonce.
   *
   * @return the chunk part start nonce
   */
  public BigInteger getChunkPartStartNonce()
  {
    return chunkPartStartNonce;
  }
}
