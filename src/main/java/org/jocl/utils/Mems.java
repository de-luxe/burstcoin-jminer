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

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_context;
import org.jocl.cl_mem;

import java.util.Arrays;

import static org.jocl.CL.*;

/**
 * Utility methods related to memory objects.
 */
public class Mems
{

  /**
   * Create a memory object with the given size.
   *
   * @param context The context for which the memory object will be created
   * @param size    The size of the memory object, in bytes
   *
   * @return The memory object
   */
  public static cl_mem create(cl_context context, int size)
  {
    cl_mem mem = clCreateBuffer(
      context, CL_MEM_READ_WRITE,
      size, null, null);
    return mem;
  }

  /**
   * Create a memory object that contains the data from the given array
   *
   * @param context The context for which the memory object will be created
   * @param array   The array
   *
   * @return The memory object
   */
  public static cl_mem create(
    cl_context context, byte array[])
  {
    return create(
      context, array.length * Sizeof.cl_char, Pointer.to(array));
  }

  /**
   * Create a memory object that contains the data from the given array
   *
   * @param context The context for which the memory object will be created
   * @param array   The array
   *
   * @return The memory object
   */
  public static cl_mem create(
    cl_context context, short array[])
  {
    return create(
      context, array.length * Sizeof.cl_short, Pointer.to(array));
  }

  /**
   * Create a memory object that contains the data from the given array
   *
   * @param context The context for which the memory object will be created
   * @param array   The array
   *
   * @return The memory object
   */
  public static cl_mem create(
    cl_context context, int array[])
  {
    return create(
      context, array.length * Sizeof.cl_int, Pointer.to(array));
  }

  /**
   * Create a memory object that contains the data from the given array
   *
   * @param context The context for which the memory object will be created
   * @param array   The array
   *
   * @return The memory object
   */
  public static cl_mem create(
    cl_context context, long array[])
  {
    return create(
      context, array.length * Sizeof.cl_long, Pointer.to(array));
  }

  /**
   * Create a memory object that contains the data from the given array
   *
   * @param context The context for which the memory object will be created
   * @param array   The array
   *
   * @return The memory object
   */
  public static cl_mem create(
    cl_context context, float array[])
  {
    return create(
      context, array.length * Sizeof.cl_float, Pointer.to(array));
  }

  /**
   * Create a memory object that contains the data from the given array
   *
   * @param context The context for which the memory object will be created
   * @param array   The array
   *
   * @return The memory object
   */
  public static cl_mem create(
    cl_context context, double array[])
  {
    return create(
      context, array.length * Sizeof.cl_double, Pointer.to(array));
  }

  /**
   * Creates a memory object with the given size that contains the data from the given source pointer.
   *
   * @param context The context for which the memory object will be created
   * @param size    The size of the memory object, in bytes
   * @param source  The pointer to the source data
   *
   * @return The memory object
   */
  private static cl_mem create(
    cl_context context, long size, Pointer source)
  {
    cl_mem mem = clCreateBuffer(
      context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
      size, source, null);
    return mem;
  }

  /**
   * Release each of the given memory objects if it is not <code>null</code>.
   *
   * @param mems The memory objects to release
   */
  public static void release(cl_mem... mems)
  {
    if(mems != null)
    {
      release(Arrays.asList(mems));
    }
  }

  /**
   * Release each of the given memory objects if it is not <code>null</code>.
   *
   * @param mems The memory objects to release
   */
  public static void release(Iterable<cl_mem> mems)
  {
    if(mems != null)
    {
      for(cl_mem mem : mems)
      {
        if(mem != null)
        {
          clReleaseMemObject(mem);
        }
      }
    }
  }


  /**
   * Private constructor to prevent instantiation
   */
  private Mems()
  {

  }
}
