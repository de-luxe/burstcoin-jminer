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

package burstcoin.jminer.core.network;

import burstcoin.jminer.core.CoreProperties;
import burstcoin.jminer.core.network.event.NetworkStateChangeEvent;
import burstcoin.jminer.core.network.model.DevPoolResult;
import burstcoin.jminer.core.network.task.NetworkRequestLastWinnerTask;
import burstcoin.jminer.core.network.task.NetworkRequestMiningInfoTask;
import burstcoin.jminer.core.network.task.NetworkSubmitDevPoolNoncesTask;
import burstcoin.jminer.core.network.task.NetworkSubmitPoolNonceTask;
import burstcoin.jminer.core.network.task.NetworkSubmitSoloNonceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The type Network.
 */
@Component
@Scope("singleton")
public class Network
  implements NetworkStateChangeEvent.Handler
{
  private static final Logger LOG = LoggerFactory.getLogger(Network.class);

  @Autowired
  private ApplicationContext context;

  @Autowired()
  @Qualifier(value = "networkPool")
  private ThreadPoolTaskExecutor networkPool;

  private String numericAccountId;
  private boolean poolMining;
  private boolean devPool;

  private String poolServer;
  private String walletServer;

  private String soloServer;
  private String passPhrase;
  private long defaultTargetDeadline;

  private long connectionTimeout;

  private int winnerRetriesOnAsync;
  private long winnerRetryIntervalInMs;

  private long blockNumber;
  private Timer timer;

  /**
   * Post construct.
   */
  @PostConstruct
  protected void postConstruct()
  {
    timer = new Timer();
    poolMining = CoreProperties.isPoolMining();
    if(poolMining)
    {
      String poolServer = CoreProperties.getPoolServer();
      String numericAccountId = CoreProperties.getNumericAccountId();

      if(!StringUtils.isEmpty(poolServer) && !StringUtils.isEmpty(numericAccountId))
      {
        this.poolServer = CoreProperties.getPoolServer();
        this.numericAccountId = CoreProperties.getNumericAccountId();

        this.walletServer = CoreProperties.getWalletServer();
        this.winnerRetriesOnAsync = CoreProperties.getWinnerRetriesOnAsync();
        this.winnerRetryIntervalInMs = CoreProperties.getWinnerRetryIntervalInMs();

        this.devPool = CoreProperties.isDevPool();
      }
      else
      {
        LOG.error("init pool network failed!");
        LOG.error("jminer.properties: 'poolServer' or 'numericAccountId' is missing?!");
      }
    }
    else
    {
      String soloServer = CoreProperties.getSoloServer();
      String passPhrase = CoreProperties.getPassPhrase();

      if(!StringUtils.isEmpty(soloServer) && !StringUtils.isEmpty(passPhrase))
      {
        this.soloServer = soloServer;
        this.passPhrase = passPhrase;
      }
      else
      {
        LOG.error("init solo network failed!");
        LOG.error("jminer.properties: 'soloServer' or 'passPhrase' is missing?!");
      }
    }
    this.defaultTargetDeadline = CoreProperties.getTargetDeadline();
    this.connectionTimeout = CoreProperties.getConnectionTimeout();

//    startMining();
  }

  /**
   * Check last winner.
   *
   * @param blockNumber the block number
   */
  public void checkLastWinner(long blockNumber)
  {
    // find winner of lastBlock on new round, if server available
    String server = !poolMining ? soloServer : walletServer != null ? walletServer : null;
    if(!StringUtils.isEmpty(server))
    {
      NetworkRequestLastWinnerTask networkRequestLastWinnerTask = context.getBean(NetworkRequestLastWinnerTask.class);
      networkRequestLastWinnerTask.init(server, blockNumber, connectionTimeout, winnerRetriesOnAsync, winnerRetryIntervalInMs);
      networkPool.execute(networkRequestLastWinnerTask);
    }
  }

  /**
   * Check result.
   *
   * @param blockNumber the block number
   * @param calculatedDeadline the calculated deadline
   * @param nonce the nonce
   * @param chunkPartStartNonce the chunk part start nonce
   */
  public void checkResult(long blockNumber, long calculatedDeadline, long nonce, long chunkPartStartNonce)
  {
    if(poolMining)
    {
      if(devPool)
      {
        NetworkSubmitDevPoolNoncesTask networkSubmitDevPoolNoncesTask = context.getBean(NetworkSubmitDevPoolNoncesTask.class);
        List<DevPoolResult> devPoolResults = Collections.singletonList(new DevPoolResult(blockNumber, calculatedDeadline,
                                                                                         nonce, chunkPartStartNonce));
        networkSubmitDevPoolNoncesTask.init(blockNumber, numericAccountId, poolServer, connectionTimeout, devPoolResults);
        networkPool.execute(networkSubmitDevPoolNoncesTask);
      }
      else
      {
        NetworkSubmitPoolNonceTask networkSubmitPoolNonceTask = context.getBean(NetworkSubmitPoolNonceTask.class);
        networkSubmitPoolNonceTask.init(blockNumber, numericAccountId, poolServer, connectionTimeout, nonce,
                                        chunkPartStartNonce, calculatedDeadline);
        networkPool.execute(networkSubmitPoolNonceTask);
      }
    }
    else
    {
      NetworkSubmitSoloNonceTask networkSubmitSoloNonceTask = context.getBean(NetworkSubmitSoloNonceTask.class);
      networkSubmitSoloNonceTask.init(blockNumber, passPhrase, soloServer, connectionTimeout, nonce, chunkPartStartNonce, calculatedDeadline);
      networkPool.execute(networkSubmitSoloNonceTask);
    }
  }

  /**
   * Check dev result.
   *
   * @param blockNumber the block number
   * @param devPoolResults the dev pool results
   */
  public void checkDevResult(long blockNumber, List<DevPoolResult> devPoolResults)
  {
    NetworkSubmitDevPoolNoncesTask networkSubmitDevPoolNoncesTask = context.getBean(NetworkSubmitDevPoolNoncesTask.class);
    networkSubmitDevPoolNoncesTask.init(blockNumber, numericAccountId, poolServer, connectionTimeout, devPoolResults);
    networkPool.execute(networkSubmitDevPoolNoncesTask);
  }

  @Override
  @EventListener
  public void handleMessage(NetworkStateChangeEvent event)
  {
    blockNumber = event.getBlockNumber();
  }

  /**
   * Check network state.
   */
  public void checkNetworkState()
  {
    String server = poolMining ? poolServer : soloServer;
    if(!StringUtils.isEmpty(server))
    {
      NetworkRequestMiningInfoTask networkRequestMiningInfoTask = context.getBean(NetworkRequestMiningInfoTask.class);
      networkRequestMiningInfoTask.init(server, blockNumber, poolMining, connectionTimeout, defaultTargetDeadline, devPool);
      networkPool.execute(networkRequestMiningInfoTask);
    }
  }

  /**
   * Start mining.
   */
  public void startMining()
  {
    timer.schedule(new TimerTask()
    {
      @Override
      public void run()
      {
        checkNetworkState();
      }
    }, 100, CoreProperties.getRefreshInterval());
  }

  /**
   * Stop timer.
   */
  public void stopTimer()
  {
    timer.cancel();
  }
}
