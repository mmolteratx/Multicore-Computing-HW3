package stack;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class StackTest {

    @Test
    public void serialTest() {
        LockFreeStack serialStack = new LockFreeStack();

        serialStack.push(1);
        serialStack.push(2);
        serialStack.push(3);

        try {
            int i = serialStack.pop();
            assertEquals(i, 3);
            i = serialStack.pop();
            assertEquals(i, 2);
            i = serialStack.pop();
            assertEquals(i, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test public void basicParallelTest() {
        LockFreeStack stack = new LockFreeStack();

        // producer
        new Thread() {
            public void run() {
                Random random = new Random();
                for(int i=0; i<10; i++) {
                    stack.push(i);
                }
            }
        }.start();

        try{wait(1000);}
        catch(Exception e) {
            e.printStackTrace();
        }

        // consumer
        new Thread() {
            public void run() {
                for(int i=9; i>0; i--) {
                    try {
                        int j = stack.pop();
                        assertEquals(i, j);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
