package com.xiaopeng.systemui.infoflow.speech.core.speech;

import com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.CarListenerManager;
import com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.SpeechAvatarManager;
import com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.SpeechContextManager;
import com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.SpeechWidgetManager;
import com.xiaopeng.systemui.infoflow.speech.core.speech.model.ContextModel;
import com.xiaopeng.systemui.infoflow.speech.core.speech.model.DialogModel;
import com.xiaopeng.systemui.infoflow.speech.core.speech.model.SceneModel;
import com.xiaopeng.systemui.infoflow.speech.core.speech.model.WidgetMode;
/* loaded from: classes24.dex */
public class SpeechManager {
    public static final String TAG = SpeechManager.class.getSimpleName();
    private CarListenerManager mCarListenerManager;
    private SpeechAvatarManager mSpeechAvatarManager;
    private SpeechContextManager mSpeechContextManager;
    private SpeechWidgetManager mSpeechWidgetManager;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class Holder {
        private static final SpeechManager Instance = new SpeechManager();

        private Holder() {
        }
    }

    public static final SpeechManager instance() {
        return Holder.Instance;
    }

    private SpeechManager() {
        new ContextModel(this);
        new DialogModel(this);
        new WidgetMode(this);
        new SceneModel();
        this.mSpeechAvatarManager = new SpeechAvatarManager();
        this.mSpeechContextManager = new SpeechContextManager();
        this.mCarListenerManager = new CarListenerManager();
        this.mSpeechWidgetManager = new SpeechWidgetManager();
    }

    public SpeechAvatarManager getSpeechAvatarManager() {
        return this.mSpeechAvatarManager;
    }

    public SpeechContextManager getSpeechContextManager() {
        return this.mSpeechContextManager;
    }

    public CarListenerManager getCarListenerManager() {
        return this.mCarListenerManager;
    }

    public SpeechWidgetManager getSpeechWidgetManager() {
        return this.mSpeechWidgetManager;
    }
}
