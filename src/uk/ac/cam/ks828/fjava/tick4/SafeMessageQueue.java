package uk.ac.cam.ks828.fjava.tick4;

public class SafeMessageQueue<T> implements MessageQueue<T> {
    private static class Link<L> {
        L val;
        Link<L> next;
        Link(L val) {
            this.val = val;
            this.next = null;
        }
    }
    private Link<T> first = null;
    private Link<T> last = null;

    // Add a new element with value val as the last Link<T> in the queue
    public synchronized void put(T val) {
        Link<T> node = new Link<>(val);
        if (first == null && last == null) {
            first = node;
            last = node;
        }  else {
            last.next = node;
            last = node;
        }
        this.notify();
    }

    // Return and remove first element in the queue
    public synchronized T take() {
        // Block until there is an element in the queue to be taken
        while(first == null) {
            try {
                this.wait();
            } catch(InterruptedException ie) {
                // Ignored exception
            }
        }
        T val = first.val;
        if (first == last) {
            first = null;
            last = null;
        } else {
            first = first.next;
        }
        return val;
    }
}