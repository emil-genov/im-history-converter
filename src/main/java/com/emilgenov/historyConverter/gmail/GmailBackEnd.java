package com.emilgenov.historyConverter.gmail;

import com.emilgenov.historyConverter.api.BackEnd;
import com.emilgenov.historyConverter.api.model.Account;
import com.emilgenov.historyConverter.api.model.History;
import com.emilgenov.historyConverter.api.model.HistoryItem;
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
import java.util.Properties;

/**
 * User: Emil Genov
 * Date: 2010-12-8
 * Time: 20:56:10
 */
public class GmailBackEnd implements BackEnd {
    private Properties configuration;
    private final static String GMAIL_USERNAME = "gmail.username";
    private final static String GMAIL_PASSWORD = "gmail.password";
    private final static String GMAIL_LABEL = "gmail.label";
    private final static String GMAIL_ACCOUNT_NAME = "gmail.accountName.";
    private final static String CONVERT_ADDRESS = "gmail.convertAddresses.";
    private final static String SUBJECT_PATTERN = "gmail.history-item.pattern";
    private SimpleDateFormat simpleDateFormat;

    public void useHistory(final History pHistory) throws Exception {
        String tLabel = configuration.getProperty(GMAIL_LABEL);
        Session tSession = createSession();
        Store tImapStore = getImapStore(tSession);
        for (Account tAccount : pHistory.getAccounts()) {
            String tFolderName = tLabel + "/" + getAccountName(tAccount);
            Folder tFolder = openChatsFolder(tImapStore, tFolderName);
            for (HistoryItem tItem : pHistory.getHistoryItems(tAccount)) {
                System.out.println("Processing message " + tItem.getUser().getName() + ":" + getDateFormatted(tItem.getDate()));
                tFolder.appendMessages(new Message[]{createMessage(tItem, tSession)});
            }
            tFolder.close(true);
            System.out.println("Finished writing in " + tFolderName);
        }
    }

    private String getAccountName(Account tAccount) {
        if (configuration.getProperty(GMAIL_ACCOUNT_NAME + tAccount.getId()) != null) {
            return configuration.getProperty(GMAIL_ACCOUNT_NAME + tAccount.getId());
        } else {
            return tAccount.getId();
        }
    }

    public void setConfiguration(final Properties pConfiguration) {
        configuration = pConfiguration;
    }

    private Session createSession() {
        Properties tProperties = System.getProperties();
        tProperties.setProperty("mail.store.protocol", "imaps");
        return Session.getDefaultInstance(tProperties, null);
    }

    private Store getImapStore(Session pSession) throws MessagingException {
        String tUserName = configuration.getProperty(GMAIL_USERNAME);
        String tPassword = configuration.getProperty(GMAIL_PASSWORD);
        Store tStore = pSession.getStore("imaps");
        tStore.connect("imap.gmail.com", tUserName, tPassword);
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
        if ("true".equals(configuration.getProperty(CONVERT_ADDRESS + pHistoryItem.getUser().getAccount().getId()))) {
            tFrom = Util.convertFromCyrillic(removeSpaces(pHistoryItem.getUser().getName()) + "@" + getAccountName(pHistoryItem.getUser().getAccount()));
        }

        tMimeMessage.setFrom(new InternetAddress(tFrom, pHistoryItem.getUser().getName(), "UTF-8"));
        tMimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(configuration.getProperty(GMAIL_USERNAME) + "@gmail.com", false));
        tMimeMessage.setSubject(getSubject(pHistoryItem));
        tMimeMessage.setDataHandler(new DataHandler(new ByteArrayDataSource(pHistoryItem.getText(), "text/html")));
        tMimeMessage.setHeader("X-Mailer", "Chat History Converter");
        tMimeMessage.setSentDate(pHistoryItem.getDate());
        return tMimeMessage;
    }

    private String getSubject(final HistoryItem pHistoryItem) {
        String tSubjectPattern = configuration.getProperty(SUBJECT_PATTERN);
        return MessageFormat.format(tSubjectPattern, pHistoryItem.getUser().getName(), getDateFormatted(pHistoryItem.getDate()));
    }

    private String getDateFormatted(final Date pDate) {
        if (simpleDateFormat == null) {
            String tDatePattern = configuration.getProperty("gmail.history-item.date-pattern");
            simpleDateFormat = new SimpleDateFormat(tDatePattern);
        }
        return simpleDateFormat.format(pDate);
    }

    private String removeSpaces(final String pName) {
        return pName.replaceAll(" ", "_");
    }
}
