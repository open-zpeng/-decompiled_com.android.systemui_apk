package com.xiaopeng.lib.apirouter.server;

import android.os.IBinder;
import android.util.Pair;
import androidx.annotation.Keep;
import com.xiaopeng.lib.apirouter.server.vuiengine.ApiRouterOverallService_Manifest;
import com.xiaopeng.lib.apirouter.server.vuiengine.ApiRouterOverallService_Stub;
import com.xiaopeng.lib.apirouter.server.vuiengine.ApiRouterSceneService_Manifest;
import com.xiaopeng.lib.apirouter.server.vuiengine.ApiRouterSceneService_Stub;
import com.xiaopeng.lib.apirouter.server.vuiengine.ApiRouterUnitySceneService_Manifest;
import com.xiaopeng.lib.apirouter.server.vuiengine.ApiRouterUnitySceneService_Stub;
import java.util.HashMap;
@Keep
/* loaded from: classes22.dex */
public class ManifestHelper_VuiEngine implements IManifestHelper {
    public HashMap<String, Pair<IBinder, String>> mapping = new HashMap<>();

    @Override // com.xiaopeng.lib.apirouter.server.IManifestHelper
    public HashMap<String, Pair<IBinder, String>> getMapping() {
        Pair stub0 = new Pair(new ApiRouterSceneService_Stub(), ApiRouterSceneService_Manifest.toJsonManifest());
        for (String key : ApiRouterSceneService_Manifest.getKey()) {
            this.mapping.put(key, stub0);
        }
        Pair stub1 = new Pair(new ApiRouterUnitySceneService_Stub(), ApiRouterUnitySceneService_Manifest.toJsonManifest());
        for (String key2 : ApiRouterUnitySceneService_Manifest.getKey()) {
            this.mapping.put(key2, stub1);
        }
        Pair stub2 = new Pair(new ApiRouterOverallService_Stub(), ApiRouterOverallService_Manifest.toJsonManifest());
        for (String key3 : ApiRouterOverallService_Manifest.getKey()) {
            this.mapping.put(key3, stub2);
        }
        return this.mapping;
    }
}
