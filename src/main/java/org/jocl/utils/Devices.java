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

import org.jocl.CLException;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.jocl.CL.*;

/**
 * Utility methods related to devices.
 */
public class Devices
{
  /**
   * Returns an unmodifiable (possibly empty) list of all devices of the given platform.
   *
   * @param platform The platform
   *
   * @return The list of matching devices
   */
  public static List<cl_device_id> getDevices(cl_platform_id platform)
  {
    return getDevices(platform, CL_DEVICE_TYPE_ALL);
  }

  /**
   * Returns an unmodifiable (possibly empty) list of all devices of the given platform that match the specified device type. The device type must be one of <br
   * /> CL_DEVICE_TYPE_CPU<br /> CL_DEVICE_TYPE_GPU<br /> CL_DEVICE_TYPE_ACCELERATOR<br /> CL_DEVICE_TYPE_DEFAULT<br /> CL_DEVICE_TYPE_ALL<br />
   *
   * @param platform   The platform
   * @param deviceType The device type
   *
   * @return The list of matching devices
   */
  public static List<cl_device_id> getDevices(
    cl_platform_id platform, long deviceType)
  {
    // The clGetDeviceIDs method may cause an exception
    // to be thrown when no matching devices are found.
    // Return an empty list in this case.
    int numDevicesArray[] = new int[1];
    try
    {
      int result = clGetDeviceIDs(
        platform, deviceType, 0, null, numDevicesArray);
      if(result == CL_DEVICE_NOT_FOUND)
      {
        return Collections.emptyList();
      }
    }
    catch(CLException e)
    {
      if(e.getStatus() == CL_DEVICE_NOT_FOUND)
      {
        return Collections.emptyList();
      }
      throw e;
    }
    int numDevices = numDevicesArray[0];
    cl_device_id devices[] = new cl_device_id[numDevices];
    clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
    return Arrays.asList(devices);
  }

  /**
   * Private constructor to prevent instantiation
   */
  private Devices()
  {
  }


  @SuppressWarnings("unused")
  private long maxFLOPs(cl_device_id device)
  {
    int maxComputeUnits = DeviceInfos.getMaxComputeUnits(device);
    int maxClockFrequency = DeviceInfos.getMaxClockFrequency(device);

    //System.out.println("maxComputeUnits "+maxComputeUnits);
    //System.out.println("maxClockFrequency "+maxClockFrequency);

    // TODO: The value '8' is only for NVIDIA cards!!!
    long coresPerComputeUnit = 8;
    long maxFlops =
      maxComputeUnits * coresPerComputeUnit *
      maxClockFrequency * 2 * 1000000;
    return maxFlops;
  }

}
