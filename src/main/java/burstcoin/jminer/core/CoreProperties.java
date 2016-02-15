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

package burstcoin.jminer.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


/**
 * The type Core properties.
 */
public class CoreProperties
{
  private static final Logger LOG = LoggerFactory.getLogger(CoreProperties.class);
  private static final String STRING_LIST_PROPERTY_DELIMITER = ",";
  private static final Properties PROPS = new Properties();

  // default values
  private static final int DEFAULT_CHUNK_PART_NONCES = 320000;
  private static final int DEFAULT_RESTART_INTERVAL = 240;
  private static final int DEFAULT_PLATFORM_ID = 0;
  private static final int DEFAULT_DEVICE_ID = 0;
  private static final boolean DEFAULT_POOL_MINING = true;
  private static final boolean DEFAULT_DEV_POOL = false;
  private static final long DEFAULT_TARGET_DEADLINE = Long.MAX_VALUE;
  private static final String DEFAULT_SOLO_SERVER = "http://localhost:8125";
  private static final int DEFAULT_READ_PROGRESS_PER_ROUND = 9;
  private static final int DEFAULT_REFRESH_INTERVAL = 2000;
  private static final int DEFAULT_CONNECTION_TIMEOUT = 6000;
  private static final int DEFAULT_WINNER_RETRIES_ON_ASYNC = 4;
  private static final int DEFAULT_WINNER_RETRY_INTERVAL_IN_MS = 500;
  private static final boolean DEFAULT_SCAN_PATHS_EVERY_ROUND = false;
  private static final int DEFAULT_DEV_POOL_COMMITS_PER_ROUND = 3;
  private static final boolean DEFAULT_BYTE_UNIT_DECIMAL = true;
  private static final boolean DEFAULT_LIST_PLOT_FILES = false;
  private static final boolean DEFAULT_SHOW_DRIVE_INFO = false;
  // there seams to be a issue on checker
  private static final boolean DEFAULT_OPT_DEV_POOL = false;


  static
  {
    try
    {
      PROPS.load(new FileInputStream(System.getProperty("user.dir") + "/jminer.properties"));
    }
    catch(IOException e)
    {
      LOG.error(e.getMessage());
    }
  }

  private static Integer readProgressPerRound;
  private static Long refreshInterval;
  private static Long connectionTimeout;
  private static Integer winnerRetriesOnAsync;
  private static Long winnerRetryIntervalInMs;
  private static Boolean scanPathsEveryRound;
  private static Integer devPoolCommitsPerRound;
  private static Boolean optDevPool;
  private static Boolean devPool;
  private static Boolean poolMining;
  private static Long targetDeadline;
  private static List<String> plotPaths;
  private static Long chunkPartNonces;
  private static Integer restartInterval;
  private static Integer deviceId;
  private static Integer platformId;
  private static String walletServer;
  private static String numericAccountId;
  private static String soloServer;
  private static String passPhrase;
  private static String poolServer;
  private static Boolean byteUnitDecimal;
  private static Boolean listPlotFiles;
  private static Boolean showDriveInfo;

  private CoreProperties()
  {
    // no instances
  }

  /**
   * Gets read progress per round.
   *
   * @return the read progress per round
   */
  public static int getReadProgressPerRound()
  {
    if(readProgressPerRound == null)
    {
      readProgressPerRound = asInteger("readProgressPerRound", DEFAULT_READ_PROGRESS_PER_ROUND);
    }
    return readProgressPerRound;
  }

  /**
   * Gets refresh interval.
   *
   * @return the refresh interval
   */
  public static long getRefreshInterval()
  {
    if(refreshInterval == null)
    {
      refreshInterval = asLong("refreshInterval", DEFAULT_REFRESH_INTERVAL);
    }
    return refreshInterval;
  }

  /**
   * Gets connection timeout.
   *
   * @return the connection timeout
   */
  public static long getConnectionTimeout()
  {
    if(connectionTimeout == null)
    {
      connectionTimeout = asLong("connectionTimeout", DEFAULT_CONNECTION_TIMEOUT);
    }
    return connectionTimeout;
  }

  /**
   * Gets winner retries on async.
   *
   * @return the winner retries on async
   */
  public static int getWinnerRetriesOnAsync()
  {
    if(winnerRetriesOnAsync == null)
    {

      winnerRetriesOnAsync = asInteger("winnerRetriesOnAsync", DEFAULT_WINNER_RETRIES_ON_ASYNC);
    }
    return winnerRetriesOnAsync;
  }

