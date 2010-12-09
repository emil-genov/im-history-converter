package com.emilgenov.historyConverter.gmail;

import com.emilgenov.historyConverter.api.BackEnd;
import com.emilgenov.historyConverter.api.model.Account;
import com.emilgenov.historyConverter.api.model.History;
import com.emilgenov.historyConverter.api.model.HistoryItem;
import com.emilgenov.historyConverter.config.Configurator;
import com.emilgenov.historyConverter.util.Util;

import javax.activation.DataHandler;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * User: Emil Genov
 * Date: 2010-12-8
 * Time: 20:56:10
 */
public class GmailBackEnd implements BackEnd {
    private final static String GMAIL_USERNAME = "gmail.username";
    private final static String GMAIL_PASSWORD = "gmail.password";
    private final static String GMAIL_LABEL = "gmail.label";
    private final static String SUBJECT_PATTERN = "gmail.history-item.pattern";
    private final static String DATE_PATTERN = "gmail.history-item.date-pattern";

    private SimpleDateFormat simpleDateFormat;
    private Configurator configurator;
    private String gmailUserName;
    private String gmailPassword;
    private String gmailLabel;
    private String subjectPattern;
    private String datePattern;

    public void setConfigurator(final Configurator pConfigurator) {
        configurator = pConfigurator;
        gmailUserName = configurator.getConfigValue(GMAIL_USERNAME);
        gmailPassword = configurator.getConfigValue(GMAIL_PASSWORD);
        gmailLabel = configurator.getConfigValue(GMAIL_LABEL);
        subjectPattern = configurator.getConfigValue(SUBJECT_PATTERN);
        datePattern = configurator.getConfigValue(DATE_PATTERN);
    }

    public void useHistory(final History pHistory) throws Exception {
        readAccountData(pHistory);
        Session tSession = createSession();
        Store tImapStore = getImapStore(tSession);
        for (Account tAccount : pHistory.getAccounts()) {
            String tFolderName = gmailLabel + "/" + getAccountName(tAccount);
            Folder tFolder = openChatsFolder(tImapStore, tFolderName);
            for (HistoryItem tItem : pHistory.getHistoryItems(tAccount)) {
                System.out.println("Processing message " + tItem.getUser().getName() + ":" + getDateFormatted(tItem.getDate()));
                tFolder.appendMessages(new Message[]{createMessage(tItem, tSession)});
            }
            tFolder.close(true);
            System.out.println("Finished writing in " + tFolderName);
        }
    }

    private Map<Account, String> accountNameMap;
    private Map<Account, Boolean> convertEmailMap;

    private void readAccountData(final History pHistory) {
        accountNameMap = new HashMap<Account, String>();
        convertEmailMap = new HashMap<Account, Boolean>();
        for (Account tAccount : pHistory.getAccounts()) {
            accountNameMap.put(tAccount, configurator.getAccountName(tAccount.getId()));
            convertEmailMap.put(tAccount, configurator.isConvertMailAddressesForAccount(tAccount.getId()));
        }
    }

    private String getAccountName(Account tAccount) {
        return accountNameMap.get(tAccount);
    }

    private Session createSession() {
        Properties tProperties = System.getProperties();
        tProperties.setProperty("mail.store.protocol", "imaps");
        return Session.getDefaultInstance(tProperties, null);
    }

    private Store getImapStore(Session pSession) throws MessagingException {
        Store tStore = pSession.getStore("imaps");
        tStore.connect("imap.gmail.com", gmailUserName, gmailPassword);
        return tStore;
    }

    private Folder openChatsFolder(Store pStore, String pLabel) throws MessagingException {
        Folder tFolder = pStore.getFolder(pLabel);
        if (!tFolder.exists()) {
            tFolder.create(Folder.HOLDS_MESSAGES);
        }
        tFolder.open(Folder.READ_WRITE);
        return tFolder;
    }

    private Message createMessage(HistoryItem pHistoryItem, Session pSession) throws MessagingException, IOException {
        Message tMimeMessage = new MimeMessage(pSession);
        String tFrom = pHistoryItem.getUser().getIdentifier();
        if (convertEmailMap.get(pHistoryItem.getUser().getAccount())) {
            tFrom = Util.convertFromCyrillic(removeSpaces(pHistoryItem.getUser().getName()) + "@" + getAccountName(pHistoryItem.getUser().getAccount()));
        }

        tMimeMessage.setFrom(new InternetAddress(tFrom, pHistoryItem.getUser().getName(), "UTF-8"));
        tMimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(gmailUserName + "@gmail.com", false));
        tMimeMessage.setSubject(getSubject(pHistoryItem));
        tMimeMessage.setDataHandler(new DataHandler(new ByteArrayDataSource(pHistoryItem.getText(), "text/html")));
        tMimeMessage.setHeader("X-Mailer", "Chat History Converter");
        tMimeMessage.setSentDate(pHistoryItem.getDate());
        return tMimeMessage;
    }

    private String getSubject(final HistoryItem pHistoryItem) {
        return MessageFormat.format(subjectPattern, pHistoryItem.getUser().getName(), getDateFormatted(pHistoryItem.getDate()));
    }

    private String getDateFormatted(final Date pDate) {
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(datePattern);
        }
        return simpleDateFormat.format(pDate);
    }

    private String removeSpaces(final String pName) {
        return pName.replaceAll(" ", "_");
    }
}
