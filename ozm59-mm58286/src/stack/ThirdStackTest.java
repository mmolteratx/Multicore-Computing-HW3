package stack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

public class ThirdStackTest {

    public static void main(String[] args) {
        int iterations = 10;
        ExecutorService pool = Executors.newFixedThreadPool(4);
        List<Callable<String>> callables = new ArrayList<>();
        LockFreeStack st = new LockFreeStack();

        for(int i = 0; i < 4; i++) {
            WorkerThread thread = new WorkerThread(iterations, st);
            pool.submit(thread);
            callables.add(thread);
        }

        pool.shutdown();
    }

    private static class WorkerThread implements Callable {
        int iterations;
        LockFreeStack stack;

        WorkerThread(int it, LockFreeStack st) {
            iterations = it;
            stack = st;
        }

        public String call() {
            for(int i = 0; i < iterations; i++) {
                stack.push(i);
            }
            for(int i = 0; i < iterations; i++) {
                try {
                    stack.pop();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

            return "Done";
        }
    }
}
