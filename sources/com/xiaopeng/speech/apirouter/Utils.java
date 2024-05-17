package com.xiaopeng.speech.apirouter;

import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.xiaopeng.speech.vui.constants.Foo;
import com.xiaopeng.speech.vui.utils.VuiUtils;
/* loaded from: classes23.dex */
public class Utils {
    private static Boolean isXpDevice = null;

    public static boolean isCorrectObserver(String packageName, String observer) {
        if (TextUtils.isEmpty(observer) || TextUtils.isEmpty(packageName) || !observer.contains(".")) {
            return false;
        }
        String observerName = observer.substring(observer.lastIndexOf("."));
        StringBuilder sb = new StringBuilder();
        sb.append(packageName);
        sb.append(observerName);
        return observer.equals(sb.toString());
    }

    public static boolean isXpDevice() {
        if (isXpDevice == null) {
            String carType = VuiUtils.getXpCduType();
            if (TextUtils.isEmpty(carType)) {
                isXpDevice = false;
            } else {
                isXpDevice = true;
            }
        }
        return isXpDevice.booleanValue();
    }

    public static boolean checkApkExist(String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            Foo.getContext().getPackageManager().getApplicationInfo(packageName, 8192);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
