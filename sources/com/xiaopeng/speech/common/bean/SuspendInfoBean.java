package com.xiaopeng.speech.common.bean;

import android.os.IBinder;
/* loaded from: classes23.dex */
public class SuspendInfoBean extends BaseBean {
    private IBinder binder;
    private String byWho;
    private String info;
    private boolean needMic;
    private int notifyType;

    public boolean isNeedMic() {
        return this.needMic;
    }

    public void setNeedMic(boolean needMic) {
        this.needMic = needMic;
    }

    public SuspendInfoBean(IBinder binder, String byWho, String info, int notifyType, boolean needMic) {
        this.binder = binder;
        this.byWho = byWho;
        this.info = info;
        this.notifyType = notifyType;
        this.needMic = needMic;
    }

    public IBinder getBinder() {
        return this.binder;
    }

    public void setBinder(IBinder binder) {
        this.binder = binder;
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
        return "SuspendInfoBean{binder=" + this.binder + ", byWho='" + this.byWho + "', info='" + this.info + "', notifyType=" + this.notifyType + ", needMic=" + this.needMic + '}';
    }
}
