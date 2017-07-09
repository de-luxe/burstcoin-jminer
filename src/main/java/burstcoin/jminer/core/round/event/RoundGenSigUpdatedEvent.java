/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 by luxe - https://github.com/de-luxe - BURST-LUXE-RED2-G6JW-H4HG5
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

package burstcoin.jminer.core.round.event;

import org.springframework.context.ApplicationEvent;

// fired if generationSignature for current block has changed
public class RoundGenSigUpdatedEvent
  extends ApplicationEvent
{
  private byte[] generationSignature;
  private long blockNumber;

  public RoundGenSigUpdatedEvent(long blockNumber, byte[] generationSignature)
  {
    super(blockNumber);

    this.blockNumber = blockNumber;
    this.generationSignature = generationSignature;
  }

  public long getBlockNumber()
  {
    return blockNumber;
  }

  public byte[] getGenerationSignature()
  {
    return generationSignature;
  }
}

