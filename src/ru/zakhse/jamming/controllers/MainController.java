package ru.zakhse.jamming.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import jdk.management.resource.internal.inst.ThreadRMHooks;
import ru.zakhse.jamming.lattice.ExperimentExecutor;
import ru.zakhse.spinner.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController {

    enum AppStatus {STOPPED, RUNNING, PAUSED}

    public AppStatus status = AppStatus.STOPPED;

    @FXML
    public Label leftStatus;
    public Label rightStatus;
    public ProgressBar progressBar;

    public LineChart<Number, Number> graph;
    public NumberAxis xAxis;
    public NumberAxis yAxis;
    public ObservableList<XYChart.Data<Number, Number>> graphData;

    public Spinner<Integer> repeatsSpinner;
    public Spinner<Integer> latticeSizeSpinner;
    public SpinnerValueFactory.IntegerSpinnerValueFactory latticeSizeSpinnerFactory;
    public SpinnerValueFactory.IntegerSpinnerValueFactory repeatsSpinnerFactory;
    public Button startButton;
    public Button stopButton;

    @FXML
    public void initialize() {
        optionsInit();
        graphInit();
    }

    private void optionsInit() {
        latticeSizeSpinnerFactory.setValue(100);
        repeatsSpinnerFactory.setValue(100);
        IntegerStringConverter.createFor(latticeSizeSpinner);
        IntegerStringConverter.createFor(repeatsSpinner);

        latticeSizeSpinner.valueProperty().addListener(((observable, oldValue, newValue) -> {
            xAxis.setUpperBound(newValue);
        }));
    }

    private void graphInit() {
        xAxis.setLabel("k-mer size");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(2);
        xAxis.setUpperBound(latticeSizeSpinner.getValue());
        //xAxis.setTickUnit(2);

        yAxis.setLabel("Filled part of lattice");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0.0);
        yAxis.setUpperBound(1.0);
        yAxis.setTickUnit(0.1);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        graphData = FXCollections.observableArrayList();
        /*graphData.add(new XYChart.Data<>(20, 0.7));
        graphData.add(new XYChart.Data<>(30, 0.8));
        graphData.add(new XYChart.Data<>(40, 0.6));
        graphData.add(new XYChart.Data<>(50, 0.5));*/
        series.setData(graphData);

        graph.getData().add(series);
        graph.setCreateSymbols(false);
    }

    public void startAction(ActionEvent actionEvent) {
        startButton.setDisable(true);
        stopButton.setDisable(false);
        latticeSizeSpinner.setDisable(true);
        repeatsSpinner.setDisable(true);
        status = AppStatus.RUNNING;

        Thread kek = new Thread(() -> {
            int kmerSize = 2;
            int allExperimentsAmount = latticeSizeSpinner.getValue() - kmerSize + 1;

            while (allExperimentsAmount > 0) {
                int newTasksAmount = allExperimentsAmount - 3 > 0 ? 3 : allExperimentsAmount;
                allExperimentsAmount -= newTasksAmount;
                CountDownLatch latch = new CountDownLatch(newTasksAmount);

                ExecutorService service = Executors.newCachedThreadPool();
                for (int j = 0; j < newTasksAmount; j++) {
                    service.submit(new ExperimentExecutor(graphData, latch, latticeSizeSpinner.getValue(), kmerSize++,
                            repeatsSpinner.getValue()));
                }
                try {latch.await();} catch (InterruptedException e) {e.printStackTrace();}
                service.shutdown();
            }
            stopAction(actionEvent);
        });
        kek.start();

    }

    public void stopAction(ActionEvent actionEvent) {
        startButton.setDisable(false);
        stopButton.setDisable(true);
        latticeSizeSpinner.setDisable(false);
        repeatsSpinner.setDisable(false);
        status = AppStatus.STOPPED;
    }
}
