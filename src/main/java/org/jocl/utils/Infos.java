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

import org.jocl.NativePointerObject;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_device_id;
import org.jocl.cl_event;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.jocl.CL.*;

/**
 * Utility methods to obtain CL information
 */
class Infos
{
  /**
   * Interface for methods to access CL information
   *
   * @param <T> The type of the CL objects
   */
  interface InfoGetter<T>
  {
    /**
     * Write the specified information for the given object into the given target
     *
     * @param object The object
     * @param name   The info name
     * @param target The target
     */
    void get(T object, int name, Pointer target);

    /**
     * Returns the size of the specified information for the given object
     *
     * @param object The object
     * @param name   The info name
     *
     * @return The size
     */
    long getSize(T object, int name);
  }

  /**
   * Implementation of an InfoGetter for devices
   */
  static final InfoGetter<cl_device_id> FOR_DEVICE =
    new InfoGetter<cl_device_id>()
    {
      private final long size[] = {0};

      @Override
      public long getSize(cl_device_id object, int name)
      {
        clGetDeviceInfo(object, name, 0, null, size);
        return size[0];
      }

      @Override
      public void get(cl_device_id object, int name, Pointer target)
      {
        long s = getSize(object, name);
        clGetDeviceInfo(object, name, s, target, null);
      }
    };

  /**
   * Implementation of an InfoGetter for kernels
   */
  static final InfoGetter<cl_kernel> FOR_KERNEL =
    new InfoGetter<cl_kernel>()
    {
      private final long size[] = {0};

      @Override
      public long getSize(cl_kernel object, int name)
      {
        clGetKernelInfo(object, name, 0, null, size);
        return size[0];
      }

      @Override
      public void get(cl_kernel object, int name, Pointer target)
      {
        long s = getSize(object, name);
        clGetKernelInfo(object, name, s, target, null);
      }
    };

  /**
   * Implementation of an InfoGetter for platforms
   */
  static final InfoGetter<cl_platform_id> FOR_PLATFORM =
    new InfoGetter<cl_platform_id>()
    {
      private final long size[] = {0};

      @Override
      public long getSize(cl_platform_id object, int name)
      {
        clGetPlatformInfo(object, name, 0, null, size);
        return size[0];
      }

      @Override
      public void get(cl_platform_id object, int name, Pointer target)
      {
        long s = getSize(object, name);
        clGetPlatformInfo(object, name, s, target, null);
      }
    };

  /**
   * Implementation of an InfoGetter for mem objects
   */
  static final InfoGetter<cl_mem> FOR_MEM =
    new InfoGetter<cl_mem>()
    {
      private final long size[] = {0};

      @Override
      public long getSize(cl_mem object, int name)
      {
        clGetMemObjectInfo(object, name, 0, null, size);
        return size[0];
      }

      @Override
      public void get(cl_mem object, int name, Pointer target)
      {
        long s = getSize(object, name);
        clGetMemObjectInfo(object, name, s, target, null);
      }
    };


  /**
   * Implementation of an InfoGetter for event objects
   */
  static final InfoGetter<cl_event> FOR_EVENT =
    new InfoGetter<cl_event>()
    {
      private final long size[] = {0};

      @Override
      public long getSize(cl_event object, int name)
      {
        clGetEventInfo(object, name, 0, null, size);
        return size[0];
      }

      @Override
      public void get(cl_event object, int name, Pointer target)
      {
        long s = getSize(object, name);
        clGetEventInfo(object, name, s, target, null);
      }
    };

  /**
   * Implementation of an InfoGetter for profiling of event objects
   */
  static final InfoGetter<cl_event> FOR_EVENT_PROFILING =
    new InfoGetter<cl_event>()
    {
      private final long size[] = {0};

      @Override
      public long getSize(cl_event object, int name)
      {
        clGetEventProfilingInfo(object, name, 0, null, size);
        return size[0];
      }

      @Override
      public void get(cl_event object, int name, Pointer target)
      {
        long s = getSize(object, name);
        clGetEventProfilingInfo(object, name, s, target, null);
      }
    };


  /**
   * Implementation of an InfoGetter for programs
   */
  static final InfoGetter<cl_program> FOR_PROGRAM =
    new InfoGetter<cl_program>()
    {
      private final long size[] = {0};

      @Override
      public long getSize(cl_program object, int name)
      {
        clGetProgramInfo(object, name, 0, null, size);
        return size[0];
      }

      @Override
      public void get(cl_program object, int name, Pointer target)
      {
        long s = getSize(object, name);
        clGetProgramInfo(object, name, s, target, null);
      }
    };

  /**
   * Implementation of an InfoGetter for command queue objects
   */
  static final InfoGetter<cl_command_queue> FOR_COMMAND_QUEUE =
    new InfoGetter<cl_command_queue>()
    {
      private final long size[] = {0};

      @Override
      public long getSize(cl_command_queue object, int name)
      {
        clGetCommandQueueInfo(object, name, 0, null, size);
        return size[0];
      }

      @Override
      public void get(cl_command_queue object, int name, Pointer target)
      {
        long s = getSize(object, name);
        clGetCommandQueueInfo(object, name, s, target, null);
      }
    };


