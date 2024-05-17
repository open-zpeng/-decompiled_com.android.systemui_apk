package com.xiaopeng.systemui.utils;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
/* loaded from: classes24.dex */
public class FileUtil {
    private static final String TAG = "FileUtil";

    public static void copyfile(File fromFile, File toFile) {
        if (!fromFile.exists() || !fromFile.isFile() || !fromFile.canRead()) {
            return;
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (!toFile.exists()) {
            try {
                Log.i(TAG, "begin copy file");
                FileInputStream fosfrom = new FileInputStream(fromFile);
                FileOutputStream fosto = new FileOutputStream(toFile);
                byte[] bt = new byte[1024];
                while (true) {
                    int c = fosfrom.read(bt);
                    if (c > 0) {
                        fosto.write(bt, 0, c);
                    } else {
                        fosfrom.close();
                        fosto.close();
                        Log.i(TAG, "success copy file");
                        return;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }
}
