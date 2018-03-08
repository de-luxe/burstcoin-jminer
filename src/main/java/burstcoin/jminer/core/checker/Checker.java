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

package burstcoin.jminer.core.checker;

import burstcoin.jminer.core.checker.event.CheckerResultEvent;
import burstcoin.jminer.core.checker.util.OCLChecker;
import burstcoin.jminer.core.reader.event.ReaderLoadedPartEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * The type Checker.
 */
@Component
@Scope("singleton")
public class Checker
{
  private static final Logger LOG = LoggerFactory.getLogger(Checker.class);

  private final ApplicationEventPublisher publisher;
  private final OCLChecker oclChecker;

  // data
  private long blockNumber;
  private byte[] generationSignature;

  @Autowired
  public Checker(ApplicationEventPublisher publisher, OCLChecker oclChecker)
  {
    this.publisher = publisher;
    this.oclChecker = oclChecker;
  }

  public void reconfigure(long blockNumber, byte[] generationSignature)
  {
    this.blockNumber = blockNumber;
    this.generationSignature = generationSignature;
  }

  @EventListener
  public void handleMessage(ReaderLoadedPartEvent event)
  {
    if(blockNumber == event.getBlockNumber() && Arrays.equals(generationSignature, event.getGenerationSignature()))
    {
      int lowestNonce;
      synchronized(oclChecker)
      {
        lowestNonce = oclChecker.findLowest(generationSignature, event.getScoops());
      }
      publisher.publishEvent(new CheckerResultEvent(blockNumber, generationSignature, event.getChunkPartStartNonce(), lowestNonce, event.getPlotFilePath(),
                                                    event.getScoops()));
    }
    else
    {
      LOG.trace("skipped check scoop ... outdated mining info...");
    }
  }
}
