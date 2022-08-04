// replace with static methods and pass array and threadcount


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class MultithreadSearches {
    int[] arr;
    final int threadCount;
    public MultithreadSearches(int[] arr, int maxThreadCount) {
        this.arr = arr;
        if (arr.length < maxThreadCount) {
            this.threadCount = arr.length;
        } else {
            this.threadCount = maxThreadCount;
        }
    }


    private class InvFinder implements Runnable {
        int remainder;
        AtomicInteger result; // ???
        public InvFinder(int remainder, AtomicInteger result) {
            this.remainder = remainder;
            this.result = result;
        }

        @Override
        public void run() {
            if (remainder == 2) { // how to create breakpoint in thread?
                System.out.println("pog");

            }
            int i = (remainder == 0 ? arr.length - threadCount : arr.length - remainder);
            for (; i > 0 && result.get() < i; i -= threadCount) { // once in K iterations?
                if (arr[i - 1] < arr[i]) {
                    result.set(i - 1);
                    return;
                }
                Thread.yield(); /// is this necessary?
            }
        }
    }

    public int findSuffixInversion() {
        AtomicInteger result = new AtomicInteger(-1); // volatile int?
        ExecutorService exec = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; ++i) {
            exec.execute(new InvFinder(i, result));
        }
        exec.shutdown();
        try {
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("ewfjewhpi");
        }
        return result.get(); // throw exceptions here or after calling
    }

    private class BinSearcher implements Runnable {
        int id;
        AtomicInteger left;
        AtomicInteger right;
        int pos;
        public BinSearcher(int id, AtomicInteger left, AtomicInteger right, int pos) {
            this.id = id;
            this.left = left;
            this.right = right;
            this.pos = pos;
        }

        @Override
        public void run() {
            while (right.get() - left.get() > threadCount) { // replace with synchronized block with right and left get
                int mid = left.get() + (right.get() - left.get()) / (threadCount + 1) * id;
                if (arr[mid] > arr[pos]) {
                    synchronized (left) {
                        if (left.get() < mid) {
                            left.set(mid);
                        }
                    }
                } else {
                    synchronized (right) {
                        if (right.get() > mid) {
                            right.set(mid);
                        }
                    }
                }
            }
        }
    }
    public int lastMore(int pos) {
        AtomicInteger left = new AtomicInteger(pos + 1);
        AtomicInteger right = new AtomicInteger(arr.length);
        ExecutorService exec = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; ++i) {
            exec.execute(new BinSearcher(i, left, right, pos));
        }

        exec.shutdown();
        try {
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println(""); // what should I do here
        }
        for (int i = right.get() - 1; i >= left.get(); --i) {
            if (arr[pos] < arr[i]) {
                return i;
            }
        }
        throw new RuntimeException("binary search failed");
    }

    public void reverseSuffix(int i) {
        for (int j = 0; j * 2 < arr.length - i; ++j) {
            int tmp = arr[i + j];
            arr[i + j] = arr[arr.length - j - 1];
            arr[arr.length - j - 1] = tmp;
        }
    }
}
