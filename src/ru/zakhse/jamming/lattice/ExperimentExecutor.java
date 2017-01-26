package ru.zakhse.jamming.lattice;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class ExperimentExecutor implements Runnable {
    private int latticeSize;
    private int kmerSize;
    private int repeats;
    private int numberOfThreads = 4;
    private ObservableList<XYChart.Data<Number, Number>> graphData;
    private CountDownLatch latch;

    public ExperimentExecutor(ObservableList<XYChart.Data<Number, Number>> graphData, CountDownLatch latch, int
            latticeSize, int kmerSize,
                              int repeats, int numberOfThreads) {
        this(graphData, latch, latticeSize, kmerSize, repeats);
        this.numberOfThreads = numberOfThreads;
    }

    public ExperimentExecutor(ObservableList<XYChart.Data<Number, Number>> graphData, CountDownLatch latch, int
            latticeSize, int kmerSize, int repeats) {
        this.latch = latch;
        this.graphData = graphData;
        this.latticeSize = latticeSize;
        this.kmerSize = kmerSize;
        this.repeats = repeats;

    }

    //region Setters
    public void setGraphData(ObservableList<XYChart.Data<Number, Number>> graphData) {this.graphData = graphData;}

    public void setLatticeSize(int latticeSize) {this.latticeSize = latticeSize;}

    public void setKmerSize(int kmerSize) {this.kmerSize = kmerSize;}

    public void setRepeats(int repeats) {this.repeats = repeats;}

    public void setNumberOfThreads(int numberOfThreads) {this.numberOfThreads = numberOfThreads;}
    //endregion

    //region Getters
    public ObservableList<XYChart.Data<Number, Number>> getGraphData() {return graphData;}

    public int getLatticeSize() {return latticeSize;}

    public int getKmerSize() {return kmerSize;}

    public int getRepeats() {return repeats;}

    public int getNumberOfThreads() {return numberOfThreads;}
    //endregion

    @Override
    public void run() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        List<Future<Double>> resultList = new LinkedList<>();
        for (int i = 0; i < repeats; i++) {
            resultList.add(executor.submit(new FieldGenerator(latticeSize, kmerSize)));
        }
        double counter = 0.0;
        for (Future<Double> future : resultList) {
            try {counter += future.get();} catch (InterruptedException | ExecutionException e) {e.printStackTrace();}
        }
        executor.shutdown();
        double finalCounter = counter;
        Platform.runLater(() -> graphData.add(new XYChart.Data<>(kmerSize, finalCounter / repeats)));
        latch.countDown();
    }
}

