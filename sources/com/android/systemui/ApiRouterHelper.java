package com.android.systemui;
/* loaded from: classes21.dex */
public class ApiRouterHelper {
    private ApiRouterListener mAPICallbackListener;

    private ApiRouterHelper() {
    }

    public static ApiRouterHelper getInstance() {
        return SingleToneHolder.sInstance;
    }

    public void setAPICallbackListener(ApiRouterListener listener) {
        this.mAPICallbackListener = listener;
    }

    public ApiRouterListener getAPICallbackListener() {
        return this.mAPICallbackListener;
    }

    /* loaded from: classes21.dex */
    private static class SingleToneHolder {
        private static ApiRouterHelper sInstance = new ApiRouterHelper();

        private SingleToneHolder() {
        }
    }
}
