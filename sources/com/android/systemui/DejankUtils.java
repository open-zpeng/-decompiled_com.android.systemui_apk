package com.android.systemui;

import android.os.Handler;
import android.view.Choreographer;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.util.Assert;
import java.util.ArrayList;
/* loaded from: classes21.dex */
public class DejankUtils {
    private static boolean sImmediate;
    private static final Choreographer sChoreographer = Choreographer.getInstance();
    private static final Handler sHandler = new Handler();
    private static final ArrayList<Runnable> sPendingRunnables = new ArrayList<>();
    private static final Runnable sAnimationCallbackRunnable = new Runnable() { // from class: com.android.systemui.DejankUtils.1
        @Override // java.lang.Runnable
        public void run() {
            for (int i = 0; i < DejankUtils.sPendingRunnables.size(); i++) {
                DejankUtils.sHandler.post((Runnable) DejankUtils.sPendingRunnables.get(i));
            }
            DejankUtils.sPendingRunnables.clear();
        }
    };

    public static void postAfterTraversal(Runnable r) {
        if (sImmediate) {
            r.run();
            return;
        }
        Assert.isMainThread();
        sPendingRunnables.add(r);
        postAnimationCallback();
    }

    public static void removeCallbacks(Runnable r) {
        Assert.isMainThread();
        sPendingRunnables.remove(r);
        sHandler.removeCallbacks(r);
    }

    private static void postAnimationCallback() {
        sChoreographer.postCallback(1, sAnimationCallbackRunnable, null);
    }

    @VisibleForTesting
    public static void setImmediate(boolean immediate) {
        sImmediate = immediate;
    }
}
