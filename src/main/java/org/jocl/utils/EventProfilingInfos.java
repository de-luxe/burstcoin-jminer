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

package org.jocl.utils;

import org.jocl.cl_event;

import static org.jocl.CL.*;

/**
 * Utility methods for obtaining profiling information about cl_event objects.
 */
public class EventProfilingInfos
{
  /**
   * The device counter, in nanoseconds, when the command of the given event was queued.
   *
   * @param event The event
   *
   * @return The value
   */
  public static long getCommandQueued(cl_event event)
  {
    return Infos.getLong(Infos.FOR_EVENT_PROFILING, event,
                         CL_PROFILING_COMMAND_QUEUED);
  }

  /**
   * The device counter, in nanoseconds, when the command of the given event was submitted.
   *
   * @param event The event
   *
   * @return The value
   */
  public static long getCommandSubmit(cl_event event)
  {
    return Infos.getLong(Infos.FOR_EVENT_PROFILING, event,
                         CL_PROFILING_COMMAND_SUBMIT);
  }

  /**
   * The device counter, in nanoseconds, when the command of the given event started execution.
   *
   * @param event The event
   *
   * @return The value
   */
  public static long getCommandStart(cl_event event)
  {
    return Infos.getLong(Infos.FOR_EVENT_PROFILING, event,
                         CL_PROFILING_COMMAND_START);
  }

  /**
   * The device counter, in nanoseconds, when the command of the given event finished execution.
   *
   * @param event The event
   *
   * @return The value
   */
  public static long getCommandEnd(cl_event event)
  {
    return Infos.getLong(Infos.FOR_EVENT_PROFILING, event,
                         CL_PROFILING_COMMAND_END);
  }

  /**
   * Private constructor to prevent instantiation
   */
  private EventProfilingInfos()
  {

  }
}
