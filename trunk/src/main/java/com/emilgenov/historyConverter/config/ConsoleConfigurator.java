package com.emilgenov.historyConverter.config;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

/**
 * User: Emil Genov
 * Date: 2010-12-9
 * Time: 15:07:10
 */
public class ConsoleConfigurator implements Configurator {
    private Scanner scanner;
    private Properties descriptions;
    private Properties defaults;

    public String getConfigValue(final String pName) {
        String tDescription = descriptions.getProperty(pName);
        String tDefaultValue = defaults.getProperty(pName);
        System.out.print(tDescription + " [" + tDefaultValue + "]:");
        String tUserEntry = scanner.nextLine();
        return (tUserEntry == null || tUserEntry.trim().length() == 0) ? tDefaultValue : tUserEntry;
    }

    public String getAccountName(final String pAccountId) {
        System.out.print("Please provide name for this account (" + pAccountId + ")[" + pAccountId + "]:");
        String tUserEntry = scanner.nextLine();
        return (tUserEntry == null || tUserEntry.trim().length() == 0) ? pAccountId : tUserEntry;
    }

    public boolean isConvertMailAddressesForAccount(final String pAccountId) {
        System.out.print("Do You want addreses for this account (" + pAccountId + ") to be converted. Please use true for facebook accounts [false]");
        return "true".equals(scanner.nextLine());
    }

    public ConsoleConfigurator() {
        scanner = new Scanner(System.in);
        descriptions = new Properties();
        try {
            descriptions.load(ConsoleConfigurator.class.getClassLoader().getResourceAsStream("config.descriptions.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Can't find config.descriptions.properties in classpath", e);
        }
        defaults = new Properties();
        try {
            defaults.load(ConsoleConfigurator.class.getClassLoader().getResourceAsStream("config.default.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Can't find config.default.properties in classpath", e);
        }
    }
}
