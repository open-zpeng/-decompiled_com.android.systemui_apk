package com.xiaopeng.lib.apirouter.server;

import android.os.IBinder;
import android.util.Log;
import android.util.Pair;
import java.lang.reflect.Field;
import java.util.HashMap;
/* loaded from: classes22.dex */
class ManifestHelperMapping {
    private static final String HELPER_CLASS_NAME = "com.xiaopeng.lib.apirouter.server.ManifestHelper";
    private static final String MAPPING = "mapping";

    ManifestHelperMapping() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static HashMap<String, Pair<IBinder, String>> reflectMapping() {
        Field field;
        try {
            Class clazz = Class.forName(HELPER_CLASS_NAME);
            if (clazz == null || (field = clazz.getField(MAPPING)) == null) {
                return null;
            }
            HashMap<String, Pair<IBinder, String>> mapping = (HashMap) field.get(clazz);
            return mapping;
        } catch (Exception e) {
            Log.w("AutoCodeMatcher", "reflectMapping", e);
            return null;
        }
    }
}
