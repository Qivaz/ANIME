package io.qivaz.anime.file;

import android.annotation.TargetApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Qinghua Zhang @create 2017/4/6.
 */
@TargetApi(21)
public class AnrFileChecker {
    private static final String ANR_FILE = "/data/anr/traces.txt";
    private static boolean anrExist;
    private static long anrLastModified;
    private static long anrLastSize;

    public static void initAppAnrInfo() {
        File anr = new File(ANR_FILE);

        if (anr.exists()) {
            anrExist = true;
            anrLastModified = anr.lastModified();
            anrLastSize = anr.length();
        }
    }

    public static boolean checkAppAnrInfo() {
        if (true) {
            return true;
        }

        File anr = new File(ANR_FILE);

        if (anrExist) {
            long latest = anr.lastModified();
            long size = anr.length();
            return latest > anrLastModified || size != anrLastSize;
        } else {
            return anr.exists();
        }
    }

    public static String getAppAnrInfo() {
        String anr = readFile(ANR_FILE);
        String thread = anr.substring(anr.indexOf("----- pid"), anr.indexOf("----- end"));
        int indexOfMainStart = thread.indexOf("\"main\" prio");
        int indexOfMainEnd = thread.indexOf("\"", indexOfMainStart + "\"main\" prio".length());
        String info = thread.substring(indexOfMainStart, indexOfMainEnd);
        return info;
    }

    public static void readToBuffer(StringBuffer buffer, String filePath) {
        InputStream is = null;
        BufferedReader reader = null;
        try {
            is = new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readFile(String filePath) {
        StringBuffer sb = new StringBuffer();
        readToBuffer(sb, filePath);
        return sb.toString();
    }
}
