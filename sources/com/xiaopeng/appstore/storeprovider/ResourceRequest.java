package com.xiaopeng.appstore.storeprovider;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
/* loaded from: classes22.dex */
public class ResourceRequest implements Parcelable {
    public static final int ACTION_QUERY_ALL = 100;
    public static final int ACTION_QUERY_LOCAL = 300;
    public static final int ACTION_QUERY_REMOTE = 200;
    public static final Parcelable.Creator<ResourceRequest> CREATOR = new Parcelable.Creator<ResourceRequest>() { // from class: com.xiaopeng.appstore.storeprovider.ResourceRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResourceRequest createFromParcel(Parcel in) {
            return new ResourceRequest(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResourceRequest[] newArray(int size) {
            return new ResourceRequest[size];
        }
    };
    private final int mAction;
    @NonNull
    private final Bundle mExtra;
    @Deprecated
    private final long mResId;
    private final int mResourceType;

    public ResourceRequest(int resourceType, int action) {
        this(resourceType, action, -1L);
    }

    public ResourceRequest(int resourceType, int action, long resId) {
        this(resourceType, action, resId, new Bundle());
    }

    private ResourceRequest(int resourceType, int action, long resId, @NonNull Bundle extra) {
        this.mResourceType = resourceType;
        this.mAction = action;
        this.mResId = resId;
        this.mExtra = extra;
    }

    public int getResourceType() {
        return this.mResourceType;
    }

    public int getAction() {
        return this.mAction;
    }

    @Deprecated
    public long getResId() {
        return this.mResId;
    }

    @NonNull
    public Bundle getExtra() {
        return this.mExtra;
    }

    public String toString() {
        return "ResourceRequest{resType=" + this.mResourceType + ", action=" + this.mAction + ", resId=" + this.mResId + '}';
    }

    protected ResourceRequest(Parcel in) {
        this.mResourceType = in.readInt();
        this.mAction = in.readInt();
        this.mResId = in.readLong();
        this.mExtra = in.readBundle(getClass().getClassLoader());
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mResourceType);
        dest.writeInt(this.mAction);
        dest.writeLong(this.mResId);
        dest.writeBundle(this.mExtra);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* loaded from: classes22.dex */
    public static class Builder {
        private final int mAction;
        private final Bundle mExtra = new Bundle();
        private final int mResourceType;

        public Builder(int resourceType, int action) {
            this.mResourceType = resourceType;
            this.mAction = action;
        }

        public Builder putExtra(String key, int value) {
            this.mExtra.putInt(key, value);
            return this;
        }

        public Builder putExtra(String key, long value) {
            this.mExtra.putLong(key, value);
            return this;
        }

        public ResourceRequest build() {
            return new ResourceRequest(this.mResourceType, this.mAction, -1L, this.mExtra);
        }
    }
}
