package com.xiaopeng.module.aiavatar.helper;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import java.util.Locale;
/* loaded from: classes23.dex */
public class TextToSpeechHelper {
    private static final String TAG = "TextToSpeechHelper";
    private static final TextToSpeechHelper sInstance = new TextToSpeechHelper();
    private boolean isInit;
    private ISpeakCallback mSpeakCallback;
    private TextToSpeech mTextToSpeech;
    private String mTts;
    private TtsCache mTtsCache;

    /* loaded from: classes23.dex */
    public interface ISpeakCallback {
        void onEnd(String str);

        void onError(String str);

        void onStart(String str, String str2);

        void onStop();
    }

    public static final TextToSpeechHelper instance() {
        return sInstance;
    }

    public String speak(Context context, String tts) {
        return speak(context, tts, null);
    }

    public String speak(Context context, String tts, ISpeakCallback listener) {
        if (TextUtils.isEmpty(tts)) {
            return null;
        }
        this.mTts = tts;
        String utteranceId = String.valueOf(System.currentTimeMillis());
        if (this.mTextToSpeech == null) {
            init(context);
        }
        if (this.isInit) {
            speakText(tts, utteranceId);
            this.mSpeakCallback = listener;
        } else {
            this.mTtsCache = new TtsCache();
            TtsCache ttsCache = this.mTtsCache;
            ttsCache.mTts = tts;
            ttsCache.listener = listener;
            ttsCache.mUtteranceId = utteranceId;
        }
        return utteranceId;
    }

    public void stop() {
        if (this.mTextToSpeech != null) {
            ThreadUtils.postWorker(new Runnable() { // from class: com.xiaopeng.module.aiavatar.helper.TextToSpeechHelper.1
                @Override // java.lang.Runnable
                public void run() {
                    TextToSpeechHelper.this.mTextToSpeech.stop();
                }
            });
        }
    }

    private void init(Context context) {
        this.mTextToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() { // from class: com.xiaopeng.module.aiavatar.helper.TextToSpeechHelper.2
            @Override // android.speech.tts.TextToSpeech.OnInitListener
            public void onInit(int status) {
                if (status == 0) {
                    TextToSpeechHelper.this.isInit = true;
                    TextToSpeechHelper.this.mTextToSpeech.setLanguage(Locale.CHINESE);
                    TextToSpeechHelper.this.mTextToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() { // from class: com.xiaopeng.module.aiavatar.helper.TextToSpeechHelper.2.1
                        @Override // android.speech.tts.UtteranceProgressListener
                        public void onStart(String utteranceId) {
                            if (TextToSpeechHelper.this.mSpeakCallback != null) {
                                TextToSpeechHelper.this.mSpeakCallback.onStart(TextToSpeechHelper.this.mTts, utteranceId);
                            }
                        }

                        @Override // android.speech.tts.UtteranceProgressListener
                        public void onDone(String utteranceId) {
                            if (TextToSpeechHelper.this.mSpeakCallback != null) {
                                TextToSpeechHelper.this.mSpeakCallback.onEnd(utteranceId);
                            }
                        }

                        @Override // android.speech.tts.UtteranceProgressListener
                        public void onError(String utteranceId) {
                            if (TextToSpeechHelper.this.mSpeakCallback != null) {
                                TextToSpeechHelper.this.mSpeakCallback.onError(utteranceId);
                            }
                        }
                    });
                    if (TextToSpeechHelper.this.mTtsCache != null) {
                        TextToSpeechHelper textToSpeechHelper = TextToSpeechHelper.this;
                        textToSpeechHelper.mSpeakCallback = textToSpeechHelper.mTtsCache.listener;
                        TextToSpeechHelper textToSpeechHelper2 = TextToSpeechHelper.this;
                        textToSpeechHelper2.speakText(textToSpeechHelper2.mTtsCache.mTts, TextToSpeechHelper.this.mTtsCache.mUtteranceId);
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void speakText(final String tts, final String utteranceId) {
        if (this.mTextToSpeech != null && !TextUtils.isEmpty(tts)) {
            ThreadUtils.postWorker(new Runnable() { // from class: com.xiaopeng.module.aiavatar.helper.TextToSpeechHelper.3
                @Override // java.lang.Runnable
                public void run() {
                    TextToSpeechHelper.this.mTextToSpeech.speak(tts, 0, null, utteranceId);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes23.dex */
    public class TtsCache {
        ISpeakCallback listener;
        String mTts;
        String mUtteranceId;

        TtsCache() {
        }
    }
}
