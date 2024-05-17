package com.xiaopeng.lib.apirouter.server.vuiengine;

import java.util.HashSet;
import java.util.Set;
/* loaded from: classes22.dex */
public class ApiRouterUnitySceneService_Manifest {
    public static final String DESCRIPTOR = "com.xiaopeng.speech.apirouter.ApiRouterUnitySceneService";
    public static final int TRANSACTION_getElementState = 0;
    public static final int TRANSACTION_onEvent = 1;

    public static String toJsonManifest() {
        return "{\"authority\":\"com.xiaopeng.speech.apirouter.ApiRouterUnitySceneService\",\"DESCRIPTOR\":\"com.xiaopeng.speech.apirouter.ApiRouterUnitySceneService\",\"TRANSACTION\":[{\"path\":\"getElementState\",\"METHOD\":\"getElementState\",\"ID\":0,\"parameter\":[{\"alias\":\"sceneId\",\"name\":\"sceneId\"},{\"alias\":\"elementId\",\"name\":\"elementId\"}]},{\"path\":\"onEvent\",\"METHOD\":\"onEvent\",\"ID\":1,\"parameter\":[{\"alias\":\"event\",\"name\":\"event\"},{\"alias\":\"data\",\"name\":\"data\"}]}]}";
    }

    public static Set<String> getKey() {
        Set<String> key = new HashSet<>(2);
        key.add("ApiRouterUnitySceneService");
        return key;
    }
}
