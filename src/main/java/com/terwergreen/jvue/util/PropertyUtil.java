package com.terwergreen.jvue.util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @Author Terwer
 * @Date 2018/12/2 19:23
 * @Version 1.0
 * @Description 读取属性的公共方法
 **/
public class PropertyUtil {
    private static final Log logger = LogFactory.getLog(PropertyUtil.class);

    /**
     * 根据文件名读取属性
     *
     * @param propertyFileName
     * @return
     */
    public static Configuration readConfig(String propertyFileName) {
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setFileName(propertyFileName));
        try {
            Configuration config = builder.getConfiguration();
            return config;
        } catch (ConfigurationException cex) {
            // loading of the configuration file failed
            logger.error("loading of the configuration file failed", cex);
        }
        return null;
    }

    /**
     * 读取Spring Boot项目的属性
     *
     * @param key
     * @return
     */
    public static String readProperty(String key) {
        Configuration config = readConfig("application.properties");
        String acrive = config.getString("spring.profiles.active");
        if (acrive.equals("dev")) {
            config = readConfig("application-dev.properties");
        } else {
            config = readConfig("application-pro.properties");
        }
        return config.getString(key);
    }
}
