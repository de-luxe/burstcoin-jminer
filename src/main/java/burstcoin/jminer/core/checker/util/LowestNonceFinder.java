package burstcoin.jminer.core.checker.util;

public interface LowestNonceFinder {
    int findLowest(byte[] gensig, byte[] data);
}
