package com.xiaopeng.appstore.storeprovider;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
/* loaded from: classes22.dex */
public class SimpleRequest extends AssembleRequest {
    public static final Parcelable.Creator<SimpleRequest> CREATOR = new Parcelable.Creator<SimpleRequest>() { // from class: com.xiaopeng.appstore.storeprovider.SimpleRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SimpleRequest createFromParcel(Parcel in) {
            return new SimpleRequest(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SimpleRequest[] newArray(int size) {
            return new SimpleRequest[size];
        }
    };
    private final String mKey;
    private final int mResType;

    public SimpleRequest(int action, int resType, @NonNull String key) {
        super(action);
        this.mResType = resType;
        this.mKey = key;
    }

    @Override // com.xiaopeng.appstore.storeprovider.AssembleRequest
    public String toString() {
        return "SimpleRequest{action=" + this.mAction + ", key='" + this.mKey + "', resType=" + this.mResType + '}';
    }

    @NonNull
    public String getKey() {
        return this.mKey;
    }

    public int getResType() {
        return this.mResType;
    }

    protected SimpleRequest(Parcel in) {
        super(in);
        this.mResType = in.readInt();
        this.mKey = in.readString();
    }

    @Override // com.xiaopeng.appstore.storeprovider.AssembleRequest, android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mResType);
        dest.writeString(this.mKey);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}
