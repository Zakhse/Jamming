package ru.zakhse.jamming.lattice;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Class to execute experiment with provided parameters
 */
public class ExperimentExecutor implements Runnable {
    private int latticeSize;
    private int kmerSize;
    private int repeats;
    private int numberOfThreads = 4;
    private ObservableList<XYChart.Data<Integer, Double>> graphData;
    private CountDownLatch latch;

    public ExperimentExecutor(ObservableList<XYChart.Data<Integer, Double>> graphData, CountDownLatch latch, int
            latticeSize, int kmerSize, int repeats) {
        this.latch = latch;
        this.graphData = graphData;
        this.latticeSize = latticeSize;
        this.kmerSize = kmerSize;
        this.repeats = repeats;

    }

    /**
     * Runs the experiment
     */
    @Override
    public void run() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);
        List<Future<Double>> resultList = new LinkedList<>();
        for (int i = 0; i < repeats; i++) {
            resultList.add(executor.submit(new FieldGenerator(latticeSize, kmerSize)));
        }
        double counter = 0.0;
        for (Future<Double> future : resultList) {
            try {counter += future.get();} catch (InterruptedException | ExecutionException e) {
                latch.countDown(); executor.shutdownNow(); return;
            }
        }
        executor.shutdown();
        double finalCounter = counter;
        Platform.runLater(() -> graphData.add(new XYChart.Data<>(kmerSize, finalCounter / repeats)));
        latch.countDown();
    }
}

