package com.xiaopeng.lib.apirouter.server.aar;

import java.util.HashSet;
import java.util.Set;
/* loaded from: classes22.dex */
public class ServerObserver_Manifest {
    public static final String DESCRIPTOR = "com.xiaopeng.aar.server.ipc.ServerObserver";
    public static final int TRANSACTION_call = 0;
    public static final int TRANSACTION_callBlob = 1;

    public static String toJsonManifest() {
        return "{\"authority\":\"com.xiaopeng.aar.server.ipc.ServerObserver\",\"DESCRIPTOR\":\"com.xiaopeng.aar.server.ipc.ServerObserver\",\"TRANSACTION\":[{\"path\":\"call\",\"METHOD\":\"call\",\"ID\":0,\"parameter\":[{\"alias\":\"type\",\"name\":\"type\"},{\"alias\":\"module\",\"name\":\"module\"},{\"alias\":\"method\",\"name\":\"method\"},{\"alias\":\"param\",\"name\":\"param\"}]},{\"path\":\"callBlob\",\"METHOD\":\"callBlob\",\"ID\":1,\"parameter\":[{\"alias\":\"type\",\"name\":\"type\"},{\"alias\":\"module\",\"name\":\"module\"},{\"alias\":\"method\",\"name\":\"method\"},{\"alias\":\"param\",\"name\":\"param\"}]}]}";
    }

    public static Set<String> getKey() {
        Set<String> key = new HashSet<>(2);
        key.add("ServerObserver");
        return key;
    }
}
