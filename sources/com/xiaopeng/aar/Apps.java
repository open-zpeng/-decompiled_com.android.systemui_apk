package com.xiaopeng.aar;

import android.content.Context;
import androidx.annotation.RestrictTo;
import com.xiaopeng.speech.common.SpeechConstant;
import com.xiaopeng.speech.protocol.query.speech.hardware.bean.StreamType;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.helper.PackageHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class Apps {
    public static final String ServerTest = "ServerTest";
    private static final HashMap<String, String> msMap = new HashMap<>();

    static {
        msMap.put("Demo", "com.xiaopeng.server.demo");
        msMap.put("carcontrol", VuiConstants.CARCONTROL);
        msMap.put("settings", VuiConstants.SETTINS);
        msMap.put("btphone", "com.xiaopeng.btphone");
        msMap.put("oobe", PackageHelper.PACKAGE_OOBE);
        msMap.put("systemui", "com.android.systemui");
        msMap.put("aiot", "com.xiaopeng.aiot");
        msMap.put("musicradio", VuiConstants.MUSIC);
        msMap.put("aiassistant", "com.xiaopeng.aiassistant");
        msMap.put("powercenter", VuiConstants.CHARGE);
        msMap.put("caraccount", PackageHelper.PACKAGE_ACCOUNT_CENTER);
        msMap.put("homespace", "com.xiaopeng.homespace");
        msMap.put("xsport", "com.xiaopeng.xsport");
        msMap.put("appstore", "com.xiaopeng.appstore");
        msMap.put(StreamType.SPEECH, SpeechConstant.SPEECH_SERVICE_PACKAGE_NAME);
        msMap.put("privacy", "com.xiaopeng.privacyservice");
    }

    public static Set<String> getApps() {
        return msMap.keySet();
    }

    public static String getPackageNames(String appId) {
        return msMap.get(appId);
    }

    public static String getAppId(Context context) {
        String pkgName = context.getPackageName();
        return getAppId(pkgName);
    }

    private static String getAppId(String pkgName) {
        for (Map.Entry<String, String> entry : msMap.entrySet()) {
            if (entry.getValue().equals(pkgName)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
