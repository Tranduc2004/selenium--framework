package framework.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static ConfigReader instance;
    private Properties properties;

    private ConfigReader() {
        properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            properties.load(fis);
        } catch (IOException e) {
            System.out.println("config.properties not found, using env variables only");
        }
    }

    public static ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key, "");
    }

    // ← THÊM METHOD NÀY
    public String getBaseUrl() {
        String url = System.getenv("BASE_URL");
        if (url == null || url.isBlank()) {
            url = properties.getProperty("base.url", "https://www.saucedemo.com");
        }
        return url;
    }

    // ← THÊM METHOD NÀY
    public String getUsername() {
        String username = System.getenv("APP_USERNAME");
        if (username == null || username.isBlank()) {
            username = properties.getProperty("app.username", "");
        }
        return username;
    }

    // ← THÊM METHOD NÀY
    public String getPassword() {
        String password = System.getenv("APP_PASSWORD");
        if (password == null || password.isBlank()) {
            password = properties.getProperty("app.password", "");
        }
        return password;
    }
}