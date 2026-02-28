package ads.user_management_service.util;

import org.springframework.beans.factory.annotation.Value;

public final class ConfigUtil {

    private static String applicationName;

    @Value("${spring.application.name:unknown-service}")
    public void setApplicationName(String name) {
        applicationName = name;
    }

    public static String getApplicationName() {
        return applicationName != null ? applicationName : "unknown-service";
    }
}
