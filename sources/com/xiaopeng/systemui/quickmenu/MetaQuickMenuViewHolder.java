package com.xiaopeng.systemui.quickmenu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.util.FeatureOption;
/* loaded from: classes24.dex */
public abstract class MetaQuickMenuViewHolder {
    protected String mClickKey;
    protected Context mContext;
    protected IntervalControl mIntervalControl;
    protected ViewGroup mParentView;
    protected View mView;
    protected String TAG = "MetaQuickMenuViewHolder";
    protected boolean mIsChinese = !FeatureOption.FO_DEVICE_INTERNATIONAL_ENABLED;
    protected boolean mSupportIHB = CarModelsManager.getConfig().isIhbSupport();

    abstract int getLayoutId();

    public View initView(Context context, ViewGroup viewGroup) {
        this.mContext = context;
        this.mParentView = viewGroup;
        int quickMenuTile = getLayoutId();
        this.mView = LayoutInflater.from(context).inflate(quickMenuTile, viewGroup, false);
        initView();
        return this.mView;
    }

    protected void dismissQuickmenu() {
        ViewGroup viewGroup = this.mParentView;
        if (viewGroup instanceof QuickMenuFloatingView) {
            ((QuickMenuFloatingView) viewGroup).dismissFloatingView();
        }
    }

    public void initView() {
    }

    public void init() {
        this.mIntervalControl = new IntervalControl("quickmenu");
    }

    public void onStart() {
        Log.d(this.TAG, "xpquickmenu onStart");
    }

    public void onStop() {
        Log.d(this.TAG, "xpquickmenu onStop");
    }

    public void onDestroy() {
        Log.d(this.TAG, "xpquickmenu onDestroy");
    }

    public void themeChanged() {
    }
}
