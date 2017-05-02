package ru.zakhse.jamming.lattice;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.*;

/*
 * KEYS:
 * repeats
 * elapsed_time
 * lattice_size
 */

/**
 * Keeps settings of experiment, allows to save and load it
 */
public class ExperimentProperties {
    private String settingsFileName = "settings.json"; // Name of the file with settings
    private String graphFileName = "graph.json"; // Name of the file with graph

    private JSONObject settingsStore;
    public ObservableList<XYChart.Data<Integer, Double>> graph;

    private ExperimentProperties() {
        settingsStore = new JSONObject();
    }

    private static class SingletoneHolder {
        private final static ExperimentProperties instance = new ExperimentProperties();
    }

    /**
     * Returns the only instance of the class (because this class is singleton)
     *
     * @return instance of {@link ExperimentProperties}
     */
    public static ExperimentProperties getInstance() {
        return SingletoneHolder.instance;
    }


    //region Getters/Setters

    /**
     * Put (or replace) the settings item's string value
     *
     * @param key   key for the settings item
     * @param value the value of the settings item
     */
    public void putSetting(String key, String value) {
        settingsStore.put(key, value);
    }

    /**
     * Put (or replace) the settings item's integer value
     *
     * @param key   key for the settings item
     * @param value the value of the settings item
     */
    public void putSetting(String key, Integer value) {
        settingsStore.put(key, value);
    }

    /**
     * Gets the settings item's string value
     *
     * @param key key for the settings item
     * @return the value of the settings item
     */
    public String getString(String key) {
        if (settingsStore.has(key))
            return settingsStore.getString(key);
        else return null;
    }

    /**
     * Gets the settings item's integer value
     *
     * @param key key for the settings item
     * @return the value of the settings item
     */
    public Integer getInt(String key) {
        if (settingsStore.has(key))
            return settingsStore.getInt(key);
        else return null;
    }
    //endregion

    /**
     * Saves settings to files
     */
    public void saveSettings() {
        try {
            Files.write(Paths.get(settingsFileName), settingsStore.toString().getBytes(), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            serializeGraph();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load saved settings from files
     */
    public void loadSettings() {
        try {
            String res = new String(Files.readAllBytes(Paths.get(settingsFileName)));
            settingsStore = new JSONObject(res);
        } catch (IOException | JSONException e) {
            System.out.println("Settings loading is failed");
            System.out.println(e.getMessage());
        }

        try {
            deserializeGraph();
        } catch (IOException | JSONException e) {
            System.out.println("Graph loading is failed");
            System.out.println(e.getMessage());
        }
    }

    private void deserializeGraph() throws IOException {
        String res = new String(Files.readAllBytes(Paths.get(graphFileName)));
        graph = FXCollections.observableArrayList();
        JSONArray graphJSON = new JSONArray(res);
        for (int i = 0; i < graphJSON.length(); i++) {
            JSONObject point = graphJSON.getJSONObject(i);
            Integer x = point.getInt("X");
            Double y = point.getDouble("Y");
            graph.add(new XYChart.Data<>(x, y));
        }
    }

    private void serializeGraph() throws IOException {
        JSONArray graphJSON = new JSONArray();
        for (XYChart.Data<Integer, Double> d : graph) {
            JSONObject point = new JSONObject();
            point.put("X", d.getXValue().intValue());
            point.put("Y", d.getYValue().doubleValue());
            graphJSON.put(point);
        }
        String res = graphJSON.toString();
        Files.write(Paths.get(graphFileName), res.getBytes(), StandardOpenOption.CREATE, StandardOpenOption
                .TRUNCATE_EXISTING);
    }
}
