package uk.ac.cam.ks828.fjava.tick4;

import java.util.HashSet;
import java.util.Set;

public class MultiQueue<T> {
    // Set of active clients
    private Set<MessageQueue<T>> outputs = new HashSet<>();

    // Add client queue to output.
    public synchronized void register(MessageQueue<T> q) {
        outputs.add(q);
    }

    // Remove client queue to output.
    public synchronized void deregister(MessageQueue<T> q) {
        outputs.remove(q);
    }

    // Message is added to all queues in output
    public synchronized void put(T message) {
        for (MessageQueue<T> queue : outputs) {
            queue.put(message);
        }
    }
}