package com.xiaopeng.speech.protocol.utils;

import android.text.TextUtils;
import android.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
/* loaded from: classes23.dex */
public class DeflaterUtils {
    public static String compressForGzip(String unGzipStr) {
        if (TextUtils.isEmpty(unGzipStr)) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(unGzipStr.getBytes());
            gzip.close();
            byte[] encode = baos.toByteArray();
            baos.flush();
            baos.close();
            return compressForBase64(encode);
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
            return null;
        } catch (IOException var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static String decompressForGzip(String gzipStr) {
        if (TextUtils.isEmpty(gzipStr)) {
            return null;
        }
        byte[] t = decompressForBase64(gzipStr);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(t);
            GZIPInputStream gzip = new GZIPInputStream(in);
            byte[] buffer = new byte[1024];
            while (true) {
                int n = gzip.read(buffer, 0, buffer.length);
                if (n > 0) {
                    out.write(buffer, 0, n);
                } else {
                    gzip.close();
                    in.close();
                    out.close();
                    return out.toString();
                }
            }
        } catch (IOException var7) {
            var7.printStackTrace();
            return null;
        }
    }

    public static String compressForBase64(byte[] encode) {
        return compressForBase64(encode, 0);
    }

    public static String compressForBase64(byte[] encode, int flags) {
        if (encode == null || encode.length == 0) {
            return null;
        }
        return Base64.encodeToString(encode, flags);
    }

    public static byte[] decompressForBase64(String str) {
        return decompressForBase64(str, 0);
    }

    public static byte[] decompressForBase64(String str, int flags) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return Base64.decode(str, flags);
    }
}
