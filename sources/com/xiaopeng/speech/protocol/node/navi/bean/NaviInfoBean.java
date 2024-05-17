package com.xiaopeng.speech.protocol.node.navi.bean;

import com.xiaopeng.speech.common.util.DontProguardClass;
@DontProguardClass
/* loaded from: classes23.dex */
public class NaviInfoBean {
    private PoiBean poiBean;
    private long remainderLift;
    private long remainderRange;
    private long remainderTime;

    public long getRemainderLift() {
        return this.remainderLift;
    }

    public void setRemainderLift(long remainderLift) {
        this.remainderLift = remainderLift;
    }

    public PoiBean getPoiBean() {
        return this.poiBean;
    }

    public void setPoiBean(PoiBean poiBean) {
        this.poiBean = poiBean;
    }

    public long getRemainderRange() {
        return this.remainderRange;
    }

    public void setRemainderRange(long remainderRange) {
        this.remainderRange = remainderRange;
    }

    public long getRemainderTime() {
        return this.remainderTime;
    }

    public void setRemainderTime(long remainderTime) {
        this.remainderTime = remainderTime;
    }

    public String toString() {
        return "NaviInfoBean{remainderRange=" + this.remainderRange + ", remainderTime=" + this.remainderTime + ", remainderLift=" + this.remainderLift + ", poiBean=" + this.poiBean + '}';
    }
}
