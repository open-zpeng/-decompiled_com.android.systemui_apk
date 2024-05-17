package com.xiaopeng.systemui.speech;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import com.android.systemui.R;
import com.android.systemui.SystemUI;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.WakeupReason;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.ui.ISpeechUI;
import com.xiaopeng.systemui.controller.ui.SystemUIController;
import com.xiaopeng.systemui.speech.component.asr.AsrComponent;
import com.xiaopeng.systemui.speech.component.asr.IAsrListener;
import com.xiaopeng.systemui.speech.component.echo.EchoComponent;
import com.xiaopeng.systemui.speech.component.echo.IEchoListener;
import com.xiaopeng.systemui.speech.component.hint.HintComponent;
import com.xiaopeng.systemui.speech.component.hint.IHintListener;
import com.xiaopeng.systemui.speech.data.ISpeechCallBack;
import com.xiaopeng.systemui.speech.data.SpeechManager;
import com.xiaopeng.systemui.speech.model.SpeechConfig;
import com.xiaopeng.systemui.speech.model.SpeechWindowInfo;
import java.util.Collection;
import java.util.HashMap;
/* loaded from: classes24.dex */
public class SpeechUI extends SystemUI {
    private static final String TAG = "Sp-SpeechUI";
    private WindowManager mWindowManager;
    private final Runnable mRemoveAllWindowRunnable = new Runnable() { // from class: com.xiaopeng.systemui.speech.-$$Lambda$SpeechUI$p_IB8xHAE6c0fgJUoKf-LGb26dE
        @Override // java.lang.Runnable
        public final void run() {
            SpeechUI.this.removeAllWindow();
        }
    };
    private final IAsrListener mAsrListener = new IAsrListener() { // from class: com.xiaopeng.systemui.speech.SpeechUI.2
        @Override // com.xiaopeng.systemui.speech.component.IComponentListener
        public View getView(int area) {
            return SpeechUI.this.getAsrOrHintOrEchoView("asr", area);
        }

        @Override // com.xiaopeng.systemui.speech.component.IComponentListener
        public void onShow(int area) {
            SpeechUI.this.showAsrOrHintOrEchoWindow("asr", area);
        }
    };
    private final IHintListener mHintListener = new IHintListener() { // from class: com.xiaopeng.systemui.speech.SpeechUI.3
        @Override // com.xiaopeng.systemui.speech.component.IComponentListener
        public View getView(int area) {
            return SpeechUI.this.getAsrOrHintOrEchoView("hint", area);
        }

        @Override // com.xiaopeng.systemui.speech.component.IComponentListener
        public void onShow(int area) {
            SpeechUI.this.showAsrOrHintOrEchoWindow("hint", area);
        }
    };
    private final IEchoListener mIEchoListener = new IEchoListener() { // from class: com.xiaopeng.systemui.speech.SpeechUI.4
        @Override // com.xiaopeng.systemui.speech.component.IComponentListener
        public View getView(int area) {
            return SpeechUI.this.getAsrOrHintOrEchoView("echo", area);
        }

        @Override // com.xiaopeng.systemui.speech.component.IComponentListener
        public void onShow(int area) {
            SpeechUI.this.showAsrOrHintOrEchoWindow("echo", area);
        }
    };
    private final HashMap<Integer, View> mWindowView = new HashMap<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override // com.android.systemui.SystemUI
    public void start() {
        boolean isNewSpeechUI = CarModelsManager.getFeature().isNewSpeechUI();
        Logger.i(TAG, "start  isNewSpeechUI: " + isNewSpeechUI);
        if (!isNewSpeechUI) {
            return;
        }
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        new AsrComponent(this.mContext, this.mAsrListener).start();
        new HintComponent(this.mContext, this.mHintListener).start();
        new EchoComponent(this.mContext, this.mIEchoListener).start();
        SystemUIController.get().getISpeechUI().addSpeechUICallBack(new ISpeechUI.ISpeechUICallBack() { // from class: com.xiaopeng.systemui.speech.-$$Lambda$SpeechUI$Cwk-S2D7c-62V_Xcr2O6faELxpc
            @Override // com.xiaopeng.systemui.controller.ui.ISpeechUI.ISpeechUICallBack
            public final void onSpeechUIEnableChanged(boolean z) {
                SpeechUI.this.lambda$start$0$SpeechUI(z);
            }
        });
        SpeechManager.get().subscribe();
        SpeechManager.get().addCallBack(new ISpeechCallBack() { // from class: com.xiaopeng.systemui.speech.SpeechUI.1
            @Override // com.xiaopeng.systemui.speech.data.ISpeechCallBack
            public void onDialogEnd(DialogEndReason endReason) {
                SpeechUI.this.mHandler.postDelayed(SpeechUI.this.mRemoveAllWindowRunnable, 300L);
            }

            @Override // com.xiaopeng.systemui.speech.data.ISpeechCallBack
            public void onDialogStart(WakeupReason wakeupReason) {
                SpeechUI.this.mHandler.removeCallbacks(SpeechUI.this.mRemoveAllWindowRunnable);
            }
        });
    }

