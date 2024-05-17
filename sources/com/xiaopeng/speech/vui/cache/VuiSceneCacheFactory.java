package com.xiaopeng.speech.vui.cache;
/* loaded from: classes.dex */
public class VuiSceneCacheFactory {
    private static final String TAG = "VuiSceneCacheFactory";
    private VuiSceneBuildCache mBuildCache;
    private VuiSceneRemoveCache mRemoveCache;
    private VuiSceneCache mSceneCache;
    private VuiSceneUpdateCache mUpdateCache;

    /* loaded from: classes.dex */
    public enum CacheType {
        BUILD(0),
        UPDATE(1),
        ADD(2),
        REMOVE(3),
        DEFAULT(4);
        
        private int type;

        CacheType(int code) {
            this.type = code;
        }

        public int getType() {
            return this.type;
        }
    }

    private VuiSceneCacheFactory() {
        this.mBuildCache = null;
        this.mUpdateCache = null;
        this.mRemoveCache = null;
        this.mSceneCache = null;
    }

    public static final VuiSceneCacheFactory instance() {
        return Holder.Instance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Holder {
        private static final VuiSceneCacheFactory Instance = new VuiSceneCacheFactory();

        private Holder() {
        }
    }

    public VuiSceneCache getSceneCache(int type) {
        if (type == CacheType.BUILD.getType()) {
            if (this.mBuildCache == null) {
                this.mBuildCache = new VuiSceneBuildCache();
            }
            return this.mBuildCache;
        } else if (type == CacheType.UPDATE.getType()) {
            if (this.mUpdateCache == null) {
                this.mUpdateCache = new VuiSceneUpdateCache();
            }
            return this.mUpdateCache;
        } else if (type == CacheType.REMOVE.getType()) {
            if (this.mRemoveCache == null) {
                this.mRemoveCache = new VuiSceneRemoveCache();
            }
            return this.mRemoveCache;
        } else {
            if (this.mSceneCache == null) {
                this.mSceneCache = new VuiSceneCache();
            }
            return this.mSceneCache;
        }
    }

    public void removeAllCache(String sceneId) {
        if (sceneId == null) {
            return;
        }
        VuiSceneBuildCache vuiSceneBuildCache = this.mBuildCache;
        if (vuiSceneBuildCache != null) {
            vuiSceneBuildCache.removeCache(sceneId);
            this.mBuildCache.removeUploadState(sceneId);
        }
        VuiSceneUpdateCache vuiSceneUpdateCache = this.mUpdateCache;
        if (vuiSceneUpdateCache != null) {
            vuiSceneUpdateCache.removeCache(sceneId);
        }
        VuiSceneRemoveCache vuiSceneRemoveCache = this.mRemoveCache;
        if (vuiSceneRemoveCache != null) {
            vuiSceneRemoveCache.removeCache(sceneId);
        }
    }

    public void removeOtherCache(String sceneId) {
        if (sceneId == null) {
            return;
        }
        VuiSceneUpdateCache vuiSceneUpdateCache = this.mUpdateCache;
        if (vuiSceneUpdateCache != null) {
            vuiSceneUpdateCache.removeCache(sceneId);
        }
        VuiSceneRemoveCache vuiSceneRemoveCache = this.mRemoveCache;
        if (vuiSceneRemoveCache != null) {
            vuiSceneRemoveCache.removeCache(sceneId);
        }
    }
}
