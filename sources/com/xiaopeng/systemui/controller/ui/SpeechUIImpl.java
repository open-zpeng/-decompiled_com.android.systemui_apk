package com.xiaopeng.systemui.controller.ui;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.ui.ISpeechUI;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
/* loaded from: classes24.dex */
class SpeechUIImpl implements ISpeechUI {
    private static final String SPEECH_UI_ENABLE_KEY = "xp_speechui_enable";
    private static final String TAG = "SpeechUIImpl";
    private final CopyOnWriteArraySet<ISpeechUI.ISpeechUICallBack> mCallBacks;
    private final Context mContext;
    private ContentObserver mObserver;
    private boolean mSpeechUIEnable;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpeechUIImpl(Context context) {
        long t = System.currentTimeMillis();
        this.mContext = context.getApplicationContext();
        this.mCallBacks = new CopyOnWriteArraySet<>();
        this.mSpeechUIEnable = isSpeechUIEnableForDb();
        monitorSpeechUI();
        Logger.d(TAG, "init : " + this.mSpeechUIEnable + " , time : " + (System.currentTimeMillis() - t));
    }

    @Override // com.xiaopeng.systemui.controller.ui.ISpeechUI
    public boolean isSpeechUIEnable() {
        return this.mSpeechUIEnable;
    }

    @Override // com.xiaopeng.systemui.controller.ui.ISpeechUI
    public void addSpeechUICallBack(ISpeechUI.ISpeechUICallBack callBack) {
        this.mCallBacks.add(callBack);
    }

    @Override // com.xiaopeng.systemui.controller.ui.ISpeechUI
    public void removeSpeechUICallBack(ISpeechUI.ISpeechUICallBack callBack) {
        this.mCallBacks.remove(callBack);
    }

    private boolean isSpeechUIEnableForDb() {
        int value = Settings.System.getInt(this.mContext.getContentResolver(), SPEECH_UI_ENABLE_KEY, 1);
        return value == 1;
    }

    private void monitorSpeechUI() {
        if (this.mObserver != null) {
            return;
        }
        this.mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.xiaopeng.systemui.controller.ui.SpeechUIImpl.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                SpeechUIImpl.this.checkChange(selfChange, uri);
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(SPEECH_UI_ENABLE_KEY), true, this.mObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkChange(boolean selfChange, Uri uri) {
        boolean isSpeechUIEnable;
        if (uri.equals(Settings.System.getUriFor(SPEECH_UI_ENABLE_KEY)) && this.mSpeechUIEnable != (isSpeechUIEnable = isSpeechUIEnableForDb())) {
            Logger.i(TAG, "checkChange isSpeechUIEnable :" + isSpeechUIEnable);
            this.mSpeechUIEnable = isSpeechUIEnable;
            notifySpeechUIEnableChanged(isSpeechUIEnable);
        }
    }

    private void notifySpeechUIEnableChanged(boolean enable) {
        if (this.mCallBacks.isEmpty()) {
            Logger.d(TAG, "notifySpeechUIEnableChanged callback isEmpty  ");
            return;
        }
        long time = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        Iterator<ISpeechUI.ISpeechUICallBack> it = this.mCallBacks.iterator();
        while (it.hasNext()) {
            ISpeechUI.ISpeechUICallBack back = it.next();
            back.onSpeechUIEnableChanged(enable);
            sb.append(back);
            sb.append(" time : ");
            sb.append(System.currentTimeMillis() - time);
            sb.append(" ; ");
            time = System.currentTimeMillis();
        }
        Logger.d(TAG, "notifySpeechUIEnableChanged : " + sb.toString());
    }
}
