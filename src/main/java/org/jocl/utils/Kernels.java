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
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_program;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.jocl.CL.*;

/**
 * Utility methods related to kernels and programs.
 */
public class Kernels
{
  /**
   * The logger used in this class
   */
//    private static final Logger logger =
//        Logger.getLogger(Kernels.class.getName());

  /**
   * Creates an OpenCL kernel for the function with the given name in the given program.
   *
   * @param program    The program
   * @param kernelName The kernel name
   *
   * @return The program
   */
  public static cl_kernel create(
    cl_program program, String kernelName)
  {
    cl_kernel kernel = clCreateKernel(program, kernelName, null);
    return kernel;
  }

  /**
   * Creates an OpenCL kernel for the function with the given name from the specified file. The program will be created using the given context, and released
   * after the kernel has been created.
   *
   * @param context        The context
   * @param fileName       The file name
   * @param kernelName     The name of the kernel
   * @param compileOptions The compile options
   *
   * @return The kernel
   *
   * @throws IOException If the file can not be read
   */
  public static cl_kernel createFromFile(
    cl_context context, String fileName, String kernelName,
    String... compileOptions)
    throws IOException
  {
    cl_program program = Programs.createFromFile(
      context, fileName, compileOptions);
    cl_kernel kernel = create(program, kernelName);
    clReleaseProgram(program);
    return kernel;
  }

  /**
   * Creates an OpenCL program for the function with the given name from the given input stream. The program will be created using the given context, and
   * released after the kernel has been created. The caller is responsible for closing the given stream after this method returns.
   *
   * @param context        The context
   * @param stream         The stream
   * @param kernelName     The kernel name
   * @param compileOptions The compile options
   *
   * @return The kernel
   *
   * @throws IOException If the stream can not be read
   */
  public static cl_kernel createFromStream(
    cl_context context, InputStream stream, String kernelName,
    String... compileOptions)
    throws IOException
  {
    cl_program program = Programs.createFromStream(
      context, stream, compileOptions);
    cl_kernel kernel = create(program, kernelName);
    clReleaseProgram(program);
    return kernel;
  }

  /**
   * Creates an OpenCL kernel for the function with the given name from the given source code. The program will be created using the given context, and released
   * after the kernel has been created.
   *
   * @param context        The context
   * @param sourceCode     The source code
   * @param kernelName     The kernel name
   * @param compileOptions The compile options
   *
   * @return The kernel
   */
  public static cl_kernel createFromSource(
    cl_context context, String sourceCode, String kernelName,
    String... compileOptions)
  {
    cl_program program = Programs.createFromSource(
      context, sourceCode, compileOptions);
    cl_kernel kernel = create(program, kernelName);
    clReleaseProgram(program);
    return kernel;
  }

  /**
   * Utility method to set the specified kernel arguments
   *
   * @param kernel The kernel
   * @param args   The arguments
   *
   * @throws IllegalArgumentException If the argument types are not primitive types and cl_mem
   */
  public static void setArgs(cl_kernel kernel, Object... args)
  {
    for(int i = 0; i < args.length; i++)
    {
      Object arg = args[i];
      if(arg instanceof cl_mem)
      {
        cl_mem value = (cl_mem) arg;
        Pointer pointer = Pointer.to(value);
        clSetKernelArg(kernel, i, Sizeof.cl_mem, pointer);
        //logger.info("argument "+i+" type is cl_mem");
      }
      else if(arg instanceof Byte)
      {
        Byte value = (Byte) arg;
        Pointer pointer = Pointer.to(new byte[]{value});
        clSetKernelArg(kernel, i, Sizeof.cl_char, pointer);
        //logger.info("argument "+i+" type is Byte");
      }
      else if(arg instanceof Short)
      {
        Short value = (Short) arg;
        Pointer pointer = Pointer.to(new short[]{value});
        clSetKernelArg(kernel, i, Sizeof.cl_short, pointer);
        //logger.info("argument "+i+" type is Short");
      }
      else if(arg instanceof Integer)
      {
        Integer value = (Integer) arg;
        Pointer pointer = Pointer.to(new int[]{value});
        clSetKernelArg(kernel, i, Sizeof.cl_int, pointer);
        //logger.info("argument "+i+" type is Integer");
      }
      else if(arg instanceof Long)
      {
        Long value = (Long) arg;
        Pointer pointer = Pointer.to(new long[]{value});
        clSetKernelArg(kernel, i, Sizeof.cl_long, pointer);
        //logger.info("argument "+i+" type is Long");
      }
      else if(arg instanceof Float)
      {
        Float value = (Float) arg;
        Pointer pointer = Pointer.to(new float[]{value});
        clSetKernelArg(kernel, i, Sizeof.cl_float, pointer);
        //logger.info("argument "+i+" type is Float");
      }
      else if(arg instanceof Double)
      {
        Double value = (Double) arg;
        Pointer pointer = Pointer.to(new double[]{value});
        clSetKernelArg(kernel, i, Sizeof.cl_double, pointer);
        //logger.info("argument "+i+" type is Double");
      }
      else
      {
        throw new IllegalArgumentException(
          "Type " + arg.getClass() + " may not be passed to a kernel");
      }
    }
  }

  /**
   * Release each of the given kernels if it is not <code>null</code>.
   *
   * @param kernels The kernels to release
   */
  public static void release(cl_kernel... kernels)
  {
    if(kernels != null)
    {
      release(Arrays.asList(kernels));
    }
  }

  /**
   * Release each of the given kernels if it is not <code>null</code>.
   *
   * @param kernels The kernels to release
   */
  public static void release(Iterable<cl_kernel> kernels)
  {
    if(kernels != null)
    {
      for(cl_kernel kernel : kernels)
      {
        if(kernel != null)
        {
          clReleaseKernel(kernel);
        }
      }
    }
  }

  /**
   * Private constructor to prevent instantiation
   */
  private Kernels()
  {

  }
}
