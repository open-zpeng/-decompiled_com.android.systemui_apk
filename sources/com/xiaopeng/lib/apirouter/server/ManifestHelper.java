package com.xiaopeng.lib.apirouter.server;

import android.os.IBinder;
import android.util.Pair;
import androidx.annotation.Keep;
import java.util.HashMap;
@Keep
/* loaded from: classes22.dex */
public class ManifestHelper {
    public static HashMap<String, Pair<IBinder, String>> mapping = new HashMap<>();

    static {
        Pair stub0 = new Pair(new ApiRouterRemoteService_Stub(), ApiRouterRemoteService_Manifest.toJsonManifest());
        for (String key : ApiRouterRemoteService_Manifest.getKey()) {
            mapping.put(key, stub0);
        }
    }
}
