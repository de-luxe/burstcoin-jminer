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
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_device_id;
import org.jocl.cl_event;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.List;

import static org.jocl.CL.*;

/**
 * Utility methods related to cl_command_queue objects
 */
public class CommandQueues
{
  /**
   * Create and return a command queue for the given device.
   *
   * @param context The context
   * @param device  The device
   *
   * @return The command queue
   */
  public static cl_command_queue create(
    cl_context context, cl_device_id device)
  {
    return create(context, device, 0);
  }

  /**
   * Create and return a command queue for the given device.
   *
   * @param context          The context
   * @param device           The device
   * @param profilingEnabled Whether profiling should be enabled
   *
   * @return The command queue
   */
  public static cl_command_queue create(
    cl_context context, cl_device_id device, boolean profilingEnabled)
  {
    return create(context, device,
                  (profilingEnabled ? CL_QUEUE_PROFILING_ENABLE : 0));
  }


  /**
   * Create and return a command queue for the given device.
   *
   * @param context    The context
   * @param device     The device
   * @param properties The command queue properties
   *
   * @return The command queue
   */
  private static cl_command_queue create(
    cl_context context, cl_device_id device, long properties)
  {
    return clCreateCommandQueue(
      context, device, properties, null);
  }

  /**
   * Create and return an unmodifiable list of command queues, one for each of the given devices.
   *
   * @param context The context
   * @param devices The devices
   *
   * @return The list of command queues
   */
  public static List<cl_command_queue> create(
    cl_context context, List<cl_device_id> devices)
  {
    return create(context, devices, 0);
  }

  /**
   * Create and return an unmodifiable list of command queues, one for each of the given devices.
   *
   * @param context          The context
   * @param devices          The devices
   * @param profilingEnabled Whether profiling should be enabled
   *
   * @return The list of command queues
   */
  public static List<cl_command_queue> create(
    cl_context context, List<cl_device_id> devices, boolean profilingEnabled)
  {
    return create(context, devices,
                  (profilingEnabled ? CL_QUEUE_PROFILING_ENABLE : 0));
  }


  /**
   * Create and return an unmodifiable list of command queues, one for each of the given devices.
   *
   * @param context    The context
   * @param devices    The devices
   * @param properties The command queue properties
   *
   * @return The list of command queues
   */
  private static List<cl_command_queue> create(
    cl_context context, List<cl_device_id> devices, long properties)
  {
    cl_command_queue commandQueues[] =
      new cl_command_queue[devices.size()];
    for(int i = 0; i < devices.size(); i++)
    {
      commandQueues[i] =
        clCreateCommandQueue(
          context, devices.get(i), properties, null);
    }
    return Arrays.asList(commandQueues);
  }

  /**
   * Release each of the given command queues if it is not <code>null</code>.
   *
   * @param commandQueues The command queues to release
   */
  public static void release(cl_command_queue... commandQueues)
  {
    if(commandQueues != null)
    {
      release(Arrays.asList(commandQueues));
    }
  }

  /**
   * Release each of the given command queues if it is not <code>null</code>.
   *
   * @param commandQueues The command queues to release
   */
  public static void release(Iterable<cl_command_queue> commandQueues)
  {
    if(commandQueues != null)
    {
      for(cl_command_queue commandQueue : commandQueues)
      {
        if(commandQueue != null)
        {
          clReleaseCommandQueue(commandQueue);
        }
      }
    }
  }


  /**
   * Finish each of the given command queues if it is not <code>null</code>.
   *
   * @param commandQueues The command queues to finish
   */
  public static void finish(cl_command_queue... commandQueues)
  {
    if(commandQueues != null)
    {
      finish(Arrays.asList(commandQueues));
    }
  }

