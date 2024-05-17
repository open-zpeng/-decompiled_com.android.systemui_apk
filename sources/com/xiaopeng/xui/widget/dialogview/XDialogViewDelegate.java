package com.xiaopeng.xui.widget.dialogview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.dialogview.XDialogViewInterface;
/* loaded from: classes25.dex */
public abstract class XDialogViewDelegate {
    protected Context mContext;
    protected XDialogView mXDelegator;

    public abstract ViewGroup getContentView();

    public abstract boolean isCloseShowing();

    public abstract boolean isNegativeButtonEnable();

    public abstract boolean isNegativeButtonShowing();

    public abstract boolean isPositiveButtonEnable();

    public abstract boolean isPositiveButtonShowing();

    public abstract void onBuildScenePrepare();

    public abstract boolean onKey(int i, KeyEvent keyEvent);

    public abstract void setCloseVisibility(boolean z);

    public abstract void setCustomView(@LayoutRes int i);

    public abstract void setCustomView(@LayoutRes int i, boolean z);

    public abstract void setCustomView(@NonNull View view);

    public abstract void setCustomView(@NonNull View view, boolean z);

    public abstract void setIcon(@DrawableRes int i);

    public abstract void setIcon(@Nullable Drawable drawable);

    public abstract void setMessage(@StringRes int i);

    public abstract void setMessage(@Nullable CharSequence charSequence);

    public abstract void setNegativeButton(int i);

    public abstract void setNegativeButton(@StringRes int i, XDialogViewInterface.OnClickListener onClickListener);

    public abstract void setNegativeButton(@Nullable CharSequence charSequence);

    public abstract void setNegativeButton(@Nullable CharSequence charSequence, XDialogViewInterface.OnClickListener onClickListener);

    public abstract void setNegativeButtonEnable(boolean z);

    public abstract void setNegativeButtonInterceptDismiss(boolean z);

    public abstract void setNegativeButtonListener(XDialogViewInterface.OnClickListener onClickListener);

    public abstract void setOnCloseListener(XDialogViewInterface.OnCloseListener onCloseListener);

    public abstract void setOnCountDownListener(XDialogViewInterface.OnCountDownListener onCountDownListener);

    public abstract void setOnDismissListener(XDialogViewInterface.OnDismissListener onDismissListener);

    public abstract void setPositiveButton(@StringRes int i);

    public abstract void setPositiveButton(@StringRes int i, XDialogViewInterface.OnClickListener onClickListener);

    public abstract void setPositiveButton(@Nullable CharSequence charSequence);

    public abstract void setPositiveButton(@Nullable CharSequence charSequence, XDialogViewInterface.OnClickListener onClickListener);

    public abstract void setPositiveButtonEnable(boolean z);

    public abstract void setPositiveButtonInterceptDismiss(boolean z);

    public abstract void setPositiveButtonListener(XDialogViewInterface.OnClickListener onClickListener);

    @Deprecated
    public abstract void setSingleChoiceItems(CharSequence[] charSequenceArr, int i, XDialogViewInterface.OnClickListener onClickListener);

    public abstract void setThemeCallback(ThemeViewModel.OnCallback onCallback);

    public abstract void setTitle(@StringRes int i);

    public abstract void setTitle(@Nullable CharSequence charSequence);

    public abstract void setTitleBarVisibility(boolean z);

    @Deprecated
    public abstract void setTitleVisibility(boolean z);

    public abstract void startNegativeButtonCountDown(int i);

    public abstract void startPositiveButtonCountDown(int i);

    public static XDialogViewDelegate create(XDialogView xDialogView, @NonNull Context context, int style) {
        return new XDialogViewDelegateImpl(xDialogView, context, style);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XDialogViewDelegate(XDialogView xDialogView, @NonNull Context context, int style) {
        this.mXDelegator = xDialogView;
        this.mContext = new ContextThemeWrapper(context, style <= 0 ? R.style.XDialogView : style);
    }
}
