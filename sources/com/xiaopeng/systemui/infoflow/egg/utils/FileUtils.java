package com.xiaopeng.systemui.infoflow.egg.utils;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Primitives;
import com.xiaopeng.lib.utils.LogUtils;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
/* loaded from: classes24.dex */
public class FileUtils {
    private static final String TAG = "FileUtils";

    public static <T> T readFileObject(File file, Class<T> classOfT) {
        return (T) Primitives.wrap(classOfT).cast(readFileObject(file, (Type) classOfT));
    }

    private static <T> T readFileObject(File file, Type typeOfT) {
        String str = readFile(file);
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            Gson gson = new Gson();
            T obj = (T) gson.fromJson(str, typeOfT);
            return obj;
        } catch (JsonSyntaxException e) {
            LogUtils.e(TAG, "---Json数据解析错误！", e);
            return null;
        }
    }

    public static String readFile(File file) {
        if (file == null || !file.exists()) {
            LogUtils.e(TAG, "---file is not exist! file = " + file);
            return null;
        }
        FileInputStream fis = null;
        String content = null;
        try {
            try {
                fis = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = fis.read(buffer);
                    if (readLength == -1) {
                        break;
                    }
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                fis.close();
                arrayOutputStream.close();
                content = arrayOutputStream.toString("utf-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
                content = null;
            }
            safeClose(fis);
            LogUtils.i(TAG, "---read file, file = " + file);
            return content;
        } catch (Throwable th) {
            safeClose(fis);
            throw th;
        }
    }

    private static void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
