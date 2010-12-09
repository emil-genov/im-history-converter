package com.emilgenov.historyConverter.api;

import com.emilgenov.historyConverter.api.model.History;

/**
 * User: Emil Genov
 * Date: 2010-12-5
 * Time: 12:50:42
 */
public interface BackEnd extends Configurable {
    void useHistory(History pHistory) throws Exception;
}
