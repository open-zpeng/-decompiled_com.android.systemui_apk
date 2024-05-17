package com.xiaopeng.xui.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.utils.XDialogUtils;
import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.vui.VuiViewScene;
import com.xiaopeng.xui.widget.XFrameLayout;
/* loaded from: classes25.dex */
public class XDialogPure extends VuiViewScene {
    private Context mContext;
    private Dialog mDialog;
    private int mSystemDialogOffsetY;
    private int mWindowBackgroundId;
    private XDialogContentView mXDialogContentView;

    public XDialogPure(@NonNull Context context) {
        this(context, null);
    }

    public XDialogPure(@NonNull Context context, Parameters parameters) {
        this.mContext = context;
        parameters = parameters == null ? Parameters.Builder() : parameters;
        if (parameters.mTheme != 0) {
            this.mDialog = new Dialog(context, parameters.mTheme);
        } else {
            this.mDialog = new Dialog(context, R.style.XAppTheme_XDialog);
        }
        if (parameters.mFullScreen) {
            XDialogUtils.requestFullScreen(this.mDialog);
        }
        this.mSystemDialogOffsetY = (int) context.getResources().getDimension(R.dimen.x_dialog_system_offset_y);
        this.mXDialogContentView = new XDialogContentView(context);
        if (parameters.mLayoutParams == null) {
            this.mDialog.setContentView(this.mXDialogContentView);
        } else {
            this.mDialog.setContentView(this.mXDialogContentView, parameters.mLayoutParams);
        }
        this.mXDialogContentView.setThemeCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialogPure$oBvFUIXdKuJ3Jvr1GZC5TRRMqAU
            @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
            public final void onThemeChanged() {
                XDialogPure.this.lambda$new$0$XDialogPure();
            }
        });
        TypedArray array = this.mDialog.getContext().obtainStyledAttributes(new int[]{16842836});
        this.mWindowBackgroundId = array.getResourceId(0, 0);
        array.recycle();
    }

    public /* synthetic */ void lambda$new$0$XDialogPure() {
        logs("onThemeChanged, mWindowBackgroundId " + this.mWindowBackgroundId);
        if (this.mWindowBackgroundId == 0) {
            this.mWindowBackgroundId = R.drawable.x_bg_dialog;
        }
        if (this.mDialog.getWindow() != null && this.mWindowBackgroundId > 0) {
            this.mDialog.getWindow().setBackgroundDrawableResource(this.mWindowBackgroundId);
        }
    }

    public void setThemeCallback(ThemeViewModel.OnCallback onCallback) {
        this.mXDialogContentView.setThemeCallback(onCallback);
    }

    public void setContentView(@LayoutRes int layoutResID) {
        View view = LayoutInflater.from(this.mContext).inflate(layoutResID, (ViewGroup) this.mXDialogContentView, false);
        setContentView(view);
    }

    public void setContentView(@NonNull View contentView) {
        this.mXDialogContentView.removeAllViews();
        this.mXDialogContentView.addView(contentView);
        setVuiView(contentView);
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    public void show() {
        if (this.mDialog.getWindow() != null) {
            WindowManager.LayoutParams lp = this.mDialog.getWindow().getAttributes();
            lp.gravity = 17;
            lp.y = lp.type != 9 ? this.mSystemDialogOffsetY : 0;
            this.mDialog.getWindow().setAttributes(lp);
        }
        this.mDialog.show();
    }

    public void dismiss() {
        this.mDialog.dismiss();
    }

    public void cancel() {
        this.mDialog.cancel();
    }

    public boolean isShowing() {
        return this.mDialog.isShowing();
    }

    public void setSystemDialog(int type) {
        if (this.mDialog.getWindow() != null) {
            this.mDialog.getWindow().setType(type);
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.mDialog.setOnDismissListener(listener);
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        this.mDialog.setOnCancelListener(listener);
    }

    public void setOnShowListener(DialogInterface.OnShowListener listener) {
        this.mDialog.setOnShowListener(listener);
    }

    public void setOnKeyListener(DialogInterface.OnKeyListener listener) {
        this.mDialog.setOnKeyListener(listener);
    }

    public void setCancelable(boolean cancelable) {
        this.mDialog.setCancelable(cancelable);
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        this.mDialog.setCanceledOnTouchOutside(cancel);
    }

    public void setScreenId(int screenId) {
        XDialogUtils.setScreenId(this.mDialog, screenId);
    }

    public int getScreenId() {
        return XDialogUtils.getScreenId(this.mDialog);
    }

    @Override // com.xiaopeng.xui.vui.VuiViewScene
    public void onBuildScenePrepare() {
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public int getVuiDisplayLocation() {
        Dialog dialog = this.mDialog;
        if (dialog == null) {
            return 0;
        }
        return XDialogUtils.getScreenId(dialog);
    }

    /* loaded from: classes25.dex */
    public static class Parameters {
        private boolean mFullScreen = Xui.isDialogFullScreen();
        private ViewGroup.LayoutParams mLayoutParams;
        private int mTheme;

        public static Parameters Builder() {
            return new Parameters();
        }

        private Parameters() {
        }

        public Parameters setTheme(int theme) {
            this.mTheme = theme;
            return this;
        }

        public Parameters setFullScreen(boolean fullScreen) {
            this.mFullScreen = fullScreen;
            return this;
        }

        public Parameters setLayoutParams(ViewGroup.LayoutParams layoutParams) {
            this.mLayoutParams = layoutParams;
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes25.dex */
    public class XDialogContentView extends XFrameLayout {
        public XDialogContentView(@NonNull Context context) {
            super(context);
        }

        @Override // android.view.View
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
            if (this.mXViewDelegate != null) {
                this.mXViewDelegate.onWindowFocusChanged(hasWindowFocus);
            }
        }

        public void setThemeCallback(ThemeViewModel.OnCallback onCallback) {
            if (this.mXViewDelegate != null && this.mXViewDelegate.getThemeViewModel() != null) {
                this.mXViewDelegate.getThemeViewModel().setCallback(onCallback);
            }
        }
    }

    private void logs(String msg) {
        XLogUtils.i("XDialogPure", msg + "--hashcode " + hashCode());
    }
}
