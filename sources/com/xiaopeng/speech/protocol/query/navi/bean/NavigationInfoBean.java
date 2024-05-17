package com.xiaopeng.speech.protocol.query.navi.bean;

import com.xiaopeng.speech.common.util.DontProguardClass;
import com.xiaopeng.speech.protocol.node.navi.bean.NaviInfoBean;
import com.xiaopeng.speech.protocol.node.navi.bean.PoiBean;
import java.util.List;
@DontProguardClass
/* loaded from: classes23.dex */
public class NavigationInfoBean {
    public PoiBean destinationPoint;
    public long remainderLift;
    public long remainderRange;
    public long remainderServieArea;
    public long remainderTime;
    public long remainderTollStation;
    public long speedLimit;
    public List<NaviInfoBean> viaPoints;

    public long getRemainderRange() {
        return this.remainderRange;
    }

    public long getRemainderServieArea() {
        return this.remainderServieArea;
    }

    public void setRemainderServieArea(long remainderServieArea) {
        this.remainderServieArea = remainderServieArea;
    }

    public long getRemainderTollStation() {
        return this.remainderTollStation;
    }

    public void setRemainderTollStation(long remainderTollStation) {
        this.remainderTollStation = remainderTollStation;
    }

    public long getSpeedLimit() {
        return this.speedLimit;
    }

    public void setSpeedLimit(long speedLimit) {
        this.speedLimit = speedLimit;
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

    public long getRemainderLift() {
        return this.remainderLift;
    }

    public void setRemainderLift(long remainderLift) {
        this.remainderLift = remainderLift;
    }

    public List<NaviInfoBean> getViaPoints() {
        return this.viaPoints;
    }

    public void setViaPoints(List<NaviInfoBean> viaPoints) {
        this.viaPoints = viaPoints;
    }

    public PoiBean getDestinationPoint() {
        return this.destinationPoint;
    }

    public void setDestinationPoint(PoiBean destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public String toString() {
        return "NavigationInfoBean{remainderRange=" + this.remainderRange + ", remainderTime=" + this.remainderTime + ", remainderLift=" + this.remainderLift + ", remainderServieArea=" + this.remainderServieArea + ", remainderTollStation=" + this.remainderTollStation + ", speedLimit=" + this.speedLimit + ", viaPoints=" + this.viaPoints + ", destinationPoint=" + this.destinationPoint + '}';
    }
}
