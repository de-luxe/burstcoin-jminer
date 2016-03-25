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

import static org.jocl.CL.*;

/**
 * Utility methods for obtaining information about cl_program objects
 */
public class ProgramInfos
{
  /**
   * The reference count. Only intended for identifying memory leaks.
   *
   * @param program The program
   *
   * @return The value
   */
  public static int getReferenceCount(cl_program program)
  {
    return Infos.getInt(Infos.FOR_PROGRAM, program,
                        CL_PROGRAM_REFERENCE_COUNT);
  }

  /**
   * The context of the program.
   *
   * @param program The program
   *
   * @return The value
   */
  public static cl_context getContext(cl_program program)
  {
    cl_context result = new cl_context();
    Infos.getPointer(Infos.FOR_PROGRAM, program,
                     CL_PROGRAM_CONTEXT, result);
    if(result.equals(new cl_context()))
    {
      return null;
    }
    return result;
  }

  /**
   * The number of devices associated with the program.
   *
   * @param program The program
   *
   * @return The value
   */
  public static int getNumDevices(cl_program program)
  {
    return Infos.getInt(Infos.FOR_PROGRAM, program,
                        CL_PROGRAM_NUM_DEVICES);
  }

  /**
   * The devices associated with the program.
   *
   * @param program The program
   *
   * @return The value
   */
  public static cl_device_id[] getDevices(cl_program program)
  {
    int numDevices = getNumDevices(program);
    cl_device_id devices[] = new cl_device_id[numDevices];
    Infos.getPointers(Infos.FOR_PROGRAM, program,
                      CL_PROGRAM_DEVICES, devices);
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
   * The program source code
   *
   * @param program The program
   *
   * @return The value
   */
  public static String getSource(cl_program program)
  {
    return Infos.getString(Infos.FOR_PROGRAM, program,
                           CL_PROGRAM_SOURCE);
  }


  /**
   * The sizes of the program binaries for each device.
   *
   * @param program The program
   *
   * @return The value
   */
  public static long[] getBinarySizes(cl_program program)
  {
    int numBinaries = getNumDevices(program);
    return Infos.getSizes(Infos.FOR_PROGRAM, program,
                          CL_PROGRAM_BINARY_SIZES, numBinaries);
  }

  /**
   * The sizes of the program binaries for each device.
   *
   * @param program The program
   *
   * @return The value
   */
  public static String[] getBinaries(cl_program program)
  {
    int numBinaries = getNumDevices(program);
    long sizes[] = getBinarySizes(program);
    byte dataArrays[][] = new byte[numBinaries][];
    Pointer dataPointers[] = new Pointer[numBinaries];
    for(int i = 0; i < numBinaries; i++)
    {
      dataArrays[i] = new byte[(int) sizes[i]];
      dataPointers[i] = Pointer.to(dataArrays[i]);
    }
    Infos.getPointers(Infos.FOR_PROGRAM, program,
                      CL_PROGRAM_BINARIES, dataPointers);
    String dataStrings[] = new String[numBinaries];
    for(int i = 0; i < numBinaries; i++)
    {
      dataStrings[i] = new String(dataArrays[i], 0, (int) sizes[i] - 1);
    }
    return dataStrings;
  }


  /**
   * Private constructor to prevent instantiation
   */
  private ProgramInfos()
  {

  }

}
