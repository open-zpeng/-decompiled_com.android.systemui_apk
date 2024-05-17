package com.xiaopeng.speech.common.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
/* loaded from: classes23.dex */
public class SliceData implements Parcelable {
    public static final Parcelable.Creator<SliceData> CREATOR = new Parcelable.Creator<SliceData>() { // from class: com.xiaopeng.speech.common.bean.SliceData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SliceData createFromParcel(Parcel in) {
            return new SliceData(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SliceData[] newArray(int size) {
            return new SliceData[size];
        }
    };
    private byte[] data;
    private int totalLength;

    public SliceData(byte[] data, int totalLength) {
        this.data = data;
        this.totalLength = totalLength;
    }

    protected SliceData(Parcel in) {
        this.data = in.readBlob();
        this.totalLength = in.readInt();
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getTotalLength() {
        return this.totalLength;
    }

    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBlob(this.data);
        dest.writeInt(this.totalLength);
    }

    public String toString() {
        return "SliceData{data=" + Arrays.toString(this.data) + ", totalLength=" + this.totalLength + '}';
    }
}
