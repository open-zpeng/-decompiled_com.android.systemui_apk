package com.xiaopeng.systemui.speech.component.asr;

import android.text.TextUtils;
import android.util.ArraySet;
import androidx.annotation.UiThread;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.speech.data.ISpeechCallBack;
import com.xiaopeng.systemui.speech.data.SpeechDataInput;
import com.xiaopeng.systemui.speech.data.SpeechManager;
import java.util.HashMap;
import java.util.Iterator;
/* loaded from: classes24.dex */
public class AsrModel {
    private static final String TAG = "Sp-AsrModel";
    private final HashMap<Integer, ArraySet<AsrAreaModelCallBack>> mAreaCallBacks;
    private final ArraySet<AsrAreaModelCallBack> mCallBacks;

    public void addCallBack(AsrAreaModelCallBack callBack) {
        Logger.d(TAG, "addCallBack " + callBack);
        synchronized (this.mCallBacks) {
            this.mCallBacks.add(callBack);
        }
    }

    public void removeCallBack(AsrAreaModelCallBack callBack) {
        Logger.d(TAG, "removeCallBack " + callBack);
        synchronized (this.mCallBacks) {
            this.mCallBacks.remove(callBack);
        }
    }

    public void addCallBack(int area, AsrAreaModelCallBack callBack) {
        Logger.d(TAG, "addCallBack " + callBack);
        synchronized (this.mAreaCallBacks) {
            ArraySet<AsrAreaModelCallBack> sets = this.mAreaCallBacks.get(Integer.valueOf(area));
            if (sets == null) {
                sets = new ArraySet<>();
                this.mAreaCallBacks.put(Integer.valueOf(area), sets);
            }
            sets.add(callBack);
        }
    }

    public void removeCallBack(int area, AsrAreaModelCallBack callBack) {
        Logger.d(TAG, "removeCallBack " + callBack);
        synchronized (this.mAreaCallBacks) {
            ArraySet<AsrAreaModelCallBack> sets = this.mAreaCallBacks.get(Integer.valueOf(area));
            if (sets != null) {
                sets.remove(callBack);
            }
        }
    }

    private AsrModel() {
        this.mCallBacks = new ArraySet<>();
        this.mAreaCallBacks = new HashMap<>();
        init();
    }

    private void init() {
        SpeechManager.get().addCallBack(new ISpeechCallBack() { // from class: com.xiaopeng.systemui.speech.component.asr.AsrModel.1
            @Override // com.xiaopeng.systemui.speech.data.ISpeechCallBack
            public void onInputText(SpeechDataInput data) {
                if (data != null) {
                    synchronized (AsrModel.this.mCallBacks) {
                        Iterator it = AsrModel.this.mCallBacks.iterator();
                        while (it.hasNext()) {
                            AsrAreaModelCallBack back = (AsrAreaModelCallBack) it.next();
                            AsrModel.this.notifyChanged(data, back);
                        }
                    }
                    synchronized (AsrModel.this.mAreaCallBacks) {
                        ArraySet<AsrAreaModelCallBack> sets = (ArraySet) AsrModel.this.mAreaCallBacks.get(Integer.valueOf(data.getSoundArea()));
                        if (sets != null) {
                            Iterator<AsrAreaModelCallBack> it2 = sets.iterator();
                            while (it2.hasNext()) {
                                AsrAreaModelCallBack back2 = it2.next();
                                AsrModel.this.notifyChanged(data, back2);
                            }
                        }
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyChanged(SpeechDataInput data, AsrAreaModelCallBack back) {
        if (TextUtils.isEmpty(data.getText())) {
            back.onAsrHide(data.getSoundArea());
        } else {
            back.onAsrShow(data);
        }
    }

    @UiThread
    /* loaded from: classes24.dex */
    public interface AsrAreaModelCallBack {
        default void onAsrHide(int area) {
        }

        default void onAsrShow(SpeechDataInput data) {
        }
    }

    /* loaded from: classes24.dex */
    private static class SingleHolder {
        private static final AsrModel sInstance = new AsrModel();

        private SingleHolder() {
        }
    }

    public static AsrModel get() {
        return SingleHolder.sInstance;
    }
}
