package com.xiaopeng.systemui.infoflow.util;

import com.google.gson.Gson;
import java.lang.reflect.Type;
/* loaded from: classes24.dex */
public class GsonUtil {
    private static final String TAG = GsonUtil.class.getSimpleName();
    private static Gson mGon = new Gson();

    public static String toJson(Object object) {
        return mGon.toJson(object);
    }

    public static <T> T fromJson(String string, Class<T> clazz) {
        return (T) mGon.fromJson(string, (Class<Object>) clazz);
    }

    public static <T> T fromJson(String string, Type type) {
        return (T) mGon.fromJson(string, type);
    }
}
