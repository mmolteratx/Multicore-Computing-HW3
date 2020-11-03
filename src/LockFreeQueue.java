package queue;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue implements MyQueue {
    // structure queue_t {Head: pointer_t, Tail: pointer_t}
    AtomicReference<Node> head;
    AtomicReference<Node> tail;

    // initialize(Q: pointer to queue_t)
    public LockFreeQueue() {
        // Allocate a new node from the free list
        // node = new_node()
        // Make it the only node in the linked list
        // node->next.ptr = NULL
        Node node = new Node(0);

        // Both Head and Tail point to it
        // Q->Head.ptr = Q->Tail.ptr = node
        head = new AtomicReference<Node>(node);
        tail = new AtomicReference<Node>(node);
    }

    // enqueue(Q: pointer to queue_t, value: data type)
    public boolean enq(Integer value) {
        // Allocate a new node from the free list
        //node = new_node()
        // Copy enqueued value into node
        // node->value = value
        // Set next pointer of node to NULL
        // node->next.ptr = NULL
        Node node = new Node(value);

        // Keep trying until Enqueue is done
        // loop
        while(true) {
            // Read Tail.ptr and Tail.count together
            // tail = Q->Tail
            AtomicReference<Node> currTail = tail;

            // Read next ptr and count fields together
            // next = tail.ptr->next
            AtomicReference<Node> next = currTail.get().next;

            // Are tail and next consistent?
            // if tail == Q->Tail
            if (currTail == tail) {
                // Was Tail pointing to the last node?
                //if next.ptr == NULL
                if (next.get() == null) {
                    // Try to link node at the end of the linked list
                    // if CAS(&tail.ptr->next, next, <node, next.count+1>)
                    if (next.compareAndSet(null, node)) {
                        // Enqueue is done.  Exit loop
                        // break
                        break;
                    } // if
                    // Tail was not pointing to the last node
                    else {
                        // Try to swing Tail to the next node
                        // CAS(&Q->Tail, tail, <next.ptr, tail.count+1>)
                        tail.compareAndSet(currTail.get(), next.get());
                    } // else
                } // if
            } // while()
            // Enqueue is done.  Try to swing Tail to the inserted node
            // CAS(&Q->Tail, tail, <node, tail.count+1>)
            tail.compareAndSet(currTail.get(), node);
        }
        return true;
    }

    // dequeue(Q: pointer to queue_t, pvalue: pointer to data type): boolean
    public Integer deq() {
        AtomicReference<Node> currHead;
        // Keep trying until Dequeue is done
        // loop
        while(true) {
            // Read Head
            // head = Q->Head
            currHead = head;

            // Read Tail
            // tail = Q->Tail
            AtomicReference<Node> currTail = tail;

            // Read Head.ptr->next
            // next = head.ptr->next
            AtomicReference<Node> next = head.get().next;

            // Are head, tail, and next consistent?
            // if head == Q->Head
            if (head == currHead) {
                // Is queue empty or Tail falling behind?
                // if head.ptr == tail.ptr
                if (currHead == currTail) {
                    // Is queue empty?
                    // if next.ptr == NULL
                    if (next.get() == null) {
                        // Queue is empty, couldn't dequeue
                        // return FALSE
                        return null;
                    }
                    // Tail is falling behind.  Try to advance it
                    // CAS(&Q->Tail, tail, <next.ptr, tail.count+1>)
                    tail.compareAndSet(currTail.get(), next.get());

                    // No need to deal with Tail
                    // else
                } else {
                    // Read value before CAS, otherwise another dequeue might free the next node
                    // Try to swing Head to the next node
                    // *pvalue = next.ptr->value
                    Node pointer = next.get();

                    // if CAS(&Q->Head, head, <next.ptr, head.count+1>)
                    if (head.compareAndSet(currHead.get(),currHead.get().next.get())) {
                        // Dequeue is done.  Exit loop
                        // break
                        break;
                    } // if
                } // else
            } // if
        } // while()
        // It is safe now to free the old node
        // free(head.ptr)
        // JAVA has garbage clean up....
        // Queue was not empty, dequeue succeeded
        // return TRUE
        return currHead.get().value;
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
}
