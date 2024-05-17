package com.xiaopeng.speech.vui.cache;

import android.text.TextUtils;
import com.xiaopeng.speech.vui.cache.VuiSceneCacheFactory;
import com.xiaopeng.speech.vui.utils.SceneMergeUtils;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/* loaded from: classes.dex */
public class VuiSceneCache {
    private String TAG = "VuiSceneCache";
    public ConcurrentMap<String, Boolean> mUploadedMap = null;
    public String mPre = null;
    private String BUILD_PRE = "build_";
    private String UPDATE_PRE = "update_";
    private String REMOVE_PRE = "remove_";
    public ConcurrentMap<String, List<VuiElement>> buildElementsMap = new ConcurrentHashMap();
    public ConcurrentMap<String, List<VuiElement>> updateElementsMap = new ConcurrentHashMap();
    public ConcurrentMap<String, List<String>> removeElementsMap = new ConcurrentHashMap();

    public void setUploadedState(String sceneId, boolean isUploaded) {
        ConcurrentMap<String, Boolean> concurrentMap;
        if (sceneId != null && (concurrentMap = this.mUploadedMap) != null) {
            concurrentMap.put(sceneId, Boolean.valueOf(isUploaded));
        }
    }

    public boolean getUploadedState(String sceneId) {
        ConcurrentMap<String, Boolean> concurrentMap;
        Boolean state;
        if (sceneId == null || (concurrentMap = this.mUploadedMap) == null || !concurrentMap.containsKey(sceneId) || (state = this.mUploadedMap.get(sceneId)) == null) {
            return false;
        }
        return state.booleanValue();
    }

    public void removeUploadState(String sceneId) {
        ConcurrentMap<String, Boolean> concurrentMap;
        if (sceneId != null && (concurrentMap = this.mUploadedMap) != null && concurrentMap.containsKey(sceneId)) {
            this.mUploadedMap.remove(sceneId);
        }
    }

    public synchronized void setCache(String sceneId, List<VuiElement> elements) {
        if (elements == null && sceneId == null) {
            return;
        }
        if (this.BUILD_PRE.equals(this.mPre)) {
            if (this.buildElementsMap != null) {
                this.buildElementsMap.put(sceneId, elements);
            }
        } else if (this.UPDATE_PRE.equals(this.mPre) && this.updateElementsMap != null) {
            this.updateElementsMap.put(sceneId, elements);
        }
    }

    public synchronized List<VuiElement> getCache(String sceneId) {
        if (sceneId == null) {
            return null;
        }
        if (this.BUILD_PRE.equals(this.mPre)) {
            if (this.buildElementsMap != null && this.buildElementsMap.containsKey(sceneId)) {
                return this.buildElementsMap.get(sceneId);
            }
        } else if (this.UPDATE_PRE.equals(this.mPre) && this.updateElementsMap != null && this.updateElementsMap.containsKey(sceneId)) {
            return this.updateElementsMap.get(sceneId);
        }
        return null;
    }

    public VuiElement getVuiElementById(String sceneId, String mElementId) {
        if (sceneId == null || mElementId == null) {
            return null;
        }
        List<VuiElement> cacheElements = getCache(sceneId);
        return getTagetElement(cacheElements, mElementId);
    }

    private VuiElement getTagetElement(List<VuiElement> elements, String mElementId) {
        if (TextUtils.isEmpty(mElementId) || elements == null) {
            return null;
        }
        VuiElement createVuiEle = new VuiElement();
        createVuiEle.setElements(elements);
        createVuiEle.setId("ab123");
        return findNode(createVuiEle, mElementId);
    }

    private VuiElement findNode(VuiElement node, String id) {
        if (node == null || TextUtils.isEmpty(id)) {
            return null;
        }
        if (id.equals(node.getId())) {
            return node;
        }
        List<VuiElement> children = node.getElements();
        if (children != null) {
            for (VuiElement child : children) {
                VuiElement target = findNode(child, id);
                if (target != null) {
                    return target;
                }
            }
        }
        return null;
    }

    public synchronized List<VuiElement> getFusionCache(String sceneId, List<VuiElement> newElements, boolean isAttribute) {
        if (!TextUtils.isEmpty(sceneId) && newElements != null && newElements.size() != 0) {
            List<VuiElement> cache = getCache(sceneId);
            if (cache != null) {
                return SceneMergeUtils.merge(cache, newElements, isAttribute);
            }
            return newElements;
        }
        return null;
    }

    public synchronized List<VuiElement> getUpdateFusionCache(String sceneId, List<VuiElement> newElements, boolean isAttruibte) {
        if (!TextUtils.isEmpty(sceneId) && newElements != null && newElements.size() != 0) {
            List<VuiElement> cache = getCache(sceneId);
            if (cache != null) {
                return SceneMergeUtils.updateMerge(cache, newElements, isAttruibte);
            }
            return newElements;
        }
        return null;
    }

    public synchronized List<VuiElement> addElementGroupToCache(String sceneId, List<VuiElement> newElements) {
        if (!TextUtils.isEmpty(sceneId) && newElements != null) {
            List<VuiElement> cache = getCache(sceneId);
            if (cache != null) {
                cache.addAll(newElements);
            }
            return cache;
        }
        return null;
    }

    public synchronized List<VuiElement> removeElementFromCache(String sceneId, String id) {
        if (!TextUtils.isEmpty(sceneId) && !TextUtils.isEmpty(id)) {
            List<VuiElement> cache = getCache(sceneId);
            if (cache != null) {
                SceneMergeUtils.removeElementById(cache, Arrays.asList(id.split(",")));
            }
            return cache;
        }
        return null;
    }

    public int getFusionType(String sceneId) {
        List<String> cache;
        List<VuiElement> cache2;
        if (sceneId == null) {
            return VuiSceneCacheFactory.CacheType.DEFAULT.getType();
        }
        VuiSceneCache buildCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
        if (buildCache != null && !buildCache.getUploadedState(sceneId)) {
            return VuiSceneCacheFactory.CacheType.BUILD.getType();
        }
        VuiSceneCache updateCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.UPDATE.getType());
        if (updateCache != null && (cache2 = updateCache.getCache(sceneId)) != null && !cache2.isEmpty()) {
            return VuiSceneCacheFactory.CacheType.UPDATE.getType();
        }
        VuiSceneCache removeCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.REMOVE.getType());
        if (removeCache != null && (cache = ((VuiSceneRemoveCache) removeCache).getRemoveCache(sceneId)) != null && !cache.isEmpty()) {
            return VuiSceneCacheFactory.CacheType.REMOVE.getType();
        }
        return VuiSceneCacheFactory.CacheType.DEFAULT.getType();
    }

    public void removeCache(String sceneId) {
        if (sceneId == null) {
            return;
        }
        if (this.BUILD_PRE.equals(this.mPre)) {
            if (this.buildElementsMap.containsKey(sceneId)) {
                this.buildElementsMap.remove(sceneId);
            }
        } else if (this.UPDATE_PRE.equals(this.mPre)) {
            if (this.updateElementsMap.containsKey(sceneId)) {
                this.updateElementsMap.remove(sceneId);
            }
        } else if (this.REMOVE_PRE.equals(this.mPre) && this.removeElementsMap.containsKey(sceneId)) {
            this.removeElementsMap.remove(sceneId);
        }
    }
}
