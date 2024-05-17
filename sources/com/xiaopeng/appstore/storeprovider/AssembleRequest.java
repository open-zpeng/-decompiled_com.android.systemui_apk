package com.xiaopeng.appstore.storeprovider;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes22.dex */
public abstract class AssembleRequest implements Parcelable {
    public static final int ASSEMBLE_ACTION_CANCEL = 400;
    public static final int ASSEMBLE_ACTION_ENQUEUE = 100;
    public static final int ASSEMBLE_ACTION_PAUSE = 200;
    public static final int ASSEMBLE_ACTION_RESUME = 300;
    protected final int mAction;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes22.dex */
    public @interface AssembleAction {
    }

    public static RequestContinuation enqueue(int resourceType, @NonNull String taskKey, String taskName) {
        return new RequestContinuation(resourceType, taskKey, taskName);
    }

    public static SimpleRequest pause(int resType, @NonNull String taskKey) {
        return new SimpleRequest(200, resType, taskKey);
    }

    public static SimpleRequest resume(int resType, @NonNull String taskKey) {
        return new SimpleRequest(300, resType, taskKey);
    }

    public static SimpleRequest cancel(int resType, @NonNull String taskKey) {
        return new SimpleRequest(400, resType, taskKey);
    }

    public AssembleRequest(int action) {
        this.mAction = action;
    }

    public String toString() {
        return "AssembleRequest{action=" + this.mAction + '}';
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AssembleRequest(Parcel in) {
        this.mAction = in.readInt();
    }

    public int getAction() {
        return this.mAction;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mAction);
    }
}
