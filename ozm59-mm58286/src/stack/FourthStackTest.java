package stack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FourthStackTest {

    public static void main(String[] args) {
        int iterations = 10;
        AtomicInteger counter = new AtomicInteger(0);
        LockFreeStack st = new LockFreeStack();

        Thread one = new Thread() {
            public void run() {
                for (int i = 0; i < iterations; i++) {
                    st.push(counter.incrementAndGet());
                }
                for (int i = 0; i < iterations; i++) {
                    try {
                        st.pop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread two = new Thread() {
            public void run() {
                for (int i = 0; i < iterations; i++) {
                    st.push(counter.incrementAndGet());
                }
                for (int i = 0; i < iterations; i++) {
                    try {
                        st.pop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        try {
            one.start();
            two.start();
            one.join();
            two.join();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
