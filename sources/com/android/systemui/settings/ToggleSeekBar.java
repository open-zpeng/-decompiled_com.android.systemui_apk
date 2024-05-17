package com.android.systemui.settings;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.SeekBar;
import com.android.settingslib.RestrictedLockUtils;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.ActivityStarter;
/* loaded from: classes21.dex */
public class ToggleSeekBar extends SeekBar {
    private String mAccessibilityLabel;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;

    public ToggleSeekBar(Context context) {
        super(context);
        this.mEnforcedAdmin = null;
    }

    public ToggleSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mEnforcedAdmin = null;
    }

    public ToggleSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mEnforcedAdmin = null;
    }

    @Override // android.widget.AbsSeekBar, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mEnforcedAdmin != null) {
            Intent intent = RestrictedLockUtils.getShowAdminSupportDetailsIntent(this.mContext, this.mEnforcedAdmin);
            ((ActivityStarter) Dependency.get(ActivityStarter.class)).postStartActivityDismissingKeyguard(intent, 0);
            return true;
        }
        if (!isEnabled()) {
            setEnabled(true);
        }
        return super.onTouchEvent(event);
    }

    public void setAccessibilityLabel(String label) {
        this.mAccessibilityLabel = label;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        String str = this.mAccessibilityLabel;
        if (str != null) {
            info.setText(str);
        }
    }

    public void setEnforcedAdmin(RestrictedLockUtils.EnforcedAdmin admin) {
        this.mEnforcedAdmin = admin;
    }
}
