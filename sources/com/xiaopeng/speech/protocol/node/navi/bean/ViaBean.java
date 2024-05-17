package com.xiaopeng.speech.protocol.node.navi.bean;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes23.dex */
public class ViaBean implements Parcelable {
    protected static final Parcelable.Creator<ViaBean> CREATOR = new Parcelable.Creator<ViaBean>() { // from class: com.xiaopeng.speech.protocol.node.navi.bean.ViaBean.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ViaBean createFromParcel(Parcel source) {
            return new ViaBean(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ViaBean[] newArray(int size) {
            return new ViaBean[size];
        }
    };
    public static final int WAYPOINT_TYPE_CHARGING_STATION = 1;
    public static final int WAYPOINT_TYPE_EXTRA = 0;
    public static final int WAYPOINT_TYPE_USER_POI = 2;
    public static final int WAYPOINT_TYPE_USER_ROAD = 3;
    private PoiBean pointInfo;
    private int viaType;

    public ViaBean() {
    }

    public ViaBean(Parcel in) {
        this.pointInfo = (PoiBean) in.readParcelable(PoiBean.class.getClassLoader());
        this.viaType = in.readInt();
    }

    public PoiBean getPointInfo() {
        return this.pointInfo;
    }

    public void setPointInfo(PoiBean pointInfo) {
        this.pointInfo = pointInfo;
    }

    public int getViaType() {
        return this.viaType;
    }

    public void setViaType(int viaType) {
        this.viaType = viaType;
    }

    public String toString() {
        return "ViaBean{pointInfo=" + this.pointInfo + ", viaType=" + this.viaType + '}';
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.pointInfo, flags);
        dest.writeInt(this.viaType);
    }
}
