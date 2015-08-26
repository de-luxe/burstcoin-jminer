package burstcoin.jminer.core.round.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class RoundFireEventTask
  implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(RoundFireEventTask.class);

  @Autowired
  private ApplicationEventPublisher publisher;

  private ApplicationEvent event;

  public <EVENT extends ApplicationEvent> void init(EVENT event)
  {
    this.event = event;
  }

  @Override
  public void run()
  {
    publisher.publishEvent(event);
  }
}
