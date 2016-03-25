/*
 * JOCL Utilities
 *
 * Copyright (c) 2011-2012 Marco Hutter - http://www.jocl.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package org.jocl.utils;

import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_device_id;

import static org.jocl.CL.*;

/**
 * Utility methods for obtaining information about cl_command_queue objects
 */
public class CommandQueueInfos
{
  /**
   * The context that this command queue belongs to
   *
   * @param commandQueue The command queue
   *
   * @return The value
   */
  public static cl_context getContext(cl_command_queue commandQueue)
  {
    cl_context result = new cl_context();
    Infos.getPointer(Infos.FOR_COMMAND_QUEUE, commandQueue,
                     CL_QUEUE_CONTEXT, result);
    if(result.equals(new cl_context()))
    {
      return null;
    }
    return result;
  }

  /**
   * The device that this command queue belongs to
   *
   * @param commandQueue The command queue
   *
   * @return The value
   */
  public static cl_device_id getDevice(cl_command_queue commandQueue)
  {
    cl_device_id result = new cl_device_id();
    Infos.getPointer(Infos.FOR_COMMAND_QUEUE, commandQueue,
                     CL_QUEUE_CONTEXT, result);
    if(result.equals(new cl_device_id()))
    {
      return null;
    }
    return result;
  }

  /**
   * The reference count. Only intended for identifying memory leaks.
   *
   * @param commandQueue The command queue
   *
   * @return The value
   */
  public static int getReferenceCount(cl_command_queue commandQueue)
  {
    return Infos.getInt(Infos.FOR_COMMAND_QUEUE, commandQueue,
                        CL_QUEUE_REFERENCE_COUNT);
  }

  /**
   * The properties of the command queue.
   *
   * @param commandQueue The command queue
   *
   * @return The value
   */
  public static long getProperties(cl_command_queue commandQueue)
  {
    return Infos.getLong(Infos.FOR_COMMAND_QUEUE, commandQueue,
                         CL_QUEUE_PROPERTIES);
  }

  /**
   * The properties of the command queue, as a String
   *
   * @param commandQueue The command queue
   *
   * @return The value
   */
  public static String getPropertiesString(cl_command_queue commandQueue)
  {
    return stringFor_cl_command_queue_properties(getProperties(commandQueue));
  }

  /**
   * Private constructor to prevent instantiation
   */
  private CommandQueueInfos()
  {
  }
}
