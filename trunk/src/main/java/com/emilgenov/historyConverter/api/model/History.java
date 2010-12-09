package com.emilgenov.historyConverter.api.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: Emil Genov
 * Date: 2010-12-5
 * Time: 12:42:56
 */
public class History {
    private final Map<Account, List<HistoryItem>> accountListMap;

    public History() {
        accountListMap = new HashMap<Account, List<HistoryItem>>();
    }

    public List<HistoryItem> getHistoryItems(final Account pAccount) {
        return accountListMap.get(pAccount);
    }

    public Set<Account> getAccounts() {
        return accountListMap.keySet();
    }

    public void addHistoryItem(final HistoryItem pHistoryItem) {
        final Account tAccount = pHistoryItem.getUser().getAccount();
        List<HistoryItem> tItemList = accountListMap.get(tAccount);
        if (tItemList == null) {
            tItemList = new ArrayList<HistoryItem>();
            accountListMap.put(tAccount, tItemList);
        }
        if (!tItemList.contains(pHistoryItem)) {
            tItemList.add(pHistoryItem);
        }
    }
}
