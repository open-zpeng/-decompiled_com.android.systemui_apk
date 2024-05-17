package com.xiaopeng.systemui.infoflow.net;
/* loaded from: classes24.dex */
public class HttpHelper {
    private static final String TAG = HttpHelper.class.getSimpleName();
    private static volatile HttpHelper mInstance;

    private HttpHelper() {
    }

    public static HttpHelper getInstance() {
        if (mInstance == null) {
            synchronized (HttpHelper.class) {
                if (mInstance == null) {
                    mInstance = new HttpHelper();
                }
            }
        }
        return mInstance;
    }
}
