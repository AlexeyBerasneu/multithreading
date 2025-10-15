package school.sorokin.javacore;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {
        DataProcessor dataProcessor = new DataProcessor(5);
        try {
            dataProcessor.submitTask(20, 300);
            //Try to show quantity of Threads before get results
            while (dataProcessor.getNonActiveTaskCount() > 0 || dataProcessor.getActiveTaskCount() > 0) {
                System.out.println("active=" + dataProcessor.getActiveTaskCount() + ", notDone=" + dataProcessor.getNonActiveTaskCount());
                Thread.sleep(1000);
                Optional<Integer> task = dataProcessor.getResultByName("Task - 8");
                if (task.isPresent()) {
                    System.out.println(task.get());
                } else {
                    System.out.println("Task not found");
                }
            }
            dataProcessor.collectResults();
            dataProcessor.printResults();
            dataProcessor.shutDown();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}