package com.shadow.jse_notification_service.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileSystemUtil {

    public static String read(String path) {
        StringBuilder fileContents = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            while (line != null) {
                fileContents.append(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContents.toString();
    }
}
