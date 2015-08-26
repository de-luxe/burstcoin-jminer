package burstcoin.jminer.core.network.model;

import java.io.Serializable;
import java.util.List;

public class Block
  implements Serializable
{
  private String previousBlockHash;
  private int payloadLength;
  private String totalAmountNQT;
  private String generationSignature;
  private String generator;
  private String generatorPublicKey;
  private String baseTarget;
  private String payloadHash;
  private String generatorRS;
  private String blockReward;
  private long requestProcessingTime;
  private int scoopNum;
  private long numberOfTransactions;
  private String blockSignature;
  private List<String> transactions;
  private String nonce;
  private String version;
  private String totalFeeNQT;
  private String nextBlock;
  private String previousBlock;
  private String block;
  private long height;
  private int timestamp;

  public Block()
  {
  }

  public String getPreviousBlockHash()
  {
    return previousBlockHash;
  }

  public int getPayloadLength()
  {
    return payloadLength;
  }

  public String getTotalAmountNQT()
  {
    return totalAmountNQT;
  }

  public String getGenerationSignature()
  {
    return generationSignature;
  }

  public String getGenerator()
  {
    return generator;
  }

  public String getGeneratorPublicKey()
  {
    return generatorPublicKey;
  }

  public String getBaseTarget()
  {
    return baseTarget;
  }

  public String getPayloadHash()
  {
    return payloadHash;
  }

  public String getGeneratorRS()
  {
    return generatorRS;
  }

  public String getBlockReward()
  {
    return blockReward;
  }

  public long getRequestProcessingTime()
  {
    return requestProcessingTime;
  }

  public int getScoopNum()
  {
    return scoopNum;
  }

  public long getNumberOfTransactions()
  {
    return numberOfTransactions;
  }

  public String getBlockSignature()
  {
    return blockSignature;
  }

  public List<String> getTransactions()
  {
    return transactions;
  }

  public String getNonce()
  {
    return nonce;
  }

  public String getVersion()
  {
    return version;
  }

  public String getTotalFeeNQT()
  {
    return totalFeeNQT;
  }

  public String getNextBlock()
  {
    return nextBlock;
  }

  public String getPreviousBlock()
  {
    return previousBlock;
  }

  public String getBlock()
  {
    return block;
  }

  public long getHeight()
  {
    return height;
  }

  public int getTimestamp()
  {
    return timestamp;
  }
}
