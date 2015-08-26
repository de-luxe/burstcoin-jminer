package burstcoin.jminer.core.checker;

import burstcoin.jminer.core.CoreProperties;
import burstcoin.jminer.core.checker.task.OCLCheckerFindAllBelowTargetTask;
import burstcoin.jminer.core.checker.task.OCLCheckerTask;
import burstcoin.jminer.core.reader.event.ReaderLoadedPartEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Scope("singleton")
public class Checker
  implements ReaderLoadedPartEvent.Handler
{
  private static final Logger LOG = LoggerFactory.getLogger(Checker.class);

  @Autowired
  private ApplicationContext context;

  @Autowired
  private SyncTaskExecutor checkTaskExecutor;

  // setting
  private boolean devPool;
  private boolean optDevPool;

  // data
  private long blockNumber;
  private long targetDeadline;
  private long baseTarget;
  private byte[] generationSignature;

  @PostConstruct
  protected void postConstruct()
  {
    this.devPool = CoreProperties.isDevPool();
    this.optDevPool = CoreProperties.isOptDevPool();
    this.targetDeadline = CoreProperties.getTargetDeadline();
  }

  public void reconfigure(long blockNumber, long baseTarget, long targetDeadline, byte[] generationSignature)
  {
    this.blockNumber = blockNumber;
    this.baseTarget = baseTarget;
    this.targetDeadline = targetDeadline;
    this.generationSignature = generationSignature;
  }

  @Override
  @EventListener
  public void handleMessage(ReaderLoadedPartEvent event)
  {
    if(blockNumber == event.getBlockNumber())
    {
      if(devPool && optDevPool)
      {
        // todo findTartget() does not work?!
        OCLCheckerFindAllBelowTargetTask oclCheckerFindAllBelowTargetTask = context.getBean(OCLCheckerFindAllBelowTargetTask.class);
        oclCheckerFindAllBelowTargetTask.init(event.getBlockNumber(),generationSignature, event.getScoops(), targetDeadline,
                                              event.getChunkPartStartNonce(), baseTarget);
        checkTaskExecutor.execute(oclCheckerFindAllBelowTargetTask);
      }
      else
      {
        OCLCheckerTask oclCheckerTask = context.getBean(OCLCheckerTask.class);
        oclCheckerTask.init(event.getBlockNumber(), generationSignature, event.getScoops(), event.getChunkPartStartNonce());
        checkTaskExecutor.execute(oclCheckerTask);
      }
    }
    else
    {
      LOG.debug("skipped check scoop ... old block ...");
    }
  }
}
