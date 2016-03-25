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

import org.jocl.cl_device_id;

import static org.jocl.CL.*;

/**
 * Utility methods for obtaining information about cl_device_id objects
 */
public class DeviceInfos
{
  /**
   * Device name string
   *
   * @param device The device
   *
   * @return The value
   */
  public static String getName(cl_device_id device)
  {
    return Infos.getString(Infos.FOR_DEVICE, device, CL_DEVICE_NAME);
  }

  /**
   * Vendor name string
   *
   * @param device The device
   *
   * @return The value
   */
  public static String getVendor(cl_device_id device)
  {
    return Infos.getString(Infos.FOR_DEVICE, device,
                           CL_DEVICE_VENDOR);
  }

  /**
   * A unique device vendor identifier
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getVendorId(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_VENDOR_ID);
  }

  /**
   * The OpenCL device type
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getType(cl_device_id device)
  {
    return Infos.getLong(Infos.FOR_DEVICE, device,
                         CL_DEVICE_TYPE);
  }

  /**
   * The OpenCL device type as a String
   *
   * @param device The device
   *
   * @return The value
   */
  public static String getTypeString(cl_device_id device)
  {
    return stringFor_cl_device_type(
      Infos.getLong(Infos.FOR_DEVICE, device,
                    CL_DEVICE_TYPE));
  }

  /**
   * OpenCL version string
   *
   * @param device The device
   *
   * @return The value
   */
  public static String getDeviceVersion(cl_device_id device)
  {
    return Infos.getString(Infos.FOR_DEVICE, device,
                           CL_DEVICE_VERSION);
  }

  /**
   * OpenCL software driver version string
   *
   * @param device The device
   *
   * @return The value
   */
  public static String getDriverVersion(cl_device_id device)
  {
    return Infos.getString(Infos.FOR_DEVICE, device,
                           CL_DRIVER_VERSION);
  }

  /**
   * OpenCL profile string
   *
   * @param device The device
   *
   * @return The value
   */
  public static String getProfile(cl_device_id device)
  {
    return Infos.getString(Infos.FOR_DEVICE, device,
                           CL_DEVICE_PROFILE);
  }

  /**
   * Maximum configured clock frequency in MHz
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getMaxClockFrequency(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_MAX_CLOCK_FREQUENCY);
  }

  /**
   * The number of parallel compute cores
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getMaxComputeUnits(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_MAX_COMPUTE_UNITS);
  }

  /**
   * Whether the device is available
   *
   * @param device The device
   *
   * @return The value
   */
  public static boolean getAvailable(cl_device_id device)
  {
    return Infos.getBool(Infos.FOR_DEVICE, device,
                         CL_DEVICE_AVAILABLE);
  }

  /**
   * The default compute device address space size bits
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getAddressBits(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_ADDRESS_BITS);
  }

  /**
   * Whether the OpenCL device is a little endian device
   *
   * @param device The device
   *
   * @return The value
   */
  public static boolean getEndianLittle(cl_device_id device)
  {
    return Infos.getBool(Infos.FOR_DEVICE, device,
                         CL_DEVICE_ENDIAN_LITTLE);
  }

  /**
   * Whether the implementation does have a compiler
   *
   * @param device The device
   *
   * @return The value
   */
  public static boolean getCompilerAvailable(cl_device_id device)
  {
    return Infos.getBool(Infos.FOR_DEVICE, device,
                         CL_DEVICE_COMPILER_AVAILABLE);
  }

  /**
   * The highest OpenCL C version supported by the compiler
   *
   * @param device The device
   *
   * @return The value
   */
  public static String getOpenclCVersion(cl_device_id device)
  {
    return Infos.getString(Infos.FOR_DEVICE, device,
                           CL_DEVICE_OPENCL_C_VERSION);
  }

  /**
   * Whether the device implements error correction
   *
   * @param device The device
   *
   * @return The value
   */
  public static boolean getErrorCorrectionSupport(cl_device_id device)
  {
    return Infos.getBool(Infos.FOR_DEVICE, device,
                         CL_DEVICE_ERROR_CORRECTION_SUPPORT);
  }

  /**
   * Describes the execution capabilities of the device
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getExecutionCapabilities(cl_device_id device)
  {
    return Infos.getLong(Infos.FOR_DEVICE, device,
                         CL_DEVICE_EXECUTION_CAPABILITIES);
  }

  /**
   * Describes the execution capabilities of the device as a String
   *
   * @param device The device
   *
   * @return The value
   */
  public static String getExecutionCapabilitiesString(cl_device_id device)
  {
    return stringFor_cl_device_exec_capabilities(
      Infos.getLong(Infos.FOR_DEVICE, device,
                    CL_DEVICE_EXECUTION_CAPABILITIES));
  }

  /**
   * A space-separated list of extension names
   *
   * @param device The device
   *
   * @return The value
   */
  public static String getExtensions(cl_device_id device)
  {
    return Infos.getString(Infos.FOR_DEVICE, device,
                           CL_DEVICE_EXTENSIONS);
  }

