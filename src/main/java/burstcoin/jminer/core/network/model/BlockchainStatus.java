package burstcoin.jminer.core.network.model;

import java.io.Serializable;

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

  public BlockchainStatus()
  {
  }

  public String getLastBlock()
  {
    return lastBlock;
  }

  public void setLastBlock(String lastBlock)
  {
    this.lastBlock = lastBlock;
  }

  public String getApplication()
  {
    return application;
  }

  public void setApplication(String application)
  {
    this.application = application;
  }

  public Boolean getIsScanning()
  {
    return isScanning;
  }

  public void setIsScanning(Boolean isScanning)
  {
    this.isScanning = isScanning;
  }

  public String getCumulativeDifficulty()
  {
    return cumulativeDifficulty;
  }

  public void setCumulativeDifficulty(String cumulativeDifficulty)
  {
    this.cumulativeDifficulty = cumulativeDifficulty;
  }

  public int getLastBlockchainFeederHeight()
  {
    return lastBlockchainFeederHeight;
  }

  public void setLastBlockchainFeederHeight(int lastBlockchainFeederHeight)
  {
    this.lastBlockchainFeederHeight = lastBlockchainFeederHeight;
  }

  public int getNumberOfBlocks()
  {
    return numberOfBlocks;
  }

  public void setNumberOfBlocks(int numberOfBlocks)
  {
    this.numberOfBlocks = numberOfBlocks;
  }

  public long getTime()
  {
    return time;
  }

  public void setTime(long time)
  {
    this.time = time;
  }

  public int getRequestProcessingTime()
  {
    return requestProcessingTime;
  }

  public void setRequestProcessingTime(int requestProcessingTime)
  {
    this.requestProcessingTime = requestProcessingTime;
  }

  public String getVersion()
  {
    return version;
  }

  public void setVersion(String version)
  {
    this.version = version;
  }

  public String getLastBlockchainFeeder()
  {
    return lastBlockchainFeeder;
  }

  public void setLastBlockchainFeeder(String lastBlockchainFeeder)
  {
    this.lastBlockchainFeeder = lastBlockchainFeeder;
  }

}
