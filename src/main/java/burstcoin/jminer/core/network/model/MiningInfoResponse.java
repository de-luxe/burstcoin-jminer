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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * The type Mining info response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MiningInfoResponse
  implements Serializable
{
  private String generationSignature;
  private String baseTarget;
  private long requestProcessingTime;
  private String height;

  // only if pool
  private long targetDeadline;

  /**
   * Instantiates a new Mining info response.
   */
  public MiningInfoResponse()
  {
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
   * Sets target deadline.
   *
   * @param targetDeadline the target deadline
   */
  public void setTargetDeadline(long targetDeadline)
  {
    this.targetDeadline = targetDeadline;
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
   * Sets generation signature.
   *
   * @param generationSignature the generation signature
   */
  public void setGenerationSignature(String generationSignature)
  {
    this.generationSignature = generationSignature;
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
   * Sets base target.
   *
   * @param baseTarget the base target
   */
  public void setBaseTarget(String baseTarget)
  {
    this.baseTarget = baseTarget;
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
   * Sets request processing time.
   *
   * @param requestProcessingTime the request processing time
   */
  public void setRequestProcessingTime(long requestProcessingTime)
  {
    this.requestProcessingTime = requestProcessingTime;
  }

  /**
   * Gets height.
   *
   * @return the height
   */
  public String getHeight()
  {
    return height;
  }

  /**
   * Sets height.
   *
   * @param height the height
   */
  public void setHeight(String height)
  {
    this.height = height;
  }
}
