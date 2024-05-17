package com.xiaopeng.speech.common.bean;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes23.dex */
public class BaseBean implements Parcelable {
    public static final Parcelable.Creator<BaseBean> CREATOR = new Parcelable.Creator<BaseBean>() { // from class: com.xiaopeng.speech.common.bean.BaseBean.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BaseBean createFromParcel(Parcel source) {
            return new BaseBean(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BaseBean[] newArray(int size) {
            return new BaseBean[size];
        }
    };
    private String outPut;
    private String title;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOutPut() {
        return this.outPut;
    }

    public void setOutPut(String outPut) {
        this.outPut = outPut;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.outPut);
    }

    public BaseBean() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseBean(Parcel in) {
        this.title = in.readString();
        this.outPut = in.readString();
    }
}
