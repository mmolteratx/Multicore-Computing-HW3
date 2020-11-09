package queue;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue implements MyQueue {
    AtomicReference<Node> head;
    AtomicReference<Node> tail;

    public LockFreeQueue() {
        // dummy initial node
        Node node = new Node(null);
        head = new AtomicReference<Node>(node);
        tail = new AtomicReference<Node>(node);
    }

    public boolean enq(Integer value) {
        Node node = new Node(value);

        while(true) {
            AtomicReference<Node> currTail = new AtomicReference<Node>(tail.get());

            // Are tail and next consistent? && Was Tail pointing to the last node?
            if (currTail.get() == tail.get() && currTail.get().next.get() == null) {
                // Try to link node at the end of the linked list
                if (currTail.get().next.compareAndSet(null, node)) {
                    tail.compareAndSet(currTail.get(), node);
                    break;
                }
                // Tail was not pointing to the last node
                else {
                    // Try to swing Tail to the next node
                    tail.compareAndSet(currTail.get(), currTail.get().next.get());
                }
            } // if
        } // while()
        return true;
    }

    public Integer deq() {
        Integer returnInt = null;
        while(true) {
            AtomicReference<Node> currTail = new AtomicReference<Node>(tail.get());
            AtomicReference<Node> currHead = new AtomicReference<Node>(head.get());
            AtomicReference<Node> next = currHead.get().next;

            // Are head, tail, and next consistent?
            if (head.get() == currHead.get()) {
                // Is queue empty or Tail falling behind?
                if (currHead.get() == currTail.get()) {
                    // Is queue empty?
                    if (next.get() == null) {
                        // Queue is empty, couldn't dequeue
                        return null;
                    }
                    // Tail is falling behind.  Try to advance it
                    tail.compareAndSet(currTail.get(), currTail.get().next.get());
                } else {
                    Node pointer = next.get();
                    if (head.compareAndSet(currHead.get(),next.get())) {
                        returnInt = currHead.get().next.get().value;
                        currHead.get().next = null;
                        break;
                    }
                } // else
            } // if
        } // while()
        return returnInt;
    }

    // structure pointer_t {ptr: pointer to node_t, count: unsigned integer}
    // use AtomicReference<Node>;

    // structure node_t {value: data type, next: pointer_t}
    protected class Node {
        public Integer value;
        public AtomicReference<Node> next;

        public Node(Integer x) {
            value = x;
            next = new AtomicReference<Node>(null);
        }
    }

    public void printQueue() {
        AtomicReference<Node> currNode = new AtomicReference<Node>(head.get());
        System.out.print("Queue consists of: ");
        while (currNode.get().next.get() != null) {
            System.out.print(currNode.get().value + ", ");
            currNode.set(currNode.get().next.get());
        }
        System.out.println(currNode.get().value + "");
        return;
    }
}
