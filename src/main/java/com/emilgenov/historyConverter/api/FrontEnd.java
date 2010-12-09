package com.emilgenov.historyConverter.api;

import com.emilgenov.historyConverter.api.model.History;

/**
 * User: Emil Genov
 * Date: 2010-12-5
 * Time: 12:49:57
 */
public interface FrontEnd extends Configurable {
    History getHistory() throws Exception;
}
