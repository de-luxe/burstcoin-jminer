package burstcoin.jminer.core;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
@ComponentScan(basePackages = "burstcoin.jminer.core")
public class CoreConfig
{
  @Bean(name = "readerPool")
  public ThreadPoolTaskExecutor readerPool()
  {
    ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
    pool.setThreadPriority(2);
    pool.setWaitForTasksToCompleteOnShutdown(true);
    return pool;
  }

  @Bean(name = "networkPool")
  public ThreadPoolTaskExecutor networkPool()
  {
    ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
    pool.setMaxPoolSize(2);
    pool.setThreadPriority(1);
    pool.setWaitForTasksToCompleteOnShutdown(true);
    return pool;
  }

  @Bean(name = "checkTaskExecutor")
  public SyncTaskExecutor taskExecutor()
  {
    return new SyncTaskExecutor();
  }

  @Bean(name = "roundPool")
  public ThreadPoolTaskExecutor roundPool()
  {
    return new ThreadPoolTaskExecutor();
  }

  @Bean
  public HttpClient httpClient()
  {
    HttpClient client = new HttpClient(new SslContextFactory(true));
    try
    {
      client.start();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    return client;
  }

  @Bean
  public ObjectMapper objectMapper()
  {
    return new ObjectMapper();
  }
}
