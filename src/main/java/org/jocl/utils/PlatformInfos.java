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

import org.jocl.cl_platform_id;

import static org.jocl.CL.*;

/**
 * Utility methods for obtaining information about cl_platform_id objects
 */
public class PlatformInfos
{
  /**
   * Platform name string
   *
   * @param platform The platform
   *
   * @return The value
   */
  public static String getName(cl_platform_id platform)
  {
    return Infos.getString(Infos.FOR_PLATFORM, platform,
                           CL_PLATFORM_NAME);
  }

  /**
   * Platform vendor string
   *
   * @param platform The platform
   *
   * @return The value
   */
  public static String getVendor(cl_platform_id platform)
  {
    return Infos.getString(Infos.FOR_PLATFORM, platform,
                           CL_PLATFORM_VENDOR);
  }

  /**
   * OpenCL version string
   *
   * @param platform The platform
   *
   * @return The value
   */
  public static String getVersion(cl_platform_id platform)
  {
    return Infos.getString(Infos.FOR_PLATFORM, platform,
                           CL_PLATFORM_VERSION);
  }

  /**
   * The profile name supported by the implementation
   *
   * @param platform The platform
   *
   * @return The value
   */
  public static String getProfile(cl_platform_id platform)
  {
    return Infos.getString(Infos.FOR_PLATFORM, platform,
                           CL_PLATFORM_PROFILE);
  }

  /**
   * A space-separated list of extension names
   *
   * @param platform The platform
   *
   * @return The value
   */
  public static String getExtensions(cl_platform_id platform)
  {
    return Infos.getString(Infos.FOR_PLATFORM, platform,
                           CL_PLATFORM_EXTENSIONS);
  }


  /**
   * Private constructor to prevent instantiation
   */
  private PlatformInfos()
  {
  }

}