  /**
   * Finish each of the given command queues if it is not <code>null</code>.
   *
   * @param commandQueues The command queues to finish
   */
  public static void finish(Iterable<cl_command_queue> commandQueues)
  {
    if(commandQueues != null)
    {
      for(cl_command_queue commandQueue : commandQueues)
      {
        if(commandQueue != null)
        {
          clFinish(commandQueue);
        }
      }
    }
  }

  //=== Kernel enqueueing ==================================================

  /**
   * Enqueue the given kernel as a 1D kernel to the given command queue, using the given global work size.
   *
   * @param commandQueue The command queue
   * @param kernel       The kernel to enqueue
   * @param globalSizeX  The global work size
   */
  public static void enqueueKernel1D(
    cl_command_queue commandQueue, cl_kernel kernel,
    long globalSizeX)
  {
    clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                           new long[]{globalSizeX},
                           null,
                           0, null, null);
  }

  /**
   * Enqueue the given kernel as a 1D kernel to the given command queue, using the given global and local work size.
   *
   * @param commandQueue The command queue
   * @param kernel       The kernel to enqueue
   * @param globalSizeX  The global work size
   * @param localSizeX   The local work size
   */
  public static void enqueueKernel1D(
    cl_command_queue commandQueue, cl_kernel kernel,
    long globalSizeX, long localSizeX)
  {
    clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                           new long[]{globalSizeX},
                           new long[]{localSizeX},
                           0, null, null);
  }

  /**
   * Enqueue the given kernel as a 2D kernel to the given command queue, using the given global work sizes.
   *
   * @param commandQueue The command queue
   * @param kernel       The kernel to enqueue
   * @param globalSizeX  The global work size for dimension 0
   * @param globalSizeY  The global work size for dimension 1
   */
  public static void enqueueKernel2D(
    cl_command_queue commandQueue, cl_kernel kernel,
    long globalSizeX, long globalSizeY)
  {
    clEnqueueNDRangeKernel(commandQueue, kernel, 2, null,
                           new long[]{globalSizeX, globalSizeY},
                           null,
                           0, null, null);
  }

  /**
   * Enqueue the given kernel as a 2D kernel to the given command queue, using the given global and local work sizes.
   *
   * @param commandQueue The command queue
   * @param kernel       The kernel to enqueue
   * @param globalSizeX  The global work size for dimension 0
   * @param globalSizeY  The global work size for dimension 1
   * @param localSizeX   The local work size for dimension 0
   * @param localSizeY   The local work size for dimension 1
   */
  public static void enqueueKernel2D(
    cl_command_queue commandQueue, cl_kernel kernel,
    long globalSizeX, long globalSizeY,
    long localSizeX, long localSizeY)
  {
    clEnqueueNDRangeKernel(commandQueue, kernel, 2, null,
                           new long[]{globalSizeX, globalSizeY},
                           new long[]{localSizeX, localSizeY},
                           0, null, null);
  }

  /**
   * Enqueue the given kernel as a 3D kernel to the given command queue, using the given global work sizes.
   *
   * @param commandQueue The command queue
   * @param kernel       The kernel to enqueue
   * @param globalSizeX  The global work size for dimension 0
   * @param globalSizeY  The global work size for dimension 1
   * @param globalSizeZ  The global work size for dimension 2
   */
  public static void enqueueKernel3D(
    cl_command_queue commandQueue, cl_kernel kernel,
    long globalSizeX, long globalSizeY, long globalSizeZ)
  {
    clEnqueueNDRangeKernel(commandQueue, kernel, 3, null,
                           new long[]{globalSizeX, globalSizeY, globalSizeZ},
                           null,
                           0, null, null);
  }

  /**
   * Enqueue the given kernel as a 3D kernel to the given command queue, using the given global and local work sizes.
   *
   * @param commandQueue The command queue
   * @param kernel       The kernel to enqueue
   * @param globalSizeX  The global work size for dimension 0
   * @param globalSizeY  The global work size for dimension 1
   * @param globalSizeZ  The global work size for dimension 2
   * @param localSizeX   The local work size for dimension 0
   * @param localSizeY   The local work size for dimension 1
   * @param localSizeZ   The local work size for dimension 2
   */
  public static void enqueueKernel3D(
    cl_command_queue commandQueue, cl_kernel kernel,
    long globalSizeX, long globalSizeY, long globalSizeZ,
    long localSizeX, long localSizeY, long localSizeZ)
  {
    clEnqueueNDRangeKernel(commandQueue, kernel, 3, null,
                           new long[]{globalSizeX, globalSizeY, globalSizeZ},
                           new long[]{localSizeX, localSizeY, localSizeZ},
                           0, null, null);
  }

  /**
   * Enqueue the given kernel as a n-D kernel to the given command queue, using the given global and local work sizes.
   *
   * @param commandQueue The command queue
   * @param kernel       The kernel to enqueue
   * @param n            The dimension of the kernel
   * @param globalSize   The global work size
   * @param localSize    The local work size
   */
  public static void enqueueKernelND(
    cl_command_queue commandQueue, cl_kernel kernel, int n,
    long globalSize[], long localSize[])
  {
    clEnqueueNDRangeKernel(commandQueue, kernel, n, null,
                           globalSize,
                           localSize,
                           0, null, null);
  }


  //=== Kernel enqueueing with events ======================================

  /**
   * Enqueue the given kernel as a 1D kernel to the given command queue, using the given global work size.
   *
   * @param commandQueue The command queue
   * @param kernel       The kernel to enqueue
   * @param globalSizeX  The global work size
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueKernel1D(
    cl_command_queue commandQueue, cl_kernel kernel,
    long globalSizeX,
    List<cl_event> waitList, cl_event event)
  {
    clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                           new long[]{globalSizeX},
                           null,
                           sizeOf(waitList), asArray(waitList), event);
  }

  /**
   * Enqueue the given kernel as a 1D kernel to the given command queue, using the given global and local work size.
   *
   * @param commandQueue The command queue
   * @param kernel       The kernel to enqueue
   * @param globalSizeX  The global work size
   * @param localSizeX   The local work size
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueKernel1D(
    cl_command_queue commandQueue, cl_kernel kernel,
    long globalSizeX, long localSizeX,
    List<cl_event> waitList, cl_event event)
  {
    clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                           new long[]{globalSizeX},
                           new long[]{localSizeX},
                           sizeOf(waitList), asArray(waitList), event);
  }

  /**
   * Enqueue the given kernel as a 2D kernel to the given command queue, using the given global work sizes.
   *
   * @param commandQueue The command queue
   * @param kernel       The kernel to enqueue
   * @param globalSizeX  The global work size for dimension 0
   * @param globalSizeY  The global work size for dimension 1
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueKernel2D(
    cl_command_queue commandQueue, cl_kernel kernel,
    long globalSizeX, long globalSizeY,
    List<cl_event> waitList, cl_event event)
  {
    clEnqueueNDRangeKernel(commandQueue, kernel, 2, null,
                           new long[]{globalSizeX, globalSizeY},
                           null,
                           sizeOf(waitList), asArray(waitList), event);
  }

  /**
   * Enqueue the given kernel as a 2D kernel to the given command queue, using the given global and local work sizes.
   *
   * @param commandQueue The command queue
   * @param kernel       The kernel to enqueue
   * @param globalSizeX  The global work size for dimension 0
   * @param globalSizeY  The global work size for dimension 1
   * @param localSizeX   The local work size for dimension 0
   * @param localSizeY   The local work size for dimension 1
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueKernel2D(
    cl_command_queue commandQueue, cl_kernel kernel,
    long globalSizeX, long globalSizeY,
    long localSizeX, long localSizeY,
    List<cl_event> waitList, cl_event event)
  {
    clEnqueueNDRangeKernel(commandQueue, kernel, 2, null,
                           new long[]{globalSizeX, globalSizeY},
                           new long[]{localSizeX, localSizeY},
                           sizeOf(waitList), asArray(waitList), event);
  }

  /**
   * Enqueue the given kernel as a 3D kernel to the given command queue, using the given global work sizes.
   *
   * @param commandQueue The command queue
   * @param kernel       The kernel to enqueue
   * @param globalSizeX  The global work size for dimension 0
   * @param globalSizeY  The global work size for dimension 1
   * @param globalSizeZ  The global work size for dimension 2
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueKernel3D(
    cl_command_queue commandQueue, cl_kernel kernel,
    long globalSizeX, long globalSizeY, long globalSizeZ,
    List<cl_event> waitList, cl_event event)
  {
    clEnqueueNDRangeKernel(commandQueue, kernel, 3, null,
                           new long[]{globalSizeX, globalSizeY, globalSizeZ},
                           null,
                           sizeOf(waitList), asArray(waitList), event);
  }

  /**
   * Enqueue the given kernel as a 3D kernel to the given command queue, using the given global and local work sizes.
   *
   * @param commandQueue The command queue
   * @param kernel       The kernel to enqueue
   * @param globalSizeX  The global work size for dimension 0
   * @param globalSizeY  The global work size for dimension 1
   * @param globalSizeZ  The global work size for dimension 2
   * @param localSizeX   The local work size for dimension 0
   * @param localSizeY   The local work size for dimension 1
   * @param localSizeZ   The local work size for dimension 2
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueKernel3D(
    cl_command_queue commandQueue, cl_kernel kernel,
    long globalSizeX, long globalSizeY, long globalSizeZ,
    long localSizeX, long localSizeY, long localSizeZ,
    List<cl_event> waitList, cl_event event)
  {
    clEnqueueNDRangeKernel(commandQueue, kernel, 3, null,
                           new long[]{globalSizeX, globalSizeY, globalSizeZ},
                           new long[]{localSizeX, localSizeY, localSizeZ},
                           sizeOf(waitList), asArray(waitList), event);
  }

  /**
   * Enqueue the given kernel as a n-D kernel to the given command queue, using the given global and local work sizes.
   *
   * @param commandQueue The command queue
   * @param kernel       The kernel to enqueue
   * @param n            The dimension of the kernel
   * @param globalSize   The global work size
   * @param localSize    The local work size. May be <code>null</code>.
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueKernelND(
    cl_command_queue commandQueue, cl_kernel kernel, int n,
    long globalSize[], long localSize[],
    List<cl_event> waitList, cl_event event)
  {
    clEnqueueNDRangeKernel(commandQueue, kernel, n, null,
                           globalSize,
                           localSize,
                           0, null, null);
    //sizeOf(waitList), asArray(waitList), event);
  }


  //=== Read/write buffers (char) ==========================================

  /**
   * Enqueue a command to read the given buffer into the given target Buffer. This command will be blocking if and only if the target Buffer is a <b>not</b> a
   * <i>direct</i> buffer. The size of the data transfer will be computed from the position and limit of the given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The source buffer
   * @param target       The target Buffer
   */
  public static void enqueueReadBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    ByteBuffer target)
  {
    enqueueReadBuffer(commandQueue, buffer, 0, target,
                      !target.isDirect(), null, null);
  }

  /**
   * Enqueue a command to read the given buffer into the given target Buffer. The size of the data transfer will be computed from the position and limit of the
   * given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The source buffer
   * @param sourceOffset The offset (in number of elements) in the source buffer
   * @param target       The target Buffer
   * @param blocking     Whether the transfer should be blocking
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueReadBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    long sourceOffset,
    ByteBuffer target,
    boolean blocking,
    List<cl_event> waitList, cl_event event)
  {
    Pointer targetPointer = PointerUtils.toBuffer(target);
    clEnqueueReadBuffer(commandQueue, buffer, blocking,
                        sourceOffset * Sizeof.cl_char,
                        target.remaining() * Sizeof.cl_char, targetPointer,
                        sizeOf(waitList), asArray(waitList), event);
  }


  /**
   * Enqueue a command to write the given buffer from the given source Buffer. This command will be blocking if and only if the source Buffer is a <b>not</b> a
   * <i>direct</i> buffer. The size of the data transfer will be computed from the position and limit of the given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The target buffer
   * @param source       The source Buffer
   */
  public static void enqueueWriteBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    ByteBuffer source)
  {
    enqueueWriteBuffer(commandQueue, buffer, 0, source,
                       !source.isDirect(), null, null);
  }

  /**
   * Enqueue a command to write the given buffer from the given source Buffer. The size of the data transfer will be computed from the position and limit of the
   * given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The target buffer
   * @param targetOffset The offset (in number of elements) in the target buffer
   * @param source       The source Buffer
   * @param blocking     Whether the transfer should be blocking
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueWriteBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    long targetOffset,
    ByteBuffer source,
    boolean blocking,
    List<cl_event> waitList, cl_event event)
  {
    Pointer sourcePointer = PointerUtils.toBuffer(source);
    clEnqueueWriteBuffer(commandQueue, buffer, blocking,
                         targetOffset * Sizeof.cl_char,
                         source.remaining() * Sizeof.cl_char, sourcePointer,
                         sizeOf(waitList), asArray(waitList), event);
  }


  //=== Read/write buffers (short) ==========================================

  /**
   * Enqueue a command to read the given buffer into the given target Buffer. This command will be blocking if and only if the target Buffer is a <b>not</b> a
   * <i>direct</i> buffer. The size of the data transfer will be computed from the position and limit of the given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The source buffer
   * @param target       The target Buffer
   */
  public static void enqueueReadBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    ShortBuffer target)
  {
    enqueueReadBuffer(commandQueue, buffer, 0, target,
                      !target.isDirect(), null, null);
  }

  /**
   * Enqueue a command to read the given buffer into the given target Buffer. The size of the data transfer will be computed from the position and limit of the
   * given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The source buffer
   * @param sourceOffset The offset (in number of elements) in the source buffer
   * @param target       The target Buffer
   * @param blocking     Whether the transfer should be blocking
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueReadBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    long sourceOffset,
    ShortBuffer target,
    boolean blocking,
    List<cl_event> waitList, cl_event event)
  {
    Pointer targetPointer = PointerUtils.toBuffer(target);
    clEnqueueReadBuffer(commandQueue, buffer, blocking,
                        sourceOffset * Sizeof.cl_short,
                        target.remaining() * Sizeof.cl_short, targetPointer,
                        sizeOf(waitList), asArray(waitList), event);
  }


  /**
   * Enqueue a command to write the given buffer from the given source Buffer. This command will be blocking if and only if the source Buffer is a <b>not</b> a
   * <i>direct</i> buffer. The size of the data transfer will be computed from the position and limit of the given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The target buffer
   * @param source       The source Buffer
   */
  public static void enqueueWriteBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    ShortBuffer source)
  {
    enqueueWriteBuffer(commandQueue, buffer, 0, source,
                       !source.isDirect(), null, null);
  }

  /**
   * Enqueue a command to write the given buffer from the given source Buffer. The size of the data transfer will be computed from the position and limit of the
   * given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The target buffer
   * @param targetOffset The offset (in number of elements) in the target buffer
   * @param source       The source Buffer
   * @param blocking     Whether the transfer should be blocking
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueWriteBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    long targetOffset,
    ShortBuffer source,
    boolean blocking,
    List<cl_event> waitList, cl_event event)
  {
    Pointer sourcePointer = PointerUtils.toBuffer(source);
    clEnqueueWriteBuffer(commandQueue, buffer, blocking,
                         targetOffset * Sizeof.cl_short,
                         source.remaining() * Sizeof.cl_short, sourcePointer,
                         sizeOf(waitList), asArray(waitList), event);
  }


  //=== Read/write buffers (int) ==========================================

  /**
   * Enqueue a command to read the given buffer into the given target Buffer. This command will be blocking if and only if the target Buffer is a <b>not</b> a
   * <i>direct</i> buffer. The size of the data transfer will be computed from the position and limit of the given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The source buffer
   * @param target       The target Buffer
   */
  public static void enqueueReadBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    IntBuffer target)
  {
    enqueueReadBuffer(commandQueue, buffer, 0, target,
                      !target.isDirect(), null, null);
  }

  /**
   * Enqueue a command to read the given buffer into the given target Buffer. The size of the data transfer will be computed from the position and limit of the
   * given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The source buffer
   * @param sourceOffset The offset (in number of elements) in the source buffer
   * @param target       The target Buffer
   * @param blocking     Whether the transfer should be blocking
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueReadBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    long sourceOffset,
    IntBuffer target,
    boolean blocking,
    List<cl_event> waitList, cl_event event)
  {
    Pointer targetPointer = PointerUtils.toBuffer(target);
    clEnqueueReadBuffer(commandQueue, buffer, blocking,
                        sourceOffset * Sizeof.cl_int,
                        target.remaining() * Sizeof.cl_int, targetPointer,
                        sizeOf(waitList), asArray(waitList), event);
  }


  /**
   * Enqueue a command to write the given buffer from the given source Buffer. This command will be blocking if and only if the source Buffer is a <b>not</b> a
   * <i>direct</i> buffer. The size of the data transfer will be computed from the position and limit of the given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The target buffer
   * @param source       The source Buffer
   */
  public static void enqueueWriteBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    IntBuffer source)
  {
    enqueueWriteBuffer(commandQueue, buffer, 0, source,
                       !source.isDirect(), null, null);
  }

  /**
   * Enqueue a command to write the given buffer from the given source Buffer. The size of the data transfer will be computed from the position and limit of the
   * given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The target buffer
   * @param targetOffset The offset (in number of elements) in the target buffer
   * @param source       The source Buffer
   * @param blocking     Whether the transfer should be blocking
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueWriteBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    long targetOffset,
    IntBuffer source,
    boolean blocking,
    List<cl_event> waitList, cl_event event)
  {
    Pointer sourcePointer = PointerUtils.toBuffer(source);
    clEnqueueWriteBuffer(commandQueue, buffer, blocking,
                         targetOffset * Sizeof.cl_int,
                         source.remaining() * Sizeof.cl_int, sourcePointer,
                         sizeOf(waitList), asArray(waitList), event);
  }


  //=== Read/write buffers (long) ==========================================

  /**
   * Enqueue a command to read the given buffer into the given target Buffer. This command will be blocking if and only if the target Buffer is a <b>not</b> a
   * <i>direct</i> buffer. The size of the data transfer will be computed from the position and limit of the given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The source buffer
   * @param target       The target Buffer
   */
  public static void enqueueReadBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    LongBuffer target)
  {
    enqueueReadBuffer(commandQueue, buffer, 0, target,
                      !target.isDirect(), null, null);
  }

  /**
   * Enqueue a command to read the given buffer into the given target Buffer. The size of the data transfer will be computed from the position and limit of the
   * given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The source buffer
   * @param sourceOffset The offset (in number of elements) in the source buffer
   * @param target       The target Buffer
   * @param blocking     Whether the transfer should be blocking
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueReadBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    long sourceOffset,
    LongBuffer target,
    boolean blocking,
    List<cl_event> waitList, cl_event event)
  {
    Pointer targetPointer = PointerUtils.toBuffer(target);
    clEnqueueReadBuffer(commandQueue, buffer, blocking,
                        sourceOffset * Sizeof.cl_long,
                        target.remaining() * Sizeof.cl_long, targetPointer,
                        sizeOf(waitList), asArray(waitList), event);
  }


  /**
   * Enqueue a command to write the given buffer from the given source Buffer. This command will be blocking if and only if the source Buffer is a <b>not</b> a
   * <i>direct</i> buffer. The size of the data transfer will be computed from the position and limit of the given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The target buffer
   * @param source       The source Buffer
   */
  public static void enqueueWriteBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    LongBuffer source)
  {
    enqueueWriteBuffer(commandQueue, buffer, 0, source,
                       !source.isDirect(), null, null);
  }

  /**
   * Enqueue a command to write the given buffer from the given source Buffer. The size of the data transfer will be computed from the position and limit of the
   * given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The target buffer
   * @param targetOffset The offset (in number of elements) in the target buffer
   * @param source       The source Buffer
   * @param blocking     Whether the transfer should be blocking
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueWriteBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    long targetOffset,
    LongBuffer source,
    boolean blocking,
    List<cl_event> waitList, cl_event event)
  {
    Pointer sourcePointer = PointerUtils.toBuffer(source);
    clEnqueueWriteBuffer(commandQueue, buffer, blocking,
                         targetOffset * Sizeof.cl_long,
                         source.remaining() * Sizeof.cl_long, sourcePointer,
                         sizeOf(waitList), asArray(waitList), event);
  }


  //=== Read/write buffers (float) ==========================================

  /**
   * Enqueue a command to read the given buffer into the given target Buffer. This command will be blocking if and only if the target Buffer is a <b>not</b> a
   * <i>direct</i> buffer. The size of the data transfer will be computed from the position and limit of the given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The source buffer
   * @param target       The target Buffer
   */
  public static void enqueueReadBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    FloatBuffer target)
  {
    enqueueReadBuffer(commandQueue, buffer, 0, target,
                      !target.isDirect(), null, null);
  }

  /**
   * Enqueue a command to read the given buffer into the given target Buffer. The size of the data transfer will be computed from the position and limit of the
   * given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The source buffer
   * @param sourceOffset The offset (in number of elements) in the source buffer
   * @param target       The target Buffer
   * @param blocking     Whether the transfer should be blocking
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueReadBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    long sourceOffset,
    FloatBuffer target,
    boolean blocking,
    List<cl_event> waitList, cl_event event)
  {
    Pointer targetPointer = PointerUtils.toBuffer(target);
    clEnqueueReadBuffer(commandQueue, buffer, blocking,
                        sourceOffset * Sizeof.cl_float,
                        target.remaining() * Sizeof.cl_float, targetPointer,
                        sizeOf(waitList), asArray(waitList), event);
  }


  /**
   * Enqueue a command to write the given buffer from the given source Buffer. This command will be blocking if and only if the source Buffer is a <b>not</b> a
   * <i>direct</i> buffer. The size of the data transfer will be computed from the position and limit of the given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The target buffer
   * @param source       The source Buffer
   */
  public static void enqueueWriteBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    FloatBuffer source)
  {
    enqueueWriteBuffer(commandQueue, buffer, 0, source,
                       !source.isDirect(), null, null);
  }

  /**
   * Enqueue a command to write the given buffer from the given source Buffer. The size of the data transfer will be computed from the position and limit of the
   * given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The target buffer
   * @param targetOffset The offset (in number of elements) in the target buffer
   * @param source       The source Buffer
   * @param blocking     Whether the transfer should be blocking
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueWriteBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    long targetOffset,
    FloatBuffer source,
    boolean blocking,
    List<cl_event> waitList, cl_event event)
  {
    Pointer sourcePointer = PointerUtils.toBuffer(source);
    clEnqueueWriteBuffer(commandQueue, buffer, blocking,
                         targetOffset * Sizeof.cl_float,
                         source.remaining() * Sizeof.cl_float, sourcePointer,
                         sizeOf(waitList), asArray(waitList), event);
  }


  //=== Read/write buffers (double) ==========================================

  /**
   * Enqueue a command to read the given buffer into the given target Buffer. This command will be blocking if and only if the target Buffer is a <b>not</b> a
   * <i>direct</i> buffer. The size of the data transfer will be computed from the position and limit of the given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The source buffer
   * @param target       The target Buffer
   */
  public static void enqueueReadBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    DoubleBuffer target)
  {
    enqueueReadBuffer(commandQueue, buffer, 0, target,
                      !target.isDirect(), null, null);
  }

  /**
   * Enqueue a command to read the given buffer into the given target Buffer. The size of the data transfer will be computed from the position and limit of the
   * given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The source buffer
   * @param sourceOffset The offset (in number of elements) in the source buffer
   * @param target       The target Buffer
   * @param blocking     Whether the transfer should be blocking
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueReadBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    long sourceOffset,
    DoubleBuffer target,
    boolean blocking,
    List<cl_event> waitList, cl_event event)
  {
    Pointer targetPointer = PointerUtils.toBuffer(target);
    clEnqueueReadBuffer(commandQueue, buffer, blocking,
                        sourceOffset * Sizeof.cl_double,
                        target.remaining() * Sizeof.cl_double, targetPointer,
                        sizeOf(waitList), asArray(waitList), event);
  }


  /**
   * Enqueue a command to write the given buffer from the given source Buffer. This command will be blocking if and only if the source Buffer is a <b>not</b> a
   * <i>direct</i> buffer. The size of the data transfer will be computed from the position and limit of the given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The target buffer
   * @param source       The source Buffer
   */
  public static void enqueueWriteBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    DoubleBuffer source)
  {
    enqueueWriteBuffer(commandQueue, buffer, 0, source,
                       !source.isDirect(), null, null);
  }

  /**
   * Enqueue a command to write the given buffer from the given source Buffer. The size of the data transfer will be computed from the position and limit of the
   * given Buffer.
   *
   * @param commandQueue The command queue
   * @param buffer       The target buffer
   * @param targetOffset The offset (in number of elements) in the target buffer
   * @param source       The source Buffer
   * @param blocking     Whether the transfer should be blocking
   * @param waitList     The event wait list. May be <code>null</code>.
   * @param event        The event for this command. May be <code>null</code>.
   */
  public static void enqueueWriteBuffer(
    cl_command_queue commandQueue,
    cl_mem buffer,
    long targetOffset,
    DoubleBuffer source,
    boolean blocking,
    List<cl_event> waitList, cl_event event)
  {
    Pointer sourcePointer = PointerUtils.toBuffer(source);
    clEnqueueWriteBuffer(commandQueue, buffer, blocking,
                         targetOffset * Sizeof.cl_double,
                         source.remaining() * Sizeof.cl_double, sourcePointer,
                         sizeOf(waitList), asArray(waitList), event);
  }


  //========================================================================


  /**
   * Returns the size of the given wait list, or 0 if it is <code>null</code>.
   *
   * @param waitList The wait list
   *
   * @return The size of the wait list
   */
  private static int sizeOf(List<cl_event> waitList)
  {
    if(waitList == null)
    {
      return 0;
    }
    return waitList.size();
  }

  /**
   * Returns the array representation of the given wait list, which is <code>null</code> if the given list is <code>null</code> or has length 0, or the elements
   * of the given list otherwise.
   *
   * @param waitList The wait list
   *
   * @return The array for the given list
   */
  private static cl_event[] asArray(List<cl_event> waitList)
  {
    if(waitList == null)
    {
      return null;
    }
    if(waitList.size() == 0)
    {
      return null;
    }
    return waitList.toArray(new cl_event[waitList.size()]);
  }


  /**
   * Private constructor to prevent instantiation
   */
  private CommandQueues()
  {
  }

}
