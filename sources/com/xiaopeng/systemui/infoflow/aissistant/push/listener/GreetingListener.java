package com.xiaopeng.systemui.infoflow.aissistant.push.listener;

import android.net.Uri;
import android.os.RemoteException;
import com.xiaopeng.lib.apirouter.ApiRouter;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class GreetingListener {
    private static final GreetingListener sInstance = new GreetingListener();

    public static final GreetingListener instance() {
        return sInstance;
    }

    public void onGreeting(String event) {
        try {
            JSONObject jsonObject = new JSONObject(event);
            boolean result = jsonObject.getBoolean("result");
            String callback = jsonObject.getString("callback");
            callback(allow(result), callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean allow(boolean result) {
        return result;
    }

    private void callback(boolean result, String callback) throws RemoteException {
        ApiRouter.route(Uri.parse(callback).buildUpon().appendQueryParameter("result", String.valueOf(result)).build());
    }
}
