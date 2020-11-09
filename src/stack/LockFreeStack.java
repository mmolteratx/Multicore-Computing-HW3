package stack;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStack implements MyStack {

    private AtomicReference<Node> headNode;
    
    public LockFreeStack() {
        headNode = new AtomicReference<Node>();
    }

    public boolean push(Integer i) {
        Node newHead = new Node(i);

        while(true) {
            Node currentHead = headNode.get();
            newHead.next = currentHead;

            // if currentHead is still equal to node referenced by headNode (no other process has attempted to update),
            // update headNode with newHead (insertion), otherwise, start over and try again
            if(headNode.compareAndSet(currentHead, newHead)) {
                // For testing only
                System.out.println("Push: " + newHead.value);
                break;
            }

            /*try {
                wait(100);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
        
        return true;
    }

    public Integer pop() throws EmptyStack {

        Node currentHead = headNode.get();
        
        // don't bother if null
        while (currentHead != null) {
            Node newHead = currentHead.next;

            // if currentHead is still equal to node referenced by headNode (no other process has attempted to update),
            // update headNode with newHead (next node), otherwise, start over and try again
            if (headNode.compareAndSet(currentHead, newHead)) {
                System.out.println("Pop: " + currentHead.value);
                break;
            }
            else {
                /*try {
                    wait(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                currentHead = headNode.get();
            }
        }

        // null handling
        if(currentHead == null)
            throw new EmptyStack();
        else
            return currentHead.value;
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
