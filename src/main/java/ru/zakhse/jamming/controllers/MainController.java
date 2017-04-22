package ru.zakhse.jamming.controllers;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import ru.zakhse.Timer;
import ru.zakhse.jamming.lattice.ExperimentExecutor;
import ru.zakhse.jamming.lattice.ExperimentProperties;
import ru.zakhse.spinner.*;

import java.sql.Time;
import java.util.concurrent.*;

public class MainController {

    enum AppStatus {READY, RUNNING, PAUSED, FINISHED}

    public AppStatus status = AppStatus.FINISHED;

    @FXML
    public Label leftStatus;
    public Label rightStatus;
    public ProgressBar progressBar;

    public LineChart<Integer, Double> graph;
    public NumberAxis xAxis;
    public NumberAxis yAxis;
    public ObservableList<XYChart.Data<Integer, Double>> graphData;

    public Spinner<Integer> repeatsSpinner;
    public Spinner<Integer> latticeSizeSpinner;
    public SpinnerValueFactory.IntegerSpinnerValueFactory latticeSizeSpinnerFactory;
    public SpinnerValueFactory.IntegerSpinnerValueFactory repeatsSpinnerFactory;
    public Button startButton;
    public Button stopButton;

    private ExecutorService executor;

    private Timer timer;
    private AnimationTimer timeRefresher;
    private ExperimentProperties properties;

    @FXML
    public void initialize() {
        properties = ExperimentProperties.getInstance();
        timer = new Timer();


        timeRefresher = new AnimationTimer() {
            @Override
            public void handle(long now) {
                rightStatus.setText(MStoString(timer.countAndGetElapsedTime()));
            }
        };


        loadSettings();
        optionsInit();
        graphInit();
    }

    private void optionsInit() {
        IntegerStringConverter.createFor(latticeSizeSpinner);
        IntegerStringConverter.createFor(repeatsSpinner);

        latticeSizeSpinner.valueProperty().addListener(((observable, oldValue, newValue) -> {
            xAxis.setUpperBound(newValue);
            clear();
        }));
        repeatsSpinner.valueProperty().addListener(((observable, oldValue, newValue) -> {
            clear();
        }));
    }

    private void graphInit() {
        xAxis.setLabel("k-mer size");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(2);
        xAxis.setUpperBound(latticeSizeSpinner.getValue());

        yAxis.setLabel("Filled part of lattice");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0.0);
        yAxis.setUpperBound(1.0);
        yAxis.setTickUnit(0.1);

        XYChart.Series<Integer, Double> graphSeries = new XYChart.Series<>();
        graphSeries.setData(graphData);
        graph.getData().add(graphSeries);
        graph.setCreateSymbols(false);

        // Animation causes some bugs!!!
        graph.setAnimated(false);
    }

    public void startAction(ActionEvent actionEvent) {
        status = AppStatus.RUNNING;
        timer.start();
        timeRefresher.start();
        reviewButtons();
        latticeSizeSpinner.setDisable(true);
        repeatsSpinner.setDisable(true);

        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            int kmerSize;
            if (graphData.size() == 0)
                kmerSize = 2;
            else
                kmerSize = graphData.get(graphData.size() - 1).getXValue() + 1;

            int allExperimentsAmount = latticeSizeSpinner.getValue() - kmerSize + 1;

            while (allExperimentsAmount > 0) {
                int newTasksAmount = allExperimentsAmount - 3 > 0 ? 3 : allExperimentsAmount;
                allExperimentsAmount -= newTasksAmount;
                CountDownLatch latch = new CountDownLatch(newTasksAmount);

                ExecutorService service = Executors.newFixedThreadPool(4);
                for (int j = 0; j < newTasksAmount; j++) {
                    service.submit(new ExperimentExecutor(graphData, latch, latticeSizeSpinner.getValue(), kmerSize++,
                            repeatsSpinner.getValue()));
                }

                try {latch.await();} catch (InterruptedException e) {service.shutdownNow(); return;}
                service.shutdown();
            }

            stopExperiment();
            status = AppStatus.FINISHED;
        });
        executor.shutdown();
    }

    public void stopAction(ActionEvent actionEvent) {
        stopExperiment();
    }

    public void stopExperiment() {
        if (status != AppStatus.RUNNING)
            return;

        executor.shutdownNow();
        status = AppStatus.PAUSED;
        timer.stop();
        timeRefresher.stop();
        latticeSizeSpinner.setDisable(false);
        repeatsSpinner.setDisable(false);
        reviewButtons();
        saveSettings();
    }

    private void reviewButtons() {
        switch (status) {
            case READY:
                stopButton.setDisable(true);
                startButton.setDisable(false);
                break;
            case RUNNING:
                stopButton.setDisable(false);
                startButton.setDisable(true);
                break;
            case PAUSED:
                stopButton.setDisable(true);
                startButton.setDisable(false);
                break;
            case FINISHED:
                stopButton.setDisable(true);
                startButton.setDisable(false);
                break;
        }
    }

    private void saveSettings() {
        properties.putSetting("repeats", repeatsSpinnerFactory.getValue());
        properties.putSetting("lattice_size", latticeSizeSpinnerFactory.getValue());
        properties.putSetting("elapsed_time", timer.getElapsedTime());
        properties.graph = graphData;
    }

    private void loadSettings() {
        properties.loadSettings();
        Integer res = properties.getInt("lattice_size");
        latticeSizeSpinnerFactory.setValue(res != null ? res : 100);

        res = properties.getInt("repeats");
        repeatsSpinnerFactory.setValue(res != null ? res : 100);

        res = properties.getInt("elapsed_time");
        if (res != null)
            timer.addTime(res);

        graphData = properties.graph;
        if (graphData == null)
            graphData = FXCollections.observableArrayList();

        rightStatus.setText(MStoString(timer.countAndGetElapsedTime()));
    }

    private void clear() {
        graphData.clear();
        timer.clear();
        rightStatus.setText(MStoString(0));
        saveSettings();
    }

    /**
     * Converts time in milliseconds to string in format HH:MM:SS
     *
     * @param ms - time in milliseconds
     * @return String representation of time in format HH:MM:SS
     */
    private String MStoString(long ms) {
        long h = TimeUnit.MILLISECONDS.toHours(ms);
        long m = TimeUnit.MILLISECONDS.toMinutes(ms) -
                TimeUnit.HOURS.toMinutes(h);
        long s = TimeUnit.MILLISECONDS.toSeconds(ms) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms));
        return String.format("%02d:%02d:%02d",
                h,
                m,
                s);
    }
}
