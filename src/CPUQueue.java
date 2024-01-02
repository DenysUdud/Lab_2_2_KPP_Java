import java.util.LinkedList;
import java.util.Queue;

public class CPUQueue {
    private Queue<CPUProcess> queue = new LinkedList<>();
    private int maxQueueLength = 0;

    public synchronized void enqueue(CPUProcess process) {
        queue.add(process);
        if (queue.size() > maxQueueLength) {
            maxQueueLength = queue.size();
        }
    }

    public synchronized CPUProcess dequeue() {
        return queue.poll();
    }

    public int getMaxQueueLength() {
        return maxQueueLength;
    }
}
