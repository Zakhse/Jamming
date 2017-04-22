package ru.zakhse.jamming.lattice;

import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

/**
 * KEYS:
 * repeats
 * elapsed_time
 * lattice_size
 */
public class ExperimentProperties {
    private JSONObject store;

    private String fileName = "settings.json"; // Name of the file with settings

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSettings() {
        try {
            String res = new String(Files.readAllBytes(Paths.get(fileName)));
            store = new JSONObject(res);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
