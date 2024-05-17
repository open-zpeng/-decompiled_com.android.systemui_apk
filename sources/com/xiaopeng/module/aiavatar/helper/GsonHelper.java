package com.xiaopeng.module.aiavatar.helper;

import com.google.gson.Gson;
/* loaded from: classes23.dex */
public class GsonHelper {
    private static GsonHelper instance;
    private static final Object mLock = new Object();
    private Gson mGson = new Gson();

    private GsonHelper() {
    }

    public static GsonHelper getInstance() {
        GsonHelper gsonHelper;
        synchronized (mLock) {
            if (instance == null) {
                instance = new GsonHelper();
            }
            gsonHelper = instance;
        }
        return gsonHelper;
    }

    public Gson getGson() {
        return this.mGson;
    }
}
