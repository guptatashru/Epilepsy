package com.hp.epilepsy.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Created by abdel-az on 3/23/2016.
 */
public class InstallationIdentity
{
    private static String sID = null;
    private static final String INSTALLATION = "INSTALLATION";

    public synchronized static String id(Context context) {
        try {
        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);

                if (!installation.exists())
                    writeInstallationFile(installation);
                sID = readInstallationFile(installation);

        }
        return sID;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String readInstallationFile(File installation) throws IOException {
        try {
            RandomAccessFile f = new RandomAccessFile(installation, "r");
            byte[] bytes = new byte[(int) f.length()];
            f.readFully(bytes);
            f.close();
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void writeInstallationFile(File installation) throws IOException {
        try {
            FileOutputStream out = new FileOutputStream(installation);
            String id = UUID.randomUUID().toString();
            out.write(id.getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
