package com.xiaopeng.speech;

import android.os.Handler;
import com.xiaopeng.speech.IRemoteDataSensor;
import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.common.bean.Value;
import com.xiaopeng.speech.common.util.LogUtils;
import java.lang.reflect.Constructor;
/* loaded from: classes23.dex */
public abstract class SpeechQuery<T> extends IRemoteDataSensor.Stub {
    protected T mQueryCaller;
    protected Handler mWorkerHandler;
    private boolean mSubscribed = false;
    protected IQueryProcessor mQueryProcessor = bind(this);

    public void setSubscribed(boolean subscribed) {
        this.mSubscribed = subscribed;
        if (this.mSubscribed) {
            onSubscribe();
        } else {
            onUnsubscribe();
        }
    }

    public void setQueryCaller(T queryCaller) {
        this.mQueryCaller = queryCaller;
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

    @Override // com.xiaopeng.speech.IRemoteDataSensor
    public Value onQuery(String dataApi, String data) {
        Value value = Value.VOID;
        if (this.mQueryProcessor != null) {
            Value value2 = performQuery(dataApi, data);
            LogUtils.i("SpeechQuery", "dataApi = " + dataApi + ",param data =" + data + ", query result value = " + value2.toString());
            return value2;
        }
        return value;
    }

    private Value performQuery(String command, String data) {
        try {
            return new Value(this.mQueryProcessor.querySensor(command, data));
        } catch (Exception e) {
            LogUtils.e(this, "performCommand error ", e);
            return Value.VOID;
        }
    }

    public String[] getQueryEvents() {
        IQueryProcessor iQueryProcessor = this.mQueryProcessor;
        if (iQueryProcessor != null) {
            return iQueryProcessor.getQueryEvents();
        }
        return null;
    }

    private IQueryProcessor bind(IRemoteDataSensor remoteDataSensor) {
        String clsName = remoteDataSensor.getClass().getName() + "_Processor";
        try {
            Class clazz = Class.forName(clsName);
            Constructor<? extends IQueryProcessor> bindingCtor = clazz.getConstructor(remoteDataSensor.getClass());
            return (IQueryProcessor) bindingCtor.newInstance(remoteDataSensor);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(this, String.format("bind %s error", clsName), e);
            return null;
        }
    }
}
