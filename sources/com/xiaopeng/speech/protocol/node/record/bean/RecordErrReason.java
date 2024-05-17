package com.xiaopeng.speech.protocol.node.record.bean;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class RecordErrReason implements Parcelable {
    public static final Parcelable.Creator<RecordErrReason> CREATOR = new Parcelable.Creator<RecordErrReason>() { // from class: com.xiaopeng.speech.protocol.node.record.bean.RecordErrReason.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RecordErrReason createFromParcel(Parcel in) {
            return new RecordErrReason(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RecordErrReason[] newArray(int size) {
            return new RecordErrReason[size];
        }
    };
    public String msg;

    public static RecordErrReason fromJson(String json) {
        RecordErrReason asrResult = new RecordErrReason();
        try {
            JSONObject object = new JSONObject(json);
            asrResult.msg = object.optString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return asrResult;
    }

    public RecordErrReason() {
    }

    protected RecordErrReason(Parcel in) {
        this.msg = in.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.msg);
    }
}