  /**
   * Whether device and host have a unified memory subsystem
   *
   * @param device The device
   *
   * @return The value
   */
  public static boolean getHostUnifiedMemory(cl_device_id device)
  {
    return Infos.getBool(Infos.FOR_DEVICE, device,
                         CL_DEVICE_HOST_UNIFIED_MEMORY);
  }

  /**
   * The resolution of device timer in nanoseconds
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getProfilingTimerResolution(cl_device_id device)
  {
    return Infos.getSize(Infos.FOR_DEVICE, device,
                         CL_DEVICE_PROFILING_TIMER_RESOLUTION);
  }

  /**
   * The supported command-queue properties
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getQueueProperties(cl_device_id device)
  {
    return Infos.getLong(Infos.FOR_DEVICE, device,
                         CL_DEVICE_QUEUE_PROPERTIES);
  }

  /**
   * The supported command-queue properties as a String
   *
   * @param device The device
   *
   * @return The value
   */
  public static String getQueuePropertiesString(cl_device_id device)
  {
    return stringFor_cl_command_queue_properties(
      Infos.getLong(Infos.FOR_DEVICE, device,
                    CL_DEVICE_QUEUE_PROPERTIES));
  }


  /**
   * Single precision floating-point capability
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getSingleFpConfig(cl_device_id device)
  {
    return Infos.getLong(Infos.FOR_DEVICE, device,
                         CL_DEVICE_SINGLE_FP_CONFIG);
  }

  /**
   * Single precision floating-point capability as a String
   *
   * @param device The device
   *
   * @return The value
   */
  public static String getSingleFpConfigString(cl_device_id device)
  {
    return stringFor_cl_device_fp_config(
      Infos.getLong(Infos.FOR_DEVICE, device,
                    CL_DEVICE_SINGLE_FP_CONFIG));
  }

  /**
   * Size of global memory cache in bytes
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getGlobalMemCacheSize(cl_device_id device)
  {
    return Infos.getLong(Infos.FOR_DEVICE, device,
                         CL_DEVICE_GLOBAL_MEM_CACHE_SIZE);
  }

  /**
   * Type of global memory cache supported
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getGlobalMemCacheType(cl_device_id device)
  {
    return Infos.getLong(Infos.FOR_DEVICE, device,
                         CL_DEVICE_GLOBAL_MEM_CACHE_TYPE);
  }

  /**
   * Size of global memory cache line in bytes
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getGlobalMemCachelineSize(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_GLOBAL_MEM_CACHELINE_SIZE);
  }

  /**
   * Size of global device memory in bytes
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getGlobalMemSize(cl_device_id device)
  {
    return Infos.getLong(Infos.FOR_DEVICE, device,
                         CL_DEVICE_GLOBAL_MEM_SIZE);
  }

  /**
   * Whether images are supported by the OpenCL device
   *
   * @param device The device
   *
   * @return The value
   */
  public static boolean getImageSupport(cl_device_id device)
  {
    return Infos.getBool(Infos.FOR_DEVICE, device,
                         CL_DEVICE_IMAGE_SUPPORT);
  }

  /**
   * Max height of 2D image in pixels
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getImage2dMaxHeight(cl_device_id device)
  {
    return Infos.getSize(Infos.FOR_DEVICE, device,
                         CL_DEVICE_IMAGE2D_MAX_HEIGHT);
  }

  /**
   * Max width of 2D image in pixels
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getImage2dMaxWidth(cl_device_id device)
  {
    return Infos.getSize(Infos.FOR_DEVICE, device,
                         CL_DEVICE_IMAGE2D_MAX_WIDTH);
  }

  /**
   * Max depth of 3D image in pixels
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getImage3dMaxDepth(cl_device_id device)
  {
    return Infos.getSize(Infos.FOR_DEVICE, device,
                         CL_DEVICE_IMAGE3D_MAX_DEPTH);
  }

  /**
   * Max height of 3D image in pixels
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getImage3dMaxHeight(cl_device_id device)
  {
    return Infos.getSize(Infos.FOR_DEVICE, device,
                         CL_DEVICE_IMAGE3D_MAX_HEIGHT);
  }

  /**
   * Max width of 3D image in pixels
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getImage3dMaxWidth(cl_device_id device)
  {
    return Infos.getSize(Infos.FOR_DEVICE, device,
                         CL_DEVICE_IMAGE3D_MAX_WIDTH);
  }

  /**
   * Size of local memory arena in bytes
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getLocalMemSize(cl_device_id device)
  {
    return Infos.getLong(Infos.FOR_DEVICE, device,
                         CL_DEVICE_LOCAL_MEM_SIZE);
  }

  /**
   * Type of local memory supported
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getLocalMemType(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_LOCAL_MEM_TYPE);
  }

  /**
   * Type of local memory supported as a String
   *
   * @param device The device
   *
   * @return The value
   */
  public static String getLocalMemTypeString(cl_device_id device)
  {
    return stringFor_cl_device_local_mem_type(
      Infos.getInt(Infos.FOR_DEVICE, device,
                   CL_DEVICE_LOCAL_MEM_TYPE));
  }

