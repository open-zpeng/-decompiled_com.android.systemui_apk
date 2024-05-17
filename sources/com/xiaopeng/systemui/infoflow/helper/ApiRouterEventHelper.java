package com.xiaopeng.systemui.infoflow.helper;

import android.net.Uri;
import com.xiaopeng.lib.apirouter.ApiRouter;
/* loaded from: classes24.dex */
public class ApiRouterEventHelper {
    public static void exitGreeting() {
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("exitGreeting");
            ApiRouter.route(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void notifySystemUIStart() {
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("onSystemUIStart");
            ApiRouter.route(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
