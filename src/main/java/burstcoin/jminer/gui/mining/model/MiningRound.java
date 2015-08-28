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

package burstcoin.jminer.gui.mining.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;


/**
 * The type Mining round.
 */
public class MiningRound
{
  // id
  private SimpleLongProperty blockNumber;
  private SimpleIntegerProperty scoopNumber;

  private SimpleStringProperty deadline;
  private SimpleStringProperty elapsedTime;
  private SimpleStringProperty percentDone;

  private SimpleStringProperty effectiveReadSpeed;
  private SimpleStringProperty winner;

  /**
   * Instantiates a new Mining round.
   */
  public MiningRound()
  {
  }

  /**
   * Gets block number.
   *
   * @return the block number
   */
  public long getBlockNumber()
  {
    return blockNumber.get();
  }

  /**
   * Block number property.
   *
   * @return the simple long property
   */
  public SimpleLongProperty blockNumberProperty()
  {
    return blockNumber;
  }

  /**
   * Sets block number.
   *
   * @param blockNumber the block number
   */
  public void setBlockNumber(long blockNumber)
  {
    this.blockNumber.set(blockNumber);
  }

  /**
   * Gets scoop number.
   *
   * @return the scoop number
   */
  public int getScoopNumber()
  {
    return scoopNumber.get();
  }

  /**
   * Scoop number property.
   *
   * @return the simple integer property
   */
  public SimpleIntegerProperty scoopNumberProperty()
  {
    return scoopNumber;
  }

  /**
   * Sets scoop number.
   *
   * @param scoopNumber the scoop number
   */
  public void setScoopNumber(int scoopNumber)
  {
    this.scoopNumber.set(scoopNumber);
  }

  /**
   * Gets deadline.
   *
   * @return the deadline
   */
  public String getDeadline()
  {
    return deadline.get();
  }

  /**
   * Deadline property.
   *
   * @return the simple string property
   */
  public SimpleStringProperty deadlineProperty()
  {
    return deadline;
  }

  /**
   * Sets deadline.
   *
   * @param deadline the deadline
   */
  public void setDeadline(String deadline)
  {
    this.deadline.set(deadline);
  }

  /**
   * Gets elapsed time.
   *
   * @return the elapsed time
   */
  public String getElapsedTime()
  {
    return elapsedTime.get();
  }

  /**
   * Elapsed time property.
   *
   * @return the simple string property
   */
  public SimpleStringProperty elapsedTimeProperty()
  {
    return elapsedTime;
  }

  /**
   * Sets elapsed time.
   *
   * @param elapsedTime the elapsed time
   */
  public void setElapsedTime(String elapsedTime)
  {
    this.elapsedTime.set(elapsedTime);
  }

  /**
   * Gets percent done.
   *
   * @return the percent done
   */
  public String getPercentDone()
  {
    return percentDone.get();
  }

  /**
   * Percent done property.
   *
   * @return the simple string property
   */
  public SimpleStringProperty percentDoneProperty()
  {
    return percentDone;
  }

  /**
   * Sets percent done.
   *
   * @param percentDone the percent done
   */
  public void setPercentDone(String percentDone)
  {
    this.percentDone.set(percentDone);
  }

  /**
   * Gets effective read speed.
   *
   * @return the effective read speed
   */
  public String getEffectiveReadSpeed()
  {
    return effectiveReadSpeed.get();
  }

  /**
   * Effective read speed property.
   *
   * @return the simple string property
   */
  public SimpleStringProperty effectiveReadSpeedProperty()
  {
    return effectiveReadSpeed;
  }

  /**
   * Sets effective read speed.
   *
   * @param effectiveReadSpeed the effective read speed
   */
  public void setEffectiveReadSpeed(String effectiveReadSpeed)
  {
    this.effectiveReadSpeed.set(effectiveReadSpeed);
  }

  /**
   * Gets winner.
   *
   * @return the winner
   */
  public String getWinner()
  {
    return winner.get();
  }

  /**
   * Winner property.
   *
   * @return the simple string property
   */
  public SimpleStringProperty winnerProperty()
  {
    return winner;
  }

  /**
   * Sets winner.
   *
   * @param winner the winner
   */
  public void setWinner(String winner)
  {
    this.winner.set(winner);
  }
}
