package org.server.processor;

import org.server.dao.Dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class ProcessingService {
    private final Map<Class, Consumer> processors = new ConcurrentHashMap<>();
    private final BlockingQueue<Object> operationQueue;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private volatile boolean shuttingDown = false;

    public ProcessingService(int queueSize, Map<Class<?>, Consumer<?>> processors, Dao dao) {
        this.operationQueue = new ArrayBlockingQueue<>(queueSize);
        this.processors.putAll(processors);

        executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Object task = operationQueue.take();
                    this.processors.get(task.getClass()).accept(task);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            List<Object> unfinishedTasks = new ArrayList<>();
            operationQueue.drainTo(unfinishedTasks);
            dao.storeUnfinishedTasks(unfinishedTasks);
        });
    }

    public void putOperationForProcessing(Object operation) {
        if (shuttingDown) {
            throw new RuntimeException("Service is shutting down. No new operations accepted.");
        }
        boolean added = operationQueue.offer(operation);

        if (!added) {
            throw new RuntimeException("Service is busy. Please try again later.");
        }
    }

    public void awaitTermination() throws InterruptedException {
        shuttingDown = true;

        final long MAX_WAIT_TIME_MILLIS = 500;
        final long SLEEP_INTERVAL_MILLIS = 100;
        long timeWaited = 0;

        while (operationQueue.size() > 0 && timeWaited < MAX_WAIT_TIME_MILLIS) {
            Thread.sleep(SLEEP_INTERVAL_MILLIS);
            timeWaited += SLEEP_INTERVAL_MILLIS;
        }

        if (operationQueue.size() > 0) {
            System.err.println("Warning: Some tasks were not processed within the given timeout.");
        }

        executor.shutdown();
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            System.err.println("Tasks did not finish in the given timeout.");
            executor.shutdownNow();
        }
    }
}
