package com.android.launcher3.util;

import android.content.ComponentName;
import android.os.UserHandle;
import java.util.Arrays;
/* loaded from: classes19.dex */
public class ComponentKey {
    public final ComponentName componentName;
    private final int mHashCode;
    public final UserHandle user;

    public ComponentKey(ComponentName componentName, UserHandle user) {
        if (componentName == null || user == null) {
            throw new NullPointerException();
        }
        this.componentName = componentName;
        this.user = user;
        this.mHashCode = Arrays.hashCode(new Object[]{componentName, user});
    }

    public int hashCode() {
        return this.mHashCode;
    }

    public boolean equals(Object o) {
        ComponentKey other = (ComponentKey) o;
        return other.componentName.equals(this.componentName) && other.user.equals(this.user);
    }

    public String toString() {
        return this.componentName.flattenToString() + "#" + this.user;
    }
}
