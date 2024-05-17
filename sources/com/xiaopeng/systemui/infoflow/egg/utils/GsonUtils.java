package com.xiaopeng.systemui.infoflow.egg.utils;

import com.google.gson.Gson;
import com.xiaopeng.lib.utils.LogUtils;
/* loaded from: classes24.dex */
public class GsonUtils {
    public static String toJson(Object obj, String from) {
        if (obj == null) {
            return null;
        }
        try {
            String json = new Gson().toJson(obj);
            return json;
        } catch (Exception e) {
            LogUtils.i("GsonUtils", "from:" + from, e);
            return null;
        }
    }

    public static String obj2String(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof String) {
            return (String) object;
        }
        if (object instanceof Number) {
            return object.toString();
        }
        Gson gson = new Gson();
        return gson.toJson(object);
    }
}
