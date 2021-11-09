package util;

import java.io.*;
import java.util.Properties;

public class PropFile {
    public static String getProperty(String property) {
        Properties propFile = new Properties();
        try {
            propFile.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
            return propFile.getProperty(property);
        } catch(IOException e) {
            return null;
        }
    }

    public static void setProperty(String name, String value){
        Properties propFile = new Properties();
        try {
            propFile.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
            propFile.setProperty(name, value);
            propFile.store(new FileOutputStream("config.properties"), null);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
