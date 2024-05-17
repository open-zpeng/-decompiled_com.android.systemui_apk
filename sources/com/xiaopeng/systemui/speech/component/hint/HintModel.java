package com.xiaopeng.systemui.speech.component.hint;

import android.text.TextUtils;
import android.util.ArraySet;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.speech.data.ISpeechCallBack;
import com.xiaopeng.systemui.speech.data.SpeechDataHint;
import com.xiaopeng.systemui.speech.data.SpeechManager;
import java.util.HashMap;
import java.util.Iterator;
/* loaded from: classes24.dex */
public class HintModel {
    private static final String TAG = "Sp-HintModel";
    private final HashMap<Integer, ArraySet<HintModelCallBack>> mAreaCallBacks;
    private final ArraySet<HintModelCallBack> mCallBacks;

    public void addCallBack(HintModelCallBack callBack) {
        Logger.d(TAG, "addCallBack " + callBack);
        synchronized (this.mCallBacks) {
            this.mCallBacks.add(callBack);
        }
    }

    public void removeCallBack(HintModelCallBack callBack) {
        Logger.d(TAG, "removeCallBack " + callBack);
        synchronized (this.mCallBacks) {
            this.mCallBacks.remove(callBack);
        }
    }

    public void addCallBack(int area, HintModelCallBack callBack) {
        Logger.d(TAG, "addCallBack " + callBack);
        synchronized (this.mAreaCallBacks) {
            ArraySet<HintModelCallBack> sets = this.mAreaCallBacks.get(Integer.valueOf(area));
            if (sets == null) {
                sets = new ArraySet<>();
                this.mAreaCallBacks.put(Integer.valueOf(area), sets);
            }
            sets.add(callBack);
        }
    }

    public void removeCallBack(int area, HintModelCallBack callBack) {
        Logger.d(TAG, "removeCallBack " + callBack);
        synchronized (this.mAreaCallBacks) {
            ArraySet<HintModelCallBack> sets = this.mAreaCallBacks.get(Integer.valueOf(area));
            if (sets != null) {
                sets.remove(callBack);
            }
        }
    }

    private HintModel() {
        this.mCallBacks = new ArraySet<>();
        this.mAreaCallBacks = new HashMap<>();
        init();
    }

    private void init() {
        SpeechManager.get().addCallBack(new ISpeechCallBack() { // from class: com.xiaopeng.systemui.speech.component.hint.HintModel.1
            @Override // com.xiaopeng.systemui.speech.data.ISpeechCallBack
            public void onTipsListeningShow(SpeechDataHint data) {
                if (data != null) {
                    synchronized (HintModel.this.mCallBacks) {
                        Iterator it = HintModel.this.mCallBacks.iterator();
                        while (it.hasNext()) {
                            HintModelCallBack back = (HintModelCallBack) it.next();
                            HintModel.this.notifyChanged(data, back);
                        }
                    }
                    synchronized (HintModel.this.mAreaCallBacks) {
                        ArraySet<HintModelCallBack> sets = (ArraySet) HintModel.this.mAreaCallBacks.get(1);
                        if (sets != null) {
                            Iterator<HintModelCallBack> it2 = sets.iterator();
                            while (it2.hasNext()) {
                                HintModelCallBack back2 = it2.next();
                                HintModel.this.notifyChanged(data, back2);
                            }
                        }
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyChanged(SpeechDataHint data, HintModelCallBack back) {
        if (TextUtils.isEmpty(data.getText())) {
            back.onHintHide(data.getSoundArea());
        } else {
            back.onHintShow(data);
        }
    }

    /* loaded from: classes24.dex */
    public interface HintModelCallBack {
        default void onHintHide(int area) {
        }

        default void onHintShow(SpeechDataHint data) {
        }
    }

    /* loaded from: classes24.dex */
    private static class SingleHolder {
        private static final HintModel sInstance = new HintModel();

        private SingleHolder() {
        }
    }

    public static HintModel get() {
        return SingleHolder.sInstance;
    }
}
