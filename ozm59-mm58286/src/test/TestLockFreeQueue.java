import org.junit.Test;
import queue.LockFreeQueue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertTrue;

public class TestLockFreeQueue implements Runnable{

    static AtomicInteger count;
    static AtomicInteger enqCount;
    static AtomicInteger deqCount;

    static AtomicBoolean enqDeq;
    static LockFreeQueue queue;

    @Test
    public void main() {
        try {
            TestQueue(5, 5, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void TestQueue(int numEnqThreads, int numDeqThreads, int numEnq) throws InterruptedException {
        count = new AtomicInteger(numEnq);
        enqCount = new AtomicInteger(0);
        deqCount = new AtomicInteger(0);

        enqDeq = new AtomicBoolean(true);
        queue = new LockFreeQueue();

        // init and start all the threads
        TestLockFreeQueue runnable = new TestLockFreeQueue();
        Thread[] threads = new Thread[numEnqThreads];

        for (int i = 0; i < numEnqThreads; i++) {
            threads[i] = new Thread(runnable);
        }
        for (Thread thread : threads) {
            thread.start();
        }

        // wait for threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        enqDeq.set(false);
        queue.printQueue();
        System.out.println("Swapping to dequeue!");

        threads = new Thread[numEnqThreads];
        for (int i = 0; i < numDeqThreads; i++) {
            threads[i] = new Thread(runnable);
        }
        for (Thread thread : threads) {
            thread.start();
        }

        // wait for threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Number en-queued: " + enqCount.get() + ", number de-queued: " + deqCount.get());
        assertTrue("Number en-queued: " + enqCount.get() + ", number de-queued: " + deqCount.get(), enqCount.get() == deqCount.get());
    }

    @Override
    public void run() {
        // enqueuing
        if(enqDeq.get()) {
            int num = count.getAndDecrement();
            while (num >= 0 ) {
                enqCount.getAndIncrement();
                queue.enq(num);
                System.out.println("  " + Thread.currentThread().getId() + ": En-queued: " + num);
                num = count.getAndDecrement();
            }
        }
        // de-queuing
        else {
            Integer num = queue.deq();
            while (num != null) {
                System.out.println("  " + Thread.currentThread().getId() + ": De-queued: " + num);
                deqCount.getAndIncrement();
                num = queue.deq();
            }
        }
    }
}
