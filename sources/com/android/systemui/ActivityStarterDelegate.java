package com.android.systemui;

import android.app.PendingIntent;
import android.content.Intent;
import android.view.View;
import com.android.systemui.plugins.ActivityStarter;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class ActivityStarterDelegate implements ActivityStarter {
    private ActivityStarter mActualStarter;

    @Override // com.android.systemui.plugins.ActivityStarter
    public void startPendingIntentDismissingKeyguard(PendingIntent intent) {
        ActivityStarter activityStarter = this.mActualStarter;
        if (activityStarter == null) {
            return;
        }
        activityStarter.startPendingIntentDismissingKeyguard(intent);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void startPendingIntentDismissingKeyguard(PendingIntent intent, Runnable intentSentCallback) {
        ActivityStarter activityStarter = this.mActualStarter;
        if (activityStarter == null) {
            return;
        }
        activityStarter.startPendingIntentDismissingKeyguard(intent, intentSentCallback);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void startPendingIntentDismissingKeyguard(PendingIntent intent, Runnable intentSentCallback, View associatedView) {
        ActivityStarter activityStarter = this.mActualStarter;
        if (activityStarter == null) {
            return;
        }
        activityStarter.startPendingIntentDismissingKeyguard(intent, intentSentCallback, associatedView);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void startActivity(Intent intent, boolean onlyProvisioned, boolean dismissShade, int flags) {
        ActivityStarter activityStarter = this.mActualStarter;
        if (activityStarter == null) {
            return;
        }
        activityStarter.startActivity(intent, onlyProvisioned, dismissShade, flags);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void startActivity(Intent intent, boolean dismissShade) {
        ActivityStarter activityStarter = this.mActualStarter;
        if (activityStarter == null) {
            return;
        }
        activityStarter.startActivity(intent, dismissShade);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void startActivity(Intent intent, boolean onlyProvisioned, boolean dismissShade) {
        ActivityStarter activityStarter = this.mActualStarter;
        if (activityStarter == null) {
            return;
        }
        activityStarter.startActivity(intent, onlyProvisioned, dismissShade);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void startActivity(Intent intent, boolean dismissShade, ActivityStarter.Callback callback) {
        ActivityStarter activityStarter = this.mActualStarter;
        if (activityStarter == null) {
            return;
        }
        activityStarter.startActivity(intent, dismissShade, callback);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void postStartActivityDismissingKeyguard(Intent intent, int delay) {
        ActivityStarter activityStarter = this.mActualStarter;
        if (activityStarter == null) {
            return;
        }
        activityStarter.postStartActivityDismissingKeyguard(intent, delay);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void postStartActivityDismissingKeyguard(PendingIntent intent) {
        ActivityStarter activityStarter = this.mActualStarter;
        if (activityStarter == null) {
            return;
        }
        activityStarter.postStartActivityDismissingKeyguard(intent);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void postQSRunnableDismissingKeyguard(Runnable runnable) {
        ActivityStarter activityStarter = this.mActualStarter;
        if (activityStarter == null) {
            return;
        }
        activityStarter.postQSRunnableDismissingKeyguard(runnable);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void dismissKeyguardThenExecute(ActivityStarter.OnDismissAction action, Runnable cancel, boolean afterKeyguardGone) {
        ActivityStarter activityStarter = this.mActualStarter;
        if (activityStarter == null) {
            return;
        }
        activityStarter.dismissKeyguardThenExecute(action, cancel, afterKeyguardGone);
    }

    public void setActivityStarterImpl(ActivityStarter starter) {
        this.mActualStarter = starter;
    }
}
