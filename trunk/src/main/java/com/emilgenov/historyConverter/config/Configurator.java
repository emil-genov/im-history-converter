package com.emilgenov.historyConverter.config;

/**
 * User: Emil Genov
 * Date: 2010-12-9
 * Time: 15:05:01
 */
public interface Configurator {
    String getConfigValue(String pName);

    String getAccountName(String pAccountId);

    boolean isConvertMailAddressesForAccount(String pAccountId);
}
