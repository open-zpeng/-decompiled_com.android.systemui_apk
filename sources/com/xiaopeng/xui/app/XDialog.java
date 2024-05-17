package com.xiaopeng.xui.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.IVuiEngine;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.app.XDialogInterface;
import com.xiaopeng.xui.utils.XDialogUtils;
import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.vui.IVuiViewScene;
import com.xiaopeng.xui.widget.dialogview.XDialogView;
import com.xiaopeng.xui.widget.dialogview.XDialogViewInterface;
import java.util.List;
/* loaded from: classes25.dex */
public class XDialog implements IVuiViewScene {
    public static final int ID_SCREEN_PRIMARY = 0;
    public static final int ID_SCREEN_SECONDARY = 1;
    private static int sObjectSize;
    private Context mContext;
    private Dialog mDialog;
    private XDialogInterface.OnClickListener mNegativeListener;
    private XDialogViewInterface.OnClickListener mNegativeListenerProxy;
    private XDialogInterface.OnCloseListener mOnCloseListener;
    private XDialogViewInterface.OnCloseListener mOnCloseListenerProxy;
    private XDialogInterface.OnCountDownListener mOnCountDownListener;
    private XDialogViewInterface.OnCountDownListener mOnCountDownListenerProxy;
    private DialogInterface.OnKeyListener mOnKeyListener;
    private XDialogInterface.OnClickListener mPositiveListener;
    private XDialogViewInterface.OnClickListener mPositiveListenerProxy;
    private int mSystemDialogOffsetY;
    private int mWindowBackgroundId;
    private XDialogView mXDialogView;

    public XDialog(@NonNull Context context) {
        this(context, 0);
    }

    public XDialog(@NonNull Context context, @Nullable Parameters parameters) {
        this(context, 0, parameters);
    }

    public XDialog(@NonNull Context context, @StyleRes int dialogViewStyle) {
        this(context, dialogViewStyle, null);
    }

    public XDialog(@NonNull Context context, @StyleRes int dialogViewStyle, @Nullable Parameters parameters) {
        this.mXDialogView = new XDialogView(context, dialogViewStyle);
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
        this.mDialog.setContentView(this.mXDialogView.getContentView());
        init();
        sObjectSize++;
    }

