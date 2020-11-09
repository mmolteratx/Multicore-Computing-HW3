package queue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockQueue implements MyQueue {
    // you are free to add members
    AtomicReference<LockQueue.Node> head;
    AtomicReference<LockQueue.Node> tail;

    Lock enqLock = new ReentrantLock();
    Lock deqLock = new ReentrantLock();

    protected AtomicInteger count = new AtomicInteger();

    public LockQueue() {
        // dummy node
        LockQueue.Node node = new LockQueue.Node(0);

        count.set(0);

        // Both Head and Tail point to it
        head = new AtomicReference<LockQueue.Node>(node);
        tail = new AtomicReference<LockQueue.Node>(node);
    }

    public boolean enq(Integer value) {
        LockQueue.Node node = new LockQueue.Node(value);
        enqLock.lock();

        tail.get().next.set(node);
        tail.set(node);

        count.getAndIncrement();

        enqLock.unlock();
        return true;
    }

    public Integer deq() {
        if (tail.get() == head.get())
            return null;

        Integer returnInt;

        deqLock.lock();

        AtomicReference<LockQueue.Node> temp_head = new AtomicReference<Node>();
        temp_head.set(head.get());

        returnInt = head.get().next.get().value;
        head.get().next.get().value = null;
        head.set(head.get().next.get());

        temp_head.get().next = null;
        temp_head.set(null);

        count.getAndDecrement();

        deqLock.unlock();
        return returnInt;
    }

    public Integer getCount() {
        return count.get();
    }

    protected class Node {
        public Integer value;
        public AtomicReference<LockQueue.Node> next;

        public Node(Integer x) {
            value = x;
            next = new AtomicReference<LockQueue.Node>(null);
        }
    }

    public void printQueue() {
        AtomicReference<LockQueue.Node> currNode = new AtomicReference<LockQueue.Node>(head.get());
        System.out.print("Queue consists of: ");
        while (currNode.get().next.get() != null) {
            System.out.print(currNode.get().value + ", ");
            currNode.set(currNode.get().next.get());
        }
        System.out.println(currNode.get().value + "");
        return;
    }
}