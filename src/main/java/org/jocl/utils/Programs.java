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
import org.jocl.cl_context;
import org.jocl.cl_device_id;
import org.jocl.cl_program;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import static org.jocl.CL.*;

/**
 * Utility methods related to programs.
 */
public class Programs
{
  /**
   * The logger used in this class
   */
  private static final Logger logger =
    Logger.getLogger(Programs.class.getName());


  /**
   * Creates an OpenCL program for the function with the given name from the specified file. The program will be created using the given context, and has to be
   * released by the caller, using {@link #release(cl_program...)}.
   *
   * @param context        The context
   * @param fileName       The file name
   * @param compileOptions The compile options
   *
   * @return The program
   *
   * @throws IOException If the file can not be read
   */
  public static cl_program createFromFile(
    cl_context context, String fileName, String... compileOptions)
    throws IOException
  {
    String sourceCode = readFile(fileName);
    return createFromSource(context, sourceCode);
  }

  /**
   * Creates an OpenCL program for the function with the given name from the given input stream. The program will be created using the given context, and has to
   * be released by the caller, using {@link #release(cl_program...)} The caller is responsible for closing the given stream after this method returns.
   *
   * @param context        The context
   * @param stream         The stream
   * @param compileOptions The compile options
   *
   * @return The program
   *
   * @throws IOException If the stream can not be read
   */
  public static cl_program createFromStream(
    cl_context context, InputStream stream, String... compileOptions)
    throws IOException
  {
    String sourceCode = readStream(stream);
    return createFromSource(context, sourceCode);
  }

  /**
   * Creates an OpenCL program for the function with the given name from the given source code. The program will be created using the given context, and has to
   * be released by the caller, using {@link #release(cl_program...)}
   *
   * @param context        The context
   * @param sourceCode     The source code
   * @param compileOptions The compile options
   *
   * @return The program
   */
  public static cl_program createFromSource(
    cl_context context, String sourceCode, String... compileOptions)
  {
    cl_program program = clCreateProgramWithSource(context, 1,
                                                   new String[]{sourceCode}, null, null);

    String compileOptionsString = null;
    if(compileOptions != null)
    {
      StringBuilder sb = new StringBuilder();
      for(String compileOption : compileOptions)
      {
        sb.append(compileOption + " ");
      }
      compileOptionsString = sb.toString();
    }
    clBuildProgram(program, 0, null, compileOptionsString, null, null);
    return program;
  }

  /**
   * Returns the build logs for the given program as a string
   *
   * @param program The program
   *
   * @return The build logs
   */
  public static String obtainBuildLogs(cl_program program)
  {
    cl_device_id devices[] = ProgramInfos.getDevices(program);

    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < devices.length; i++)
    {
      sb.append("Build log for device " + i + ":\n");
      long logSize[] = new long[1];
      clGetProgramBuildInfo(program, devices[i],
                            CL_PROGRAM_BUILD_LOG, 0, null, logSize);
      byte logData[] = new byte[(int) logSize[0]];
      clGetProgramBuildInfo(program, devices[i],
                            CL_PROGRAM_BUILD_LOG, logSize[0], Pointer.to(logData), null);
      sb.append(new String(logData, 0, logData.length - 1));
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * Read the contents of the file with the specified name and return it as a String. Returns <code>null</code> if an IO error occurs.
   *
   * @param fileName The file name
   *
   * @return The file contents as a string
   *
   * @throws IOException If the file can not be read
   */
  private static String readFile(String fileName)
    throws IOException
  {
    InputStream stream = null;
    try
    {
      stream = new FileInputStream(fileName);
      return readStream(stream);
    }
    finally
    {
      if(stream != null)
      {
        try
        {
          stream.close();
        }
        catch(IOException e)
        {
          logger.warning("Could not close stream");
        }
      }
    }
  }


  /**
   * Read the contents of the given stream and return it as a String.
   *
   * @param stream The stream
   *
   * @return The stream contents as a string
   *
   * @throws IOException If something goes wrong
   */
  private static String readStream(InputStream stream)
    throws IOException
  {
    BufferedReader br = new BufferedReader(
      new InputStreamReader(stream));
    StringBuffer sb = new StringBuffer();
    String line = null;
    while(true)
    {
      line = br.readLine();
      if(line == null)
      {
        break;
      }
      sb.append(line).append("\n");
    }
    return sb.toString();
  }


  /**
   * Release each of the given programs if it is not <code>null</code>.
   *
   * @param programs The programs to release
   */
  public static void release(cl_program... programs)
  {
    if(programs != null)
    {
      for(cl_program program : programs)
      {
        if(program != null)
        {
          clReleaseProgram(program);
        }
      }
    }
  }

  /**
   * Private constructor to prevent instantiation
   */
  private Programs()
  {
  }
}
