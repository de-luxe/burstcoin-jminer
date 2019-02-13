package burstcoin.jminer.core.checker.util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import pocminer.generate.MiningPlot;

public class ShaLibChecker implements LowestNonceFinder {

    private final ShabalLibrary shabalLibrary;

    public ShaLibChecker() {
        shabalLibrary = Native.load("shabal_lib", ShabalLibrary.class);
        shabalLibrary.shabal_init();
    }

    private interface ShabalLibrary extends Library {
        long shabal_findBestDeadline(byte[] scoops, long numScoops, byte[] gensig);
        void shabal_init();
    }

    @Override
    public int findLowest(byte[] gensig, byte[] data) {
        return (int) shabalLibrary.shabal_findBestDeadline(data, data.length / MiningPlot.SCOOP_SIZE, gensig);
    }
}
