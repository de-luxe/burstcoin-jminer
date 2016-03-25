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

import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;

import java.util.Arrays;
import java.util.List;

import static org.jocl.CL.*;

/**
 * Utility methods related to cl_context objects
 */
public class Contexts
{
  /**
   * Create a new context for the given platform and devices.
   *
   * @param platform The platform
   * @param devices  The devices
   *
   * @return The new context
   */
  public static cl_context create(
    cl_platform_id platform, cl_device_id... devices)
  {
    return create(platform, Arrays.asList(devices), null);
  }

  /**
   * Create a new context for the given platform and devices.
   *
   * @param platform The platform
   * @param devices  The devices
   *
   * @return The new context
   */
  public static cl_context create(
    cl_platform_id platform, List<cl_device_id> devices)
  {
    return create(platform, devices, null);
  }

  /**
   * Create a new context with the given platform and devices, using the given additional context properties. The additional context properties are assumed to
   * contain pairs of long values, where even entries contain the context property name, and odd values the corresponding value. This array may be
   * <code>null</code>.
   *
   * @param platform                    The platform
   * @param devices                     The devices
   * @param additionalContextProperties Additional context properties
   *
   * @return The new context
   */
  public static cl_context create(
    cl_platform_id platform, List<cl_device_id> devices,
    long additionalContextProperties[])
  {
    cl_device_id devicesArray[] =
      devices.toArray(new cl_device_id[devices.size()]);

    cl_context_properties contextProperties = new cl_context_properties();
    contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
    if(additionalContextProperties != null)
    {
      for(int i = 0; i < additionalContextProperties.length / 2; i++)
      {
        contextProperties.addProperty(
          additionalContextProperties[i * 2 + 0],
          additionalContextProperties[i * 2 + 1]);
      }
    }
    cl_context context = clCreateContext(
      contextProperties, devicesArray.length,
      devicesArray, null, null, null);
    return context;
  }


  /**
   * Release each of the given contexts if it is not <code>null</code>.
   *
   * @param contexts The contexts to release
   */
  public static void release(cl_context... contexts)
  {
    if(contexts != null)
    {
      for(cl_context context : contexts)
      {
        if(context != null)
        {
          clReleaseContext(context);
        }
      }
    }
  }

  /**
   * Release each of the given contexts if it is not <code>null</code>.
   *
   * @param contexts The contexts to release
   */
  public static void release(Iterable<cl_context> contexts)
  {
    if(contexts != null)
    {
      for(cl_context context : contexts)
      {
        if(context != null)
        {
          clReleaseContext(context);
        }
      }
    }
  }


  /**
   * Private constructor to prevent instantiation
   */
  private Contexts()
  {
  }

}
