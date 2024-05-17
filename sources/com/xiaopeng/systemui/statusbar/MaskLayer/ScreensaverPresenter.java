package com.xiaopeng.systemui.statusbar.MaskLayer;

import android.content.Context;
import android.os.RemoteException;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.SystemController;
import com.xiaopeng.systemui.infoflow.helper.TimePickHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.app.XMaskLayer;
/* loaded from: classes24.dex */
public class ScreensaverPresenter implements IMaskLayer, SystemController.OnTimeFormatChangedListener {
    private static final String TAG = "ScreensaverPresenter";
    private Context mContext;
    private TimePickHelper.OnTimeChangedListener mOnTimeChangedListener = new TimePickHelper.OnTimeChangedListener() { // from class: com.xiaopeng.systemui.statusbar.MaskLayer.-$$Lambda$ScreensaverPresenter$Y6_ZmvLDMYiawCpmdA7YjH0gEB8
        @Override // com.xiaopeng.systemui.infoflow.helper.TimePickHelper.OnTimeChangedListener
        public final void onTimeChanged() {
            ScreensaverPresenter.this.lambda$new$0$ScreensaverPresenter();
        }
    };
    private ScreensaverClockView mScreensaverClockView;
    private XMaskLayer xMaskLayer3;

    @Override // com.xiaopeng.systemui.statusbar.MaskLayer.IMaskLayer
    public void updateView(boolean on) {
        Logger.d(TAG, "updateScreensaverView : " + on);
        if (on) {
            ScreensaverClockView screensaverClockView = this.mScreensaverClockView;
            if (screensaverClockView == null || screensaverClockView.getParent() == null) {
                init();
            }
            this.mScreensaverClockView.updateTime();
            return;
        }
        stop();
    }

    /* loaded from: classes24.dex */
    private static class SingleHolder {
        private static final ScreensaverPresenter sInstance = new ScreensaverPresenter();

        private SingleHolder() {
        }
    }

    public static ScreensaverPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    private void init() {
        Logger.d(TAG, "start");
        this.mContext = ContextUtils.getContext();
        attachView();
        TimePickHelper.instance().addListener(this.mOnTimeChangedListener);
        SystemController.getInstance(ContextUtils.getContext()).addOnTimeFormatChangeListener(this);
    }

    private void attachView() {
        this.mScreensaverClockView = new ScreensaverClockView(this.mContext);
        XMaskLayer.XMaskLayerBuilder builder = new XMaskLayer.XMaskLayerBuilder();
        this.xMaskLayer3 = builder.setContext(Xui.getContext()).setClickable(true).setStackWindow(true).setScreenId(1).setItemView(this.mScreensaverClockView).create();
        this.xMaskLayer3.show();
        this.mScreensaverClockView.animationIn();
    }

    public void stop() {
        TimePickHelper.instance().removeListenter(this.mOnTimeChangedListener);
        SystemController.getInstance(ContextUtils.getContext()).removeOnTimeFormatChangeListener(this);
        ScreensaverClockView screensaverClockView = this.mScreensaverClockView;
        if (screensaverClockView != null) {
            screensaverClockView.animationOut();
        }
        removeView();
    }

    private void removeView() {
        ScreensaverClockView screensaverClockView = this.mScreensaverClockView;
        if (screensaverClockView != null && screensaverClockView.getParent() != null) {
            try {
                this.xMaskLayer3.cancel();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            this.xMaskLayer3 = null;
            this.mScreensaverClockView = null;
        }
    }

    public /* synthetic */ void lambda$new$0$ScreensaverPresenter() {
        this.mScreensaverClockView.updateTime();
    }

    @Override // com.xiaopeng.systemui.controller.SystemController.OnTimeFormatChangedListener
    public void onTimeFormatChanged() {
        Logger.d(TAG, "onTimeFormatChanged");
        this.mScreensaverClockView.onTimeFormatChanged();
    }
}
