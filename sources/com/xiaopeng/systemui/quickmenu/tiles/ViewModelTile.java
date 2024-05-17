package com.xiaopeng.systemui.quickmenu.tiles;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
/* loaded from: classes24.dex */
public class ViewModelTile extends XpTile implements LifecycleOwner {
    protected Context mContext;
    private LifecycleRegistry mLifecycleRegistry;

    public ViewModelTile(String tileSpec) {
        super(tileSpec);
        this.mLifecycleRegistry = new LifecycleRegistry(this);
        this.mContext = ContextUtils.getContext();
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void destroy() {
        this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void create() {
        this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public int getCurrentState() {
        return 0;
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    int convertState(int state) {
        return state;
    }

    @Override // androidx.lifecycle.LifecycleOwner
    @NonNull
    public Lifecycle getLifecycle() {
        return this.mLifecycleRegistry;
    }
}
