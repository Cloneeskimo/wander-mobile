package com.jacoboaks.wandermobile.util;

import android.util.Log;

import com.jacoboaks.wandermobile.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class Util {

    //Data
    public final static boolean DEBUG = true;

    /**
     * @purpose to generate an appropriate log tag for logging information
     * @param sourceFile the name of the source code file from which this method is called
     * @param method the name of the method from which this method is called
     * @return the appropriate log tag for logging
     */
    public final static String getLogTag(String sourceFile, String method) {
        return "[WNDR][" + sourceFile + "][" + method + "]";
    }

    /**
     * @purpose is to log a fatal error and return an appropriate exception to throw
     * @param sourceFile the name of the source code file from which this method is called
     * @param method the name of the method from which this method is called
     * @param error the error message
     * @return the exception to be thrown
     */
    public final static RuntimeException fatalError(String sourceFile, String method, String error) {
        String logTag = Util.getLogTag(sourceFile, method);
        Log.e(logTag, error);
        return new RuntimeException(logTag + ": " + error);
    }

    /**
     * @purpose is to convert an InputStream to a String
     * @param stream the stream to convert
     * @return the converted string
     */
    public final static String inputStreamToString(InputStream stream) {

        //convert to ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;

        //read from ByteArrayOutputStream
        try {
            while ((length = stream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }

            //convert string and return
            return baos.toString("UTF-8");

        //catch any exceptions
        } catch (Exception e) {
            throw Util.fatalError("Util.java", "inputStreamToString(InputStream)",
                    "unable to read from InputStream: " + e.getMessage());
        }
    }

    /**
     * @purpose is to convert a string to a string list by adding breaks every time the given
     * character appears in the string
     * @param value the string to split
     * @param newLine the character to split by
     * @return the list of strings split from the given string
     */
    public final static List<String> stringToStringList(String value, char newLine) {

        //create list
        List<String> result = new ArrayList<>();
        int beginning = 0;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == newLine) {
                result.add(value.substring(beginning, i));
                beginning = i + 1;
            }
        }

        //return list
        return result;
    }

    /**
     * @purpose is to convert a resource file to a list of strings to be parsed
     * @param resourceID the id of the resource to be read
     * @return the list of string available for parsing
     */
    public final static List<String> readResourceFile(int resourceID) {
        InputStream stream = MainActivity.context.getResources().openRawResource(resourceID);
        String data = Util.inputStreamToString(stream);
        return stringToStringList(data, '\n');
    }

    /**
     * @purpose is to convert a float list to an array
     * @param list the list to convert
     * @return the converted list
     */
    public static float[] flistToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] a = new float[size];
        for (int i = 0; i < size; i++) a[i] = list.get(i);
        return a;
    }

    /**
     * @purpose is to convert a integer list to an array
     * @param list the list to convert
     * @return the converted list
     */
    public static int[] ilistToArray(List<Integer> list) {
        int size = list != null ? list.size() : 0;
        int[] a = new int[size];
        for (int i = 0; i < size; i++) a[i] = list.get(i);
        return a;
    }
}
