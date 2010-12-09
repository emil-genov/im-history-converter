package com.emilgenov.historyConverter.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * User: Emil Genov
 * Date: 2010-12-5
 * Time: 15:12:22
 */
public class Util {

    /**
     * Returns the contents of the file in a byte array
     *
     * @param file File this method should read
     * @return byte[] Returns a byte[] array of the contents of the file
     */
    public static byte[] getBytesFromFile(File file) throws IOException {

        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        /*
         * You cannot create an array using a long type. It needs to be an int
         * type. Before converting to an int type, check to ensure that file is
         * not loarger than Integer.MAX_VALUE;
         */
        if (length > Integer.MAX_VALUE) {
            System.out.println("File is too large to process");
            return null;
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while ((offset < bytes.length) &&
                ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)) {

            offset += numRead;

        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;

    }

    public static Character from[] = {'à', 'á', 'â', 'ã', 'ä', 'å', 'æ', 'ç', 'è', 'é', 'ê', 'ë', 'ì', 'í', 'î', 'ï', 'ð', 'ñ', 'ò', 'ó', 'ô', 'õ', 'ö', '÷', 'ø', 'ø', 'ü', 'ú', 'þ', 'ÿ'};
    public static List<Character> fromList = Arrays.asList(from);
    public static String to[] = {"a", "b", "v", "g", "d", "e", "zh", "z", "i", "j", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "h", "c", "ch", "sh", "sht", "y", "y", "yu", "ya"};

    public static String convertFromCyrillic(String pCyrString) {
        String tRet = "";
        for (int i = 0; i < pCyrString.length(); i++) {
            char c = pCyrString.charAt(i);
            if (Character.isLowerCase(c)) {
                if (fromList.contains(c)) {
                    tRet += to[fromList.indexOf(c)];
                } else {
                    tRet += c;
                }
            } else {
                char lowerChar = Character.toLowerCase(c);
                if (fromList.contains(lowerChar)) {
                    tRet += to[fromList.indexOf(lowerChar)].toUpperCase();
                } else {
                    tRet += c;
                }
            }
        }
        return tRet;
    }
}
