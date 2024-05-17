package com.xiaopeng.systemui.quickmenu.tiles;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public abstract class XpTile {
    List<Callback> mCallbacks = new ArrayList();
    protected boolean mIsBusying = false;
    protected int mScreenId = 0;
    protected String mTileKey;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface BaseState {
        public static final int INIT = -1;
        public static final int OFF = 1;
        public static final int ON = 2;
    }

    /* loaded from: classes24.dex */
    public interface Callback {
        void onStateChanged(String str, int i);
    }

    public abstract void click(int i);

    abstract int convertState(int i);

    public abstract void create();

    public abstract void destroy();

    public abstract int getCurrentState();

    public XpTile(String tileSpec) {
        this.mTileKey = "";
        this.mTileKey = tileSpec;
    }

    public boolean isAvailable() {
        return true;
    }

    public String getTileKey() {
        return this.mTileKey;
    }

    public void refreshState(int state) {
        for (Callback callback : this.mCallbacks) {
            callback.onStateChanged(this.mTileKey, convertState(state));
        }
    }

    public void setScreenId(int id) {
        this.mScreenId = id;
    }

    public void addCallback(Callback callback) {
        if (!this.mCallbacks.contains(callback)) {
            this.mCallbacks.add(callback);
        }
    }

    public int refreshType() {
        return -1;
    }

    public int getCurrentType() {
        return -1;
    }

    public void removeCallback(Callback callback) {
        this.mCallbacks.remove(callback);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        XpTile otherTile = (XpTile) obj;
        return this.mTileKey.equals(otherTile.mTileKey);
    }
}