  /**
   * Gets winner retry interval in ms.
   *
   * @return the winner retry interval in ms
   */
  public static long getWinnerRetryIntervalInMs()
  {
    if(winnerRetryIntervalInMs == null)
    {
      winnerRetryIntervalInMs = asLong("winnerRetryIntervalInMs", DEFAULT_WINNER_RETRY_INTERVAL_IN_MS);
    }
    return winnerRetryIntervalInMs;
  }

  /**
   * Is scan paths every round.
   *
   * @return the boolean
   */
  public static boolean isScanPathsEveryRound()
  {
    if(scanPathsEveryRound == null)
    {
      scanPathsEveryRound = asBoolean("scanPathsEveryRound", DEFAULT_SCAN_PATHS_EVERY_ROUND);
    }
    return scanPathsEveryRound;
  }

  /**
   * Gets dev pool commits per round.
   *
   * @return the dev pool commits per round
   */
  public static int getDevPoolCommitsPerRound()
  {
    if(devPoolCommitsPerRound == null)
    {
      devPoolCommitsPerRound = asInteger("devPoolCommitsPerRound", DEFAULT_DEV_POOL_COMMITS_PER_ROUND);
    }
    return devPoolCommitsPerRound;
  }

  /**
   * Is opt dev pool.
   *
   * @return the boolean
   */
  public static boolean isOptDevPool()
  {
    if(optDevPool == null)
    {
      optDevPool = asBoolean("optDevPool", DEFAULT_OPT_DEV_POOL);
    }
    return optDevPool;
  }

  /**
   * Is dev pool.
   *
   * @return the boolean
   */
  public static boolean isDevPool()
  {
    if(devPool == null)
    {
      devPool = asBoolean("devPool", DEFAULT_DEV_POOL);
    }
    return devPool;
  }

  /**
   * Is pool mining.
   *
   * @return the boolean
   */
  public static boolean isPoolMining()
  {
    if(poolMining == null)
    {
      poolMining = asBoolean("poolMining", DEFAULT_POOL_MINING);
    }
    return poolMining;
  }

  /**
   * Gets target deadline.
   *
   * @return the target deadline
   */
  public static long getTargetDeadline()
  {
    if(targetDeadline == null)
    {
      targetDeadline = asLong("targetDeadline", DEFAULT_TARGET_DEADLINE);
    }
    return targetDeadline;
  }

  /**
   * Gets pool server.
   *
   * @return the pool server
   */
  public static String getPoolServer()
  {
    if(poolServer == null)
    {
      poolServer = asString("poolServer", null);
      if(poolServer == null)
      {
        LOG.error("Error: property 'poolServer' is required for pool-mining!");
      }
    }
    return poolServer;
  }

  /**
   * Gets wallet server.
   *
   * @return the wallet server
   */
  public static String getWalletServer()
  {
    if(walletServer == null)
    {
      walletServer = asString("walletServer", "disabled");
      if(isPoolMining())
      {
        LOG.info("Winner feature disabled, property 'walletServer' undefined!");

      }
    }
    return walletServer.equals("disabled") ? null : walletServer; //no default, to turn winner on and off.
  }

  /**
   * Gets numeric account id.
   *
   * @return the numeric account id
   */
  public static String getNumericAccountId()
  {
    if(numericAccountId == null)
    {
      numericAccountId = asString("numericAccountId", null);
      boolean poolMining = isPoolMining();
      if(poolMining && numericAccountId == null)
      {
        LOG.error("Error: property 'numericAccountId' is required for pool-mining!");
      }
    }
    return numericAccountId;
  }

  /**
   * Gets solo server.
   *
   * @return the solo server
   */
  public static String getSoloServer()
  {
    if(soloServer == null)
    {
      soloServer = asString("soloServer", DEFAULT_SOLO_SERVER);
      boolean poolMining = isPoolMining();
      if(!poolMining && soloServer.equals(DEFAULT_SOLO_SERVER))
      {
        LOG.info("Default '" + DEFAULT_SOLO_SERVER + "' used for 'soloServer' property!");
      }
    }
    return !StringUtils.isEmpty(soloServer) ? soloServer : DEFAULT_SOLO_SERVER;
  }

  /**
   * Gets pass phrase.
   *
   * @return the pass phrase
   */
  public static String getPassPhrase()
  {
    if(passPhrase == null)
    {
      passPhrase = asString("passPhrase", "noPassPhrase");
      boolean poolMining = isPoolMining();
      if(!poolMining && passPhrase.equals("noPassPhrase"))
      {
        LOG.error("Error: property 'passPhrase' is required for solo-mining!");
      }
    }
    return passPhrase; // we deliver "noPassPhrase", should find no plots!
  }

  /**
   * Gets platform id.
   *
   * @return the platform id
   */
  public static int getPlatformId()
  {
    if(platformId == null)
    {
      platformId = asInteger("platformId", DEFAULT_PLATFORM_ID);
    }
    return platformId;
  }

