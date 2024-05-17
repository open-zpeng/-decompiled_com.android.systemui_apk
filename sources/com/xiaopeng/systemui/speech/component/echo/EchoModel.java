package com.xiaopeng.systemui.speech.component.echo;

import android.text.TextUtils;
import android.util.ArraySet;
import androidx.annotation.UiThread;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.speech.data.ISpeechCallBack;
import com.xiaopeng.systemui.speech.data.SpeechDataEcho;
import com.xiaopeng.systemui.speech.data.SpeechManager;
import java.util.HashMap;
import java.util.Iterator;
/* loaded from: classes24.dex */
public class EchoModel {
    private static final String TAG = "Sp-EchoModel";
    private final HashMap<Integer, ArraySet<EchoAreaModelCallBack>> mAreaCallBacks;
    private final ArraySet<EchoAreaModelCallBack> mCallBacks;

    public void addCallBack(EchoAreaModelCallBack callBack) {
        Logger.d(TAG, "addCallBack " + callBack);
        synchronized (this.mCallBacks) {
            this.mCallBacks.add(callBack);
        }
    }

    public void removeCallBack(EchoAreaModelCallBack callBack) {
        Logger.d(TAG, "removeCallBack " + callBack);
        synchronized (this.mCallBacks) {
            this.mCallBacks.remove(callBack);
        }
    }

    public void addCallBack(int area, EchoAreaModelCallBack callBack) {
        Logger.d(TAG, "addCallBack " + callBack);
        synchronized (this.mAreaCallBacks) {
            ArraySet<EchoAreaModelCallBack> sets = this.mAreaCallBacks.get(Integer.valueOf(area));
            if (sets == null) {
                sets = new ArraySet<>();
                this.mAreaCallBacks.put(Integer.valueOf(area), sets);
            }
            sets.add(callBack);
        }
    }

    public void removeCallBack(int area, EchoAreaModelCallBack callBack) {
        Logger.d(TAG, "removeCallBack " + callBack);
        synchronized (this.mAreaCallBacks) {
            ArraySet<EchoAreaModelCallBack> sets = this.mAreaCallBacks.get(Integer.valueOf(area));
            if (sets != null) {
                sets.remove(callBack);
            }
        }
    }

    private EchoModel() {
        this.mCallBacks = new ArraySet<>();
        this.mAreaCallBacks = new HashMap<>();
        init();
    }

    private void init() {
        SpeechManager.get().addCallBack(new ISpeechCallBack() { // from class: com.xiaopeng.systemui.speech.component.echo.EchoModel.1
            @Override // com.xiaopeng.systemui.speech.data.ISpeechCallBack
            public void onTtsEcho(SpeechDataEcho data) {
                if (data != null) {
                    synchronized (EchoModel.this.mCallBacks) {
                        Iterator it = EchoModel.this.mCallBacks.iterator();
                        while (it.hasNext()) {
                            EchoAreaModelCallBack back = (EchoAreaModelCallBack) it.next();
                            EchoModel.this.notifyChanged(data, back);
                        }
                    }
                    synchronized (EchoModel.this.mAreaCallBacks) {
                        ArraySet<EchoAreaModelCallBack> sets = (ArraySet) EchoModel.this.mAreaCallBacks.get(Integer.valueOf(data.getSoundArea()));
                        if (sets != null) {
                            Iterator<EchoAreaModelCallBack> it2 = sets.iterator();
                            while (it2.hasNext()) {
                                EchoAreaModelCallBack back2 = it2.next();
                                EchoModel.this.notifyChanged(data, back2);
                            }
                        }
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyChanged(SpeechDataEcho data, EchoAreaModelCallBack back) {
        if (TextUtils.isEmpty(data.getText())) {
            back.onEchoHide(data.getSoundArea());
        } else {
            back.onEchoShow(data);
        }
    }

    @UiThread
    /* loaded from: classes24.dex */
    public interface EchoAreaModelCallBack {
        default void onEchoHide(int area) {
        }

        default void onEchoShow(SpeechDataEcho data) {
        }
    }

    /* loaded from: classes24.dex */
    private static class SingleHolder {
        private static final EchoModel sInstance = new EchoModel();

        private SingleHolder() {
        }
    }

    public static EchoModel get() {
        return SingleHolder.sInstance;
    }
}
