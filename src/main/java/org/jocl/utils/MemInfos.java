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
import org.jocl.cl_mem;

import static org.jocl.CL.*;

/**
 * Utility methods for obtaining information about cl_mem objects
 */
public class MemInfos
{
  /**
   * The type of the memory object
   *
   * @param mem The mem
   *
   * @return The value
   */
  public static int getType(cl_mem mem)
  {
    return Infos.getInt(Infos.FOR_MEM, mem,
                        CL_MEM_TYPE);
  }

  /**
   * The type of the memory object as a String
   *
   * @param mem The mem
   *
   * @return The value
   */
  public static String getTypeString(cl_mem mem)
  {
    return stringFor_cl_mem_object_type(
      Infos.getInt(Infos.FOR_MEM, mem,
                   CL_MEM_TYPE));
  }

  /**
   * The flags specified when the memory object was created
   *
   * @param mem The mem
   *
   * @return The value
   */
  public static long getFlags(cl_mem mem)
  {
    return Infos.getLong(Infos.FOR_MEM, mem,
                         CL_MEM_FLAGS);
  }

  /**
   * The flags specified when the memory object was created
   *
   * @param mem The mem
   *
   * @return The value
   */
  public static String getFlagsString(cl_mem mem)
  {
    return stringFor_cl_mem_flags(
      Infos.getLong(Infos.FOR_MEM, mem,
                    CL_MEM_FLAGS));
  }

  /**
   * Size of the memory object in bytes
   *
   * @param mem The mem
   *
   * @return The value
   */
  public static long getSize(cl_mem mem)
  {
    return Infos.getSize(Infos.FOR_MEM, mem,
                         CL_MEM_SIZE);
  }

  /**
   * The host pointer specified when the memory object was created.
   *
   * @param mem The mem
   *
   * @return The value
   */
  public static Pointer getHostPtr(cl_mem mem)
  {
    Pointer result = new Pointer();
    Infos.getPointer(Infos.FOR_MEM, mem,
                     CL_MEM_HOST_PTR, result);
    if(result.equals(new Pointer()))
    {
      return null;
    }
    return result;
  }

  /**
   * The map count - only provided for debugging.
   *
   * @param mem The mem
   *
   * @return The value
   */
  public static int getMapCount(cl_mem mem)
  {
    return Infos.getInt(Infos.FOR_MEM, mem,
                        CL_MEM_MAP_COUNT);
  }

  /**
   * The reference count - only provided for identifying memory leaks.
   *
   * @param mem The mem
   *
   * @return The value
   */
  public static int getReferenceCount(cl_mem mem)
  {
    return Infos.getInt(Infos.FOR_MEM, mem,
                        CL_MEM_REFERENCE_COUNT);
  }

  /**
   * The context that this memory object belongs to
   *
   * @param mem The mem
   *
   * @return The value
   */
  public static cl_context getContext(cl_mem mem)
  {
    cl_context result = new cl_context();
    Infos.getPointer(Infos.FOR_MEM, mem,
                     CL_MEM_CONTEXT, result);
    if(result.equals(new cl_context()))
    {
      return null;
    }
    return result;
  }

  /**
   * The memory object specified as buffer argument to clCreateSubBuffer, or NULL
   *
   * @param mem The mem
   *
   * @return The value
   */
  public static cl_mem getAssociatedMemobject(cl_mem mem)
  {
    cl_mem result = new cl_mem();
    Infos.getPointer(Infos.FOR_MEM, mem,
                     CL_MEM_ASSOCIATED_MEMOBJECT, result);
    if(result.equals(new cl_mem()))
    {
      return null;
    }
    return result;
  }

  /**
   * If the memory object is a sub-buffer, this is its offset, otherwise it is 0
   *
   * @param mem The mem
   *
   * @return The value
   */
  public static long getOffset(cl_mem mem)
  {
    return Infos.getSize(Infos.FOR_MEM, mem,
                         CL_MEM_OFFSET);
  }


  /**
   * Private constructor to prevent instantiation
   */
  private MemInfos()
  {

  }
}
