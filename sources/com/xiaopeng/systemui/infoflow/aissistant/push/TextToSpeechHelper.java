package com.xiaopeng.systemui.infoflow.aissistant.push;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.SystemUIApplication;
import java.util.Locale;
/* loaded from: classes24.dex */
public class TextToSpeechHelper {
    private static final String TAG = "TextToSpeechHelper";
    private static final TextToSpeechHelper sInstance = new TextToSpeechHelper();
    private boolean isInit;
    private ISpeakCallback mSpeakCallback;
    private TextToSpeech mTextToSpeech;
    private TtsCache mTtsCache;

    /* loaded from: classes24.dex */
    public interface ISpeakCallback {
        void onEnd(String str);

        void onError(String str);

        void onStart(String str);

        void onStop(String str);
    }

    public static final TextToSpeechHelper instance() {
        return sInstance;
    }

    public String speak(String tts, ISpeakCallback listener) {
        if (TextUtils.isEmpty(tts)) {
            return null;
        }
        String utteranceId = String.valueOf(System.currentTimeMillis());
        if (this.mTextToSpeech == null) {
            init();
        }
        if (this.isInit) {
            this.mTextToSpeech.stop();
            speak(tts, utteranceId);
            this.mSpeakCallback = listener;
        } else {
            this.mTtsCache = new TtsCache();
            TtsCache ttsCache = this.mTtsCache;
            ttsCache.mTts = tts;
            ttsCache.listener = listener;
            ttsCache.mUtteranceId = utteranceId;
            Log.i(TAG, "TtsCache : " + this.mTtsCache);
        }
        return utteranceId;
    }

    public void stop() {
        Log.i(TAG, "stop");
        TextToSpeech textToSpeech = this.mTextToSpeech;
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    private void init() {
        Log.i(TAG, "init");
        this.mTextToSpeech = new TextToSpeech(SystemUIApplication.getContext(), new TextToSpeech.OnInitListener() { // from class: com.xiaopeng.systemui.infoflow.aissistant.push.TextToSpeechHelper.1
            @Override // android.speech.tts.TextToSpeech.OnInitListener
            public void onInit(int status) {
                Log.i(TextToSpeechHelper.TAG, "onInit status : " + status);
                if (status == 0) {
                    TextToSpeechHelper.this.isInit = true;
                    TextToSpeechHelper.this.mTextToSpeech.setLanguage(Locale.CHINESE);
                    TextToSpeechHelper.this.mTextToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() { // from class: com.xiaopeng.systemui.infoflow.aissistant.push.TextToSpeechHelper.1.1
                        @Override // android.speech.tts.UtteranceProgressListener
                        public void onStart(String utteranceId) {
                            Log.i(TextToSpeechHelper.TAG, "onStart utteranceId : " + utteranceId);
                            if (TextToSpeechHelper.this.mSpeakCallback != null) {
                                TextToSpeechHelper.this.mSpeakCallback.onStart(utteranceId);
                            }
                        }

                        @Override // android.speech.tts.UtteranceProgressListener
                        public void onDone(String utteranceId) {
                            Log.i(TextToSpeechHelper.TAG, "onDone utteranceId : " + utteranceId);
                            if (TextToSpeechHelper.this.mSpeakCallback != null) {
                                TextToSpeechHelper.this.mSpeakCallback.onEnd(utteranceId);
                                TextToSpeechHelper.this.mSpeakCallback = null;
                                TextToSpeechHelper.this.mTtsCache = null;
                            }
                        }

                        @Override // android.speech.tts.UtteranceProgressListener
                        public void onError(String utteranceId) {
                            Log.i(TextToSpeechHelper.TAG, "onDone utteranceId : " + utteranceId);
                            if (TextToSpeechHelper.this.mSpeakCallback != null) {
                                TextToSpeechHelper.this.mSpeakCallback.onError(utteranceId);
                                TextToSpeechHelper.this.mSpeakCallback = null;
                                TextToSpeechHelper.this.mTtsCache = null;
                            }
                        }
                    });
                    TextToSpeechHelper.this.mTextToSpeech.setOnStateChangedListener(new TextToSpeech.OnStateChangedListener() { // from class: com.xiaopeng.systemui.infoflow.aissistant.push.TextToSpeechHelper.1.2
                        public void onStateChanged(String name, int state) {
                            Log.i(TextToSpeechHelper.TAG, "onStateChanged state : " + state);
                            if (state == 0 && TextToSpeechHelper.this.mSpeakCallback != null) {
                                TextToSpeechHelper.this.mSpeakCallback.onStop(name);
                                TextToSpeechHelper.this.mSpeakCallback = null;
                                TextToSpeechHelper.this.mTtsCache = null;
                            }
                        }
                    });
                    if (TextToSpeechHelper.this.mTtsCache != null) {
                        TextToSpeechHelper textToSpeechHelper = TextToSpeechHelper.this;
                        textToSpeechHelper.mSpeakCallback = textToSpeechHelper.mTtsCache.listener;
                        TextToSpeechHelper textToSpeechHelper2 = TextToSpeechHelper.this;
                        textToSpeechHelper2.speak(textToSpeechHelper2.mTtsCache.mTts, TextToSpeechHelper.this.mTtsCache.mUtteranceId);
                        return;
                    }
                    return;
                }
                TextToSpeechHelper.this.mTextToSpeech = null;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void speak(String tts, String utteranceId) {
        Log.i(TAG, "speak tts : " + tts);
        if (this.mTextToSpeech != null && !TextUtils.isEmpty(tts)) {
            this.mTextToSpeech.speak(tts, 0, null, utteranceId);
        }
    }

    /* loaded from: classes24.dex */
    class TtsCache {
        ISpeakCallback listener;
        String mTts;
        String mUtteranceId;

        TtsCache() {
        }
    }
}
