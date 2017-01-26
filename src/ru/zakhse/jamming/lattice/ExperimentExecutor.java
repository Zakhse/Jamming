package ru.zakhse.jamming.lattice;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class ExperimentExecutor {
    private int latticeSize = 100;
    private int kmerSize = 10;
    private int repeats = 50;
    private int numberOfThreads = 4;

    public ExperimentExecutor(int latticeSize, int kmerSize, int repeats, int numberOfThreads) {
        this(latticeSize, kmerSize, repeats);
        this.numberOfThreads = numberOfThreads;
    }

    public ExperimentExecutor(int latticeSize, int kmerSize, int repeats) {
        this.latticeSize = latticeSize;
        this.kmerSize = kmerSize;
        this.repeats = repeats;
    }

    //region Setters
    public void setLatticeSize(int latticeSize) {
        this.latticeSize = latticeSize;
    }

    public void setKmerSize(int kmerSize) {
        this.kmerSize = kmerSize;
    }

    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }
    //endregion

    //region Getters
    public int getLatticeSize() {

        return latticeSize;
    }

    public int getKmerSize() {
        return kmerSize;
    }

    public int getRepeats() {
        return repeats;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }
    //endregion

    public static void main(String[] args) {
        int repeats = 70;
        int sizeOfMatrix = 100; // N x N
        int kmerSize = 14;
        int numberOfThreads = 4;

        long time1 = System.currentTimeMillis();

        //region Running a series of experiments
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);
        List<Future<Double>> resultList = new LinkedList<>();
        for (int i = 0; i < repeats; i++) {
            resultList.add(executor.submit(new FieldGenerator(sizeOfMatrix, kmerSize)));
        }
        double counter = 0.0;
        for (Future<Double> future : resultList) {
            try {counter += future.get();} catch (InterruptedException | ExecutionException e) {e.printStackTrace();}
        }
        executor.shutdown();
        System.out.printf("Average filled part = %.3f\n", counter / repeats);
        //endregion

        long time2 = System.currentTimeMillis();
        System.out.printf("Elapsed time: %.2f seconds\n", (time2 - time1) / 1000.0);
    }
}

