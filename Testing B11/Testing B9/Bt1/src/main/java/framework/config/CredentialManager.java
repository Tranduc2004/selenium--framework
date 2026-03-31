// src/main/java/framework/config/CredentialManager.java
package framework.config;

public class CredentialManager {

    public static String getUsername() {
        // Ưu tiên đọc từ biến môi trường (khi chạy CI/CD)
        String username = System.getenv("APP_USERNAME");
        if (username == null || username.isBlank()) {
            // Fallback: đọc từ file config (khi chạy local)
            username = ConfigReader.getInstance().getProperty("app.username");
        }
        return username;
    }

    public static String getPassword() {
        // Ưu tiên đọc từ biến môi trường (khi chạy CI/CD)
        String password = System.getenv("APP_PASSWORD");
        if (password == null || password.isBlank()) {
            // Fallback: đọc từ file config (khi chạy local)
            password = ConfigReader.getInstance().getProperty("app.password");
        }
        return password;
    }
}