  /**
   * Gets device id.
   *
   * @return the device id
   */
  public static int getDeviceId()
  {
    if(deviceId == null)
    {
      deviceId = asInteger("deviceId", DEFAULT_DEVICE_ID);
    }
    return deviceId;
  }

  /**
   * Gets restart interval.
   *
   * @return the restart interval
   */
  public static int getRestartInterval()
  {
    if(restartInterval == null)
    {
      restartInterval = asInteger("restartInterval", DEFAULT_RESTART_INTERVAL);
    }
    return restartInterval;
  }

  /**
   * Gets plot paths.
   *
   * @return the plot paths
   */
  public static List<String> getPlotPaths()
  {
    if(plotPaths == null)
    {
      plotPaths = asStringList("plotPaths", new ArrayList<>());
      if(plotPaths.isEmpty())
      {
        LOG.error("Error: property 'plotPaths' required!  "); // as long as we have no scan feature
      }
    }

    return plotPaths;
  }

  /**
   * Gets chunk part nonces.
   *
   * @return the chunk part nonces
   */
  public static long getChunkPartNonces()
  {
    if(chunkPartNonces == null)
    {
      chunkPartNonces = asLong("chunkPartNonces", DEFAULT_CHUNK_PART_NONCES);
    }
    return chunkPartNonces;
  }

  public static boolean isByteUnitDecimal()
  {
    if(byteUnitDecimal == null)
    {
      byteUnitDecimal = asBoolean("byteUnitDecimal", DEFAULT_BYTE_UNIT_DECIMAL);
    }
    return byteUnitDecimal;
  }

  public static boolean isListPlotFiles()
  {
    if(listPlotFiles == null)
    {
      listPlotFiles = asBoolean("listPlotFiles", DEFAULT_LIST_PLOT_FILES);
    }
    return listPlotFiles;
  }

  public static boolean isShowDriveInfo()
  {
    if(showDriveInfo == null)
    {
      showDriveInfo = asBoolean("showDriveInfo", DEFAULT_SHOW_DRIVE_INFO);
    }
    return showDriveInfo;
  }

  private static Boolean asBoolean(String key, boolean defaultValue)
  {
    String booleanProperty = PROPS.containsKey(key) ? String.valueOf(PROPS.getProperty(key)) : null;
    Boolean value = null;
    if(!StringUtils.isEmpty(booleanProperty))
    {
      try
      {
        value = Boolean.valueOf(booleanProperty);
      }
      catch(Exception e)
      {
        LOG.error("property: '" + key + "' value should be of type 'boolean' (e.g. 'true' or 'false').");
      }
    }
    return value != null ? value : defaultValue;
  }

  private static int asInteger(String key, int defaultValue)
  {
    String integerProperty = PROPS.containsKey(key) ? String.valueOf(PROPS.getProperty(key)) : null;
    Integer value = null;
    if(!StringUtils.isEmpty(integerProperty))
    {
      try
      {
        value = Integer.valueOf(integerProperty);
      }
      catch(NumberFormatException e)
      {
        LOG.error("value of property: '" + key + "' should be a numeric (int) value.");
      }
    }
    return value != null ? value : defaultValue;
  }

  private static long asLong(String key, long defaultValue)
  {
    String integerProperty = PROPS.containsKey(key) ? String.valueOf(PROPS.getProperty(key)) : null;
    Long value = null;
    if(!StringUtils.isEmpty(integerProperty))
    {
      try
      {
        value = Long.valueOf(integerProperty);
      }
      catch(NumberFormatException e)
      {
        LOG.error("value of property: '" + key + "' should be a numeric (long) value.");
      }
    }
    return value != null ? value : defaultValue;
  }

  private static List<String> asStringList(String key, List<String> defaultValue)
  {
    String stringListProperty = PROPS.containsKey(key) ? String.valueOf(PROPS.getProperty(key)) : null;
    List<String> value = null;
    if(!StringUtils.isEmpty(stringListProperty))
    {
      try
      {
        value = Arrays.asList(stringListProperty.trim().split(STRING_LIST_PROPERTY_DELIMITER));
      }
      catch(NullPointerException | NumberFormatException e)
      {
        LOG.error("property: '" + key + "' value should be 'string(s)' separated by '" + STRING_LIST_PROPERTY_DELIMITER + "' (comma).");
      }
    }

    return value != null ? value : defaultValue;
  }

  private static String asString(String key, String defaultValue)
  {
    String value = PROPS.containsKey(key) ? String.valueOf(PROPS.getProperty(key)) : defaultValue;
    return StringUtils.isEmpty(value) ? defaultValue : value;
  }
}
