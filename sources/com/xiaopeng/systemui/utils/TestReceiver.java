package com.xiaopeng.systemui.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.xiaopeng.speech.jarvisproto.DMWait;
import com.xiaopeng.speech.protocol.event.OOBEEvent;
import com.xiaopeng.speech.protocol.node.tts.TtsEcho;
import com.xiaopeng.speech.protocol.query.speech.hardware.bean.StreamType;
import com.xiaopeng.systemui.infoflow.speech.SpeechPresenter;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.secondarywindow.PreInstalledPresenter;
import com.xiaopeng.systemui.speech.data.SpeechDataHint;
import com.xiaopeng.systemui.speech.data.SpeechDataInput;
import com.xiaopeng.systemui.speech.data.SpeechManager;
import com.xiaopeng.systemui.statusbar.MaskLayer.WatermarkPresenter;
import com.xiaopeng.xui.app.XDialog;
import java.util.Random;
/* loaded from: classes24.dex */
public class TestReceiver extends BroadcastReceiver {
    private static final String TAG = "TestReceiver";

    public void register(Context context) {
        com.xiaopeng.systemui.infoflow.util.Logger.d(TAG, " register");
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.system_TEST");
        context.registerReceiver(this, filter);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("com.android.system_TEST".equals(action)) {
            String a = intent.getStringExtra("action");
            String data = intent.getStringExtra("data");
            com.xiaopeng.systemui.infoflow.util.Logger.d(TAG, " a " + a + " , data " + data);
            if ("PreInstalled".equals(a)) {
                if ("docheck".equals(data)) {
                    PreInstalledPresenter.get().test();
                } else if ("doQuery".equals(data)) {
                    PreInstalledPresenter.get().testDoQuery();
                }
            } else if ("showVoiceWave".equals(a)) {
                Log.d(TAG, data);
                Log.d(TAG, "" + Integer.parseInt(data.split(",")[0]) + Integer.parseInt(data.split(",")[1]));
                SpeechPresenter.getInstance().showVoiceWaveAnim(Integer.parseInt(data.split(",")[0]), Integer.parseInt(data.split(",")[1]));
            } else if ("dialog".equals(a)) {
                new XDialog(context).setTitle("demo").setMessage("demo").setSystemDialog(2008).setPositiveButton("ok").show();
            } else if ("updateRepairMode".equals(a)) {
                if (OOBEEvent.STRING_TRUE.equals(data)) {
                    WatermarkPresenter.getInstance().getRepairModeMaskLayer().updateView(true);
                } else if (OOBEEvent.STRING_FALSE.equals(data)) {
                    WatermarkPresenter.getInstance().getRepairModeMaskLayer().updateView(false);
                }
            } else if ("updateDiagnosticMode".equals(a)) {
                if (OOBEEvent.STRING_TRUE.equals(data)) {
                    WatermarkPresenter.getInstance().getDiagnosticModeMaskLayer().updateView(true);
                } else if (OOBEEvent.STRING_FALSE.equals(data)) {
                    WatermarkPresenter.getInstance().getDiagnosticModeMaskLayer().updateView(false);
                }
            } else if (StreamType.SPEECH.equals(a)) {
                int count = new Random().nextInt(8);
                StringBuilder res = new StringBuilder("1");
                for (int i = 0; i < count; i++) {
                    res.append("12");
                }
                int area = intent.getIntExtra("area", 1);
                if ("hint".equals(data)) {
                    SpeechDataHint dataHint = new SpeechDataHint();
                    dataHint.setRelateText("你好啊");
                    dataHint.setSoundArea(area);
                    dataHint.setText("为什么啊实打实大师");
                    SpeechManager.get().getContextListener().onTipsListeningShow(GsonUtil.toJson(dataHint));
                } else if ("asr".equals(data)) {
                    SpeechDataInput dataHint2 = new SpeechDataInput();
                    dataHint2.setSoundArea(area);
                    dataHint2.setText("连接蓝牙耳机后才可调节耳机音量");
                    SpeechManager.get().getContextListener().onInputText(GsonUtil.toJson(dataHint2));
                } else if ("echo_ok".equals(data)) {
                    TtsEcho echo = new TtsEcho(area, "1", "连接蓝牙耳机后才可调节耳机音量", 1, 1L);
                    SpeechManager.get().getContextListener().onTtsEcho(echo);
                } else if ("echo_fail".equals(data)) {
                    TtsEcho echo2 = new TtsEcho(area, "1", "失败" + ((Object) res), 2, 1L);
                    SpeechManager.get().getContextListener().onTtsEcho(echo2);
                } else if ("echo_end".equals(data)) {
                    TtsEcho echo3 = new TtsEcho(area, "1", "", 2, 1L);
                    SpeechManager.get().getContextListener().onTtsEcho(echo3);
                } else if (DMWait.STATUS_END.equals(data)) {
                    SpeechManager.get().getDialogListener().onDialogEnd(null);
                }
            }
        }
    }
}
