package ru.zakhse;

public class Methods {
    static public boolean isInteger(String value) {
        if (value == null) {
            return false;
        }
        try {
            new Integer(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
