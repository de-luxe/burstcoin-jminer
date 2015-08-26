package burstcoin.jminer.core.checker.task;


import burstcoin.jminer.core.checker.event.CheckerResultEvent;
import burstcoin.jminer.core.checker.util.OCLChecker;
import fr.cryptohash.Shabal256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pocminer.generate.MiningPlot;

import java.math.BigInteger;


@Component
@Scope("prototype")
public class OCLCheckerTask
  implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(OCLCheckerTask.class);

  @Autowired
  private ApplicationEventPublisher publisher;

  private final OCLChecker oclChecker;

  private long blockNumber;
  private byte[] generationSignature;
  private byte[] scoops;
  private long chunkPartStartNonce;

  @Autowired
  public OCLCheckerTask(OCLChecker oclChecker)
  {
    this.oclChecker = oclChecker;
  }

  public void init(long blockNumber, byte[] generationSignature, byte[] scoops, long chunkPartStartNonce)
  {
    this.blockNumber = blockNumber;
    this.generationSignature = generationSignature;
    this.scoops = scoops;
    this.chunkPartStartNonce = chunkPartStartNonce;
  }

  @Override
  public void run()
  {
    int lowestNonce;
    synchronized(oclChecker)
    {
      lowestNonce = oclChecker.findLowest(generationSignature, scoops);
    }
    long nonce = chunkPartStartNonce + lowestNonce;

    BigInteger result = calculateResult(scoops, generationSignature, lowestNonce);
    publisher.publishEvent(new CheckerResultEvent(blockNumber, chunkPartStartNonce, nonce, result));
  }

  private BigInteger calculateResult(byte[] scoops, byte[] generationSignature, int nonce)
  {
    Shabal256 md = new Shabal256();
    md.reset();
    md.update(generationSignature);
    md.update(scoops, nonce * MiningPlot.SCOOP_SIZE, MiningPlot.SCOOP_SIZE);
    byte[] hash = md.digest();
    return new BigInteger(1, new byte[]{hash[7], hash[6], hash[5], hash[4], hash[3], hash[2], hash[1], hash[0]});
  }
}
