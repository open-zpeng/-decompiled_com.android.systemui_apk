package com.xiaopeng.appstore.storeprovider;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
/* loaded from: classes22.dex */
public class EnqueueRequest extends AssembleRequest {
    public static final Parcelable.Creator<EnqueueRequest> CREATOR = new Parcelable.Creator<EnqueueRequest>() { // from class: com.xiaopeng.appstore.storeprovider.EnqueueRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EnqueueRequest createFromParcel(Parcel in) {
            return new EnqueueRequest(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EnqueueRequest[] newArray(int size) {
            return new EnqueueRequest[size];
        }
    };
    @NonNull
    private final RequestContinuation mContinuation;

    @NonNull
    public RequestContinuation getContinuation() {
        return this.mContinuation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public EnqueueRequest(@NonNull RequestContinuation continuation) {
        super(100);
        this.mContinuation = continuation;
    }

    @Override // com.xiaopeng.appstore.storeprovider.AssembleRequest
    public String toString() {
        return "EnqueueRequest{contin='" + this.mContinuation + '}';
    }

    protected EnqueueRequest(Parcel in) {
        super(in);
        this.mContinuation = (RequestContinuation) in.readParcelable(RequestContinuation.class.getClassLoader());
    }

    @Override // com.xiaopeng.appstore.storeprovider.AssembleRequest, android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.mContinuation, flags);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}
