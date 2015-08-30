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

package burstcoin.jminer.core.checker.task;

import burstcoin.jminer.core.checker.event.CheckerDevResultEvent;
import burstcoin.jminer.core.checker.util.OCLChecker;
import burstcoin.jminer.core.network.model.DevPoolResult;
import fr.cryptohash.Shabal256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pocminer.generate.MiningPlot;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * The type OCL checker find all below target task.
 */
@Component
@Scope("prototype")
public class OCLCheckerFindAllBelowTargetTask
  implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(OCLCheckerFindAllBelowTargetTask.class);

  @Autowired
  private ApplicationEventPublisher publisher;

  private final OCLChecker oclChecker;

  private long blockNumber;
  private byte[] generationSignature;
  private byte[] scoops;
  private long chunkPartStartNonce;
  private long targetDeadline;
  private long baseTarget;

  /**
   * Instantiates a new OCL checker find all below target task.
   *
   * @param oclChecker the ocl checker
   */
  @Autowired
  public OCLCheckerFindAllBelowTargetTask(OCLChecker oclChecker)
  {
    this.oclChecker = oclChecker;
  }

  /**
   * Init void.
   *
   * @param blockNumber the block number
   * @param generationSignature the generation signature
   * @param scoops the scoops
   * @param targetDeadline the target deadline
   * @param chunkPartStartNonce the chunk part start nonce
   * @param baseTarget the base target
   */
  public void init(long blockNumber, byte[] generationSignature, byte[] scoops, long targetDeadline, long chunkPartStartNonce, long baseTarget)
  {
    this.blockNumber = blockNumber;
    this.generationSignature = generationSignature;
    this.scoops = scoops;
    this.targetDeadline = targetDeadline;
    this.chunkPartStartNonce = chunkPartStartNonce;
    this.baseTarget = baseTarget;
  }

  @Override
  public void run()
  {
    int[] lowestNonces;
    synchronized(oclChecker)
    {
      // todo not working?!
      lowestNonces = oclChecker.findTarget(generationSignature, scoops, targetDeadline);
    }

    if(lowestNonces != null)
    {
      List<DevPoolResult> devPoolResults = new ArrayList<>();
      for(int lowestNonce : lowestNonces)
      {
        long nonce = chunkPartStartNonce + lowestNonce;
        BigInteger result = calculateResult(scoops, generationSignature, lowestNonce);
        BigInteger deadline = result.divide(BigInteger.valueOf(baseTarget));
        long calculatedDeadline = deadline.longValue();

        devPoolResults.add(new DevPoolResult(blockNumber, calculatedDeadline, nonce, chunkPartStartNonce));
      }
      publisher.publishEvent(new CheckerDevResultEvent(blockNumber, chunkPartStartNonce, devPoolResults));
    }
    else
    {
      publisher.publishEvent(new CheckerDevResultEvent(blockNumber, chunkPartStartNonce, null));
    }
  }

  private BigInteger calculateResult(byte[] scoops, byte[] generationSignature, int nonce)
  {
    Shabal256 md = new Shabal256();
    md.reset();
    md.update(generationSignature);
    md.update(scoops, nonce * MiningPlot.SCOOP_SIZE, MiningPlot.SCOOP_SIZE);
    byte[] hash = md.digest();
    return new BigInteger(1, new byte[]{hash[7], hash[6], hash[5], hash[4], hash[3], hash[2], hash[1], hash[0]});
  }
}
