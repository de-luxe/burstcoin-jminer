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

package burstcoin.jminer.core.network.model;

import java.util.List;


public class Block
  extends Base
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