  /**
   * Max number of __constant kernel arguments
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getMaxConstantArgs(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_MAX_CONSTANT_ARGS);
  }

  /**
   * Max size in bytes of a constant buffer allocation
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getMaxConstantBufferSize(cl_device_id device)
  {
    return Infos.getLong(Infos.FOR_DEVICE, device,
                         CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE);
  }

  /**
   * Max size of memory object allocation in bytes
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getMaxMemAllocSize(cl_device_id device)
  {
    return Infos.getLong(Infos.FOR_DEVICE, device,
                         CL_DEVICE_MAX_MEM_ALLOC_SIZE);
  }

  /**
   * Max size in bytes of the kernel arguments
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getMaxParameterSize(cl_device_id device)
  {
    return Infos.getSize(Infos.FOR_DEVICE, device,
                         CL_DEVICE_MAX_PARAMETER_SIZE);
  }

  /**
   * Max number of simultaneous image objects that can be read by a kernel
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getMaxReadImageArgs(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_MAX_READ_IMAGE_ARGS);
  }

  /**
   * Maximum number of samplers that can be used in a kernel
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getMaxSamplers(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_MAX_SAMPLERS);
  }

  /**
   * Maximum number of work-items in a work-group
   *
   * @param device The device
   *
   * @return The value
   */
  public static long getMaxWorkGroupSize(cl_device_id device)
  {
    return Infos.getSize(Infos.FOR_DEVICE, device,
                         CL_DEVICE_MAX_WORK_GROUP_SIZE);
  }

  /**
   * Maximum dimensions that specify the global and local work-item IDs
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getMaxWorkItemDimensions(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS);
  }

  /**
   * Maximum number of work-items for each dimension
   *
   * @param device The device
   *
   * @return The value
   */
  public static long[] getMaxWorkItemSizes(cl_device_id device)
  {
    return Infos.getSizes(Infos.FOR_DEVICE, device,
                          CL_DEVICE_MAX_WORK_ITEM_SIZES, getMaxWorkItemDimensions(device));
  }

  /**
   * Max number of simultaneous image objects that can be written to by a kernel
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getMaxWriteImageArgs(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_MAX_WRITE_IMAGE_ARGS);
  }

  /**
   * Describes the alignment in bits of memory objects
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getMemBaseAddrAlign(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_MEM_BASE_ADDR_ALIGN);
  }

  /**
   * The smallest alignment in bytes which can be used for any data type
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getMinDataTypeAlignSize(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_MIN_DATA_TYPE_ALIGN_SIZE);
  }

  /**
   * Native ISA vector width (char)
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getNativeVectorWidthChar(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_NATIVE_VECTOR_WIDTH_CHAR);
  }

  /**
   * Native ISA vector width (short)
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getNativeVectorWidthShort(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_NATIVE_VECTOR_WIDTH_SHORT);
  }

  /**
   * Native ISA vector width (int)
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getNativeVectorWidthInt(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_NATIVE_VECTOR_WIDTH_INT);
  }

  /**
   * Native ISA vector width (long)
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getNativeVectorWidthLong(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_NATIVE_VECTOR_WIDTH_LONG);
  }

  /**
   * Native ISA vector width (float)
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getNativeVectorWidthFloat(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_NATIVE_VECTOR_WIDTH_FLOAT);
  }

  /**
   * Native ISA vector width (double)
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getNativeVectorWidthDouble(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE);
  }

  /**
   * Native ISA vector width (half)
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getNativeVectorWidthHalf(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_NATIVE_VECTOR_WIDTH_HALF);
  }

  /**
   * Preferred native vector width (char)
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getPreferredVectorWidthChar(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_PREFERRED_VECTOR_WIDTH_CHAR);
  }

  /**
   * Preferred native vector width (short)
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getPreferredVectorWidthShort(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_PREFERRED_VECTOR_WIDTH_SHORT);
  }

  /**
   * Preferred native vector width (int)
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getPreferredVectorWidthInt(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_PREFERRED_VECTOR_WIDTH_INT);
  }

  /**
   * Preferred native vector width (long)
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getPreferredVectorWidthLong(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_PREFERRED_VECTOR_WIDTH_LONG);
  }

  /**
   * Preferred native vector width (float)
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getPreferredVectorWidthFloat(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT);
  }

  /**
   * Preferred native vector width (double)
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getPreferredVectorWidthDouble(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE);
  }

  /**
   * Preferred native vector width (half)
   *
   * @param device The device
   *
   * @return The value
   */
  public static int getPreferredVectorWidthHalf(cl_device_id device)
  {
    return Infos.getInt(Infos.FOR_DEVICE, device,
                        CL_DEVICE_PREFERRED_VECTOR_WIDTH_HALF);
  }


  /**
   * Private constructor to prevent instantiation
   */
  private DeviceInfos()
  {

  }
}
