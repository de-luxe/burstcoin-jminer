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

import org.jocl.cl_context;
import org.jocl.cl_kernel;
import org.jocl.cl_program;

import static org.jocl.CL.*;

/**
 * Utility methods for obtaining information about cl_kernel objects
 */
public class KernelInfos
{
  /**
   * The kernel function name.
   *
   * @param kernel The kernel
   *
   * @return The value
   */
  public static String getFunctionName(cl_kernel kernel)
  {
    return Infos.getString(Infos.FOR_KERNEL, kernel,
                           CL_KERNEL_FUNCTION_NAME);
  }

  /**
   * The reference count - only provided for identifying memory leaks.
   *
   * @param kernel The kernel
   *
   * @return The value
   */
  public static int getReferenceCount(cl_kernel kernel)
  {
    return Infos.getInt(Infos.FOR_KERNEL, kernel,
                        CL_KERNEL_REFERENCE_COUNT);
  }

  /**
   * The number of arguments
   *
   * @param kernel The kernel
   *
   * @return The value
   */
  public static int getNumArgs(cl_kernel kernel)
  {
    return Infos.getInt(Infos.FOR_KERNEL, kernel,
                        CL_KERNEL_NUM_ARGS);
  }

  /**
   * The context that this kernel belongs to
   *
   * @param kernel The kernel
   *
   * @return The value
   */
  public static cl_context getContext(cl_kernel kernel)
  {
    cl_context result = new cl_context();
    Infos.getPointer(Infos.FOR_KERNEL, kernel,
                     CL_KERNEL_CONTEXT, result);
    if(result.equals(new cl_context()))
    {
      return null;
    }
    return result;
  }

  /**
   * The program that this kernel belongs to
   *
   * @param kernel The kernel
   *
   * @return The value
   */
  public static cl_program getProgram(cl_kernel kernel)
  {
    cl_program result = new cl_program();
    Infos.getPointer(Infos.FOR_KERNEL, kernel,
                     CL_KERNEL_PROGRAM, result);
    if(result.equals(new cl_program()))
    {
      return null;
    }
    return result;
  }


  /**
   * Private constructor to prevent instantiation
   */
  private KernelInfos()
  {
  }

}
