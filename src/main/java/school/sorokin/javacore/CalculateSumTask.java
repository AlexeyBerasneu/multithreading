package school.sorokin.javacore;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class CalculateSumTask implements Callable<Integer> {
    private String taskName;
    private List<Integer> listOfNumbers;
    private AtomicInteger activeCounter;

    public CalculateSumTask(String taskName, List<Integer> listOfNumbers, AtomicInteger activeCounter) {
        this.taskName = taskName;
        this.listOfNumbers = listOfNumbers;
        this.activeCounter = activeCounter;
    }

    @Override
    public Integer call() throws Exception {
        activeCounter.incrementAndGet();
        try {
            System.out.println("Starting task: " + taskName);
            System.out.println("Thread name: " + Thread.currentThread().getName());
            Integer sum = listOfNumbers.stream().reduce(0, Integer::sum);
            Thread.sleep(200);
            return sum;
        } finally {
            activeCounter.decrementAndGet();
        }
    }

    public String getTaskName() {
        return taskName;
    }
}
