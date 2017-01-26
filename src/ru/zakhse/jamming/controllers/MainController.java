package ru.zakhse.jamming.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import ru.zakhse.spinner.*;

public class MainController {
    @FXML
    public Label leftStatus;
    public Label rightStatus;
    public ProgressBar progressBar;
    public LineChart<Number, Number> graph;
    public Spinner<Integer> repeatsSpinner;
    public Spinner<Integer> kmerSizeSpinner;
    public Spinner<Integer> latticeSizeSpinner;
    public SpinnerValueFactory.IntegerSpinnerValueFactory latticeSizeSpinnerFactory;
    public SpinnerValueFactory.IntegerSpinnerValueFactory kmerSizeSpinnerFactory;
    public SpinnerValueFactory.IntegerSpinnerValueFactory repeatsSpinnerFactory;

    @FXML
    public void initialize() {
        optionsInit();
    }

    private void optionsInit() {
        latticeSizeSpinnerFactory.setValue(100);
        kmerSizeSpinnerFactory.setValue(10);
        repeatsSpinnerFactory.setValue(100);
        IntegerStringConverter.createFor(latticeSizeSpinner);
        IntegerStringConverter.createFor(kmerSizeSpinner);
        IntegerStringConverter.createFor(repeatsSpinner);
    }
}
