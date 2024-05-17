package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.UserHandle;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy;
/* loaded from: classes21.dex */
public class StatusBarIconHolder {
    public static final int TYPE_ICON = 0;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_WIFI = 1;
    private StatusBarIcon mIcon;
    private StatusBarSignalPolicy.MobileIconState mMobileState;
    private StatusBarSignalPolicy.WifiIconState mWifiState;
    private int mType = 0;
    private int mTag = 0;
    private boolean mVisible = true;

    public static StatusBarIconHolder fromIcon(StatusBarIcon icon) {
        StatusBarIconHolder wrapper = new StatusBarIconHolder();
        wrapper.mIcon = icon;
        return wrapper;
    }

    public static StatusBarIconHolder fromResId(Context context, int resId, CharSequence contentDescription) {
        StatusBarIconHolder holder = new StatusBarIconHolder();
        holder.mIcon = new StatusBarIcon(UserHandle.SYSTEM, context.getPackageName(), Icon.createWithResource(context, resId), 0, 0, contentDescription);
        return holder;
    }

    public static StatusBarIconHolder fromWifiIconState(StatusBarSignalPolicy.WifiIconState state) {
        StatusBarIconHolder holder = new StatusBarIconHolder();
        holder.mWifiState = state;
        holder.mType = 1;
        return holder;
    }

    public static StatusBarIconHolder fromMobileIconState(StatusBarSignalPolicy.MobileIconState state) {
        StatusBarIconHolder holder = new StatusBarIconHolder();
        holder.mMobileState = state;
        holder.mType = 2;
        holder.mTag = state.subId;
        return holder;
    }

    public int getType() {
        return this.mType;
    }

    public StatusBarIcon getIcon() {
        return this.mIcon;
    }

    public StatusBarSignalPolicy.WifiIconState getWifiState() {
        return this.mWifiState;
    }

    public void setWifiState(StatusBarSignalPolicy.WifiIconState state) {
        this.mWifiState = state;
    }

    public StatusBarSignalPolicy.MobileIconState getMobileState() {
        return this.mMobileState;
    }

    public void setMobileState(StatusBarSignalPolicy.MobileIconState state) {
        this.mMobileState = state;
    }

    public boolean isVisible() {
        int i = this.mType;
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    return true;
                }
                return this.mMobileState.visible;
            }
            return this.mWifiState.visible;
        }
        return this.mIcon.visible;
    }

    public void setVisible(boolean visible) {
        if (isVisible() == visible) {
            return;
        }
        int i = this.mType;
        if (i == 0) {
            this.mIcon.visible = visible;
        } else if (i == 1) {
            this.mWifiState.visible = visible;
        } else if (i == 2) {
            this.mMobileState.visible = visible;
        }
    }

    public int getTag() {
        return this.mTag;
    }
}
