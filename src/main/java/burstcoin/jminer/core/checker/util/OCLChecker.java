/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 by luxe - https://github.com/de-luxe -  BURST-LUXE-RED2-G6JW-H4HG5
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

package burstcoin.jminer.core.checker.util;

import burstcoin.jminer.core.CoreProperties;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.jocl.CL.*;

/**
 * Org. OCLChecker code and the used openCL kernels are provided by 'burst dev'. Please donate: BURST-QHCJ-9HB5-PTGC-5Q8J9
 */
@Component
@Scope("singleton")
public class OCLChecker
{
  private static final Logger LOG = LoggerFactory.getLogger(OCLChecker.class);

  cl_context context;
  cl_command_queue queue;
  cl_program program;
  cl_kernel kernel[] = new cl_kernel[3];

  long workgroupSize[] = new long[3];
  long computeUnits;

  cl_mem gensigMem;
  cl_mem bestMem;

  @PostConstruct
  protected void postConstruct()
  {
    initChecker(CoreProperties.getPlatformId(), CoreProperties.getDeviceId());
  }

  public void initChecker(int platformId, int deviceId)
  {
    CL.setExceptionsEnabled(true);

    int numPlatforms[] = new int[1];
    clGetPlatformIDs(0, null, numPlatforms);

    if(platformId >= numPlatforms[0])
    {
      throw new ArrayIndexOutOfBoundsException("Invalid platform id");
    }

    cl_platform_id platforms[] = new cl_platform_id[numPlatforms[0]];
    clGetPlatformIDs(platforms.length, platforms, null);

    int[] numDevices = new int[1];
    clGetDeviceIDs(platforms[platformId], CL_DEVICE_TYPE_GPU, 0, null, numDevices);

    if(deviceId >= numDevices[0])
    {
      throw new ArrayIndexOutOfBoundsException("Invalid device id");
    }

    cl_device_id devices[] = new cl_device_id[numDevices[0]];
    clGetDeviceIDs(platforms[platformId], CL_DEVICE_TYPE_GPU, devices.length, devices, null);

    cl_context_properties contextProperties = new cl_context_properties();
    contextProperties.addProperty(CL_CONTEXT_PLATFORM, platforms[platformId]);

    context = clCreateContext(contextProperties, 1, new cl_device_id[]{devices[deviceId]}, null, null, null);

    queue = clCreateCommandQueue(context, devices[deviceId], 0, null);

    String kernelSource;
    try
    {
      InputStream inputStream = OCLChecker.class.getResourceAsStream("calcdeadlines.cl");
      kernelSource = readInputStreamAsString(inputStream);
      inputStream.close();
    }
    catch(IOException e)
    {
      throw new RuntimeException("Failed to read calcdeadlines.cl file", e);
    }

    program = clCreateProgramWithSource(context, 1, new String[]{kernelSource}, null, null);
    clBuildProgram(program, 0, null, "-I kernel", null, null);

    kernel[0] = clCreateKernel(program, "calculate_deadlines", null);
    kernel[1] = clCreateKernel(program, "reduce_best", null);
    kernel[2] = clCreateKernel(program, "reduce_target", null);

    long[] maxWorkGroupSize = new long[1];
    for(int i = 0; i < 3; i++)
    {
      clGetKernelWorkGroupInfo(kernel[i], devices[deviceId], CL_KERNEL_WORK_GROUP_SIZE, 8, Pointer.to(maxWorkGroupSize), null);
      LOG.debug("Max work group size: " + maxWorkGroupSize[0]);
      workgroupSize[i] = maxWorkGroupSize[0];
    }

    long[] maxComputeUnits = new long[1];
    clGetDeviceInfo(devices[deviceId], CL_DEVICE_MAX_COMPUTE_UNITS, 8, Pointer.to(maxComputeUnits), null);
    LOG.debug("Max compute units: " + maxComputeUnits[0]);

    computeUnits = maxComputeUnits[0];

    gensigMem = clCreateBuffer(context, CL_MEM_READ_ONLY, 32, null, null);
    bestMem = clCreateBuffer(context, CL_MEM_WRITE_ONLY, 400, null, null); // org 400 // tested 5000

    LOG.info("success ... started openCL context!");
  }

  public void reset(int platformId, int deviceId)
  {
    CL.clReleaseContext(context);
    initChecker(platformId, deviceId);
  }

