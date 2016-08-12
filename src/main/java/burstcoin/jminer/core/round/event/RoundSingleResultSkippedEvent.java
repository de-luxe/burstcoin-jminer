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

package burstcoin.jminer.core.round.event;

import burstcoin.jminer.core.round.Round;
import org.springframework.context.ApplicationEvent;

import java.math.BigInteger;

/**
 * fired if deadline is skipped cause of targetDeadline
 */
public class RoundSingleResultSkippedEvent
  extends ApplicationEvent
{
  private long blockNumber;
  private BigInteger nonce;
  private BigInteger chunkPartStartNonce;

  private long calculatedDeadline;
  private long targetDeadline;
  private boolean poolMining;

  /**
   * Instantiates a new Round single result skipped event.
   *
   * @param source the source
   * @param blockNumber the block number
   * @param nonce the nonce
   * @param chunkPartStartNonce the chunk part start nonce
   * @param calculatedDeadline the calculated deadline
   * @param targetDeadline the target deadline
   * @param poolMining the pool mining
   */
  public RoundSingleResultSkippedEvent(Round source, long blockNumber, BigInteger nonce, BigInteger chunkPartStartNonce, long calculatedDeadline, long targetDeadline,
                                       boolean poolMining)
  {
    super(source);

    this.nonce = nonce;
    this.blockNumber = blockNumber;
    this.chunkPartStartNonce = chunkPartStartNonce;

    this.calculatedDeadline = calculatedDeadline;
    this.targetDeadline = targetDeadline;
    this.poolMining = poolMining;
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
  public BigInteger getNonce()
  {
    return nonce;
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

  /**
   * Gets calculated deadline.
   *
   * @return the calculated deadline
   */
  public long getCalculatedDeadline()
  {
    return calculatedDeadline;
  }

  /**
   * Gets target deadline.
   *
   * @return the target deadline
   */
  public long getTargetDeadline()
  {
    return targetDeadline;
  }

  /**
   * Is pool mining.
   *
   * @return the boolean
   */
  public boolean isPoolMining()
  {
    return poolMining;
  }
}
