package burstcoin.jminer.core.network.model;

import java.io.Serializable;

public class MiningInfoResponse
  implements Serializable
{
  private String generationSignature;
  private String baseTarget;
  private long requestProcessingTime;
  private String height;

  // only if pool
  private long targetDeadline;

  public MiningInfoResponse()
  {
  }

  public long getTargetDeadline()
  {
    return targetDeadline;
  }

  public void setTargetDeadline(long targetDeadline)
  {
    this.targetDeadline = targetDeadline;
  }

  public String getGenerationSignature()
  {
    return generationSignature;
  }

  public void setGenerationSignature(String generationSignature)
  {
    this.generationSignature = generationSignature;
  }

  public String getBaseTarget()
  {
    return baseTarget;
  }

  public void setBaseTarget(String baseTarget)
  {
    this.baseTarget = baseTarget;
  }

  public long getRequestProcessingTime()
  {
    return requestProcessingTime;
  }

  public void setRequestProcessingTime(long requestProcessingTime)
  {
    this.requestProcessingTime = requestProcessingTime;
  }

  public String getHeight()
  {
    return height;
  }

  public void setHeight(String height)
  {
    this.height = height;
  }
}
