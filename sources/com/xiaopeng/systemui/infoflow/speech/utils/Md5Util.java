package com.xiaopeng.systemui.infoflow.speech.utils;

import com.xiaopeng.lib.utils.info.BuildInfoUtils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/* loaded from: classes24.dex */
public class Md5Util {
    private static String[] sCharSet = {"0", "1", "2", "3", BuildInfoUtils.BID_LAN, BuildInfoUtils.BID_PT_SPECIAL_1, BuildInfoUtils.BID_PT_SPECIAL_2, "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public static String toMd5Hex(String source) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] result = md5.digest(source.getBytes());
            StringBuilder sb = new StringBuilder(32);
            for (byte x : result) {
                int h = (x >>> 4) & 15;
                int l = x & 15;
                sb.append(sCharSet[h]);
                sb.append(sCharSet[l]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toBit16Md5Hex(String source) {
        return toMd5Hex(source).substring(8, 24);
    }
}
