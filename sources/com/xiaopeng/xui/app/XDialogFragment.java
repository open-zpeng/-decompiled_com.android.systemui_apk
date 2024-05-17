package com.xiaopeng.xui.app;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.theme.XThemeManager;
import com.xiaopeng.xui.utils.XDialogUtils;
import com.xiaopeng.xui.widget.dialogview.XDialogView;
import com.xiaopeng.xui.widget.dialogview.XDialogViewInterface;
/* loaded from: classes25.dex */
public abstract class XDialogFragment extends DialogFragment {
    private static final String TAG = "XDialogFragment";
    protected XDialogView mXDialogView;

    protected abstract XDialogView onCreateDialogView(Context context);

    @Override // androidx.fragment.app.Fragment
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        XDialogView dialogView = onCreateDialogView(getContext());
        if (dialogView != null) {
            this.mXDialogView = dialogView;
            this.mXDialogView.setOnDismissListener(new XDialogViewInterface.OnDismissListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialogFragment$x5B-nKf3XZWM7G-iAeQ2siL8JDY
                @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnDismissListener
                public final void onDismiss(XDialogView xDialogView) {
                    XDialogFragment.this.lambda$onCreateView$0$XDialogFragment(xDialogView);
                }
            });
            if (isFullScreen()) {
                XDialogUtils.requestFullScreen(getDialog());
            }
            return this.mXDialogView.getContentView();
        }
        return null;
    }

    public /* synthetic */ void lambda$onCreateView$0$XDialogFragment(XDialogView dialogView1) {
        dismiss();
    }

    protected boolean isFullScreen() {
        return Xui.isDialogFullScreen();
    }

    @Override // androidx.fragment.app.DialogFragment
    public int getTheme() {
        return R.style.XAppTheme_XDialog;
    }

    @Override // androidx.fragment.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getDialog() != null && getDialog().getWindow() != null) {
            XThemeManager.setWindowBackgroundResource(newConfig, getDialog().getWindow(), R.drawable.x_bg_dialog);
        }
    }
}
