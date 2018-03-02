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


import org.springframework.context.ApplicationEvent;

/**
 * fired after mining started
 */
public class RoundStartedEvent
  extends ApplicationEvent
{
  private long scoopNumber;
  private long capacity;
  private long targetDeadline;
  private long baseTarget;
  private byte[] generationSignature;
  private boolean restart;
  private long blockNumber;

  /**
   * Instantiates a new Round started event.
   *  @param blockNumber the block number
   * @param scoopNumber the scoop number
   * @param capacity the capacity
   * @param targetDeadline the target deadline
   * @param baseTarget the base target
   * @param generationSignature
   */
  public RoundStartedEvent(boolean restart, long blockNumber, long scoopNumber, long capacity, long targetDeadline, long baseTarget, byte[] generationSignature)
  {
    super(blockNumber);

    this.restart = restart;
    this.blockNumber = blockNumber;
    this.scoopNumber = scoopNumber;
    this.capacity = capacity;
    this.targetDeadline = targetDeadline;
    this.baseTarget = baseTarget;
    this.generationSignature = generationSignature;
  }

  public byte[] getGenerationSignature()
  {
    return generationSignature;
  }

  public boolean isRestart()
  {
    return restart;
  }

  /**
   * Gets capacity.
   *
   * @return the capacity
   */
  public long getCapacity()
  {
    return capacity;
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
   * Gets scoop number.
   *
   * @return the scoop number
   */
  public long getScoopNumber()
  {
    return scoopNumber;
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
