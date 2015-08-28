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

package burstcoin.jminer.core.network.model;

import java.io.Serializable;

/**
 * The type Blockchain status.
 */
public class BlockchainStatus
  implements Serializable
{
  private String lastBlock;
  private String application;
  private Boolean isScanning;
  private String cumulativeDifficulty;
  private int lastBlockchainFeederHeight;
  private int numberOfBlocks;
  private long time;
  private int requestProcessingTime;
  private String version;
  private String lastBlockchainFeeder;

  /**
   * Instantiates a new Blockchain status.
   */
  public BlockchainStatus()
  {
  }

  /**
   * Gets last block.
   *
   * @return the last block
   */
  public String getLastBlock()
  {
    return lastBlock;
  }

  /**
   * Sets last block.
   *
   * @param lastBlock the last block
   */
  public void setLastBlock(String lastBlock)
  {
    this.lastBlock = lastBlock;
  }

  /**
   * Gets application.
   *
   * @return the application
   */
  public String getApplication()
  {
    return application;
  }

  /**
   * Sets application.
   *
   * @param application the application
   */
  public void setApplication(String application)
  {
    this.application = application;
  }

  /**
   * Gets is scanning.
   *
   * @return the is scanning
   */
  public Boolean getIsScanning()
  {
    return isScanning;
  }

  /**
   * Sets is scanning.
   *
   * @param isScanning the is scanning
   */
  public void setIsScanning(Boolean isScanning)
  {
    this.isScanning = isScanning;
  }

  /**
   * Gets cumulative difficulty.
   *
   * @return the cumulative difficulty
   */
  public String getCumulativeDifficulty()
  {
    return cumulativeDifficulty;
  }

  /**
   * Sets cumulative difficulty.
   *
   * @param cumulativeDifficulty the cumulative difficulty
   */
  public void setCumulativeDifficulty(String cumulativeDifficulty)
  {
    this.cumulativeDifficulty = cumulativeDifficulty;
  }

  /**
   * Gets last blockchain feeder height.
   *
   * @return the last blockchain feeder height
   */
  public int getLastBlockchainFeederHeight()
  {
    return lastBlockchainFeederHeight;
  }

  /**
   * Sets last blockchain feeder height.
   *
   * @param lastBlockchainFeederHeight the last blockchain feeder height
   */
  public void setLastBlockchainFeederHeight(int lastBlockchainFeederHeight)
  {
    this.lastBlockchainFeederHeight = lastBlockchainFeederHeight;
  }

  /**
   * Gets number of blocks.
   *
   * @return the number of blocks
   */
  public int getNumberOfBlocks()
  {
    return numberOfBlocks;
  }

  /**
   * Sets number of blocks.
   *
   * @param numberOfBlocks the number of blocks
   */
  public void setNumberOfBlocks(int numberOfBlocks)
  {
    this.numberOfBlocks = numberOfBlocks;
  }

  /**
   * Gets time.
   *
   * @return the time
   */
  public long getTime()
  {
    return time;
  }

  /**
   * Sets time.
   *
   * @param time the time
   */
  public void setTime(long time)
  {
    this.time = time;
  }

  /**
   * Gets request processing time.
   *
   * @return the request processing time
   */
  public int getRequestProcessingTime()
  {
    return requestProcessingTime;
  }

  /**
   * Sets request processing time.
   *
   * @param requestProcessingTime the request processing time
   */
  public void setRequestProcessingTime(int requestProcessingTime)
  {
    this.requestProcessingTime = requestProcessingTime;
  }

  /**
   * Gets version.
   *
   * @return the version
   */
  public String getVersion()
  {
    return version;
  }

  /**
   * Sets version.
   *
   * @param version the version
   */
  public void setVersion(String version)
  {
    this.version = version;
  }

  /**
   * Gets last blockchain feeder.
   *
   * @return the last blockchain feeder
   */
  public String getLastBlockchainFeeder()
  {
    return lastBlockchainFeeder;
  }

  /**
   * Sets last blockchain feeder.
   *
   * @param lastBlockchainFeeder the last blockchain feeder
   */
  public void setLastBlockchainFeeder(String lastBlockchainFeeder)
  {
    this.lastBlockchainFeeder = lastBlockchainFeeder;
  }

}
