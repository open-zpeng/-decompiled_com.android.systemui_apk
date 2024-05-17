package com.xiaopeng.speech;

import android.os.Handler;
import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.common.util.SimpleCallbackList;
import com.xiaopeng.speech.coreapi.IEventObserver;
import com.xiaopeng.speech.jarvisproto.DMListening;
import com.xiaopeng.speech.jarvisproto.DMWait;
import java.lang.reflect.Constructor;
/* loaded from: classes23.dex */
public abstract class SpeechNode<T> extends IEventObserver.Stub {
    protected Handler mWorkerHandler;
    private boolean mSubscribed = false;
    protected SimpleCallbackList<T> mListenerList = new SimpleCallbackList<>();
    protected ICommandProcessor mCommandProcessor = bind(this);

    public void setSubscribed(boolean subscribed) {
        this.mSubscribed = subscribed;
        if (this.mSubscribed) {
            onSubscribe();
        } else {
            onUnsubscribe();
        }
    }

    public boolean isSubscribed() {
        return this.mSubscribed;
    }

    protected void onSubscribe() {
    }

    protected void onUnsubscribe() {
    }

    public Handler getWorkerHandler() {
        return this.mWorkerHandler;
    }

    public void setWorkerHandler(Handler workerHandler) {
        this.mWorkerHandler = workerHandler;
    }

    public void addListener(T listener) {
        this.mListenerList.addCallback(listener);
    }

    public void removeListener(T listener) {
        this.mListenerList.removeCallback(listener);
    }

    @Override // com.xiaopeng.speech.coreapi.IEventObserver
    public void onMessage(final String command, final String data) {
        if (this.mCommandProcessor != null) {
            Handler handler = this.mWorkerHandler;
            if (handler != null) {
                handler.post(new Runnable() { // from class: com.xiaopeng.speech.SpeechNode.1
                    @Override // java.lang.Runnable
                    public void run() {
                        SpeechNode.this.performCommand(command, data);
                    }
                });
            } else {
                performCommand(command, data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performCommand(String command, String data) {
        if (!DMListening.EVENT.equals(command) && !DMWait.EVENT.equals(command)) {
            LogUtils.d(this, "performCommand, command = " + command + ", data = " + data);
        }
        try {
            this.mCommandProcessor.performCommand(command, data);
        } catch (Exception e) {
            LogUtils.e(this, "performCommand error ", e);
        }
    }

    public String[] getSubscribeEvents() {
        ICommandProcessor iCommandProcessor = this.mCommandProcessor;
        if (iCommandProcessor != null) {
            return iCommandProcessor.getSubscribeEvents();
        }
        return null;
    }

    private ICommandProcessor bind(IEventObserver eventObserver) {
        String clsName = eventObserver.getClass().getName() + "_Processor";
        try {
            Class clazz = Class.forName(clsName);
            Constructor<? extends ICommandProcessor> bindingCtor = clazz.getConstructor(eventObserver.getClass());
            return (ICommandProcessor) bindingCtor.newInstance(eventObserver);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(this, String.format("bind %s error", clsName), e);
            return null;
        }
    }
}
