package com.xiaopeng.lib.apirouter.server;

import android.os.IBinder;
import android.util.Pair;
import androidx.annotation.Keep;
import com.xiaopeng.lib.apirouter.server.aar.ClientObserver_Manifest;
import com.xiaopeng.lib.apirouter.server.aar.ClientObserver_Stub;
import com.xiaopeng.lib.apirouter.server.aar.ServerObserver_Manifest;
import com.xiaopeng.lib.apirouter.server.aar.ServerObserver_Stub;
import java.util.HashMap;
@Keep
/* loaded from: classes22.dex */
public class ManifestHelper_aar implements IManifestHelper {
    public HashMap<String, Pair<IBinder, String>> mapping = new HashMap<>();

    @Override // com.xiaopeng.lib.apirouter.server.IManifestHelper
    public HashMap<String, Pair<IBinder, String>> getMapping() {
        Pair stub0 = new Pair(new ServerObserver_Stub(), ServerObserver_Manifest.toJsonManifest());
        for (String key : ServerObserver_Manifest.getKey()) {
            this.mapping.put(key, stub0);
        }
        Pair stub1 = new Pair(new ClientObserver_Stub(), ClientObserver_Manifest.toJsonManifest());
        for (String key2 : ClientObserver_Manifest.getKey()) {
            this.mapping.put(key2, stub1);
        }
        return this.mapping;
    }
}
