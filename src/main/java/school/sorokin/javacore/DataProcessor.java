package school.sorokin.javacore;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class DataProcessor {
    private final AtomicInteger count = new AtomicInteger(1);// Count for Task
    private final AtomicInteger activeCounter = new AtomicInteger(0);// Count for detecting Thread
    private final ExecutorService executor;
    private final List<Future<Integer>> futures;
    private final List<CalculateSumTask> listOfTasks;
    private final Map<String, Integer> results;

    public DataProcessor(int quantityOfThreads) {
        this.executor = Executors.newFixedThreadPool(quantityOfThreads);
        futures = new ArrayList<>();
        listOfTasks = new ArrayList<>();
        results = new HashMap<>();
    }

    public void submitTask(int taskCount, int countNumbers) throws InterruptedException, ExecutionException {
        for (int i = 1; i <= taskCount; i++) {
            String name = "Task - " + count.getAndIncrement();
            listOfTasks.add(new CalculateSumTask(name, getConstantListOfNumbers(countNumbers), activeCounter));
        }
        for (CalculateSumTask task : listOfTasks) {
            futures.add(executor.submit(task));
        }
    }

    public void collectResults() throws ExecutionException, InterruptedException {
        for (int i = 0; i < listOfTasks.size(); i++) {
            Integer val = futures.get(i).get();
            synchronized (results) {
                results.put(listOfTasks.get(i).getTaskName(), val);
            }
        }
    }

    public void printResults() {
        synchronized (results) {
            for (Map.Entry<String, Integer> entry : results.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    public int getActiveTaskCount() {
        return activeCounter.get();
    }

    public int getNonActiveTaskCount() {
        int count = 0;
        for (Future<Integer> future : futures) {
            if (!future.isDone()) {
                count++;
            }
        }
        return count;
    }

    public Optional<Integer> getResultByName(String taskName) throws ExecutionException, InterruptedException {
        int position = 0;
        for (int i = 0; i < listOfTasks.size(); i++) {
            if (listOfTasks.get(i).getTaskName().equals(taskName)) {
                position = i;
            }
        }
        if (futures.get(position).isDone()) {
            return Optional.of(futures.get(position).get());
        } else {
            return Optional.empty();
        }
    }

    public void shutDown() {
        executor.shutdown();
    }

    private List<Integer> getRandomListOfNumbers(int quantity) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            list.add((int) (Math.random() * 300));
        }
        return list;
    }

    private List<Integer> getConstantListOfNumbers(int quantity) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            list.add(i);
        }
        return list;
    }
}
