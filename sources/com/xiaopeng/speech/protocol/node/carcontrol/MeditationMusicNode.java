package com.xiaopeng.speech.protocol.node.carcontrol;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.MeditationMusicEvent;
/* loaded from: classes23.dex */
public class MeditationMusicNode extends SpeechNode<MeditationMusicListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MeditationMusicEvent.MEDITATION_MUSIC_CONTROL_PREV)
    public void onPrev(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MeditationMusicListener) obj).onPrev();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MeditationMusicEvent.MEDITATION_MUSIC_CONTROL_NEXT)
    public void onNext(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MeditationMusicListener) obj).onNext();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MeditationMusicEvent.MEDITATION_MUSIC_CONTROL_PAUSE)
    public void onPause(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MeditationMusicListener) obj).onPause();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MeditationMusicEvent.MEDITATION_MUSIC_CONTROL_RESUME)
    public void onResume(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MeditationMusicListener) obj).onResume();
            }
        }
    }
}
