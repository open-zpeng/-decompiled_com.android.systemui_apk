package com.xiaopeng.systemui.infoflow.message.manager;

import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class ModeManager {
    private static final long LAYOUT_DURATION = 500;
    public static final int MODE_CHANGING_TO_MUSIC = 3;
    public static final int MODE_CHANGING_TO_NORMAL = 4;
    public static final int MODE_MAP = 2;
    public static final int MODE_MUSIC = 1;
    public static final int MODE_NORMAL = 0;
    private static volatile ModeManager mInstance;
    private List<OnModeChangedListener> mListeners = new ArrayList();
    private static final String TAG = ModeManager.class.getSimpleName();
    private static volatile int mCurrentMode = 0;
    private static long mStartLayoutTime = 0;

    /* loaded from: classes24.dex */
    public interface OnModeChangedListener {
        void onModeChanged(int i);
    }

    public static ModeManager getInstance() {
        if (mInstance == null) {
            synchronized (ModeManager.class) {
                if (mInstance == null) {
                    mInstance = new ModeManager();
                }
            }
        }
        return mInstance;
    }

    private ModeManager() {
    }

    public boolean isMusicMode() {
        return mCurrentMode == 1;
    }

    public boolean isMapMode() {
        return mCurrentMode == 2;
    }

    public boolean isNormalMode() {
        return mCurrentMode == 0;
    }

    public static void saveBeginLayoutTime() {
        mStartLayoutTime = System.currentTimeMillis();
    }

    public static boolean isStackLayoutFinished() {
        long currentTime = System.currentTimeMillis();
        return currentTime - mStartLayoutTime > 500;
    }

    public synchronized void setMode(int mode) {
        if (mCurrentMode != mode) {
            mCurrentMode = mode;
            notifyListener();
        }
    }

    private void notifyListener() {
        for (int i = 0; i < this.mListeners.size(); i++) {
            this.mListeners.get(i).onModeChanged(mCurrentMode);
        }
    }

    public void addModeChangedListener(OnModeChangedListener listener) {
        if (!this.mListeners.contains(listener)) {
            this.mListeners.add(listener);
        }
    }

    public void removeListener(OnModeChangedListener listener) {
        if (this.mListeners.contains(listener)) {
            this.mListeners.remove(listener);
        }
    }
}
