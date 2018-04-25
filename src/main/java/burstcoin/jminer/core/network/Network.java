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

package burstcoin.jminer.core.network;

import burstcoin.jminer.core.CoreProperties;
import burstcoin.jminer.core.network.event.NetworkStateChangeEvent;
import burstcoin.jminer.core.network.task.NetworkRequestLastWinnerTask;
import burstcoin.jminer.core.network.task.NetworkRequestMiningInfoTask;
import burstcoin.jminer.core.network.task.NetworkRequestPoolInfoTask;
import burstcoin.jminer.core.network.task.NetworkRequestTriggerServerTask;
import burstcoin.jminer.core.network.task.NetworkSubmitPoolNonceTask;
import burstcoin.jminer.core.network.task.NetworkSubmitSoloNonceFallbackTask;
import burstcoin.jminer.core.network.task.NetworkSubmitSoloNonceTask;
import burstcoin.jminer.core.reader.Reader;
import burstcoin.jminer.core.reader.data.Plots;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Timer;
import java.util.TimerTask;

@Component
@Scope("singleton")
public class Network
{
  private static final Logger LOG = LoggerFactory.getLogger(Network.class);

  private final ApplicationContext context;
  private final SimpleAsyncTaskExecutor networkPool;

  private String numericAccountId;

  private String poolServer;
  private String walletServer;

  private String soloServer;
  private String passPhrase;

  private long connectionTimeout;

  private int winnerRetriesOnAsync;
  private long winnerRetryIntervalInMs;

  private long blockNumber;
  private Timer timer;
  private byte[] generationSignature;
  private String mac; // unique system id
  private Plots plots;

  @Autowired
  public Network(ApplicationContext context, @Qualifier(value = "networkPool") SimpleAsyncTaskExecutor networkPool)
  {
    this.context = context;
    this.networkPool = networkPool;
  }

  @PostConstruct
  protected void postConstruct()
  {
    // init drives/plotfiles ... ensure miner starts after that
    Reader reader = context.getBean(Reader.class);
    plots = reader.getPlots();

    mac = getMac();
    timer = new Timer();

    if(CoreProperties.isPoolMining())
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
    this.connectionTimeout = CoreProperties.getConnectionTimeout();
  }

  private String getMac()
  {
    InetAddress ip;
    StringBuilder sb = new StringBuilder();
    try
    {
      ip = InetAddress.getLocalHost();
      NetworkInterface network = NetworkInterface.getByInetAddress(ip);
      byte[] mac = network.getHardwareAddress();
      sb = new StringBuilder();
      for(int i = 0; i < mac.length; i++)
      {
        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
      }
    }
    catch(Exception e)
    {
      LOG.debug("Could not create MAC address as unique id for mining system. Fallback capacity used.");
    }
    return sb.toString();
  }

  @EventListener
  public void handleMessage(NetworkStateChangeEvent event)
  {
    blockNumber = event.getBlockNumber();
    generationSignature = event.getGenerationSignature();
  }

  public void checkNetworkState()
  {
    String server = CoreProperties.isPoolMining() ? poolServer : soloServer;
    if(!StringUtils.isEmpty(server))
    {
      NetworkRequestMiningInfoTask networkRequestMiningInfoTask = context.getBean(NetworkRequestMiningInfoTask.class);
      networkRequestMiningInfoTask.init(server, blockNumber, generationSignature, connectionTimeout, plots.getSize());
      networkPool.execute(networkRequestMiningInfoTask);
    }
  }

  // ensure wallet-server does not stuck on solo mining
  public void triggerServer()
  {
    if(!StringUtils.isEmpty(soloServer))
    {
      NetworkRequestTriggerServerTask networkRequestTriggerServerTask = context.getBean(NetworkRequestTriggerServerTask.class);
      networkRequestTriggerServerTask.init(soloServer, numericAccountId, connectionTimeout);
      networkPool.execute(networkRequestTriggerServerTask);
    }
  }

