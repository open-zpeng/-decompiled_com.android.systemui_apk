package com.xiaopeng.systemui.speech.data;

import android.text.TextUtils;
import android.util.ArraySet;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.jarvisproto.DialogSoundAreaStatus;
import com.xiaopeng.speech.jarvisproto.SoundAreaStatus;
import com.xiaopeng.speech.protocol.SpeechModel;
import com.xiaopeng.speech.protocol.node.context.AbsContextListener;
import com.xiaopeng.speech.protocol.node.context.ContextNode;
import com.xiaopeng.speech.protocol.node.dialog.AbsDialogListener;
import com.xiaopeng.speech.protocol.node.dialog.DialogNode;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.WakeupReason;
import com.xiaopeng.speech.protocol.node.tts.TtsEcho;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.speech.data.SpeechManager;
import java.util.Iterator;
/* loaded from: classes24.dex */
public class SpeechManager {
    private static final String TAG = "Sp-SpeechManager";
    private final ArraySet<ISpeechCallBack> mCallBacks;
    private final AbsContextListener mContextListener;
    private final AbsDialogListener mDialogListener;
    private final SpeechModel mSpeechModel;

    /* synthetic */ SpeechManager(AnonymousClass1 x0) {
        this();
    }

    private SpeechManager() {
        this.mDialogListener = new AnonymousClass1();
        this.mContextListener = new AnonymousClass2();
        this.mSpeechModel = new SpeechModel();
        this.mCallBacks = new ArraySet<>();
    }

    public void stop() {
        SpeechClient.instance().getWakeupEngine().stopDialog();
    }

    public void subscribe() {
        this.mSpeechModel.subscribe(DialogNode.class, this.mDialogListener);
        this.mSpeechModel.subscribe(ContextNode.class, this.mContextListener);
    }

    public void unsubscribe() {
        this.mSpeechModel.unsubscribe(DialogNode.class, this.mDialogListener);
        this.mSpeechModel.unsubscribe(ContextNode.class, this.mContextListener);
    }

