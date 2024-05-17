package com.android.systemui.statusbar.phone;

import com.android.systemui.plugins.ActivityStarter;
/* loaded from: classes21.dex */
public interface KeyguardDismissHandler {
    void executeWhenUnlocked(ActivityStarter.OnDismissAction onDismissAction, boolean z);
}
