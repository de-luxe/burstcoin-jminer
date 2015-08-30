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

package burstcoin.jminer.core.round.event;

import org.springframework.context.ApplicationEvent;

/**
 * The type Round finished event.
 */
public class RoundFinishedEvent
  extends ApplicationEvent
{
  private long blockNumber;
  private long bestCommittedDeadline;
  private long roundTime;

  /**
   * Instantiates a new Round finished event.
   *
   * @param blockNumber the block number
   * @param bestCommittedDeadline the best committed deadline
   * @param roundTime the round time
   */
  public RoundFinishedEvent(long blockNumber, long bestCommittedDeadline, long roundTime)
  {
    super(blockNumber);
    this.blockNumber = blockNumber;
    this.bestCommittedDeadline = bestCommittedDeadline;
    this.roundTime = roundTime;
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
   * Gets best committed deadline.
   *
   * @return the best committed deadline
   */
  public long getBestCommittedDeadline()
  {
    return bestCommittedDeadline;
  }

  /**
   * Gets round time.
   *
   * @return the round time
   */
  public long getRoundTime()
  {
    return roundTime;
  }
}
