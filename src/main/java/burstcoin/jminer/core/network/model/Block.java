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

package burstcoin.jminer.core.network.model;

import java.io.Serializable;
import java.util.List;

/**
 * The type Block.
 */
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

  /**
   * Instantiates a new Block.
   */
  public Block()
  {
  }

  /**
   * Gets previous block hash.
   *
   * @return the previous block hash
   */
  public String getPreviousBlockHash()
  {
    return previousBlockHash;
  }

  /**
   * Gets payload length.
   *
   * @return the payload length
   */
  public int getPayloadLength()
  {
    return payloadLength;
  }

  /**
   * Gets total amount nQT.
   *
   * @return the total amount nQT
   */
  public String getTotalAmountNQT()
  {
    return totalAmountNQT;
  }

  /**
   * Gets generation signature.
   *
   * @return the generation signature
   */
  public String getGenerationSignature()
  {
    return generationSignature;
  }

  /**
   * Gets generator.
   *
   * @return the generator
   */
  public String getGenerator()
  {
    return generator;
  }

  /**
   * Gets generator public key.
   *
   * @return the generator public key
   */
  public String getGeneratorPublicKey()
  {
    return generatorPublicKey;
  }

  /**
   * Gets base target.
   *
   * @return the base target
   */
  public String getBaseTarget()
  {
    return baseTarget;
  }

  /**
   * Gets payload hash.
   *
   * @return the payload hash
   */
  public String getPayloadHash()
  {
    return payloadHash;
  }

  /**
   * Gets generator rS.
   *
   * @return the generator rS
   */
  public String getGeneratorRS()
  {
    return generatorRS;
  }

  /**
   * Gets block reward.
   *
   * @return the block reward
   */
  public String getBlockReward()
  {
    return blockReward;
  }

  /**
   * Gets request processing time.
   *
   * @return the request processing time
   */
  public long getRequestProcessingTime()
  {
    return requestProcessingTime;
  }

  /**
   * Gets scoop num.
   *
   * @return the scoop num
   */
  public int getScoopNum()
  {
    return scoopNum;
  }

  /**
   * Gets number of transactions.
   *
   * @return the number of transactions
   */
  public long getNumberOfTransactions()
  {
    return numberOfTransactions;
  }

  /**
   * Gets block signature.
   *
   * @return the block signature
   */
  public String getBlockSignature()
  {
    return blockSignature;
  }

  /**
   * Gets transactions.
   *
   * @return the transactions
   */
  public List<String> getTransactions()
  {
    return transactions;
  }

  /**
   * Gets nonce.
   *
   * @return the nonce
   */
  public String getNonce()
  {
    return nonce;
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
   * Gets total fee nQT.
   *
   * @return the total fee nQT
   */
  public String getTotalFeeNQT()
  {
    return totalFeeNQT;
  }

  /**
   * Gets next block.
   *
   * @return the next block
   */
  public String getNextBlock()
  {
    return nextBlock;
  }

  /**
   * Gets previous block.
   *
   * @return the previous block
   */
  public String getPreviousBlock()
  {
    return previousBlock;
  }

  /**
   * Gets block.
   *
   * @return the block
   */
  public String getBlock()
  {
    return block;
  }

  /**
   * Gets height.
   *
   * @return the height
   */
  public long getHeight()
  {
    return height;
  }

  /**
   * Gets timestamp.
   *
   * @return the timestamp
   */
  public int getTimestamp()
  {
    return timestamp;
  }
}
