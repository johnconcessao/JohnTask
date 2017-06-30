package com.xyzcorp.environment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LocalizationConfigurator {
    protected static final Logger LOGGER = LogManager.getLogger(LocalizationConfigurator.class);
    private static volatile LocalizationConfigurator localizationConfigurator;
    private static Properties properties = new Properties();

    private LocalizationConfigurator() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream i18nStream = loader.getResourceAsStream(String.format("i18n/%s.properties", EnvironmentConfigurator.getInstance().getLocalization()));
        try {
            properties.load(i18nStream);
            i18nStream.close();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    public static LocalizationConfigurator getInstance() {
        LocalizationConfigurator sysProps = localizationConfigurator;
        if (sysProps == null) {
            synchronized (EnvironmentConfigurator.class) {
                sysProps = localizationConfigurator;
                if (sysProps == null) {
                    try {
                        localizationConfigurator = sysProps = new LocalizationConfigurator();
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
                }
            }
        }
        return sysProps;
    }

    public String getLocalizedText() {
        return properties.getProperty("label.text");
    }
}
