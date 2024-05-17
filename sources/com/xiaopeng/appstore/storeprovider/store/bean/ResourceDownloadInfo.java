package com.xiaopeng.appstore.storeprovider.store.bean;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
/* loaded from: classes22.dex */
public class ResourceDownloadInfo implements Parcelable {
    public static final Parcelable.Creator<ResourceDownloadInfo> CREATOR = new Parcelable.Creator<ResourceDownloadInfo>() { // from class: com.xiaopeng.appstore.storeprovider.store.bean.ResourceDownloadInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResourceDownloadInfo createFromParcel(Parcel source) {
            return new ResourceDownloadInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResourceDownloadInfo[] newArray(int size) {
            return new ResourceDownloadInfo[size];
        }
    };
    private long mDownloadId;
    private long mDownloadedBytes;
    private String mExpandInstalledContent;
    private String mFileUri;
    private String mRscId;
    private long mSpeed;
    private int mStatus;
    private String mTitle;
    private long mTotalBytes;
    private String mUrl;
    private int mVisibility;

    public ResourceDownloadInfo() {
        this.mStatus = 1;
    }

    @NonNull
    public String toString() {
        return "ResInfo{download=" + this.mDownloadId + ", resId='" + this.mRscId + "', title='" + this.mTitle + "', status=" + this.mStatus + ", total=" + this.mTotalBytes + ", downloaded=" + this.mDownloadedBytes + ", speed=" + this.mSpeed + '}';
    }

    public long getDownloadId() {
        return this.mDownloadId;
    }

    public void setDownloadId(long downloadId) {
        this.mDownloadId = downloadId;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public long getSpeed() {
        return this.mSpeed;
    }

    public void setSpeed(long speed) {
        this.mSpeed = speed;
    }

    public int getVisibility() {
        return this.mVisibility;
    }

    public void setVisibility(int visibility) {
        this.mVisibility = visibility;
    }

    public static Parcelable.Creator<ResourceDownloadInfo> getCREATOR() {
        return CREATOR;
    }

    public String getExpandInstalledContent() {
        return this.mExpandInstalledContent;
    }

    public void setExpandInstalledContent(String expandInstalledContent) {
        this.mExpandInstalledContent = expandInstalledContent;
    }

    public String getRscId() {
        return this.mRscId;
    }

    public void setRscId(String id) {
        this.mRscId = id;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public long getTotalBytes() {
        return this.mTotalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.mTotalBytes = totalBytes;
    }

    public long getDownloadedBytes() {
        return this.mDownloadedBytes;
    }

    public void setDownloadedBytes(long downloadedBytes) {
        this.mDownloadedBytes = downloadedBytes;
    }

    public String getFileUri() {
        return this.mFileUri;
    }

    public void setFileUri(String fileUri) {
        this.mFileUri = fileUri;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mDownloadId);
        dest.writeString(this.mRscId);
        dest.writeString(this.mUrl);
        dest.writeString(this.mTitle);
        dest.writeInt(this.mStatus);
        dest.writeLong(this.mTotalBytes);
        dest.writeLong(this.mDownloadedBytes);
        dest.writeLong(this.mSpeed);
        dest.writeInt(this.mVisibility);
        dest.writeString(this.mFileUri);
        dest.writeString(this.mExpandInstalledContent);
    }

    protected ResourceDownloadInfo(Parcel in) {
        this.mStatus = 1;
        this.mDownloadId = in.readLong();
        this.mRscId = in.readString();
        this.mUrl = in.readString();
        this.mTitle = in.readString();
        this.mStatus = in.readInt();
        this.mTotalBytes = in.readLong();
        this.mDownloadedBytes = in.readLong();
        this.mSpeed = in.readLong();
        this.mVisibility = in.readInt();
        this.mFileUri = in.readString();
        this.mExpandInstalledContent = in.readString();
    }
}