    public /* synthetic */ void lambda$start$0$SpeechUI(boolean enable) {
        Logger.i(TAG, "onSpeechUIEnableChanged enable=" + enable);
        if (!enable) {
            removeAllWindow();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getAsrOrHintOrEchoView(String from, int area) {
        View view = this.mWindowView.get(Integer.valueOf(area));
        Logger.i(TAG, "getAsrOrHintOrEchoView from=" + from + " area=" + area + " view=" + view);
        if (view == null) {
            View view2 = createAsrOrHintOrEchoView(area);
            initAsrOrHintOrEchoView(view2);
            return view2;
        }
        return view;
    }

    private View createAsrOrHintOrEchoView(int area) {
        int layout = SpeechConfig.get().getLayout(area);
        if (layout != 0) {
            View view = View.inflate(this.mContext, layout, null);
            this.mWindowView.put(Integer.valueOf(area), view);
            return view;
        }
        Logger.w(TAG, "createAndAddView layout is 0  area=" + area);
        return null;
    }

    private void initAsrOrHintOrEchoView(View view) {
        if (view != null) {
            view.findViewById(R.id.speech_touch_stop_view).setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.speech.-$$Lambda$SpeechUI$e90VHbpecBHphnZw8xrAWgs-KwQ
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    SpeechUI.lambda$initAsrOrHintOrEchoView$1(view2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$initAsrOrHintOrEchoView$1(View v) {
        Logger.i(TAG, "initAsrOrHintOrEchoView click stop speech" + v);
        SpeechManager.get().stop();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showAsrOrHintOrEchoWindow(String from, int area) {
        View view = this.mWindowView.get(Integer.valueOf(area));
        if (view != null && view.getParent() == null) {
            WindowManager.LayoutParams lp = getWindow(area);
            Logger.i(TAG, "showAsrOrHintOrEchoWindow from:" + from + " area:" + area + " lp:" + lp.toString());
            if (isEnable()) {
                this.mWindowManager.addView(view, lp);
                return;
            }
            Logger.w(TAG, "showAsrOrHintOrEchoWindow not enbale !!!!!  from:" + from + " area:" + area + " lp:" + lp.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeAllWindow() {
        Logger.i(TAG, "removeAllWindow");
        Collection<View> views = this.mWindowView.values();
        for (View view : views) {
            if (view.getParent() != null) {
                this.mWindowManager.removeViewImmediate(view);
            }
        }
    }

    private boolean isEnable() {
        return SystemUIController.get().getISpeechUI().isSpeechUIEnable();
    }

    private WindowManager.LayoutParams getWindow(int area) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = 8388616;
        layoutParams.format = -3;
        SpeechWindowInfo info = SpeechConfig.get().getWindowInfo(area);
        layoutParams.setTitle(info.getName());
        layoutParams.type = info.getWindowType();
        layoutParams.x = info.getX();
        layoutParams.y = info.getY();
        layoutParams.width = info.getW();
        layoutParams.height = info.getH();
        layoutParams.gravity = info.getGravity();
        return layoutParams;
    }
}
