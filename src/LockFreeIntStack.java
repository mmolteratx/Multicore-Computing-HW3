import java.util.concurrent.atomic.AtomicReference;

public class LockFreeIntStack {

    private AtomicReference<Node<Integer>> headNode = new AtomicReference<Node<Integer>>();

    public void push(Integer i) {
        Node<Integer> newHead = new Node<Integer>(i);

        while(true) {
            Node<Integer> currentHead = headNode.get();
            newHead.next = currentHead;

            // if currentHead is still equal to node referenced by headNode (no other process has attempted to update),
            // update headNode with newHead (insertion), otherwise, start over and try again
            if(headNode.compareAndSet(currentHead, newHead)) {break;}
        }
    }

    public Integer pop() {

        Node<Integer> currentHead = headNode.get();

        while (currentHead != null) {
            Node<Integer> newHead = currentHead.next;

            // if currentHead is still equal to node referenced by headNode (no other process has attempted to update),
            // update headNode with newHead (next node), otherwise, start over and try again
            if (headNode.compareAndSet(currentHead, newHead)) {break;}
        }

        // return correct value
        if(currentHead == null)
            return null;
        else
            return currentHead.getValue();
    }

    // vanilla node, but Integer only
    private static class Node<Integer> {
        private Integer value;
        Node<Integer> next;

        Node(Integer value) {
            this.value = value;
        }

        public Integer getValue() {return this.value;}
    }
}
