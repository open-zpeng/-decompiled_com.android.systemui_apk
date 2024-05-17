package com.xiaopeng.systemui.infoflow.speech;

import com.xiaopeng.speech.jarvisproto.SoundAreaStatus;
import java.util.ArrayList;
/* loaded from: classes24.dex */
public interface ISpeechPresenter {
    ArrayList<SoundAreaStatus> getSoundAreaStatus();

    void onFocusChanged(int i);

    void onListeningStatusChanged(boolean z);

    void onSoundAreaStatusChanged(SoundAreaStatus soundAreaStatus);

    void onTipsListeningShow(String str);

    void onTipsListeningStop();

    void onTtsEnd(String str);

    void onTtsStart(String str);

    void registerSpeechListener();

    void unregisterSpeechListener();
}
