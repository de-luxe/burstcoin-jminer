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

package burstcoin.jminer.core.checker;

import burstcoin.jminer.core.CoreProperties;
import burstcoin.jminer.core.checker.task.OCLCheckerFindAllBelowTargetTask;
import burstcoin.jminer.core.checker.task.OCLCheckerTask;
import burstcoin.jminer.core.reader.event.ReaderLoadedPartEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * The type Checker.
 */
@Component
@Scope("singleton")
public class Checker
  implements ReaderLoadedPartEvent.Handler
{
  private static final Logger LOG = LoggerFactory.getLogger(Checker.class);

  @Autowired
  private ApplicationContext context;

  @Autowired
  private SyncTaskExecutor checkTaskExecutor;

  // setting
  private boolean devPool;
  private boolean optDevPool;

  // data
  private long blockNumber;
  private long targetDeadline;
  private long baseTarget;
  private byte[] generationSignature;

  /**
   * Post construct.
   */
  @PostConstruct
  protected void postConstruct()
  {
    this.devPool = CoreProperties.isDevPool();
    this.optDevPool = CoreProperties.isOptDevPool();
    this.targetDeadline = CoreProperties.getTargetDeadline();
  }

  /**
   * Reconfigure void.
   *
   * @param blockNumber the block number
   * @param baseTarget the base target
   * @param targetDeadline the target deadline
   * @param generationSignature the generation signature
   */
  public void reconfigure(long blockNumber, long baseTarget, long targetDeadline, byte[] generationSignature)
  {
    this.blockNumber = blockNumber;
    this.baseTarget = baseTarget;
    this.targetDeadline = targetDeadline;
    this.generationSignature = generationSignature;
  }

  @Override
  @EventListener
  public void handleMessage(ReaderLoadedPartEvent event)
  {
    if(blockNumber == event.getBlockNumber())
    {
      if(devPool && optDevPool)
      {
        // todo findTartget() does not work! optDevPool not supported yet!
        OCLCheckerFindAllBelowTargetTask oclCheckerFindAllBelowTargetTask = context.getBean(OCLCheckerFindAllBelowTargetTask.class);
        oclCheckerFindAllBelowTargetTask.init(event.getBlockNumber(), generationSignature, event.getScoops(), targetDeadline,
                                              event.getChunkPartStartNonce(), baseTarget);
        checkTaskExecutor.execute(oclCheckerFindAllBelowTargetTask);
      }
      else
      {
        OCLCheckerTask oclCheckerTask = context.getBean(OCLCheckerTask.class);
        oclCheckerTask.init(event.getBlockNumber(), generationSignature, event.getScoops(), event.getChunkPartStartNonce());
        checkTaskExecutor.execute(oclCheckerTask);
      }
    }
    else
    {
      LOG.debug("skipped check scoop ... old block ...");
    }
  }
}
