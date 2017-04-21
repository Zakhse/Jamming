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

    // ELAPSED TIME
    // in milliseconds
    private volatile long elapsedTime;

    public void clear() {
        this.elapsedTime = 0;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public long getElapsedTime() {

        return elapsedTime;
    }

    public synchronized void addTime(long milliseconds) {
        elapsedTime += milliseconds;
    }

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

    public void saveSettings() {
        try {
            Files.write(Paths.get(fileName), store.toString().getBytes(), StandardOpenOption.CREATE);
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
