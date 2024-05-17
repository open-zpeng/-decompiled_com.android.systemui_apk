package com.xiaopeng.systemui.qs.views;

import android.content.Context;
import android.view.ViewGroup;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import com.xiaopeng.systemui.qs.TileState;
import com.xiaopeng.systemui.qs.tilemodels.XpTileModel;
/* loaded from: classes24.dex */
public abstract class TileView implements LifecycleOwner {
    protected Context mContext;
    protected final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    protected XpTileModel mTile;
    protected TileState mTileState;

    public abstract XpTileModel getTile();

    public abstract ViewGroup getView();

    protected abstract void initTile();

    protected abstract void initView();
}
