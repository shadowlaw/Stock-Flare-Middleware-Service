package com.shadow.jse_notification_service.monitoring.info;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.shadow.jse_notification_service.util.FileSystemUtil.read;

@Component
public class ApplicationInfoContributor implements InfoContributor {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${app.version.path}")
    private String appVersionPath;

    @Autowired
    private ServletWebServerApplicationContext webServerApplicationContext;

    @Override
    public void contribute(Info.Builder builder) {
        String version = read(appVersionPath);
        builder.withDetail("app-name", appName);
        builder.withDetail("app-version", version);
        try {
            builder.withDetail("app-host", InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            builder.withDetail("app-host", "UNKNOWN");
        }

        builder.withDetail("app-port", webServerApplicationContext.getWebServer().getPort());
    }
}
