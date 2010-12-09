package com.emilgenov.historyConverter.api.model;

/**
 * User: Emil Genov
 * Date: 2010-12-5
 * Time: 12:43:34
 */
public class Account {
    private String id;

    public String getId() {
        return id;
    }

    public Account(final String pId) {
        id = pId;
    }
}
