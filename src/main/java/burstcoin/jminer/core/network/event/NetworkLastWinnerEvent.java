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

package burstcoin.jminer.core.network.event;

import org.springframework.context.ApplicationEvent;

/**
 * fired if last winner was found
 */
public class NetworkLastWinnerEvent
  extends ApplicationEvent
{
  private long lastBlockNumber;
  private String winner;

  /**
   * Instantiates a new Network last winner event.
   *
   * @param source the source
   * @param lastBlockNumber the last block number
   * @param winner the winner
   */
  public NetworkLastWinnerEvent(Object source, long lastBlockNumber, String winner)
  {
    super(source);
    this.lastBlockNumber = lastBlockNumber;
    this.winner = winner;
  }

  /**
   * Gets last block number.
   *
   * @return the last block number
   */
  public long getLastBlockNumber()
  {
    return lastBlockNumber;
  }

  /**
   * Gets winner.
   *
   * @return the winner
   */
  public String getWinner()
  {
    return winner;
  }
}
