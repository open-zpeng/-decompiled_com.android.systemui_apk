package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.SystemClock;
import android.util.Slog;
import android.widget.Toast;
import com.android.systemui.R;
import com.android.systemui.SysUIToast;
/* loaded from: classes21.dex */
public class ScreenPinningNotify {
    private static final long SHOW_TOAST_MINIMUM_INTERVAL = 1000;
    private static final String TAG = "ScreenPinningNotify";
    private final Context mContext;
    private long mLastShowToastTime;
    private Toast mLastToast;

    public ScreenPinningNotify(Context context) {
        this.mContext = context;
    }

    public void showPinningStartToast() {
        makeAllUserToastAndShow(R.string.screen_pinning_start);
    }

    public void showPinningExitToast() {
        makeAllUserToastAndShow(R.string.screen_pinning_exit);
    }

    public void showEscapeToast(boolean isGestureNavEnabled, boolean isRecentsButtonVisible) {
        int i;
        long showToastTime = SystemClock.elapsedRealtime();
        if (showToastTime - this.mLastShowToastTime < 1000) {
            Slog.i(TAG, "Ignore toast since it is requested in very short interval.");
            return;
        }
        Toast toast = this.mLastToast;
        if (toast != null) {
            toast.cancel();
        }
        if (isGestureNavEnabled) {
            i = R.string.screen_pinning_toast_gesture_nav;
        } else if (isRecentsButtonVisible) {
            i = R.string.screen_pinning_toast;
        } else {
            i = R.string.screen_pinning_toast_recents_invisible;
        }
        this.mLastToast = makeAllUserToastAndShow(i);
        this.mLastShowToastTime = showToastTime;
    }

    private Toast makeAllUserToastAndShow(int resId) {
        Toast toast = SysUIToast.makeText(this.mContext, resId, 1);
        toast.show();
        return toast;
    }
}
