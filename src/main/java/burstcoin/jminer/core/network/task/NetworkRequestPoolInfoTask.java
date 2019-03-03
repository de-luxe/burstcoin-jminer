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

package burstcoin.jminer.core.network.task;

import burstcoin.jminer.core.CoreProperties;
import burstcoin.jminer.core.network.event.NetworkPoolInfoEvent;
import burstcoin.jminer.core.network.model.Account;
import burstcoin.jminer.core.network.model.Accounts;
import burstcoin.jminer.core.network.model.RewardRecipient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")
public class NetworkRequestPoolInfoTask
  implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(NetworkRequestMiningInfoTask.class);

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final ApplicationEventPublisher publisher;

  @Autowired
  public NetworkRequestPoolInfoTask(HttpClient httpClient, ObjectMapper objectMapper, ApplicationEventPublisher publisher)
  {
    this.httpClient = httpClient;
    this.objectMapper = objectMapper;
    this.publisher = publisher;
  }

  @Override
  public void run()
  {
    String rewardRecipientAccountId = getRewardRecipientAccountId(CoreProperties.getNumericAccountId());
    if(rewardRecipientAccountId != null)
    {
      // number of registered miner accounts
      List<String> accountIdsOfRewardRecipient = getAccountIdsOfRewardRecipient(rewardRecipientAccountId);
      Account account = getAccount(rewardRecipientAccountId);
      if(account != null)
      {
        publisher.publishEvent(new NetworkPoolInfoEvent(account.getAccountRS(), account.getBalanceNQT(), account.getForgedBalanceNQT(),
                accountIdsOfRewardRecipient != null ? accountIdsOfRewardRecipient.size() : 0));
      }
    }
  }

  private String getRewardRecipientAccountId(String accountId)
  {
    RewardRecipient rewardRecipient = null;
    try
    {
      ContentResponse response = httpClient.newRequest(CoreProperties.getWalletServer() + "/burst?requestType=getRewardRecipient&account=" + accountId)
        .timeout(CoreProperties.getConnectionTimeout(), TimeUnit.MILLISECONDS)
        .send();

      String contentAsString = response.getContentAsString();

      if(!contentAsString.contains("error"))
      {
        rewardRecipient = objectMapper.readValue(contentAsString, RewardRecipient.class);
      }
      else
      {
        LOG.warn("Error: Failed to 'getRewardRecipient' for pool info: " + contentAsString);
      }
    }
    catch(Exception e)
    {
      LOG.warn("Error: Failed to 'getRewardRecipient' for pool info: " + e.getMessage());
    }
    return rewardRecipient != null ? rewardRecipient.getRewardRecipient() : null;
  }

  private List<String> getAccountIdsOfRewardRecipient(String rewardRecipientAccountId)
  {
    Accounts accounts = null;
    try
    {
      String requestUri = CoreProperties.getWalletServer() + "/burst?requestType=getAccountsWithRewardRecipient&account=" + rewardRecipientAccountId;
      ContentResponse response = httpClient.newRequest(requestUri)
        .timeout(CoreProperties.getConnectionTimeout(), TimeUnit.MILLISECONDS)
        .send();

      String contentAsString = response.getContentAsString();

      if(!contentAsString.contains("error"))
      {
        accounts = objectMapper.readValue(contentAsString, Accounts.class);
      }
      else
      {
        LOG.warn("Error: Failed to 'getAccountIdsOfRewardRecipient' for pool info: " + contentAsString);
      }
    }
    catch(Exception e)
    {
      LOG.warn("Error: Failed to 'getAccountIdsOfRewardRecipient' for pool info: " + e.getMessage());
    }
    return accounts != null ? accounts.getAccounts() : null;
  }

  private Account getAccount(String accountId)
  {
    Account account = null;
    try
    {
      ContentResponse response = httpClient.newRequest(CoreProperties.getWalletServer() + "/burst?requestType=getAccount&account=" + accountId)
        .timeout(CoreProperties.getConnectionTimeout(), TimeUnit.MILLISECONDS)
        .send();

      String contentAsString = response.getContentAsString();

      if(!contentAsString.contains("error"))
      {
        account = objectMapper.readValue(contentAsString, Account.class);
      }
      else
      {
        LOG.warn("Error: Failed to 'getAccount' for pool info: " + contentAsString);
      }
    }
    catch(Exception e)
    {
      LOG.warn("Error: Failed to 'getAccount' for pool info: " + e.getMessage());
    }
    return account;
  }
}

