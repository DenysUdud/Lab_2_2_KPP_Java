import java.util.LinkedList;
import java.util.Queue;

class CPUProcess extends Thread {
    private static final int MIN_INTERVAL = 100;
    private static final int MAX_INTERVAL = 1000;
    private static final int MIN_PROCESSING_TIME = 50;
    private static final int MAX_PROCESSING_TIME = 500;

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
        int numberOfProcesses = 5;

        CPUProcess[] processes = new CPUProcess[numberOfProcesses];
        for (int i = 0; i < numberOfProcesses; i++) {
            processes[i] = new CPUProcess(queue);
            processes[i].start();
        }

        CPU cpu = new CPU(queue);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < numberOfProcesses; i++) {
            processes[i].interrupt();
        }

        cpu.interrupt();

        System.out.println("Max Queue Length: " + queue.getMaxQueueLength());
    }
}
