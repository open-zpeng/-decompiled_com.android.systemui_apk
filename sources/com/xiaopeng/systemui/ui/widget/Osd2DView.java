package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.view.WindowManager;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.OsdController;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.statusbar.StatusBarGlobal;
/* loaded from: classes24.dex */
public class Osd2DView implements IOsdView {
    private static final String TAG = "Osd2DView";
    private WindowManager.LayoutParams mParams;
    private int mScreenId = -1;
    private Context mContext = ContextUtils.getContext();
    private OsdView mOsdView = new OsdView(this.mContext);
    private WindowManager mWindowManager = StatusBarGlobal.getInstance(this.mContext).getWindowManager();

    @Override // com.xiaopeng.systemui.ui.widget.IOsdView
    public void dispatchConfigurationChanged(Configuration newConfig) {
        this.mOsdView.dispatchConfigurationChanged(newConfig);
    }

    @Override // com.xiaopeng.systemui.ui.widget.IOsdView
    public void showOsd(OsdController.OsdParams params) {
        this.mOsdView.apply(params);
    }

    @Override // com.xiaopeng.systemui.ui.widget.IOsdView
    public void showOsd(boolean show) {
        if (show) {
            showOsd();
        } else {
            hideOsd();
        }
    }

    private void showOsd() {
        int screenId = this.mOsdView.getScreenId();
        boolean changed = screenId != this.mScreenId;
        this.mScreenId = screenId;
        if (this.mOsdView.isAttachedToWindow()) {
            try {
                if (changed) {
                    this.mParams = createLayoutParams(screenId);
                    this.mWindowManager.removeViewImmediate(this.mOsdView);
                    this.mWindowManager.addView(this.mOsdView, this.mParams);
                } else {
                    this.mWindowManager.updateViewLayout(this.mOsdView, this.mParams);
                }
                return;
            } catch (Exception e) {
                Logger.d(TAG, "handleShow updateViewLayout e=" + e);
                return;
            }
        }
        try {
            this.mParams = createLayoutParams(screenId);
            this.mWindowManager.addView(this.mOsdView, this.mParams);
        } catch (Exception e2) {
            try {
                this.mParams = createLayoutParams(screenId);
                this.mWindowManager.removeViewImmediate(this.mOsdView);
                this.mWindowManager.addView(this.mOsdView, this.mParams);
            } catch (Exception ee) {
                Logger.d(TAG, "handleShow addView ee=" + ee);
            }
            Logger.d(TAG, "handleShow addView e=" + e2);
        }
    }

    private void hideOsd() {
        OsdView osdView = this.mOsdView;
        if (osdView != null && osdView.isAttachedToWindow()) {
            this.mWindowManager.removeViewImmediate(this.mOsdView);
        }
    }

    private WindowManager.LayoutParams createLayoutParams(int sharedId) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.height = this.mContext.getResources().getDimensionPixelSize(R.dimen.osd_height);
        lp.width = this.mContext.getResources().getDimensionPixelSize(R.dimen.osd_width);
        lp.format = -3;
        lp.windowAnimations = 16973828;
        lp.type = WindowHelper.TYPE_OSD;
        lp.setTitle("OSD");
        lp.flags = 16777352;
        lp.gravity = 17;
        if (sharedId == 0) {
            lp.xpFlags |= 16;
        } else if (sharedId == 1) {
            lp.xpFlags |= 32;
        }
        return lp;
    }
}
