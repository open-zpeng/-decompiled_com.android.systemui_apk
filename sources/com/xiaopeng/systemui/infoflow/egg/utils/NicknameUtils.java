package com.xiaopeng.systemui.infoflow.egg.utils;

import android.net.Uri;
import android.os.RemoteException;
import com.xiaopeng.lib.apirouter.ApiRouter;
/* loaded from: classes24.dex */
public class NicknameUtils {
    public static String getNickname() {
        Uri.Builder builder = new Uri.Builder();
        builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("getNickname");
        try {
            return (String) ApiRouter.route(builder.build());
        } catch (RemoteException e) {
            e.printStackTrace();
            return "鹏友";
        }
    }
}
