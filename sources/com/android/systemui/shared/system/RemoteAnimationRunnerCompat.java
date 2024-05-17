package com.android.systemui.shared.system;
/* loaded from: classes21.dex */
public interface RemoteAnimationRunnerCompat {
    void onAnimationCancelled();

    void onAnimationStart(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, Runnable runnable);
}
