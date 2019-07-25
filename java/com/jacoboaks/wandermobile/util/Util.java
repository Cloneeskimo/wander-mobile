package com.jacoboaks.wandermobile.util;

import android.util.Log;

import com.jacoboaks.wandermobile.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
     * @purpose is to log an error and return
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
}
