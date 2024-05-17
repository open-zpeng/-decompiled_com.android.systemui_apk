package com.xiaopeng.speech.vui.cache;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class VuiSceneRemoveCache extends VuiSceneCache {
    public VuiSceneRemoveCache() {
        this.mPre = "remove_";
    }

    public List<String> getRemoveCache(String sceneId) {
        if (this.removeElementsMap.containsKey(sceneId)) {
            return this.removeElementsMap.get(sceneId);
        }
        return null;
    }

    public void setCache(String sceneId, String id) {
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(sceneId)) {
            return;
        }
        String[] ids = id.split(",");
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(ids));
        this.removeElementsMap.put(sceneId, list);
    }

    public void addRemoveIdToCache(String sceneId, String id) {
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(sceneId)) {
            return;
        }
        List<String> list = getRemoveCache(sceneId);
        if (list == null) {
            list = new ArrayList();
        }
        list.add(id);
        this.removeElementsMap.put(sceneId, list);
    }

    public void deleteRemoveIdFromCache(String sceneId, String id) {
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(sceneId)) {
            return;
        }
        List<String> list = getRemoveCache(sceneId);
        if (list != null && list.contains(id)) {
            list.remove(id);
        }
        this.removeElementsMap.put(sceneId, list);
    }
}
