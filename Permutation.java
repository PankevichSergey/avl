import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;




public class Permutation {
    int[] p;
    Permutation(int n) {
        p = new int[n];
        for (int i = 0; i < n; ++i) {
            p[i] = i;
        }
    }
    boolean nextPermutation(int threadCount) {
        MultithreadSearches searches = new MultithreadSearches(p, threadCount);
        int pos = searches.findSuffixInversion();
        if (pos < 0) {
            return false;
        }
        int pos2 = searches.lastMore(pos);
        int tmp = p[pos];
        p[pos] = p[pos2];
        p[pos2] = tmp;
        searches.reverseSuffix(pos + 1);
        return true;
    }
}

