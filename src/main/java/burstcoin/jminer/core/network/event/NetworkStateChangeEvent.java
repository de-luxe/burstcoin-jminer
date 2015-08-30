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

package burstcoin.jminer.core.network.event;

import org.springframework.context.ApplicationEvent;

/**
 * The type Network state change event.
 */
/* fired if a new block has started */
public class NetworkStateChangeEvent
  extends ApplicationEvent
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
    void handleMessage(NetworkStateChangeEvent event);
  }

  private long blockNumber;
  private byte[] generationSignature;
  private long baseTarget;

  // targetDeadline provided via property or server in miningInfo (depends on solo/pool mining)
  private long targetDeadline;

  /**
   * Instantiates a new Network state change event.
   *
   * @param blockNumber the block number
   * @param baseTarget the base target
   * @param generationSignature the generation signature
   * @param targetDeadline the target deadline
   */
  public NetworkStateChangeEvent(long blockNumber, long baseTarget, byte[] generationSignature, long targetDeadline)
  {
    super(blockNumber);

    this.blockNumber = blockNumber;
    this.baseTarget = baseTarget;
    this.generationSignature = generationSignature;
    this.targetDeadline = targetDeadline;
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
   * Get generation signature.
   *
   * @return the byte [ ]
   */
  public byte[] getGenerationSignature()
  {
    return generationSignature;
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
   * Gets base target.
   *
   * @return the base target
   */
  public long getBaseTarget()
  {
    return baseTarget;
  }
}
