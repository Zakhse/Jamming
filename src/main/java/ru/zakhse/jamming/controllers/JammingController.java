package ru.zakhse.jamming.controllers;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import ru.zakhse.Timer;
import ru.zakhse.jamming.lattice.ExperimentExecutor;
import ru.zakhse.jamming.lattice.ExperimentProperties;
import ru.zakhse.spinner.*;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.*;

public class JammingController implements Initializable {
    enum AppStatus {READY, RUNNING, PAUSED, FINISHED}

    private AppStatus status = AppStatus.FINISHED;

    @FXML
    private LineChart<Integer, Double> graph;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Spinner<Integer> repeatsSpinner;
    @FXML
    private Spinner<Integer> latticeSizeSpinner;
    @FXML
    private SpinnerValueFactory.IntegerSpinnerValueFactory latticeSizeSpinnerFactory;
    @FXML
    private SpinnerValueFactory.IntegerSpinnerValueFactory repeatsSpinnerFactory;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Label statusValue;
    @FXML
    private MenuItem aboutMenu;


    private ExecutorService executor;
    private Timer timer;
    private AnimationTimer timeRefresher;
    private ExperimentProperties properties;
    private ObservableList<XYChart.Data<Integer, Double>> graphData;
    private ResourceBundle resourceBundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;

        properties = ExperimentProperties.getInstance();
        timer = new Timer();

        timeRefresher = new AnimationTimer() {
            @Override
            public void handle(long now) {
                statusValue.setText(MStoString(timer.countAndGetElapsedTime()));
            }
        };

        loadSettings();
        optionsInit();
        graphInit();
        saveSettings();
    }

    private void optionsInit() {
        IntegerStringConverter.createFor(latticeSizeSpinner);
        IntegerStringConverter.createFor(repeatsSpinner);

        latticeSizeSpinner.valueProperty().addListener(((observable, oldValue, newValue) -> {
            xAxis.setUpperBound(newValue);
            clear();
        }));
        repeatsSpinner.valueProperty().addListener(((observable, oldValue, newValue) -> clear()));
    }

    private void graphInit() {
        xAxis.setLabel(resourceBundle.getString("graph.XTitle"));
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(2);
        xAxis.setUpperBound(latticeSizeSpinner.getValue());

        yAxis.setLabel(resourceBundle.getString("graph.YTitle"));
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0.0);
        yAxis.setUpperBound(1.0);
        yAxis.setTickUnit(0.1);

        XYChart.Series<Integer, Double> graphSeries = new XYChart.Series<>();
        graphSeries.setData(graphData);
        graph.getData().add(graphSeries);
        graph.setCreateSymbols(false);
        graph.setLegendVisible(false);

        // Animation causes some bugs!!!
        graph.setAnimated(false);
    }

    public void startAction() {
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

            ExecutorService service = Executors.newFixedThreadPool(3);
            while (allExperimentsAmount > 0) {
                int newTasksAmount = allExperimentsAmount - 3 > 0 ? 3 : allExperimentsAmount;
                allExperimentsAmount -= newTasksAmount;
                CountDownLatch latch = new CountDownLatch(newTasksAmount);

                for (int j = 0; j < newTasksAmount; j++) {
                    service.submit(new ExperimentExecutor(graphData, latch, latticeSizeSpinner.getValue(), kmerSize++,
                            repeatsSpinner.getValue()));
                }

                try {latch.await();} catch (InterruptedException e) {service.shutdownNow(); return;}
            }
            service.shutdown();
            stopExperiment();
            status = AppStatus.FINISHED;
        });
        executor.shutdown();
    }

    public void stopAction() {
        stopExperiment();
    }


    private Alert aboutAlert;

    public void openAbout() {
        if (aboutAlert == null) {
            aboutAlert = new Alert(Alert.AlertType.INFORMATION);
            aboutAlert.setTitle(null);
            aboutAlert.setHeaderText(aboutMenu.getText());
            aboutAlert.setContentText(resourceBundle.getString("menu.help.about.info"));
        }
        aboutAlert.show();
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

        statusValue.setText(MStoString(timer.countAndGetElapsedTime()));
    }

    private void clear() {
        graphData.clear();
        timer.clear();
        statusValue.setText(MStoString(0));
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
