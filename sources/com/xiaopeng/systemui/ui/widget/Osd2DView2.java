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
public class Osd2DView2 implements IOsdView {
    private static final String TAG = "Osd2DView2";
    private WindowManager.LayoutParams mParams;
    private int mScreenId = -1;
    private Context mContext = ContextUtils.getContext();
    private OsdView2 mOsdView = new OsdView2(this.mContext);
    private WindowManager mWindowManager = StatusBarGlobal.getInstance(this.mContext).getWindowManager();

    @Override // com.xiaopeng.systemui.ui.widget.IOsdView
    public void dispatchConfigurationChanged(Configuration newConfig) {
        this.mOsdView.dispatchConfigurationChanged(newConfig);
    }

    @Override // com.xiaopeng.systemui.ui.widget.IOsdView
    public void showOsd(OsdController.OsdParams params) {
        Logger.d(TAG, "showOsd params=" + params);
        this.mOsdView.apply(params);
    }

    @Override // com.xiaopeng.systemui.ui.widget.IOsdView
    public void showOsd(boolean show) {
        Logger.d(TAG, "showOsd show=" + show);
        if (show) {
            showOsd();
        } else {
            hideOsd();
        }
    }

    private void showOsd() {
        if (this.mParams == null) {
            this.mParams = createLayoutParams();
        }
        int screenId = this.mOsdView.getScreenId();
        boolean changed = screenId != this.mScreenId;
        this.mScreenId = screenId;
        if (this.mOsdView.isAttachedToWindow()) {
            try {
                if (changed) {
                    this.mWindowManager.removeViewImmediate(this.mOsdView);
                    this.mWindowManager.addView(this.mOsdView, this.mParams);
                } else {
                    this.mWindowManager.updateViewLayout(this.mOsdView, this.mParams);
                }
                return;
            } catch (Exception e) {
                Logger.d(TAG, "showOsd 1 e=" + e);
                return;
            }
        }
        try {
            this.mWindowManager.addView(this.mOsdView, this.mParams);
        } catch (Exception e2) {
            try {
                this.mWindowManager.removeViewImmediate(this.mOsdView);
                this.mWindowManager.addView(this.mOsdView, this.mParams);
            } catch (Exception ee) {
                Logger.d(TAG, "showOsd 2 ee=" + ee);
            }
            Logger.d(TAG, "showOsd 3 e=" + e2);
        }
    }

    private void hideOsd() {
        OsdView2 osdView2 = this.mOsdView;
        if (osdView2 != null && osdView2.isAttachedToWindow()) {
            this.mWindowManager.removeViewImmediate(this.mOsdView);
        }
    }

    private WindowManager.LayoutParams createLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, WindowHelper.TYPE_OSD, 25165960, -3);
        layoutParams.windowAnimations = 16973828;
        layoutParams.width = this.mContext.getResources().getDimensionPixelSize(R.dimen.view_osd2_window_width);
        layoutParams.height = this.mContext.getResources().getDimensionPixelSize(R.dimen.view_osd2_window_height);
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.gravity = 49;
        return layoutParams;
    }
}
