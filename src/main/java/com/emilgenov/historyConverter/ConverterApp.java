package com.emilgenov.historyConverter;

import com.emilgenov.historyConverter.api.BackEnd;
import com.emilgenov.historyConverter.api.FrontEnd;
import com.emilgenov.historyConverter.api.model.History;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * User: Emil Genov
 * Date: 2010-12-5
 * Time: 13:17:48
 */
public class ConverterApp {

    private static final String FRONT_END_CONFIG = "front-end";
    private static final String BACK_END_CONFIG = "back-end";

    public static void main(String args[]) throws IOException {
        String configFile = "config.properties";
        if (args.length == 1) {
            configFile = args[0];
        }
        final Properties tProperties = new Properties();
        tProperties.load(new FileInputStream(configFile));

        FrontEnd tEnd;
        try {
            tEnd = instantiateFrontEnd(tProperties);
        } catch (Exception e) {
            System.err.println("Problem instantiating front end.");
            e.printStackTrace();
            return;
        }

        final History tHistory;
        try {
            tHistory = tEnd.getHistory();
        } catch (Exception e) {
            System.err.println("Problem using front end.");
            e.printStackTrace();
            return;
        }

        BackEnd tBackEnd;
        try {
            tBackEnd = instantiateBackEnd(tProperties);
        } catch (Exception e) {
            System.err.println("Problem instantiating back end.");
            e.printStackTrace();
            return;
        }

        try {
            tBackEnd.useHistory(tHistory);
        } catch (Exception e) {
            System.err.println("Problem using back end.");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private static FrontEnd instantiateFrontEnd(final Properties pProperties) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final Class<FrontEnd> tClass = (Class<FrontEnd>) Class.forName(pProperties.getProperty(FRONT_END_CONFIG));
        final FrontEnd tEnd = tClass.newInstance();
        tEnd.setConfiguration(pProperties);
        return tEnd;
    }

    private static BackEnd instantiateBackEnd(final Properties pProperties) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final Class<BackEnd> tClass = (Class<BackEnd>) Class.forName(pProperties.getProperty(BACK_END_CONFIG));
        final BackEnd tEnd = tClass.newInstance();
        tEnd.setConfiguration(pProperties);
        return tEnd;
    }
}
