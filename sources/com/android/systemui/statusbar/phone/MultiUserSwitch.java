package com.android.systemui.statusbar.phone;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import com.android.systemui.Dependency;
import com.android.systemui.Prefs;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.statusbar.policy.KeyguardUserSwitcher;
import com.android.systemui.statusbar.policy.UserSwitcherController;
/* loaded from: classes21.dex */
public class MultiUserSwitch extends FrameLayout implements View.OnClickListener {
    private boolean mKeyguardMode;
    private KeyguardUserSwitcher mKeyguardUserSwitcher;
    protected QSPanel mQsPanel;
    private final int[] mTmpInt2;
    private UserSwitcherController.BaseUserAdapter mUserListener;
    final UserManager mUserManager;
    protected UserSwitcherController mUserSwitcherController;

    public MultiUserSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTmpInt2 = new int[2];
        this.mUserManager = UserManager.get(getContext());
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnClickListener(this);
        refreshContentDescription();
    }

    public void setQsPanel(QSPanel qsPanel) {
        this.mQsPanel = qsPanel;
        setUserSwitcherController((UserSwitcherController) Dependency.get(UserSwitcherController.class));
    }

    public boolean hasMultipleUsers() {
        UserSwitcherController.BaseUserAdapter baseUserAdapter = this.mUserListener;
        return (baseUserAdapter == null || baseUserAdapter.getUserCount() == 0 || !Prefs.getBoolean(getContext(), Prefs.Key.SEEN_MULTI_USER, false)) ? false : true;
    }

    public void setUserSwitcherController(UserSwitcherController userSwitcherController) {
        this.mUserSwitcherController = userSwitcherController;
        registerListener();
        refreshContentDescription();
    }

    public void setKeyguardUserSwitcher(KeyguardUserSwitcher keyguardUserSwitcher) {
        this.mKeyguardUserSwitcher = keyguardUserSwitcher;
    }

    public void setKeyguardMode(boolean keyguardShowing) {
        this.mKeyguardMode = keyguardShowing;
        registerListener();
    }

    public boolean isMultiUserEnabled() {
        boolean userSwitcherEnabled = Settings.Global.getInt(this.mContext.getContentResolver(), "user_switcher_enabled", 0) != 0;
        if (!userSwitcherEnabled || !UserManager.supportsMultipleUsers() || UserManager.isDeviceInDemoMode(this.mContext) || this.mUserManager.hasUserRestriction("no_user_switch")) {
            return false;
        }
        boolean guestEnabled = !((DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class)).getGuestUserDisabled(null);
        return this.mUserSwitcherController.getSwitchableUserCount() > 1 || (guestEnabled && !this.mUserManager.hasUserRestriction("no_add_user")) || this.mContext.getResources().getBoolean(R.bool.qs_show_user_switcher_for_single_user);
    }

    private void registerListener() {
        UserSwitcherController controller;
        if (this.mUserManager.isUserSwitcherEnabled() && this.mUserListener == null && (controller = this.mUserSwitcherController) != null) {
            this.mUserListener = new UserSwitcherController.BaseUserAdapter(controller) { // from class: com.android.systemui.statusbar.phone.MultiUserSwitch.1
                @Override // android.widget.BaseAdapter
                public void notifyDataSetChanged() {
                    MultiUserSwitch.this.refreshContentDescription();
                }

                @Override // android.widget.Adapter
                public View getView(int position, View convertView, ViewGroup parent) {
                    return null;
                }
            };
            refreshContentDescription();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (this.mKeyguardMode) {
            KeyguardUserSwitcher keyguardUserSwitcher = this.mKeyguardUserSwitcher;
            if (keyguardUserSwitcher != null) {
                keyguardUserSwitcher.show(true);
            }
        } else if (this.mQsPanel != null && this.mUserSwitcherController != null) {
            View center = getChildCount() > 0 ? getChildAt(0) : this;
            center.getLocationInWindow(this.mTmpInt2);
            int[] iArr = this.mTmpInt2;
            iArr[0] = iArr[0] + (center.getWidth() / 2);
            int[] iArr2 = this.mTmpInt2;
            iArr2[1] = iArr2[1] + (center.getHeight() / 2);
            this.mQsPanel.showDetailAdapter(true, getUserDetailAdapter(), this.mTmpInt2);
        }
    }

    @Override // android.view.View
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        refreshContentDescription();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshContentDescription() {
        UserSwitcherController userSwitcherController;
        String currentUser = null;
        if (this.mUserManager.isUserSwitcherEnabled() && (userSwitcherController = this.mUserSwitcherController) != null) {
            currentUser = userSwitcherController.getCurrentUserName(this.mContext);
        }
        String text = null;
        if (!TextUtils.isEmpty(currentUser)) {
            text = this.mContext.getString(R.string.accessibility_quick_settings_user, currentUser);
        }
        if (!TextUtils.equals(getContentDescription(), text)) {
            setContentDescription(text);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(Button.class.getName());
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(Button.class.getName());
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    protected DetailAdapter getUserDetailAdapter() {
        return this.mUserSwitcherController.userDetailAdapter;
    }
}
