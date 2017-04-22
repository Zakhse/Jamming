package ru.zakhse.jamming.lattice;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.sun.javafx.collections.ObservableListWrapper;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.Path;
import java.util.*;

/**
 * KEYS:
 * repeats
 * elapsed_time
 * lattice_size
 */
public class ExperimentProperties {
    private JSONObject store;
    public ObservableList<XYChart.Data<Integer, Double>> graph;

    private String fileName = "settings.json"; // Name of the file with settings
    private String graphFileName = "graph.json"; // Name of the file with graph

    private ExperimentProperties() {
        store = new JSONObject();
    }
    //JSON

    private static class SingletoneHolder {
        private final static ExperimentProperties instance = new ExperimentProperties();
    }

    public static ExperimentProperties getInstance() {
        return SingletoneHolder.instance;
    }


    //region Getters/Setters

    public void putSetting(String key, String value) {
        store.put(key, value);
    }

    public void putSetting(String key, long value) {
        store.put(key, value);
    }

    public String getString(String key) {
        if (store.has(key))
            return store.getString(key);
        else return null;
    }

    public Integer getInt(String key) {
        if (store.has(key))
            return store.getInt(key);
        else return null;
    }
    //endregion

    public void saveSettings() {
        try {
            Files.write(Paths.get(fileName), store.toString().getBytes(), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            serializeGraph();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSettings() {
        try {
            String res = new String(Files.readAllBytes(Paths.get(fileName)));
            store = new JSONObject(res);
        } catch (IOException | JSONException e) {
            System.out.print("Settings loading is failed");
            System.out.print(e.getMessage());
        }

        try {
            deserializeGraph();
        } catch (IOException | JSONException e) {
            System.out.print("Graph loading is failed");
            System.out.print(e.getMessage());
        }
    }

    private void deserializeGraph() throws IOException {
        String res = new String(Files.readAllBytes(Paths.get(graphFileName)));
        ArrayList<XYChart.Data<Integer, Double>> newList = new JSONDeserializer<ArrayList<XYChart.Data<Integer,
                Double>>>()
                .use(null, ArrayList.class)
                .use("values", XYChart.Data.class)
                .use("values.XValue", Integer.class)
                .use("values.YValue", Double.class)
                .deserialize(res);
        graph = FXCollections.observableArrayList(newList);
    }

    private void serializeGraph() throws IOException {
        String res = new JSONSerializer().include("XValue", "YValue").exclude("*").serialize(graph);
        Files.write(Paths.get(graphFileName), res.getBytes(), StandardOpenOption.CREATE, StandardOpenOption
                .TRUNCATE_EXISTING);
    }
}
