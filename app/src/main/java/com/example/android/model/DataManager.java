package com.example.android.model;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Simple serializer for UserData stored in app-internal storage.
 */
public final class DataManager {
    private static final String DATA_FILE = "userdata.ser";

    private DataManager() {}

    public static UserData load(Context context) {
        File file = new File(context.getFilesDir(), DATA_FILE);
        if (!file.exists()) {
            return new UserData();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (UserData) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new UserData();
        }
    }

    public static void save(Context context, UserData data) {
        File file = new File(context.getFilesDir(), DATA_FILE);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
