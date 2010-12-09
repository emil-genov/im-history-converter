package com.emilgenov.historyConverter.api.model;

/**
 * User: Emil Genov
 * Date: 2010-12-5
 * Time: 12:30:41
 */
public class User {
    private String identifier;
    private String name;
    private byte[] picture;
    private Account account;

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public byte[] getPicture() {
        return picture;
    }

    public Account getAccount() {
        return account;
    }

    public User(final String pIdentifier, final String pName, final byte[] pPicture, final Account pAccount) {
        identifier = pIdentifier;
        name = pName;
        picture = pPicture;
        account = pAccount;
    }
}
