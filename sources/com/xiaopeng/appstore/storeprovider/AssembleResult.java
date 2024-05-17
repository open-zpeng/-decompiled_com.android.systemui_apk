package com.xiaopeng.appstore.storeprovider;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes22.dex */
public class AssembleResult implements Parcelable {
    public static final Parcelable.Creator<AssembleResult> CREATOR = new Parcelable.Creator<AssembleResult>() { // from class: com.xiaopeng.appstore.storeprovider.AssembleResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AssembleResult createFromParcel(Parcel in) {
            return new AssembleResult(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AssembleResult[] newArray(int size) {
            return new AssembleResult[size];
        }
    };
    public static final int ERROR_EXECUTE = 10002;
    public static final int ERROR_FORCE_UPDATE = 201;
    public static final int ERROR_HTTP_WRONG = 301;
    public static final int ERROR_INVALID_ARGUMENT = 10001;
    public static final int ERROR_NOT_DONE_AGREEMENT = 105;
    public static final int ERROR_NOT_LOGIN = 101;
    public static final int ERROR_REPEAT_TASK = 401;
    public static final int ERROR_SUSPEND = 201;
    public static final int SUCCESS_CODE = 1;
    private final int mCode;
    private final String mDesc;

    public static AssembleResult createSuccess() {
        return new AssembleResult(1, "Success");
    }

    public static AssembleResult createFail(int code, String desc) {
        return new AssembleResult(code, desc);
    }

    private AssembleResult(int code, String desc) {
        this.mCode = code;
        this.mDesc = desc;
    }

    protected AssembleResult(Parcel in) {
        this.mCode = in.readInt();
        this.mDesc = in.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mCode);
        dest.writeString(this.mDesc);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean isSuccessful() {
        return this.mCode == 1;
    }

    public int getCode() {
        return this.mCode;
    }

    public String getDesc() {
        return this.mDesc;
    }

    public String toString() {
        return "AssembleResult{code=" + this.mCode + ", desc='" + this.mDesc + "'}";
    }
}
