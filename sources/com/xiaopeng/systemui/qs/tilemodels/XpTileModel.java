package com.xiaopeng.systemui.qs.tilemodels;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes24.dex */
public abstract class XpTileModel {
    protected String mTileKey;
    protected int mScreenId = 0;
    protected int mCurrentState = -1;
    protected String mBuriedBtnId = "";
    public Context mContext = ContextUtils.getContext();
    protected MutableLiveData<Integer> mCurrentLivedata = new MutableLiveData<>();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface BaseState {
        public static final int INIT = -1;
        public static final int OFF = 1;
        public static final int ON = 2;
    }

    public abstract void click(int i);

    protected abstract int getCurrentState();

    public XpTileModel(String tileSpec) {
        this.mTileKey = "";
        this.mTileKey = tileSpec;
        this.mCurrentLivedata.setValue(Integer.valueOf(this.mCurrentState));
    }

    public MutableLiveData<Integer> getCurrentData() {
        return this.mCurrentLivedata;
    }

    public void setScreenId(int id) {
        this.mScreenId = id;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int convertState(int value) {
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        XpTileModel otherTile = (XpTileModel) obj;
        return this.mTileKey.equals(otherTile.mTileKey);
    }
}
