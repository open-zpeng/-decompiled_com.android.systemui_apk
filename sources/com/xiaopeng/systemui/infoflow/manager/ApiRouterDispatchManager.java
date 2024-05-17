package com.xiaopeng.systemui.infoflow.manager;

import android.util.Log;
import com.android.systemui.ApiRouterHelper;
import com.android.systemui.ApiRouterListener;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.module.aiavatar.system.EventDispatcherManager;
import com.xiaopeng.systemui.infoflow.aissistant.push.listener.GreetingListener;
import com.xiaopeng.systemui.infoflow.message.listener.XNotificationListener;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
/* loaded from: classes24.dex */
public class ApiRouterDispatchManager implements ApiRouterListener {
    private static final String AIAVATAR_MODULE_PACKAGE_NAME = "com.xiaopeng.module.aiavatar";
    private static final String AIPUSH_MODULE_PACKAGE_NAME = "com.xiaopeng.aiassistant";
    private static final String GREETING_MODULE_PACKAGE_NAME = "com.xiaopeng.aiassistant.greeting";
    public static final String NAPA = "com.xiaopeng.napa";
    private static final String TAG = "ApiRouterDispatch";

    public ApiRouterDispatchManager() {
        ApiRouterHelper.getInstance().setAPICallbackListener(this);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.android.systemui.ApiRouterListener
    public void updateEvent(final String event, String packageName) {
        char c;
        Log.i(TAG, "event:" + event + " packageName : " + packageName);
        switch (packageName.hashCode()) {
            case -1028797115:
                if (packageName.equals(GREETING_MODULE_PACKAGE_NAME)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -483336593:
                if (packageName.equals("com.xiaopeng.module.aiavatar")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 159863650:
                if (packageName.equals("com.xiaopeng.aiassistant")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1798423736:
                if (packageName.equals("com.xiaopeng.napa")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            EventDispatcherManager.getInstance().dispatch(event);
        } else if (c == 1 || c == 2) {
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.manager.ApiRouterDispatchManager.1
                @Override // java.lang.Runnable
                public void run() {
                    XNotificationListener.getInstance(SystemUIApplication.getContext()).onAIPushMessage(event);
                }
            });
        } else if (c == 3) {
            GreetingListener.instance().onGreeting(event);
        }
    }
}
