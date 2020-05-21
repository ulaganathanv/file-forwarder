package com.sample.util;

import com.sample.FileForwarderApplication;

import java.io.InputStream;
import java.util.Properties;

public class PropertyReader extends Properties {
    private static PropertyReader instance = null;

    private PropertyReader() {
    }

    public static PropertyReader getInstance() {
        if (instance == null) {
            try {
                instance = new PropertyReader();
                InputStream inputStream = FileForwarderApplication.class.getClassLoader().getResourceAsStream("application.properties");
                instance.load(inputStream);
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return instance;
    }
}
