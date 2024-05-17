package com.xiaopeng.appstore.storeprovider;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes22.dex */
public class Resource implements Parcelable {
    public static final Parcelable.Creator<Resource> CREATOR = new Parcelable.Creator<Resource>() { // from class: com.xiaopeng.appstore.storeprovider.Resource.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Resource createFromParcel(Parcel in) {
            return new Resource(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Resource[] newArray(int size) {
            return new Resource[size];
        }
    };
    private long mCreateTime;
    private String mDes;
    private String mDownloadUrl;
    private String mExpandContent;
    private String mExpandInstalledContent;
    private Bundle mLocalData;
    private String mPrice;
    private int mResType;
    private String mRscIcon;
    private String mRscId;
    private String mRscName;
    private long mUpdateTime;

    public Resource() {
    }

    public String toString() {
        return "Res{url='" + this.mDownloadUrl + "', rscId='" + this.mRscId + "', rscName='" + this.mRscName + "', rscIcon='" + this.mRscIcon + "', create='" + this.mCreateTime + "', update=" + this.mUpdateTime + ", expandContent='" + this.mExpandContent + "', expandInstalled='" + this.mExpandInstalledContent + "', des='" + this.mDes + "', price='" + this.mPrice + "', type='" + this.mResType + "'}";
    }

    public String getDownloadUrl() {
        return this.mDownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.mDownloadUrl = downloadUrl;
    }

    public String getRscId() {
        return this.mRscId;
    }

    public void setRscId(String rscId) {
        this.mRscId = rscId;
    }

    public String getRscName() {
        return this.mRscName;
    }

    public void setRscName(String rscName) {
        this.mRscName = rscName;
    }

    public String getRscIcon() {
        return this.mRscIcon;
    }

    public void setRscIcon(String rscIcon) {
        this.mRscIcon = rscIcon;
    }

    public long getCreateTime() {
        return this.mCreateTime;
    }

    public void setCreateTime(long createTime) {
        this.mCreateTime = createTime;
    }

    public long getUpdateTime() {
        return this.mUpdateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.mUpdateTime = updateTime;
    }

    public String getExpandContent() {
        return this.mExpandContent;
    }

    public void setExpandContent(String expandContent) {
        this.mExpandContent = expandContent;
    }

    public String getExpandInstalledContent() {
        return this.mExpandInstalledContent;
    }

    public void setExpandInstalledContent(String expandInstalledContent) {
        this.mExpandInstalledContent = expandInstalledContent;
    }

    public String getDes() {
        return this.mDes;
    }

    public void setDes(String des) {
        this.mDes = des;
    }

    public String getPrice() {
        return this.mPrice;
    }

    public void setPrice(String price) {
        this.mPrice = price;
    }

    public int getResType() {
        return this.mResType;
    }

    public void setResType(int resType) {
        this.mResType = resType;
    }

    public Bundle getLocalData() {
        return this.mLocalData;
    }

    public void setLocalData(Bundle localData) {
        this.mLocalData = localData;
    }

    protected Resource(Parcel in) {
        this.mDownloadUrl = in.readString();
        this.mRscId = in.readString();
        this.mRscName = in.readString();
        this.mRscIcon = in.readString();
        this.mCreateTime = in.readLong();
        this.mUpdateTime = in.readLong();
        this.mExpandContent = in.readString();
        this.mExpandInstalledContent = in.readString();
        this.mDes = in.readString();
        this.mPrice = in.readString();
        this.mResType = in.readInt();
        this.mLocalData = in.readBundle(getClass().getClassLoader());
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDownloadUrl);
        dest.writeString(this.mRscId);
        dest.writeString(this.mRscName);
        dest.writeString(this.mRscIcon);
        dest.writeLong(this.mCreateTime);
        dest.writeLong(this.mUpdateTime);
        dest.writeString(this.mExpandContent);
        dest.writeString(this.mExpandInstalledContent);
        dest.writeString(this.mDes);
        dest.writeString(this.mPrice);
        dest.writeInt(this.mResType);
        dest.writeBundle(this.mLocalData);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}
