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

import org.jocl.Sizeof;
import org.jocl.cl_context;
import org.jocl.cl_device_id;

import static org.jocl.CL.*;

/**
 * Utility methods for obtaining information about cl_context objects
 */
public class ContextInfos
{

  /**
   * The reference count. Only intended for identifying memory leaks.
   *
   * @param context The context
   *
   * @return The value
   */
  public static int getReferenceCount(cl_context context)
  {
    return Infos.getInt(Infos.FOR_CONTEXT, context,
                        CL_CONTEXT_REFERENCE_COUNT);
  }

  /**
   * The number of devices associated with the context.
   *
   * @param context The context
   *
   * @return The value
   */
  public static int getNumDevices(cl_context context)
  {
    return Infos.getInt(Infos.FOR_CONTEXT, context,
                        CL_CONTEXT_NUM_DEVICES);
  }

  /**
   * The devices associated with the context.
   *
   * @param context The context
   *
   * @return The value
   */
  public static cl_device_id[] getDevices(cl_context context)
  {
    int numDevices = getNumDevices(context);
    cl_device_id devices[] = new cl_device_id[numDevices];
    Infos.getPointers(Infos.FOR_CONTEXT, context,
                      CL_CONTEXT_DEVICES, devices);
    cl_device_id nullDevice = new cl_device_id();
    for(int i = 0; i < numDevices; i++)
    {
      if(devices[i].equals(nullDevice))
      {
        devices[i] = null;
      }
    }
    return devices;
  }

  /**
   * The properties of the context.
   *
   * @param context The context
   *
   * @return The value
   */
  public static long[] getProperties(cl_context context)
  {
    long valueSize = Infos.FOR_CONTEXT.getSize(context,
                                               CL_CONTEXT_PROPERTIES);
    int numValues = (int) (valueSize / Sizeof.POINTER);
    return Infos.getSizes(Infos.FOR_CONTEXT, context,
                          CL_CONTEXT_PROPERTIES, numValues);
  }

  /**
   * The properties of the context, as Strings. The strings are the key-value pairs of the properties. The even indices of the properties array are assumed to
   * be of type cl_context_properties, and the odd entries are assumed to be values that are returned in a hexadecimal form.
   *
   * @param context The context
   *
   * @return The value
   */
  public static String[] getPropertiesStrings(cl_context context)
  {
    long properties[] = getProperties(context);
    // Omit trailing '0' entry
    String result[] = new String[properties.length - 1];
    for(int i = 0; i < properties.length / 2; i++)
    {
      long p0 = properties[i * 2 + 0];
      long p1 = properties[i * 2 + 1];
      result[i * 2 + 0] = stringFor_cl_context_properties((int) p0);
      result[i * 2 + 1] = "0x" + Long.toHexString(p1);
    }
    return result;
  }

  /**
   * Private constructor to prevent instantiation
   */
  private ContextInfos()
  {
  }
}
