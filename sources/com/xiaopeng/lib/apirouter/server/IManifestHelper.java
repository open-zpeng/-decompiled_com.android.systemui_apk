package com.xiaopeng.lib.apirouter.server;

import android.os.IBinder;
import android.util.Pair;
import java.util.HashMap;
/* loaded from: classes22.dex */
public interface IManifestHelper {
    HashMap<String, Pair<IBinder, String>> getMapping();
}
