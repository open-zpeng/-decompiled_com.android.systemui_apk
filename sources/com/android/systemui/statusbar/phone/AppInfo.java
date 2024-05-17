package com.android.systemui.statusbar.phone;

import android.content.ComponentName;
import android.os.UserHandle;
/* loaded from: classes21.dex */
class AppInfo {
    private final ComponentName mComponentName;
    private final UserHandle mUser;

    public AppInfo(ComponentName componentName, UserHandle user) {
        if (componentName == null || user == null) {
            throw new IllegalArgumentException();
        }
        this.mComponentName = componentName;
        this.mUser = user;
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    public UserHandle getUser() {
        return this.mUser;
    }

    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AppInfo other = (AppInfo) obj;
        return this.mComponentName.equals(other.mComponentName) && this.mUser.equals(other.mUser);
    }
}
