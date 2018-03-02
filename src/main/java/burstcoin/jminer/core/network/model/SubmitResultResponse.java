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
 * The type Submit result response.
 */
@JsonIgnoreProperties(ignoreUnknown = true) // prevent future incompatible values
public class SubmitResultResponse
  implements Serializable
{
  private String result;
  private long requestProcessingTime;
  private long deadline;

  // pool response
  private long block;

  // one or the other is used
  private String deadlineString;
  private String deadlineText;

  private long targetDeadline;

  protected SubmitResultResponse()
  {
  }

  public SubmitResultResponse(String result, long requestProcessingTime, long deadline)
  {
    this.result = result;
    this.requestProcessingTime = requestProcessingTime;
    this.deadline = deadline;
  }

  /**
   * Gets result.
   *
   * @return the result
   */
  public String getResult()
  {
    return result;
  }

  /**
   * Sets result.
   *
   * @param result the result
   */
  public void setResult(String result)
  {
    this.result = result;
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
   * Gets deadline.
   *
   * @return the deadline
   */
  public long getDeadline()
  {
    return deadline;
  }

  /**
   * Sets deadline.
   *
   * @param deadline the deadline
   */
  public void setDeadline(long deadline)
  {
    this.deadline = deadline;
  }

  /**
   * Gets block.
   *
   * @return the block
   */
  public long getBlock()
  {
    return block;
  }

  /**
   * Sets block.
   *
   * @param block the block
   */
  public void setBlock(long block)
  {
    this.block = block;
  }

  /**
   * Gets deadline string.
   *
   * @return the deadline string
   */
  public String getDeadlineString()
  {
    return deadlineString;
  }

  /**
   * Sets deadline string.
   *
   * @param deadlineString the deadline string
   */
  public void setDeadlineString(String deadlineString)
  {
    this.deadlineString = deadlineString;
  }

  public String getDeadlineText()
  {
    return deadlineText;
  }

  public void setDeadlineText(String deadlineText)
  {
    this.deadlineText = deadlineText;
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
}