  public int findLowest(byte[] gensig, byte[] data)
  {
    cl_mem dataMem;
    cl_mem deadlineMem;

    long numNonces = data.length / 64;
    long calcWorkgroups = numNonces / workgroupSize[0];
    // thx blago
    if(numNonces % workgroupSize[0] != 0) // if(numNonces % 64 != 0) // org.
    {
      calcWorkgroups++;
    }
    clEnqueueWriteBuffer(queue, gensigMem, false, 0, 32, Pointer.to(gensig), 0, null, null);
    dataMem = clCreateBuffer(context, CL_MEM_READ_ONLY, calcWorkgroups * workgroupSize[0] * 64, null, null);
    clEnqueueWriteBuffer(queue, dataMem, false, 0, data.length, Pointer.to(data), 0, null, null);
    deadlineMem = clCreateBuffer(context, CL_MEM_READ_WRITE, calcWorkgroups * workgroupSize[0] * 8, null, null);
    clSetKernelArg(kernel[0], 0, Sizeof.cl_mem, Pointer.to(gensigMem));
    clSetKernelArg(kernel[0], 1, Sizeof.cl_mem, Pointer.to(dataMem));
    clSetKernelArg(kernel[0], 2, Sizeof.cl_mem, Pointer.to(deadlineMem));
    clEnqueueNDRangeKernel(queue, kernel[0], 1, null, new long[]{calcWorkgroups * workgroupSize[0]}, new long[]{workgroupSize[0]}, 0, null, null);
    clSetKernelArg(kernel[1], 0, Sizeof.cl_mem, Pointer.to(deadlineMem));
    long len[] = {data.length / 64};
    clSetKernelArg(kernel[1], 1, Sizeof.cl_uint, Pointer.to(len));
    clSetKernelArg(kernel[1], 2, Sizeof.cl_uint * workgroupSize[1], null);
    clSetKernelArg(kernel[1], 3, Sizeof.cl_ulong * workgroupSize[1], null);
    clSetKernelArg(kernel[1], 4, Sizeof.cl_mem, Pointer.to(bestMem));
    clEnqueueNDRangeKernel(queue, kernel[1], 1, null, new long[]{workgroupSize[1]}, new long[]{workgroupSize[1]}, 0, null, null);
    int best[] = new int[1];
    clEnqueueReadBuffer(queue, bestMem, true, 0, 4, Pointer.to(best), 0, null, null);
    clReleaseMemObject(dataMem);
    clReleaseMemObject(deadlineMem);
    return best[0];
  }

  public int[] findTarget(byte[] gensig, byte[] data, long target)
  {
    long numNonces = data.length / 64;
    long calcWorkgroups = numNonces / workgroupSize[0];
    // thx blago
    if(numNonces % workgroupSize[0] != 0) // org. if(numNonces % 64 != 0)
    {
      calcWorkgroups++;
    }
    clEnqueueWriteBuffer(queue, gensigMem, false, 0, 32, Pointer.to(gensig), 0, null, null);
    cl_mem dataMem = clCreateBuffer(context, CL_MEM_READ_ONLY, calcWorkgroups * workgroupSize[0] * 64, null, null);
    clEnqueueWriteBuffer(queue, dataMem, false, 0, data.length, Pointer.to(data), 0, null, null);
    cl_mem deadlineMem = clCreateBuffer(context, CL_MEM_READ_WRITE, calcWorkgroups * workgroupSize[0] * 8, null, null);
    clSetKernelArg(kernel[0], 0, Sizeof.cl_mem, Pointer.to(gensigMem));
    clSetKernelArg(kernel[0], 1, Sizeof.cl_mem, Pointer.to(dataMem));
    clSetKernelArg(kernel[0], 2, Sizeof.cl_mem, Pointer.to(deadlineMem));
    clEnqueueNDRangeKernel(queue, kernel[0], 1, null, new long[]{calcWorkgroups * workgroupSize[0]}, new long[]{workgroupSize[0]}, 0, null, null);
    int best[] = new int[]{0};
    clEnqueueWriteBuffer(queue, bestMem, false, 0, 4, Pointer.to(best), 0, null, null);
    clSetKernelArg(kernel[2], 0, Sizeof.cl_mem, Pointer.to(deadlineMem));
    long len[] = {data.length / 64};
    clSetKernelArg(kernel[2], 1, Sizeof.cl_uint, Pointer.to(len));
    long targetPtr[] = new long[]{target};
    clSetKernelArg(kernel[2], 2, Sizeof.cl_ulong, Pointer.to(targetPtr));
    clSetKernelArg(kernel[2], 3, Sizeof.cl_mem, Pointer.to(bestMem));
    clEnqueueNDRangeKernel(queue, kernel[2], 1, null, new long[]{workgroupSize[2]}, new long[]{workgroupSize[2]}, 0, null, null);
    clEnqueueReadBuffer(queue, bestMem, true, 0, 4, Pointer.to(best), 0, null, null);
    if(best[0] == 0)
    {
      return null;
    }
    int bestVals[] = new int[best[0]];
    clEnqueueReadBuffer(queue, bestMem, true, 4, 4 * best[0], Pointer.to(bestVals), 0, null, null);
    clReleaseMemObject(dataMem);
    clReleaseMemObject(deadlineMem);
    return bestVals;
  }

  public static String readInputStreamAsString(InputStream in)
    throws IOException
  {
    BufferedInputStream bis = new BufferedInputStream(in);
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    int result = bis.read();
    while(result != -1)
    {
      byte b = (byte) result;
      buf.write(b);
      result = bis.read();
    }
    return buf.toString();
  }
}