  /**
   * Implementation of an InfoGetter for context queue objects
   */
  static final InfoGetter<cl_context> FOR_CONTEXT =
    new InfoGetter<cl_context>()
    {
      private final long size[] = {0};

      @Override
      public long getSize(cl_context object, int name)
      {
        clGetContextInfo(object, name, 0, null, size);
        return size[0];
      }

      @Override
      public void get(cl_context object, int name, Pointer target)
      {
        long s = getSize(object, name);
        clGetContextInfo(object, name, s, target, null);
      }
    };


  /**
   * Returns the value of the info parameter with the given name
   *
   * @param infoGetter The info getter
   * @param object     The object
   * @param paramName  The parameter name
   * @param <T>        The object type
   *
   * @return The value
   */
  static <T> boolean getBool(
    InfoGetter<T> infoGetter, T object, int paramName)
  {
    return getInts(infoGetter, object, paramName, 1)[0] != 0;
  }

  /**
   * Returns the value of the info parameter with the given name
   *
   * @param infoGetter The info getter
   * @param object     The object
   * @param paramName  The parameter name
   * @param <T>        The object type
   *
   * @return The value
   */
  static <T> int getInt(
    InfoGetter<T> infoGetter, T object, int paramName)
  {
    return getInts(infoGetter, object, paramName, 1)[0];
  }

  /**
   * Returns the values of the info parameter with the given name
   *
   * @param infoGetter The info getter
   * @param object     The object
   * @param paramName  The parameter name
   * @param <T>        The object type
   * @param numValues  The number of values
   *
   * @return The values
   */
  static <T> int[] getInts(
    InfoGetter<T> infoGetter, T object, int paramName, int numValues)
  {
    int values[] = new int[numValues];
    infoGetter.get(object, paramName, Pointer.to(values));
    return values;
  }

  /**
   * Returns the value of the info parameter with the given name
   *
   * @param infoGetter The info getter
   * @param object     The object
   * @param paramName  The parameter name
   * @param <T>        The object type
   *
   * @return The value
   */
  static <T> long getLong(
    InfoGetter<T> infoGetter, T object, int paramName)
  {
    return getLongs(infoGetter, object, paramName, 1)[0];
  }

  /**
   * Returns the values of the info parameter with the given name
   *
   * @param infoGetter The info getter
   * @param object     The object
   * @param paramName  The parameter name
   * @param <T>        The object type
   * @param numValues  The number of values
   *
   * @return The values
   */
  static <T> long[] getLongs(
    InfoGetter<T> infoGetter, T object, int paramName, int numValues)
  {
    long values[] = new long[numValues];
    infoGetter.get(object, paramName, Pointer.to(values));
    return values;
  }

  /**
   * Returns the value of the info parameter with the given name
   *
   * @param infoGetter The info getter
   * @param object     The object
   * @param paramName  The parameter name
   * @param <T>        The object type
   *
   * @return The value
   */
  static <T> long getSize(
    InfoGetter<T> infoGetter, T object, int paramName)
  {
    return getSizes(infoGetter, object, paramName, 1)[0];
  }

  /**
   * Returns the values of the info parameter with the given name
   *
   * @param infoGetter The info getter
   * @param object     The object
   * @param paramName  The parameter name
   * @param <T>        The object type
   * @param numValues  The number of values
   *
   * @return The values
   */
  static <T> long[] getSizes(
    InfoGetter<T> infoGetter, T object, int paramName, int numValues)
  {
    ByteBuffer buffer = ByteBuffer.allocate(
      numValues * Sizeof.size_t).order(ByteOrder.nativeOrder());
    infoGetter.get(object, paramName, Pointer.to(buffer));
    long values[] = new long[numValues];
    if(Sizeof.size_t == 4)
    {
      for(int i = 0; i < numValues; i++)
      {
        values[i] = buffer.getInt(i * Sizeof.size_t);
      }
    }
    else
    {
      for(int i = 0; i < numValues; i++)
      {
        values[i] = buffer.getLong(i * Sizeof.size_t);
      }
    }
    return values;
  }

  /**
   * Returns the value of the info parameter with the given name
   *
   * @param infoGetter The info getter
   * @param object     The object
   * @param paramName  The parameter name
   * @param <T>        The object type
   *
   * @return The value
   */
  static <T> String getString(
    InfoGetter<T> infoGetter, T object, int paramName)
  {
    int size = (int) infoGetter.getSize(object, paramName);
    byte buffer[] = new byte[size];
    infoGetter.get(object, paramName, Pointer.to(buffer));
    String result = new String(buffer, 0, buffer.length - 1);
    return result.trim();
  }


  /**
   * Returns the value of the info parameter with the given name
   *
   * @param infoGetter The info getter
   * @param object     The object
   * @param paramName  The parameter name
   * @param <T>        The object type
   * @param <R>        The return type
   * @param target     The target
   *
   * @return The value
   */
  static <T, R extends NativePointerObject> R getPointer(
    InfoGetter<T> infoGetter, T object, int paramName, R target)
  {
    infoGetter.get(object, paramName, Pointer.to(target));
    return target;
  }

  /**
   * Returns the value of the info parameter with the given name
   *
   * @param infoGetter The info getter
   * @param object     The object
   * @param paramName  The parameter name
   * @param <T>        The object type
   * @param <R>        The return type
   * @param target     The target
   *
   * @return The value
   */
  static <T, R extends NativePointerObject> R[] getPointers(
    InfoGetter<T> infoGetter, T object, int paramName, R[] target)
  {
    infoGetter.get(object, paramName, Pointer.to(target));
    return target;
  }

  /**
   * Private constructor to prevent instantiation
   */
  private Infos()
  {

  }
}