  public void checkLastWinner(long blockNumber)
  {
    // find winner of lastBlock on new round, if server available
    String server = !CoreProperties.isPoolMining() ? soloServer : walletServer;
    if(!StringUtils.isEmpty(server))
    {
      NetworkRequestLastWinnerTask networkRequestLastWinnerTask = context.getBean(NetworkRequestLastWinnerTask.class);
      networkRequestLastWinnerTask.init(server, blockNumber, connectionTimeout, winnerRetriesOnAsync, winnerRetryIntervalInMs);
      networkPool.execute(networkRequestLastWinnerTask);
    }
  }

  public void checkPoolInfo()
  {
    if(CoreProperties.isPoolMining() && walletServer != null)
    {
      NetworkRequestPoolInfoTask networkRequestPoolInfoTask = context.getBean(NetworkRequestPoolInfoTask.class);
      networkRequestPoolInfoTask.init(walletServer, numericAccountId, connectionTimeout);
      networkPool.execute(networkRequestPoolInfoTask);
    }
  }

  public void commitResult(long blockNumber, long calculatedDeadline, BigInteger nonce, BigInteger chunkPartStartNonce, long totalCapacity,
                           BigInteger result, String plotFilePath)
  {
    if(CoreProperties.isPoolMining())
    {
      NetworkSubmitPoolNonceTask networkSubmitPoolNonceTask = context.getBean(NetworkSubmitPoolNonceTask.class);
      networkSubmitPoolNonceTask.init(blockNumber, generationSignature, numericAccountId, poolServer, connectionTimeout, nonce,
                                      chunkPartStartNonce, calculatedDeadline, totalCapacity, result, plotFilePath, mac);
      networkPool.execute(networkSubmitPoolNonceTask);
    }
    else
    {
      NetworkSubmitSoloNonceTask networkSubmitSoloNonceTask = context.getBean(NetworkSubmitSoloNonceTask.class);
      networkSubmitSoloNonceTask.init(blockNumber, generationSignature, passPhrase, soloServer, connectionTimeout, nonce, chunkPartStartNonce,
                                      calculatedDeadline, result);
      networkPool.execute(networkSubmitSoloNonceTask);

      if(CoreProperties.isRecommitDeadlines() && calculatedDeadline < 1200)
      {
        // recommit #1 after 5 sec.
        NetworkSubmitSoloNonceFallbackTask networkSubmitSoloNonceRecommitTask = context.getBean(NetworkSubmitSoloNonceFallbackTask.class);
        networkSubmitSoloNonceRecommitTask.init(soloServer, 5000L, passPhrase, connectionTimeout, nonce, calculatedDeadline);
        networkPool.execute(networkSubmitSoloNonceRecommitTask);

        // recommit #2 after 10 sec.
        NetworkSubmitSoloNonceFallbackTask networkSubmitSoloNonceRecommitTask2 = context.getBean(NetworkSubmitSoloNonceFallbackTask.class);
        networkSubmitSoloNonceRecommitTask2.init(soloServer, 10000L, passPhrase, connectionTimeout, nonce, calculatedDeadline);
        networkPool.execute(networkSubmitSoloNonceRecommitTask2);

        // recommit #3 after 15 sec.
        NetworkSubmitSoloNonceFallbackTask networkSubmitSoloNonceRecommitTask3 = context.getBean(NetworkSubmitSoloNonceFallbackTask.class);
        networkSubmitSoloNonceRecommitTask3.init(soloServer, 15000L, passPhrase, connectionTimeout, nonce, calculatedDeadline);
        networkPool.execute(networkSubmitSoloNonceRecommitTask3);
      }
    }

  }

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

    // on solo mining
    if(!CoreProperties.isPoolMining() && CoreProperties.isTriggerServer())
    {
      timer.schedule(new TimerTask()
      {
        @Override
        public void run()
        {
          triggerServer();
        }
      }, 5000, 25000);
    }
  }
}
