package com.emilgenov.historyConverter.api.model;

import java.util.Date;

/**
 * User: Emil Genov
 * Date: 2010-12-5
 * Time: 12:29:15
 */
public class HistoryItem {
    private Date date;
    private User user;
    private String text;

    public HistoryItem(final Date pDate, final User pUser, final String pText) {
        date = pDate;
        user = pUser;
        text = pText;
    }

    public Date getDate() {
        return date;
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final HistoryItem that = (HistoryItem) o;

        if (date != null ? !date.equals(that.date) : that.date != null) {
            return false;
        }
        if (user != null ? !user.equals(that.user) : that.user != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int tresult = date != null ? date.hashCode() : 0;
        tresult = 31 * tresult + (user != null ? user.hashCode() : 0);
        return tresult;
    }
}
