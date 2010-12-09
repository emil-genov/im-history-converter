package com.emilgenov.historyConverter;

import com.emilgenov.historyConverter.api.BackEnd;
import com.emilgenov.historyConverter.api.FrontEnd;
import com.emilgenov.historyConverter.api.model.History;
import com.emilgenov.historyConverter.config.Configurator;
import com.emilgenov.historyConverter.config.ConsoleConfigurator;
import com.emilgenov.historyConverter.config.PropertiesConfigurator;

import java.io.IOException;

/**
 * User: Emil Genov
 * Date: 2010-12-5
 * Time: 13:17:48
 */
public class ConverterApp {

    private static final String FRONT_END_CONFIG = "front-end";
    private static final String BACK_END_CONFIG = "back-end";

    public static void main(String args[]) throws IOException {
        Configurator tConfigurator;
        if (args.length == 1) {
            tConfigurator = new PropertiesConfigurator(args[0]);
        } else {
            tConfigurator = new ConsoleConfigurator();
        }

        FrontEnd tEnd;
        try {
            tEnd = instantiateFrontEnd(tConfigurator);
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
            tBackEnd = instantiateBackEnd(tConfigurator);
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

    private static FrontEnd instantiateFrontEnd(final Configurator pConfigurator) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final Class<FrontEnd> tClass = (Class<FrontEnd>) Class.forName(pConfigurator.getConfigValue(FRONT_END_CONFIG));
        final FrontEnd tEnd = tClass.newInstance();
        tEnd.setConfigurator(pConfigurator);
        return tEnd;
    }

    private static BackEnd instantiateBackEnd(final Configurator pConfigurator) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final Class<BackEnd> tClass = (Class<BackEnd>) Class.forName(pConfigurator.getConfigValue(BACK_END_CONFIG));
        final BackEnd tEnd = tClass.newInstance();
        tEnd.setConfigurator(pConfigurator);
        return tEnd;
    }
}
