package com.xiaopeng.speech.common.bean;

import android.os.IBinder;
/* loaded from: classes23.dex */
public class DisableInfoBean extends BaseBean {
    private IBinder binder;
    private String byWho;
    private String info;
    private int notifyType;
    private int type;

    public DisableInfoBean(IBinder binder, int type, String byWho, String info, int notifyType) {
        this.binder = binder;
        this.type = type;
        this.byWho = byWho;
        this.info = info;
        this.notifyType = notifyType;
    }

    public IBinder getBinder() {
        return this.binder;
    }

    public void setBinder(IBinder binder) {
        this.binder = binder;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getByWho() {
        return this.byWho;
    }

    public void setByWho(String byWho) {
        this.byWho = byWho;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getNotifyType() {
        return this.notifyType;
    }

    public void setNotifyType(int notifyType) {
        this.notifyType = notifyType;
    }

    public String toString() {
        return "DisableInfoBean{binder=" + this.binder + ", type=" + this.type + ", byWho='" + this.byWho + "', info='" + this.info + "', notifyType=" + this.notifyType + '}';
    }
}
