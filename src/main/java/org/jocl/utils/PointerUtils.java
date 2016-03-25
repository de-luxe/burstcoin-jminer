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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

/**
 * Package-private class that offers the methods that are otherwise only available in JOCL > 0.1.7
 */
class PointerUtils
{
  /**
   * Creates a new Pointer to the given buffer, taking into account the array offset and position of the given buffer.
   *
   * @param buffer The buffer
   *
   * @return The new pointer
   *
   * @throws IllegalArgumentException If the given buffer is null or is neither direct nor has a backing array
   */
  public static Pointer toBuffer(Buffer buffer)
  {
    if(buffer instanceof ByteBuffer)
    {
      return computePointer((ByteBuffer) buffer);
    }
    if(buffer instanceof ShortBuffer)
    {
      return computePointer((ShortBuffer) buffer);
    }
    if(buffer instanceof IntBuffer)
    {
      return computePointer((IntBuffer) buffer);
    }
    if(buffer instanceof LongBuffer)
    {
      return computePointer((LongBuffer) buffer);
    }
    if(buffer instanceof FloatBuffer)
    {
      return computePointer((FloatBuffer) buffer);
    }
    if(buffer instanceof DoubleBuffer)
    {
      return computePointer((DoubleBuffer) buffer);
    }
    throw new IllegalArgumentException(
      "Unknown buffer type: " + buffer);

  }

  /**
   * Creates a new Pointer to the given buffer, taking into account the position and array offset of the given buffer.
   *
   * @param buffer The buffer
   *
   * @return The pointer
   *
   * @throws IllegalArgumentException If the given buffer is null or is neither direct nor has a backing array
   */
  private static Pointer computePointer(ByteBuffer buffer)
  {
    Pointer result = null;
    if(buffer.isDirect())
    {
      int oldPosition = buffer.position();
      buffer.position(0);
      result = Pointer.to(buffer.slice()).withByteOffset(
        oldPosition * Sizeof.cl_char);
      buffer.position(oldPosition);
    }
    else if(buffer.hasArray())
    {
      ByteBuffer t = ByteBuffer.wrap(buffer.array());
      int elementOffset = buffer.position() + buffer.arrayOffset();
      result = Pointer.to(t).withByteOffset(
        elementOffset * Sizeof.cl_char);
    }
    else
    {
      throw new IllegalArgumentException(
        "Buffer may not be null and must have an array or be direct");
    }
    return result;
  }


  /**
   * Creates a new Pointer to the given buffer, taking into account the position and array offset of the given buffer.
   *
   * @param buffer The buffer
   *
   * @return The pointer
   *
   * @throws IllegalArgumentException If the given buffer is null or is neither direct nor has a backing array
   */
  private static Pointer computePointer(ShortBuffer buffer)
  {
    Pointer result = null;
    if(buffer.isDirect())
    {
      int oldPosition = buffer.position();
      buffer.position(0);
      result = Pointer.to(buffer.slice()).withByteOffset(
        oldPosition * Sizeof.cl_short);
      buffer.position(oldPosition);
    }
    else if(buffer.hasArray())
    {
      ShortBuffer t = ShortBuffer.wrap(buffer.array());
      int elementOffset = buffer.position() + buffer.arrayOffset();
      result = Pointer.to(t).withByteOffset(
        elementOffset * Sizeof.cl_short);
    }
    else
    {
      throw new IllegalArgumentException(
        "Buffer may not be null and must have an array or be direct");
    }
    return result;
  }


  /**
   * Creates a new Pointer to the given buffer, taking into account the position and array offset of the given buffer.
   *
   * @param buffer The buffer
   *
   * @return The pointer
   *
   * @throws IllegalArgumentException If the given buffer is null or is neither direct nor has a backing array
   */
  private static Pointer computePointer(IntBuffer buffer)
  {
    Pointer result = null;
    if(buffer.isDirect())
    {
      int oldPosition = buffer.position();
      buffer.position(0);
      result = Pointer.to(buffer.slice()).withByteOffset(
        oldPosition * Sizeof.cl_int);
      buffer.position(oldPosition);
    }
    else if(buffer.hasArray())
    {
      IntBuffer t = IntBuffer.wrap(buffer.array());
      int elementOffset = buffer.position() + buffer.arrayOffset();
      result = Pointer.to(t).withByteOffset(
        elementOffset * Sizeof.cl_int);
    }
    else
    {
      throw new IllegalArgumentException(
        "Buffer may not be null and must have an array or be direct");
    }
    return result;
  }


  /**
   * Creates a new Pointer to the given buffer, taking into account the position and array offset of the given buffer.
   *
   * @param buffer The buffer
   *
   * @return The pointer
   *
   * @throws IllegalArgumentException If the given buffer is null or is neither direct nor has a backing array
   */
  private static Pointer computePointer(LongBuffer buffer)
  {
    Pointer result = null;
    if(buffer.isDirect())
    {
      int oldPosition = buffer.position();
      buffer.position(0);
      result = Pointer.to(buffer.slice()).withByteOffset(
        oldPosition * Sizeof.cl_long);
      buffer.position(oldPosition);
    }
    else if(buffer.hasArray())
    {
      LongBuffer t = LongBuffer.wrap(buffer.array());
      int elementOffset = buffer.position() + buffer.arrayOffset();
      result = Pointer.to(t).withByteOffset(
        elementOffset * Sizeof.cl_long);
    }
    else
    {
      throw new IllegalArgumentException(
        "Buffer may not be null and must have an array or be direct");
    }
    return result;
  }


  /**
   * Creates a new Pointer to the given buffer, taking into account the position and array offset of the given buffer.
   *
   * @param buffer The buffer
   *
   * @return The pointer
   *
   * @throws IllegalArgumentException If the given buffer is null or is neither direct nor has a backing array
   */
  private static Pointer computePointer(FloatBuffer buffer)
  {
    Pointer result = null;
    if(buffer.isDirect())
    {
      int oldPosition = buffer.position();
      buffer.position(0);
      result = Pointer.to(buffer.slice()).withByteOffset(
        oldPosition * Sizeof.cl_float);
      buffer.position(oldPosition);
    }
    else if(buffer.hasArray())
    {
      FloatBuffer t = FloatBuffer.wrap(buffer.array());
      int elementOffset = buffer.position() + buffer.arrayOffset();
      result = Pointer.to(t).withByteOffset(
        elementOffset * Sizeof.cl_float);
    }
    else
    {
      throw new IllegalArgumentException(
        "Buffer may not be null and must have an array or be direct");
    }
    return result;
  }


  /**
   * Creates a new Pointer to the given buffer, taking into account the position and array offset of the given buffer.
   *
   * @param buffer The buffer
   *
   * @return The pointer
   *
   * @throws IllegalArgumentException If the given buffer is null or is neither direct nor has a backing array
   */
  private static Pointer computePointer(DoubleBuffer buffer)
  {
    Pointer result = null;
    if(buffer.isDirect())
    {
      int oldPosition = buffer.position();
      buffer.position(0);
      result = Pointer.to(buffer.slice()).withByteOffset(
        oldPosition * Sizeof.cl_double);
      buffer.position(oldPosition);
    }
    else if(buffer.hasArray())
    {
      DoubleBuffer t = DoubleBuffer.wrap(buffer.array());
      int elementOffset = buffer.position() + buffer.arrayOffset();
      result = Pointer.to(t).withByteOffset(
        elementOffset * Sizeof.cl_double);
    }
    else
    {
      throw new IllegalArgumentException(
        "Buffer may not be null and must have an array or be direct");
    }
    return result;
  }

}
