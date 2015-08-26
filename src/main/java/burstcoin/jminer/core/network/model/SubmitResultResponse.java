package burstcoin.jminer.core.network.model;

import java.io.Serializable;

public class SubmitResultResponse
  implements Serializable
{
  private String result;
  private long requestProcessingTime;
  private long deadline;

  // pool response
  private long block;
  private String deadlineString;
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

  public String getResult()
  {
    return result;
  }

  public void setResult(String result)
  {
    this.result = result;
  }

  public long getRequestProcessingTime()
  {
    return requestProcessingTime;
  }

  public void setRequestProcessingTime(long requestProcessingTime)
  {
    this.requestProcessingTime = requestProcessingTime;
  }

  public long getDeadline()
  {
    return deadline;
  }

  public void setDeadline(long deadline)
  {
    this.deadline = deadline;
  }

  public long getBlock()
  {
    return block;
  }

  public void setBlock(long block)
  {
    this.block = block;
  }

  public String getDeadlineString()
  {
    return deadlineString;
  }

  public void setDeadlineString(String deadlineString)
  {
    this.deadlineString = deadlineString;
  }

  public long getTargetDeadline()
  {
    return targetDeadline;
  }

  public void setTargetDeadline(long targetDeadline)
  {
    this.targetDeadline = targetDeadline;
  }
}