    private void init() {
        this.mSystemDialogOffsetY = (int) this.mContext.getResources().getDimension(R.dimen.x_dialog_system_offset_y);
        this.mXDialogView.setOnDismissListener(new XDialogViewInterface.OnDismissListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$9P4fB9VBMHmiGhl_mlGUJko3V2E
            @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnDismissListener
            public final void onDismiss(XDialogView xDialogView) {
                XDialog.this.lambda$init$0$XDialog(xDialogView);
            }
        });
        this.mXDialogView.setThemeCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$s3CmKHbTxXoHkg0e6JUBfY_y_-Q
            @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
            public final void onThemeChanged() {
                XDialog.this.lambda$init$1$XDialog();
            }
        });
        this.mXDialogView.setScreenPositionCallback(new XDialogViewInterface.IScreenPositionCallback() { // from class: com.xiaopeng.xui.app.XDialog.1
            @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.IScreenPositionCallback
            public int getScreenPositionInfo() {
                return XDialog.this.getVuiDisplayLocation();
            }
        });
        this.mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$WZ3Q9pXFsK8sg4PXWTmacfC8DAk
            @Override // android.content.DialogInterface.OnKeyListener
            public final boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return XDialog.this.lambda$init$2$XDialog(dialogInterface, i, keyEvent);
            }
        });
        TypedArray array = this.mDialog.getContext().obtainStyledAttributes(new int[]{16842836});
        this.mWindowBackgroundId = array.getResourceId(0, 0);
        array.recycle();
    }

    public /* synthetic */ void lambda$init$0$XDialog(XDialogView dialogView) {
        dismiss();
    }

    public /* synthetic */ void lambda$init$1$XDialog() {
        logs("onThemeChanged, mWindowBackgroundId " + this.mWindowBackgroundId);
        if (this.mWindowBackgroundId == 0) {
            this.mWindowBackgroundId = R.drawable.x_bg_dialog;
        }
        if (this.mDialog.getWindow() != null && this.mWindowBackgroundId > 0) {
            this.mDialog.getWindow().setBackgroundDrawableResource(this.mWindowBackgroundId);
        }
    }

    public /* synthetic */ boolean lambda$init$2$XDialog(DialogInterface dialog, int keyCode, KeyEvent event) {
        DialogInterface.OnKeyListener onKeyListener = this.mOnKeyListener;
        if (onKeyListener != null && onKeyListener.onKey(dialog, keyCode, event)) {
            logs("custom key listener return true  keyCode : " + keyCode + ", event " + event.getAction());
            return true;
        }
        return this.mXDialogView.onKey(keyCode, event);
    }

    private void initCloseListenerProxy() {
        if (this.mOnCloseListenerProxy == null) {
            this.mOnCloseListenerProxy = new XDialogViewInterface.OnCloseListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$l1LARSaNT8tDepVXLgxNqVdJkkc
                @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnCloseListener
                public final boolean onClose(XDialogView xDialogView) {
                    return XDialog.this.lambda$initCloseListenerProxy$3$XDialog(xDialogView);
                }
            };
            this.mXDialogView.setOnCloseListener(this.mOnCloseListenerProxy);
        }
    }

    public /* synthetic */ boolean lambda$initCloseListenerProxy$3$XDialog(XDialogView dialogView) {
        XDialogInterface.OnCloseListener onCloseListener = this.mOnCloseListener;
        if (onCloseListener != null) {
            return onCloseListener.onClose(this);
        }
        return false;
    }

    private void initCountDownListenerProxy() {
        if (this.mOnCountDownListenerProxy == null) {
            this.mOnCountDownListenerProxy = new XDialogViewInterface.OnCountDownListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$fbj-1PFv3EpqSHcLX-8gv1vcQx4
                @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnCountDownListener
                public final boolean onCountDown(XDialogView xDialogView, int i) {
                    return XDialog.this.lambda$initCountDownListenerProxy$4$XDialog(xDialogView, i);
                }
            };
            this.mXDialogView.setOnCountDownListener(this.mOnCountDownListenerProxy);
        }
    }

    public /* synthetic */ boolean lambda$initCountDownListenerProxy$4$XDialog(XDialogView dialogView, int which) {
        XDialogInterface.OnCountDownListener onCountDownListener = this.mOnCountDownListener;
        if (onCountDownListener != null) {
            return onCountDownListener.onCountDown(this, which);
        }
        return false;
    }

    private void initPositiveListenerProxy() {
        if (this.mPositiveListenerProxy == null) {
            this.mPositiveListenerProxy = new XDialogViewInterface.OnClickListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$8umUr7E18qQsZk8zQujzHQFIHPY
                @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnClickListener
                public final void onClick(XDialogView xDialogView, int i) {
                    XDialog.this.lambda$initPositiveListenerProxy$5$XDialog(xDialogView, i);
                }
            };
        }
    }

    public /* synthetic */ void lambda$initPositiveListenerProxy$5$XDialog(XDialogView dialogView, int which) {
        XDialogInterface.OnClickListener onClickListener = this.mPositiveListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, which);
        }
    }

    private void initNegativeListenerProxy() {
        if (this.mNegativeListenerProxy == null) {
            this.mNegativeListenerProxy = new XDialogViewInterface.OnClickListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$FFTotuu-r5O5A4gt3f42vBsU9o4
                @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnClickListener
                public final void onClick(XDialogView xDialogView, int i) {
                    XDialog.this.lambda$initNegativeListenerProxy$6$XDialog(xDialogView, i);
                }
            };
        }
    }

    public /* synthetic */ void lambda$initNegativeListenerProxy$6$XDialog(XDialogView dialogView, int which) {
        XDialogInterface.OnClickListener onClickListener = this.mNegativeListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, which);
        }
    }

    public ViewGroup getContentView() {
        return this.mXDialogView.getContentView();
    }

    public XDialog setPositiveButtonInterceptDismiss(boolean interceptDismiss) {
        this.mXDialogView.setPositiveButtonInterceptDismiss(interceptDismiss);
        return this;
    }

    public XDialog setNegativeButtonInterceptDismiss(boolean interceptDismiss) {
        this.mXDialogView.setNegativeButtonInterceptDismiss(interceptDismiss);
        return this;
    }

    public XDialog setTitle(@Nullable CharSequence text) {
        this.mXDialogView.setTitle(text);
        return this;
    }

    public XDialog setTitle(@StringRes int resId) {
        this.mXDialogView.setTitle(resId);
        return this;
    }

    public XDialog setIcon(@DrawableRes int iconId) {
        this.mXDialogView.setIcon(iconId);
        return this;
    }

    public XDialog setIcon(@Nullable Drawable icon) {
        this.mXDialogView.setIcon(icon);
        return this;
    }

    public XDialog setMessage(@Nullable CharSequence text) {
        this.mXDialogView.setMessage(text);
        return this;
    }

    public XDialog setMessage(@StringRes int resId) {
        this.mXDialogView.setMessage(resId);
        return this;
    }

    public XDialog setCustomView(@NonNull View view) {
        this.mXDialogView.setCustomView(view);
        return this;
    }

    public XDialog setCustomView(@NonNull View view, boolean useScroll) {
        this.mXDialogView.setCustomView(view, useScroll);
        return this;
    }

    public XDialog setCustomView(@LayoutRes int resId) {
        this.mXDialogView.setCustomView(resId);
        return this;
    }

    public XDialog setCustomView(@LayoutRes int resId, boolean useScroll) {
        this.mXDialogView.setCustomView(resId, useScroll);
        return this;
    }

    public XDialog setCloseVisibility(boolean show) {
        this.mXDialogView.setCloseVisibility(show);
        return this;
    }

    public void setThemeCallback(ThemeViewModel.OnCallback onCallback) {
        this.mXDialogView.setThemeCallback(onCallback);
    }

    public boolean isCloseShowing() {
        return this.mXDialogView.isCloseShowing();
    }

    @Deprecated
    public XDialog setTitleVisibility(boolean show) {
        setTitleBarVisibility(show);
        return this;
    }

    public XDialog setTitleBarVisibility(boolean show) {
        this.mXDialogView.setTitleBarVisibility(show);
        return this;
    }

    public XDialog setPositiveButton(@StringRes int resId) {
        this.mXDialogView.setPositiveButton(resId);
        return this;
    }

    public XDialog setPositiveButton(@Nullable CharSequence text) {
        this.mXDialogView.setPositiveButton(text);
        return this;
    }

    public XDialog setPositiveButtonListener(XDialogInterface.OnClickListener listener) {
        this.mPositiveListener = listener;
        if (listener != null) {
            initPositiveListenerProxy();
        }
        this.mXDialogView.setPositiveButtonListener(this.mPositiveListenerProxy);
        return this;
    }

    public XDialog setPositiveButton(@StringRes int resId, XDialogInterface.OnClickListener listener) {
        setPositiveButton(resId);
        setPositiveButtonListener(listener);
        return this;
    }

    public XDialog setPositiveButton(@Nullable CharSequence text, XDialogInterface.OnClickListener listener) {
        setPositiveButton(text);
        setPositiveButtonListener(listener);
        return this;
    }

    public XDialog setNegativeButton(@StringRes int resId) {
        this.mXDialogView.setNegativeButton(resId);
        return this;
    }

    public XDialog setNegativeButton(@Nullable CharSequence text) {
        this.mXDialogView.setNegativeButton(text);
        return this;
    }

    public XDialog setNegativeButtonListener(XDialogInterface.OnClickListener listener) {
        this.mNegativeListener = listener;
        if (listener != null) {
            initNegativeListenerProxy();
        }
        this.mXDialogView.setNegativeButtonListener(this.mNegativeListenerProxy);
        return this;
    }

    public XDialog setNegativeButton(@StringRes int resId, XDialogInterface.OnClickListener listener) {
        setNegativeButton(resId);
        setNegativeButtonListener(listener);
        return this;
    }

    public XDialog setNegativeButton(@Nullable CharSequence text, XDialogInterface.OnClickListener listener) {
        setNegativeButton(text);
        setNegativeButtonListener(listener);
        return this;
    }

    public XDialog setPositiveButtonEnable(boolean enable) {
        this.mXDialogView.setPositiveButtonEnable(enable);
        return this;
    }

    public XDialog setNegativeButtonEnable(boolean enable) {
        this.mXDialogView.setNegativeButtonEnable(enable);
        return this;
    }

    public boolean isPositiveButtonEnable() {
        return this.mXDialogView.isPositiveButtonEnable();
    }

    public boolean isNegativeButtonEnable() {
        return this.mXDialogView.isNegativeButtonEnable();
    }

    public boolean isPositiveButtonShowing() {
        return this.mXDialogView.isPositiveButtonShowing();
    }

    public boolean isNegativeButtonShowing() {
        return this.mXDialogView.isNegativeButtonShowing();
    }

    public void show() {
        show(0, 0);
    }

    public void show(int delayDismissPositive, int delayDismissNegative) {
        logs("show");
        if (delayDismissPositive > 0 && delayDismissNegative == 0) {
            this.mXDialogView.startPositiveButtonCountDown(delayDismissPositive);
        }
        if (delayDismissNegative > 0 && delayDismissPositive == 0) {
            this.mXDialogView.startNegativeButtonCountDown(delayDismissNegative);
        }
        if (this.mDialog.getWindow() != null) {
            WindowManager.LayoutParams lp = this.mDialog.getWindow().getAttributes();
            lp.gravity = 17;
            lp.y = lp.type != 9 ? this.mSystemDialogOffsetY : 0;
            this.mDialog.getWindow().setAttributes(lp);
        }
        this.mDialog.show();
    }

    public void dismiss() {
        logs("dismiss");
        this.mDialog.dismiss();
    }

    public void cancel() {
        logs("cancel");
        this.mDialog.cancel();
    }

    public boolean isShowing() {
        return this.mDialog.isShowing();
    }

    public XDialog setOnCloseListener(XDialogInterface.OnCloseListener listener) {
        this.mOnCloseListener = listener;
        if (listener != null) {
            initCloseListenerProxy();
        }
        return this;
    }

    public XDialog setOnCountDownListener(XDialogInterface.OnCountDownListener listener) {
        this.mOnCountDownListener = listener;
        if (listener != null) {
            initCountDownListenerProxy();
        }
        return this;
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    public XDialog setSystemDialog(int type) {
        if (this.mDialog.getWindow() != null) {
            this.mDialog.getWindow().setType(type);
        }
        return this;
    }

    public XDialog setScreenId(int screenId) {
        XDialogUtils.setScreenId(this.mDialog, screenId);
        return this;
    }

    public int getScreenId() {
        return XDialogUtils.getScreenId(this.mDialog);
    }

    public XDialog setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.mDialog.setOnDismissListener(listener);
        return this;
    }

    public XDialog setOnCancelListener(DialogInterface.OnCancelListener listener) {
        this.mDialog.setOnCancelListener(listener);
        return this;
    }

    public XDialog setOnShowListener(DialogInterface.OnShowListener listener) {
        this.mDialog.setOnShowListener(listener);
        return this;
    }

    public XDialog setOnKeyListener(DialogInterface.OnKeyListener listener) {
        this.mOnKeyListener = listener;
        return this;
    }

    public XDialog setCancelable(boolean cancelable) {
        this.mDialog.setCancelable(cancelable);
        return this;
    }

    public XDialog setCanceledOnTouchOutside(boolean cancel) {
        this.mDialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiSceneId(String sceneId) {
        this.mXDialogView.setVuiSceneId(sceneId);
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiEngine(IVuiEngine vuiEngine) {
        this.mXDialogView.setVuiEngine(vuiEngine);
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiElementListener(IVuiElementListener listener) {
        this.mXDialogView.setVuiElementListener(listener);
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setCustomViewIdList(List<Integer> list) {
        this.mXDialogView.setCustomViewIdList(list);
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void initVuiScene(String sceneId, IVuiEngine vuiEngine) {
        this.mXDialogView.initVuiScene(sceneId, vuiEngine);
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void initVuiScene(String sceneId, IVuiEngine vuiEngine, IVuiSceneListener listener) {
        this.mXDialogView.initVuiScene(sceneId, vuiEngine, listener);
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public int getVuiDisplayLocation() {
        Dialog dialog = this.mDialog;
        if (dialog == null) {
            return 0;
        }
        int id = XDialogUtils.getScreenId(dialog);
        logs("getVuiDisplayLocation   value:  " + id);
        return id;
    }

    protected void finalize() throws Throwable {
        super.finalize();
        sObjectSize--;
        logs(" finalize object size " + sObjectSize);
    }

    /* loaded from: classes25.dex */
    public static class Parameters {
        private boolean mFullScreen = Xui.isDialogFullScreen();
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
    }

    private void logs(String msg) {
        XLogUtils.i("XDialog", msg + "--hashcode " + hashCode());
    }
}
