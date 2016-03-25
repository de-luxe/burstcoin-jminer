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

import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_event;

import static org.jocl.CL.*;

/**
 * Utility methods for obtaining information about cl_event objects.
 */
public class EventInfos
{
  /**
   * The command-queue associated with event
   *
   * @param event The event
   *
   * @return The value
   */
  public static cl_command_queue getCommandQueue(cl_event event)
  {
    cl_command_queue result = new cl_command_queue();
    Infos.getPointer(Infos.FOR_EVENT, event,
                     CL_EVENT_COMMAND_QUEUE, result);
    if(result.equals(new cl_command_queue()))
    {
      return null;
    }
    return result;
  }

  /**
   * The context that this event belongs to
   *
   * @param event The event
   *
   * @return The value
   */
  public static cl_context getContext(cl_event event)
  {
    cl_context result = new cl_context();
    Infos.getPointer(Infos.FOR_EVENT, event,
                     CL_EVENT_CONTEXT, result);
    if(result.equals(new cl_context()))
    {
      return null;
    }
    return result;
  }

  /**
   * The command associated with event
   *
   * @param event The event
   *
   * @return The value
   */
  public static int getCommandType(cl_event event)
  {
    return Infos.getInt(Infos.FOR_EVENT, event,
                        CL_EVENT_COMMAND_TYPE);
  }

  /**
   * The command associated with event, as a String
   *
   * @param event The event
   *
   * @return The value
   */
  public static String getCommandTypeString(cl_event event)
  {
    return stringFor_cl_command_type(
      Infos.getInt(Infos.FOR_EVENT, event,
                   CL_EVENT_COMMAND_TYPE));
  }

  /**
   * The execution status of the command identified by event.
   *
   * @param event The event
   *
   * @return The value
   */
  public static int getCommandExecutionStatus(cl_event event)
  {
    return Infos.getInt(Infos.FOR_EVENT, event,
                        CL_EVENT_COMMAND_EXECUTION_STATUS);

  }

  /**
   * The execution status of the command identified by event, as a String
   *
   * @param event The event
   *
   * @return The value
   */
  public static String getCommandExecutionStatusString(cl_event event)
  {
    return stringFor_command_execution_status(
      Infos.getInt(Infos.FOR_EVENT, event,
                   CL_EVENT_COMMAND_EXECUTION_STATUS));
  }


  /**
   * The reference count. Only intended for identifying memory leaks.
   *
   * @param event The event
   *
   * @return The value
   */
  public static int getReferenceCount(cl_event event)
  {
    return Infos.getInt(Infos.FOR_EVENT, event,
                        CL_EVENT_REFERENCE_COUNT);
  }


  /**
   * Private constructor to prevent instantiation
   */
  private EventInfos()
  {
  }
}
