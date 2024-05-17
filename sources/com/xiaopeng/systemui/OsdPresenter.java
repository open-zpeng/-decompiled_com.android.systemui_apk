package com.xiaopeng.systemui;

import android.content.Context;
import com.xiaopeng.systemui.controller.AudioController;
import com.xiaopeng.systemui.controller.OsdController;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
/* loaded from: classes24.dex */
public class OsdPresenter implements IOsdPresenter {
    private Context mContext;

    /* loaded from: classes24.dex */
    private static class SingleHolder {
        private static final OsdPresenter sInstance = new OsdPresenter();

        private SingleHolder() {
        }
    }

    private OsdPresenter() {
        this.mContext = ContextUtils.getContext();
    }

    public static IOsdPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    @Override // com.xiaopeng.systemui.IOsdPresenter
    public void stopAutoHideOsd() {
        OsdController.getInstance(this.mContext).stopAutoHideOsd();
    }

    @Override // com.xiaopeng.systemui.IOsdPresenter
    public void startAutoHideOsd() {
        OsdController.getInstance(this.mContext).startAutoHideOsd();
    }

    @Override // com.xiaopeng.systemui.IOsdPresenter
    public void setVolume(int streamType, int volume) {
        AudioController.getInstance(this.mContext).setVolume(streamType, volume);
    }

    @Override // com.xiaopeng.systemui.IOsdPresenter
    public void setInTouchMode(boolean inTouchMode) {
        OsdController.getInstance(this.mContext).setInTouchMode(inTouchMode);
    }
}
