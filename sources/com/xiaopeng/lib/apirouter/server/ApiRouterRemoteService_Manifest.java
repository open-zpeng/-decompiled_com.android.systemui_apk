package com.xiaopeng.lib.apirouter.server;

import java.util.HashSet;
import java.util.Set;
/* loaded from: classes22.dex */
public class ApiRouterRemoteService_Manifest {
    public static final String DESCRIPTOR = "com.android.systemui.ApiRouterRemoteService";
    public static final int TRANSACTION_updateEvent = 0;

    public static String toJsonManifest() {
        return "{\"authority\":\"com.android.systemui.ApiRouterRemoteService\",\"DESCRIPTOR\":\"com.android.systemui.ApiRouterRemoteService\",\"TRANSACTION\":[{\"path\":\"updateEvent\",\"METHOD\":\"updateEvent\",\"ID\":0,\"parameter\":[{\"alias\":\"event\",\"name\":\"event\"},{\"alias\":\"packageName\",\"name\":\"packageName\"}]}]}";
    }

    public static Set<String> getKey() {
        Set<String> key = new HashSet<>(2);
        key.add("ApiRouterRemoteService");
        return key;
    }
}
