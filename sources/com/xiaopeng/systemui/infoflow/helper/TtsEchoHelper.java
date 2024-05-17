package com.xiaopeng.systemui.infoflow.helper;

import android.util.SparseArray;
import android.widget.TextView;
import com.xiaopeng.systemui.infoflow.speech.ITtsEchoView;
import com.xiaopeng.systemui.infoflow.speech.TtsEchoView;
/* loaded from: classes24.dex */
public class TtsEchoHelper {
    private SparseArray<ITtsEchoView> mTtsEchoViewSparseArray;

    public static final TtsEchoHelper getInstance() {
        return SingletonHolder.sInstance;
    }

    /* loaded from: classes24.dex */
    private static class SingletonHolder {
        private static final TtsEchoHelper sInstance = new TtsEchoHelper();

        private SingletonHolder() {
        }
    }

    private TtsEchoHelper() {
        this.mTtsEchoViewSparseArray = new SparseArray<>();
    }

    public void showTtsEchoText(int loc, String text) {
        ITtsEchoView ttsEchoView = this.mTtsEchoViewSparseArray.get(loc);
        if (ttsEchoView != null) {
            ttsEchoView.setTtsEcho(text);
        }
    }

    public void updateTtsEchoContainer(int loc, TextView view) {
        ITtsEchoView ttsEchoView = this.mTtsEchoViewSparseArray.get(loc);
        if (ttsEchoView == null) {
            ttsEchoView = new TtsEchoView(loc);
            this.mTtsEchoViewSparseArray.put(loc, ttsEchoView);
        }
        ttsEchoView.updateTtsEchoView(view);
    }
}
