package com.xiaopeng.appstore.storeprovider;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import java.util.Objects;
/* loaded from: classes22.dex */
public class RequestContinuation implements Parcelable {
    public static final Parcelable.Creator<RequestContinuation> CREATOR = new Parcelable.Creator<RequestContinuation>() { // from class: com.xiaopeng.appstore.storeprovider.RequestContinuation.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RequestContinuation createFromParcel(Parcel in) {
            return new RequestContinuation(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RequestContinuation[] newArray(int size) {
            return new RequestContinuation[size];
        }
    };
    private String mDownloadUrl;
    @NonNull
    private final Bundle mExtraData;
    private String mIconUrl;
    private int mNotificationVisibility;
    private final RequestContinuation mParent;
    private final int mResourceType;
    @NonNull
    private final String mTaskKey;
    private final String mTaskName;
    private int mUseSystemUidDownload;

    public RequestContinuation(int resourceType, @NonNull String taskKey) {
        this(resourceType, taskKey, null);
    }

    public RequestContinuation(int resourceType, @NonNull String taskKey, String taskName) {
        this(resourceType, taskKey, taskName, null);
    }

    RequestContinuation(int resourceType, @NonNull String taskKey, String taskName, RequestContinuation parent) {
        this.mNotificationVisibility = 100;
        this.mUseSystemUidDownload = 0;
        this.mResourceType = resourceType;
        this.mParent = parent;
        this.mTaskKey = taskKey;
        this.mExtraData = new Bundle();
        taskName = TextUtils.isEmpty(taskName) ? getNameFromUrl(this.mDownloadUrl) : taskName;
        this.mTaskName = TextUtils.isEmpty(taskName) ? this.mTaskKey : taskName;
    }

    public RequestContinuation getParent() {
        return this.mParent;
    }

    @NonNull
    public String getTaskKey() {
        return this.mTaskKey;
    }

    public String getTaskName() {
        return this.mTaskName;
    }

    public int getResourceType() {
        return this.mResourceType;
    }

    public String getDownloadUrl() {
        return this.mDownloadUrl;
    }

    public RequestContinuation setDownloadUrl(String downloadUrl) {
        this.mDownloadUrl = downloadUrl;
        return this;
    }

    public int getNotificationVisibility() {
        return this.mNotificationVisibility;
    }

    public RequestContinuation setNotificationVisibility(int notificationVisibility) {
        this.mNotificationVisibility = notificationVisibility;
        return this;
    }

    public String getIconUrl() {
        return this.mIconUrl;
    }

    public RequestContinuation setIconUrl(String iconUrl) {
        this.mIconUrl = iconUrl;
        return this;
    }

    public boolean isUseSystemUidDownload() {
        return this.mUseSystemUidDownload == 1;
    }

    public RequestContinuation setUseSystemUidDownload(boolean useSystemUidDownload) {
        this.mUseSystemUidDownload = useSystemUidDownload ? 1 : 0;
        return this;
    }

    @NonNull
    public Bundle getExtraData() {
        return this.mExtraData;
    }

    public RequestContinuation putExtraAll(Bundle extra) {
        this.mExtraData.putAll(extra);
        return this;
    }

    public RequestContinuation putExtra(String key, String value) {
        this.mExtraData.putString(key, value);
        return this;
    }

    public RequestContinuation putExtra(String key, long value) {
        this.mExtraData.putLong(key, value);
        return this;
    }

    public RequestContinuation putExtra(String key, int value) {
        this.mExtraData.putInt(key, value);
        return this;
    }

    public RequestContinuation putExtra(String key, boolean value) {
        this.mExtraData.putBoolean(key, value);
        return this;
    }

    public RequestContinuation then(int resourceType) {
        return new RequestContinuation(resourceType, getTaskKey(), getTaskName(), this);
    }

    public EnqueueRequest request() {
        return new EnqueueRequest(this);
    }

    public String toString() {
        return "RequestContin{type=" + this.mResourceType + ", key=" + this.mTaskKey + ", name=" + this.mTaskKey + ", url='" + this.mDownloadUrl + "', parent='" + this.mParent + "', useSystemDownload=" + this.mUseSystemUidDownload + '}';
    }

    protected RequestContinuation(Parcel in) {
        this.mNotificationVisibility = 100;
        this.mUseSystemUidDownload = 0;
        this.mResourceType = in.readInt();
        this.mTaskKey = (String) Objects.requireNonNull(in.readString());
        this.mTaskName = in.readString();
        this.mDownloadUrl = in.readString();
        this.mNotificationVisibility = in.readInt();
        this.mIconUrl = in.readString();
        this.mUseSystemUidDownload = in.readInt();
        this.mExtraData = (Bundle) Objects.requireNonNull(in.readBundle(getClass().getClassLoader()));
        this.mParent = (RequestContinuation) in.readParcelable(getClass().getClassLoader());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mResourceType);
        dest.writeString(this.mTaskKey);
        dest.writeString(this.mTaskName);
        dest.writeString(this.mDownloadUrl);
        dest.writeInt(this.mNotificationVisibility);
        dest.writeString(this.mIconUrl);
        dest.writeInt(this.mUseSystemUidDownload);
        dest.writeBundle(this.mExtraData);
        dest.writeParcelable(this.mParent, 0);
    }

    private static String getNameFromUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        return Uri.parse(url).getLastPathSegment();
    }
}
