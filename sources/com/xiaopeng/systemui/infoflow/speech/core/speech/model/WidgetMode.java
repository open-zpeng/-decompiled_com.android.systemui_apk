package com.xiaopeng.systemui.infoflow.speech.core.speech.model;

import com.xiaopeng.speech.protocol.SpeechModel;
import com.xiaopeng.speech.protocol.node.widget.AbsWidgetListener;
import com.xiaopeng.speech.protocol.node.widget.WidgetNode;
import com.xiaopeng.systemui.infoflow.speech.core.speech.SpeechManager;
import com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.SpeechWidgetManager;
/* loaded from: classes24.dex */
public class WidgetMode extends SpeechModel {
    private static final String TAG = WidgetMode.class.getSimpleName();
    protected SpeechManager mSpeechManager;

    public WidgetMode(SpeechManager speechManager) {
        this.mSpeechManager = speechManager;
        subscribe(WidgetNode.class, new WidgetListener());
    }

    /* loaded from: classes24.dex */
    private class WidgetListener extends AbsWidgetListener {
        private WidgetListener() {
        }

        @Override // com.xiaopeng.speech.protocol.node.widget.AbsWidgetListener, com.xiaopeng.speech.protocol.node.widget.WidgetListener
        public void onAcWidgetOn() {
            WidgetMode.this.getSpeechWidgetManager().onAcWidget();
        }
    }

    public SpeechWidgetManager getSpeechWidgetManager() {
        return this.mSpeechManager.getSpeechWidgetManager();
    }
}
