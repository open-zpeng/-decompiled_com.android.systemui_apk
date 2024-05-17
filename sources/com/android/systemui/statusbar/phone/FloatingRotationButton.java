package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.android.settingslib.Utils;
import com.android.systemui.R;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
import com.android.systemui.statusbar.policy.KeyButtonView;
import com.xiaopeng.systemui.helper.WindowHelper;
/* loaded from: classes21.dex */
public class FloatingRotationButton implements RotationButton {
    private static final float BACKGROUND_ALPHA = 0.92f;
    private boolean mCanShow = true;
    private final Context mContext;
    private final int mDiameter;
    private boolean mIsShowing;
    private KeyButtonDrawable mKeyButtonDrawable;
    private final KeyButtonView mKeyButtonView;
    private final int mMargin;
    private RotationButtonController mRotationButtonController;
    private final WindowManager mWindowManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FloatingRotationButton(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        this.mKeyButtonView = (KeyButtonView) LayoutInflater.from(this.mContext).inflate(R.layout.rotate_suggestion, (ViewGroup) null);
        this.mKeyButtonView.setVisibility(0);
        Resources res = this.mContext.getResources();
        this.mDiameter = res.getDimensionPixelSize(R.dimen.floating_rotation_button_diameter);
        this.mMargin = Math.max(res.getDimensionPixelSize(R.dimen.floating_rotation_button_min_margin), res.getDimensionPixelSize(R.dimen.rounded_corner_content_padding));
    }

    @Override // com.android.systemui.statusbar.phone.RotationButton
    public void setRotationButtonController(RotationButtonController rotationButtonController) {
        this.mRotationButtonController = rotationButtonController;
    }

    @Override // com.android.systemui.statusbar.phone.RotationButton
    public View getCurrentView() {
        return this.mKeyButtonView;
    }

    @Override // com.android.systemui.statusbar.phone.RotationButton
    public boolean show() {
        if (!this.mCanShow || this.mIsShowing) {
            return false;
        }
        this.mIsShowing = true;
        int i = this.mDiameter;
        int i2 = this.mMargin;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(i, i, i2, i2, WindowHelper.TYPE_NAVIGATION_BAR_PANEL, 8, -3);
        lp.privateFlags |= 16;
        lp.setTitle("FloatingRotationButton");
        int rotation = this.mWindowManager.getDefaultDisplay().getRotation();
        if (rotation != 0) {
            if (rotation == 1) {
                lp.gravity = 85;
            } else if (rotation == 2) {
                lp.gravity = 53;
            } else if (rotation == 3) {
                lp.gravity = 51;
            }
        } else {
            lp.gravity = 83;
        }
        updateIcon();
        this.mWindowManager.addView(this.mKeyButtonView, lp);
        KeyButtonDrawable keyButtonDrawable = this.mKeyButtonDrawable;
        if (keyButtonDrawable != null && keyButtonDrawable.canAnimate()) {
            this.mKeyButtonDrawable.resetAnimation();
            this.mKeyButtonDrawable.startAnimation();
        }
        return true;
    }

    @Override // com.android.systemui.statusbar.phone.RotationButton
    public boolean hide() {
        if (this.mIsShowing) {
            this.mWindowManager.removeViewImmediate(this.mKeyButtonView);
            this.mIsShowing = false;
            return true;
        }
        return false;
    }

    @Override // com.android.systemui.statusbar.phone.RotationButton
    public boolean isVisible() {
        return this.mIsShowing;
    }

    @Override // com.android.systemui.statusbar.phone.RotationButton
    public void updateIcon() {
        if (!this.mIsShowing) {
            return;
        }
        this.mKeyButtonDrawable = getImageDrawable();
        this.mKeyButtonView.setImageDrawable(this.mKeyButtonDrawable);
        this.mKeyButtonDrawable.setCallback(this.mKeyButtonView);
        KeyButtonDrawable keyButtonDrawable = this.mKeyButtonDrawable;
        if (keyButtonDrawable != null && keyButtonDrawable.canAnimate()) {
            this.mKeyButtonDrawable.resetAnimation();
            this.mKeyButtonDrawable.startAnimation();
        }
    }

    @Override // com.android.systemui.statusbar.phone.RotationButton
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mKeyButtonView.setOnClickListener(onClickListener);
    }

    @Override // com.android.systemui.statusbar.phone.RotationButton
    public void setOnHoverListener(View.OnHoverListener onHoverListener) {
        this.mKeyButtonView.setOnHoverListener(onHoverListener);
    }

    @Override // com.android.systemui.statusbar.phone.RotationButton
    public KeyButtonDrawable getImageDrawable() {
        Context context = new ContextThemeWrapper(this.mContext.getApplicationContext(), this.mRotationButtonController.getStyleRes());
        int dualToneDarkTheme = Utils.getThemeAttr(context, R.attr.darkIconTheme);
        int dualToneLightTheme = Utils.getThemeAttr(context, R.attr.lightIconTheme);
        Context lightContext = new ContextThemeWrapper(context, dualToneLightTheme);
        Context darkContext = new ContextThemeWrapper(context, dualToneDarkTheme);
        int darkColor = Utils.getColorAttrDefaultColor(darkContext, R.attr.singleToneColor);
        Color ovalBackgroundColor = Color.valueOf(Color.red(darkColor), Color.green(darkColor), Color.blue(darkColor), 0.92f);
        return KeyButtonDrawable.create(lightContext, Utils.getColorAttrDefaultColor(lightContext, R.attr.singleToneColor), darkColor, R.drawable.ic_sysbar_rotate_button, false, ovalBackgroundColor);
    }

    @Override // com.android.systemui.statusbar.phone.RotationButton
    public void setDarkIntensity(float darkIntensity) {
        this.mKeyButtonView.setDarkIntensity(darkIntensity);
    }

    @Override // com.android.systemui.statusbar.phone.RotationButton
    public void setCanShowRotationButton(boolean canShow) {
        this.mCanShow = canShow;
        if (!this.mCanShow) {
            hide();
        }
    }
}
