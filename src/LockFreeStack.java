package stack;

// No testing done; still need to verify that concurrency is handled successfully

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStack implements MyStack {

    private AtomicReference<Node<Integer>> headNode; 
    
    public LockFreeStack() {
        headNode = new AtomicReference<Node<Integer>>();
    }

    public boolean push(Integer i) {
        Node<Integer> newHead = new Node<Integer>(i);

        while(true) {
            Node<Integer> currentHead = headNode.get();
            newHead.next = currentHead;

            // if currentHead is still equal to node referenced by headNode (no other process has attempted to update),
            // update headNode with newHead (insertion), otherwise, start over and try again
            if(headNode.compareAndSet(currentHead, newHead)) {break;}
        }
        
        return true;
    }

    public Integer pop() throws EmptyStack {

        Node<Integer> currentHead = headNode.get();
        
        // don't bother if null
        while (currentHead != null) {
            Node<Integer> newHead = currentHead.next;

            // if currentHead is still equal to node referenced by headNode (no other process has attempted to update),
            // update headNode with newHead (next node), otherwise, start over and try again
            if (headNode.compareAndSet(currentHead, newHead)) {break;}
            else {currentHead = headNode.get();}
        }

        // null handling
        if(currentHead == null)
            throw new Exception("EmptyStack");
        else
            return currentHead.getValue();
    }

    // vanilla node, but Integer only
    protected class Node {
        public Integer value;
        public Node next;

        public Node(Integer x) {
            value = x;
            next = null;
        }
    }
}
