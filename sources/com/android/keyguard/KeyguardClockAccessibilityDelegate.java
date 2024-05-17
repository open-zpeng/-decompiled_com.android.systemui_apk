package com.android.keyguard;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
/* loaded from: classes19.dex */
class KeyguardClockAccessibilityDelegate extends View.AccessibilityDelegate {
    private final String mFancyColon;

    public KeyguardClockAccessibilityDelegate(Context context) {
        this.mFancyColon = context.getString(R.string.keyguard_fancy_colon);
    }

    @Override // android.view.View.AccessibilityDelegate
    public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(host, event);
        if (TextUtils.isEmpty(this.mFancyColon)) {
            return;
        }
        CharSequence text = event.getContentDescription();
        if (!TextUtils.isEmpty(text)) {
            event.setContentDescription(replaceFancyColon(text));
        }
    }

    @Override // android.view.View.AccessibilityDelegate
    public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
        if (TextUtils.isEmpty(this.mFancyColon)) {
            super.onPopulateAccessibilityEvent(host, event);
            return;
        }
        CharSequence text = ((TextView) host).getText();
        if (!TextUtils.isEmpty(text)) {
            event.getText().add(replaceFancyColon(text));
        }
    }

    @Override // android.view.View.AccessibilityDelegate
    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(host, info);
        if (TextUtils.isEmpty(this.mFancyColon)) {
            return;
        }
        if (!TextUtils.isEmpty(info.getText())) {
            info.setText(replaceFancyColon(info.getText()));
        }
        if (!TextUtils.isEmpty(info.getContentDescription())) {
            info.setContentDescription(replaceFancyColon(info.getContentDescription()));
        }
    }

    private CharSequence replaceFancyColon(CharSequence text) {
        if (TextUtils.isEmpty(this.mFancyColon)) {
            return text;
        }
        return text.toString().replace(this.mFancyColon, NavigationBarInflaterView.KEY_IMAGE_DELIM);
    }

    public static boolean isNeeded(Context context) {
        return !TextUtils.isEmpty(context.getString(R.string.keyguard_fancy_colon));
    }
}
