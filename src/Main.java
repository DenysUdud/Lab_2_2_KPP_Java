import java.util.LinkedList;
import java.util.Queue;

class CPUQueue {
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

class CPUProcess extends Thread {
    private static final int MIN_INTERVAL = 1;
    private static final int MAX_INTERVAL = 50000;
    private static final int MIN_PROCESSING_TIME = 50;
    private static final int MAX_PROCESSING_TIME = 200000;

    private CPUQueue queue;

    public CPUProcess(CPUQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep((long) (Math.random() * (MAX_INTERVAL - MIN_INTERVAL) + MIN_INTERVAL));

                synchronized (queue) {
                    queue.enqueue(this);
                }

                synchronized (this) {
                    wait();
                }

                int processingTime = (int) (Math.random() * (MAX_PROCESSING_TIME - MIN_PROCESSING_TIME) + MIN_PROCESSING_TIME);
                Thread.sleep(processingTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class CPU extends Thread {
    private CPUQueue queue;

    public CPU(CPUQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            CPUProcess process;
            synchronized (queue) {
                process = queue.dequeue();
            }

            if (process != null) {
                synchronized (process) {
                    process.notify();
                }
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        CPUQueue queue = new CPUQueue();
        CPUProcess process = new CPUProcess(queue);
        CPU cpu = new CPU(queue);

        process.start();
        cpu.start();

        try {
            Thread.sleep(100000); // Run the simulation for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        process.interrupt();
        cpu.interrupt();

        System.out.println("Max Queue Length: " + queue.getMaxQueueLength());
    }
}
