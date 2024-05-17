package com.xiaopeng.systemui;

import android.util.SparseArray;
/* loaded from: classes24.dex */
public class MusicPlayerViewManger {
    private SparseArray<IMusicPlayerView> mPlayerViewSparseArray;

    /* loaded from: classes24.dex */
    private static class SingleHolder {
        private static final MusicPlayerViewManger sInstance = new MusicPlayerViewManger();

        private SingleHolder() {
        }
    }

    private MusicPlayerViewManger() {
        this.mPlayerViewSparseArray = new SparseArray<>();
    }

    public static MusicPlayerViewManger getInstance() {
        return SingleHolder.sInstance;
    }

    public void addMusicPlayerView(int displayId, IMusicPlayerView musicPlayerView) {
        this.mPlayerViewSparseArray.put(displayId, musicPlayerView);
    }

    public IMusicPlayerView getMusicPlayerView(int displayId) {
        return this.mPlayerViewSparseArray.get(displayId);
    }
}
