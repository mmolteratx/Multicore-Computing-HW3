package queue;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockQueue implements MyQueue {
    // you are free to add members
    AtomicReference<LockQueue.Node> head;
    AtomicReference<LockQueue.Node> tail;

    Lock enqLock = new ReentrantLock();
    Lock deqLock = new ReentrantLock();

    public LockQueue() {
        // dummy node
        LockQueue.Node node = new LockQueue.Node(0);

        // Both Head and Tail point to it
        head = new AtomicReference<LockQueue.Node>(node);
        tail = new AtomicReference<LockQueue.Node>(node);
    }

    public boolean enq(Integer value) {
        LockQueue.Node node = new LockQueue.Node(value);
        enqLock.lock();

        tail.get().next.set(node);
        tail.set(node);

        enqLock.unlock();
        return true;
    }

    public Integer deq() {
        if (tail.get() == head.get())
            return null;

        Integer returnInt;

        deqLock.lock();

        returnInt = head.get().value;
        head.get().next.set(null);

        head.set(head.get().next.get());

        deqLock.unlock();
        return returnInt;
    }

    protected class Node {
        public Integer value;
        public AtomicReference<LockQueue.Node> next;

        public Node(Integer x) {
            value = x;
            next = new AtomicReference<LockQueue.Node>(null);
        }
    }
}