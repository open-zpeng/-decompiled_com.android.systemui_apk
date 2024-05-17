package com.xiaopeng.appstore.storeprovider;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
/* loaded from: classes22.dex */
public class AssembleInfo implements Parcelable {
    public static final Parcelable.Creator<AssembleInfo> CREATOR = new Parcelable.Creator<AssembleInfo>() { // from class: com.xiaopeng.appstore.storeprovider.AssembleInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AssembleInfo createFromParcel(Parcel in) {
            return new AssembleInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AssembleInfo[] newArray(int size) {
            return new AssembleInfo[size];
        }
    };
    public static final int STATE_CANCELLED = 1150;
    public static final int STATE_COMPLETE = 1100;
    public static final int STATE_ERROR = 1000;
    public static final int STATE_LOADING = 1;
    public static final int STATE_PAUSED = 200;
    public static final int STATE_PENDING = 2;
    public static final int STATE_RUNNING = 100;
    public static final int STATE_RUNNING_DOWNLOAD = 101;
    public static final int STATE_RUNNING_INSTALLING = 102;
    public static final int STATE_UNKNOWN = 0;
    private int mCanPause;
    private int mCanStop;
    private final Bundle mExtra;
    private final String mKey;
    private String mName;
    private int mNotificationVisibility;
    private float mProgress;
    private final int mResourceType;
    private int mState;

    public static boolean isPreparing(int state) {
        return state >= 1 && state < 100;
    }

    public static boolean isRunning(int state) {
        return state >= 100 && state < 200;
    }

    public static boolean isPaused(int state) {
        return state >= 200 && state < 300;
    }

    public static boolean isError(int state) {
        return state >= 1000 && state < 1100;
    }

    public static boolean isCompleted(int state) {
        return state >= 1100 && state < 1150;
    }

    public static boolean isCancelled(int state) {
        return state >= 1150 && state < 1200;
    }

    public static boolean isFinished(int state) {
        return state >= 1000 && state <= 1200;
    }

    public static String stateToStr(int state) {
        if (state != 0) {
            if (state != 1) {
                if (state != 2) {
                    if (state != 200) {
                        if (state != 1000) {
                            if (state != 1100) {
                                if (state != 1150) {
                                    switch (state) {
                                        case 100:
                                            return "STATE_RUNNING";
                                        case 101:
                                            return "STATE_RUNNING_DOWNLOAD";
                                        case 102:
                                            return "STATE_RUNNING_INSTALLING";
                                        default:
                                            return state + "";
                                    }
                                }
                                return "STATE_CANCELLED";
                            }
                            return "STATE_COMPLETE";
                        }
                        return "STATE_ERROR";
                    }
                    return "STATE_PAUSED";
                }
                return "STATE_PENDING";
            }
            return "STATE_LOADING";
        }
        return "STATE_UNKNOWN";
    }

    public AssembleInfo(int resourceType, String key) {
        this.mState = 0;
        this.mNotificationVisibility = 100;
        this.mResourceType = resourceType;
        this.mKey = key;
        this.mExtra = new Bundle();
    }

    public AssembleInfo copy(String newKey) {
        AssembleInfo newInfo = new AssembleInfo(getResourceType(), newKey);
        newInfo.setNotificationVisibility(getNotificationVisibility());
        newInfo.setProgress(getProgress());
        newInfo.setState(getState());
        newInfo.setCanStop(canStop());
        newInfo.setCanPause(canPause());
        newInfo.mExtra.putAll(this.mExtra);
        return newInfo;
    }

    public long getExtraLong(String key) {
        return this.mExtra.getLong(key);
    }

    public String getExtraString(String key) {
        return this.mExtra.getString(key);
    }

    public void putExtra(String key, long value) {
        this.mExtra.putLong(key, value);
    }

    public void putExtra(String key, String value) {
        this.mExtra.putString(key, value);
    }

    public void putExtraAll(@NonNull Bundle bundle) {
        this.mExtra.putAll(bundle);
    }

    public void setState(int state) {
        this.mState = state;
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
    }

    public int getResourceType() {
        return this.mResourceType;
    }

    public String getKey() {
        return this.mKey;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getState() {
        return this.mState;
    }

    public float getProgress() {
        return this.mProgress;
    }

    public boolean canPause() {
        return this.mCanPause == 1;
    }

    public void setCanPause(boolean canPause) {
        this.mCanPause = canPause ? 1 : 0;
    }

    public boolean canStop() {
        return this.mCanStop == 1;
    }

    public void setCanStop(boolean canStop) {
        this.mCanStop = canStop ? 1 : 0;
    }

    public int getNotificationVisibility() {
        return this.mNotificationVisibility;
    }

    public void setNotificationVisibility(int notificationVisibility) {
        this.mNotificationVisibility = notificationVisibility;
    }

    public String toString() {
        return "AssembleInfo{key='" + this.mKey + "', resType=" + this.mResourceType + ", name=" + this.mName + ", state=" + stateToStr(this.mState) + ", prog=" + this.mProgress + ", visible=" + this.mNotificationVisibility + ", canPause=" + this.mCanPause + ", canStop=" + this.mCanStop + ", extra=" + this.mExtra + '}';
    }

    protected AssembleInfo(Parcel in) {
        this.mState = 0;
        this.mNotificationVisibility = 100;
        this.mKey = in.readString();
        this.mResourceType = in.readInt();
        this.mName = in.readString();
        this.mState = in.readInt();
        this.mProgress = in.readFloat();
        this.mNotificationVisibility = in.readInt();
        this.mCanPause = in.readInt();
        this.mCanStop = in.readInt();
        this.mExtra = in.readBundle(getClass().getClassLoader());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mKey);
        dest.writeInt(this.mResourceType);
        dest.writeString(this.mName);
        dest.writeInt(this.mState);
        dest.writeFloat(this.mProgress);
        dest.writeInt(this.mNotificationVisibility);
        dest.writeInt(this.mCanPause);
        dest.writeInt(this.mCanStop);
        dest.writeBundle(this.mExtra);
    }
}