    public void addCallBack(final ISpeechCallBack callBack) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.speech.data.-$$Lambda$SpeechManager$Q943s4BS_GhbYix0voB6gtGEAz0
            @Override // java.lang.Runnable
            public final void run() {
                SpeechManager.this.lambda$addCallBack$0$SpeechManager(callBack);
            }
        });
    }

    public /* synthetic */ void lambda$addCallBack$0$SpeechManager(ISpeechCallBack callBack) {
        this.mCallBacks.add(callBack);
    }

    public /* synthetic */ void lambda$removeCallBack$1$SpeechManager(ISpeechCallBack callBack) {
        this.mCallBacks.remove(callBack);
    }

    public void removeCallBack(final ISpeechCallBack callBack) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.speech.data.-$$Lambda$SpeechManager$J52nSzDzX569sohDVSVS4FRGS5Y
            @Override // java.lang.Runnable
            public final void run() {
                SpeechManager.this.lambda$removeCallBack$1$SpeechManager(callBack);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.systemui.speech.data.SpeechManager$1  reason: invalid class name */
    /* loaded from: classes24.dex */
    public class AnonymousClass1 extends AbsDialogListener {
        AnonymousClass1() {
        }

        @Override // com.xiaopeng.speech.protocol.node.dialog.AbsDialogListener, com.xiaopeng.speech.protocol.node.dialog.DialogListener
        public void onDialogStart(final WakeupReason wakeupReason) {
            super.onDialogStart(wakeupReason);
            Logger.i(SpeechManager.TAG, "onDialogStart");
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.speech.data.-$$Lambda$SpeechManager$1$JGxNWi77jenE0uA9eATRkStPmDw
                @Override // java.lang.Runnable
                public final void run() {
                    SpeechManager.AnonymousClass1.this.lambda$onDialogStart$0$SpeechManager$1(wakeupReason);
                }
            });
        }

        public /* synthetic */ void lambda$onDialogStart$0$SpeechManager$1(WakeupReason wakeupReason) {
            Iterator it = SpeechManager.this.mCallBacks.iterator();
            while (it.hasNext()) {
                ISpeechCallBack callBack = (ISpeechCallBack) it.next();
                callBack.onDialogStart(wakeupReason);
            }
        }

        @Override // com.xiaopeng.speech.protocol.node.dialog.AbsDialogListener, com.xiaopeng.speech.protocol.node.dialog.DialogListener
        public void onDialogEnd(final DialogEndReason endReason) {
            super.onDialogEnd(endReason);
            Logger.i(SpeechManager.TAG, "onDialogEnd");
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.speech.data.-$$Lambda$SpeechManager$1$iaMvgyhwXrCBLHQi5uqhZ65ZX_c
                @Override // java.lang.Runnable
                public final void run() {
                    SpeechManager.AnonymousClass1.this.lambda$onDialogEnd$1$SpeechManager$1(endReason);
                }
            });
        }

        public /* synthetic */ void lambda$onDialogEnd$1$SpeechManager$1(DialogEndReason endReason) {
            Iterator it = SpeechManager.this.mCallBacks.iterator();
            while (it.hasNext()) {
                ISpeechCallBack callBack = (ISpeechCallBack) it.next();
                callBack.onDialogEnd(endReason);
            }
        }

        @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
        public void onSoundAreaStatusChanged(final SoundAreaStatus status) {
            Logger.i(SpeechManager.TAG, "onSoundAreaStatusChanged " + status);
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.speech.data.-$$Lambda$SpeechManager$1$QUqEui_9rbCKuAWCJPB8-B73IaM
                @Override // java.lang.Runnable
                public final void run() {
                    SpeechManager.AnonymousClass1.this.lambda$onSoundAreaStatusChanged$2$SpeechManager$1(status);
                }
            });
        }

        public /* synthetic */ void lambda$onSoundAreaStatusChanged$2$SpeechManager$1(SoundAreaStatus status) {
            Iterator it = SpeechManager.this.mCallBacks.iterator();
            while (it.hasNext()) {
                ISpeechCallBack callBack = (ISpeechCallBack) it.next();
                callBack.onSoundAreaStatusChanged(status);
            }
        }

        @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
        public void onDialogSoundAreaStatusChanged(final DialogSoundAreaStatus status) {
            Logger.i(SpeechManager.TAG, "onDialogSoundAreaStatusChanged  " + status);
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.speech.data.-$$Lambda$SpeechManager$1$-lZu6iZrcIY-Xj1Ep54rwZ2jqv0
                @Override // java.lang.Runnable
                public final void run() {
                    SpeechManager.AnonymousClass1.this.lambda$onDialogSoundAreaStatusChanged$3$SpeechManager$1(status);
                }
            });
        }

        public /* synthetic */ void lambda$onDialogSoundAreaStatusChanged$3$SpeechManager$1(DialogSoundAreaStatus status) {
            Iterator it = SpeechManager.this.mCallBacks.iterator();
            while (it.hasNext()) {
                ISpeechCallBack callBack = (ISpeechCallBack) it.next();
                callBack.onDialogSoundAreaStatusChanged(status);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.systemui.speech.data.SpeechManager$2  reason: invalid class name */
    /* loaded from: classes24.dex */
    public class AnonymousClass2 extends AbsContextListener {
        AnonymousClass2() {
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onInputText(String data) {
            if (TextUtils.isEmpty(data)) {
                Logger.w(SpeechManager.TAG, "onInputText  data = " + data);
                return;
            }
            final SpeechDataInput dataInput = (SpeechDataInput) GsonUtil.fromJson(data, (Class<Object>) SpeechDataInput.class);
            Logger.i(SpeechManager.TAG, "onInputText dataInput = " + dataInput + " , data = " + data);
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.speech.data.-$$Lambda$SpeechManager$2$HjkJthBp4t9WKGtQpM-NWFSJYic
                @Override // java.lang.Runnable
                public final void run() {
                    SpeechManager.AnonymousClass2.this.lambda$onInputText$0$SpeechManager$2(dataInput);
                }
            });
        }

        public /* synthetic */ void lambda$onInputText$0$SpeechManager$2(SpeechDataInput dataInput) {
            Iterator it = SpeechManager.this.mCallBacks.iterator();
            while (it.hasNext()) {
                ISpeechCallBack callBack = (ISpeechCallBack) it.next();
                callBack.onInputText(dataInput);
            }
        }

        @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onTtsEcho(TtsEcho echo) {
            Logger.i(SpeechManager.TAG, "onTtsEcho  " + echo);
            if (echo == null) {
                return;
            }
            final SpeechDataEcho data = new SpeechDataEcho(echo);
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.speech.data.-$$Lambda$SpeechManager$2$Koo9VX8SF9K_24txMfkpEtTaSps
                @Override // java.lang.Runnable
                public final void run() {
                    SpeechManager.AnonymousClass2.this.lambda$onTtsEcho$1$SpeechManager$2(data);
                }
            });
        }

        public /* synthetic */ void lambda$onTtsEcho$1$SpeechManager$2(SpeechDataEcho data) {
            Iterator it = SpeechManager.this.mCallBacks.iterator();
            while (it.hasNext()) {
                ISpeechCallBack callBack = (ISpeechCallBack) it.next();
                callBack.onTtsEcho(data);
            }
        }

        @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onTipsListeningShow(String data) {
            Logger.i(SpeechManager.TAG, "onTipsListeningShow  " + data);
            SpeechDataHint dataHint = (SpeechDataHint) GsonUtil.fromJson(data, (Class<Object>) SpeechDataHint.class);
            if (dataHint == null) {
                dataHint = new SpeechDataHint(1);
            }
            final SpeechDataHint finalDataHint = dataHint;
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.speech.data.-$$Lambda$SpeechManager$2$V96mR4drSRa1r-oKC63tF-r0yRg
                @Override // java.lang.Runnable
                public final void run() {
                    SpeechManager.AnonymousClass2.this.lambda$onTipsListeningShow$2$SpeechManager$2(finalDataHint);
                }
            });
        }

        public /* synthetic */ void lambda$onTipsListeningShow$2$SpeechManager$2(SpeechDataHint finalDataHint) {
            Iterator it = SpeechManager.this.mCallBacks.iterator();
            while (it.hasNext()) {
                ISpeechCallBack callBack = (ISpeechCallBack) it.next();
                callBack.onTipsListeningShow(finalDataHint);
            }
        }
    }

    public AbsContextListener getContextListener() {
        return this.mContextListener;
    }

    public AbsDialogListener getDialogListener() {
        return this.mDialogListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final SpeechManager sInstance = new SpeechManager(null);

        private SingleHolder() {
        }
    }

    public static SpeechManager get() {
        return SingleHolder.sInstance;
    }
}
