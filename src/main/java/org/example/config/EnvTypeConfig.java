package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 环境类型配置
 */
@Component
public class EnvTypeConfig {

    private static String envType;

    @Value("${app.env.type:dev}")
    public void setEnvType(String envType) {
        EnvTypeConfig.envType = envType;
    }

    /**
     * 获取当前环境类型
     * @return 环境类型
     */
    public static String getEnvType() {
        return envType != null ? envType : "dev";
    }
}