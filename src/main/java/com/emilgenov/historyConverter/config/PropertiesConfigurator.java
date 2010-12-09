package com.emilgenov.historyConverter.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * User: Emil Genov
 * Date: 2010-12-9
 * Time: 15:06:42
 */
public class PropertiesConfigurator implements Configurator {
    private Properties properties;

    private final static String GMAIL_ACCOUNT_NAME = "gmail.accountName.";
    private final static String CONVERT_ADDRESS = "gmail.convertAddresses.";


    public String getConfigValue(final String pName) {
        return properties.getProperty(pName);
    }

    public String getAccountName(final String pAccountId) {
        return properties.getProperty(GMAIL_ACCOUNT_NAME + pAccountId);
    }

    public boolean isConvertMailAddressesForAccount(final String pAccountId) {
        return "true".equals(properties.getProperty(CONVERT_ADDRESS + pAccountId));
    }

    public PropertiesConfigurator(String pFileName) {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(pFileName));
        } catch (IOException e) {
            throw new RuntimeException("Problem with config file.", e);
        }
    }
}
