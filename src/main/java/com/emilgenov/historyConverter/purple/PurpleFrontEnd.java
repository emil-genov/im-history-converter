package com.emilgenov.historyConverter.purple;

import com.emilgenov.historyConverter.api.FrontEnd;
import com.emilgenov.historyConverter.api.model.Account;
import com.emilgenov.historyConverter.api.model.History;
import com.emilgenov.historyConverter.api.model.HistoryItem;
import com.emilgenov.historyConverter.api.model.User;
import com.emilgenov.historyConverter.util.Util;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * User: Emil Genov
 * Date: 2010-12-5
 * Time: 12:52:02
 */
public class PurpleFrontEnd implements FrontEnd {

    private static final String PATH_CONFIG = "purple.input-path";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd.HHmmssZ");
    private Map<String, User> stringUserMap = new HashMap<String, User>();
    private String[] paths;


    public void setConfiguration(final Properties pConfiguration) {
        final String tPaths = pConfiguration.getProperty(PATH_CONFIG);
        paths = tPaths.split(";");
    }


    public History getHistory() {
        final Map<String, Account> tAccounts = new HashMap<String, Account>();
        final History tHistory = new History();
        for (String path : paths) {
            getHistory(new File(path), tHistory, tAccounts);
        }
        return tHistory;
    }

    public void getHistory(File pInputPath, History pHistory, final Map<String, Account> pAccounts) {
        if (!pInputPath.exists()) {
            throw new RuntimeException("purple.input-path configuration parameter does not point to valid directory in file system.");
        }

        stringUserMap.putAll(getUsers(pInputPath, pAccounts));

        final File tLogsFolder = new File(pInputPath, "logs");
        if (!tLogsFolder.exists()) {
            throw new RuntimeException("logs folder not found, corrupted or incompatible version of purple history.");
        }
        File[] tProtocols = tLogsFolder.listFiles();
        for (File tProtocol : tProtocols) {
            File[] tAccounts = tProtocol.listFiles();
            for (File tAccount : tAccounts) {
                File[] tUsers = tAccount.listFiles();
                for (File tUserFile : tUsers) {
                    String tUserFileName = tUserFile.getName();
                    User tUser = stringUserMap.get(tUserFileName);
                    if (tUser == null) {
                        continue;
                    }
                    System.out.println("Processing user " + tUser.getName() + " on " + tUser.getAccount().getId());
                    File[] tHistoryFiles = tUserFile.listFiles();
                    for (File tHistoryFile : tHistoryFiles) {
                        Date tDate = null;
                        try {
                            tDate = dateFormat.parse(tHistoryFile.getName());
                        } catch (ParseException e) {
                        }
                        String tContent = null;
                        try {
                            tContent = removeHeader(new String(Util.getBytesFromFile(tHistoryFile), Charset.forName("UTF-8")));
                        } catch (IOException e) {
                        }
                        HistoryItem tItem = new HistoryItem(tDate, tUser, tContent);
                        pHistory.addHistoryItem(tItem);
                    }
                }
            }
        }
    }

    private String removeHeader(final String pText) {
        int start = pText.indexOf("<font");
        return pText.substring(start);
    }

    private Map<String, User> getUsers(File pInputPath, final Map<String, Account> pAccounts) {
        final Map<String, User> users = new HashMap<String, User>();

        final File tFile = new File(pInputPath, "blist.xml");
        if (!tFile.exists()) {
            throw new RuntimeException("blist.xml file not found, corrupted or incompatible version of purple history.");
        }

        SAXBuilder builder = new SAXBuilder();
        Document doc;
        try {
            doc = builder.build(new InputSource(new FileInputStream(tFile)));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }

        Element tPurple = doc.getRootElement();
        final Element tBlist = tPurple.getChild("blist");
        final List<Element> tGroups = tBlist.getChildren("group");
        for (Element tGroup : tGroups) {
            final List<Element> tContacts = tGroup.getChildren("contact");
            for (Element tContact : tContacts) {
                final Element tBuddy = tContact.getChild("buddy");
                final String tAccountName = tBuddy.getAttributeValue("account");
                final String tName = tBuddy.getChild("name").getText();
                final String tAlias = tBuddy.getChild("alias").getText();
                final List<Element> tSettings = tBuddy.getChildren("setting");
                String tIcon = null;
                for (Element tSetting : tSettings) {
                    if (tSetting.getAttributeValue("name").equals("buddy_icon")) {
                        tIcon = tSetting.getText();
                        break;
                    }
                }
                Account tAccount = pAccounts.get(tAccountName);
                if (tAccount == null) {
                    tAccount = new Account(tAccountName);
                    pAccounts.put(tAccountName, tAccount);
                }
                users.put(tName, new User(tName, tAlias, getIconFile(tIcon, pInputPath), tAccount));
            }
        }
        return users;
    }

    private byte[] getIconFile(String pFileName, File pInputPath) {
        if (pFileName == null) {
            return null;
        }
        final File tIconFolder = new File(pInputPath, "icons");
        if (!tIconFolder.exists()) {
            throw new RuntimeException("icon folder not found, corrupted or incompatible version of purple history.");
        }
        final File tIconFile = new File(tIconFolder, pFileName);
        try {
            return Util.getBytesFromFile(tIconFile);
        } catch (IOException e) {
            return null;
        }
    }
}
