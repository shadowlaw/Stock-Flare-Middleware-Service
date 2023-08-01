package com.shadow.jse_middleware_service;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ImageUtils {

    public static String encodeFileToBase64Binary(String path, String mediaType) throws IOException {
        File file = new File(path);
        FileInputStream fileInputStreamReader = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fileInputStreamReader.read(bytes);
        return new StringBuilder("data:").append(mediaType).append(";base64,").append(new String(Base64.encodeBase64(bytes), StandardCharsets.UTF_8)).toString();
    }

    public static String encodeFileToBase64Binary(String path) throws IOException {
        return encodeFileToBase64Binary(path, MediaType.IMAGE_PNG_VALUE);
    }
}
