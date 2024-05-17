package com.xiaopeng.speech.protocol.node.record.bean;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class AsrResult implements Parcelable {
    public static final Parcelable.Creator<AsrResult> CREATOR = new Parcelable.Creator<AsrResult>() { // from class: com.xiaopeng.speech.protocol.node.record.bean.AsrResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AsrResult createFromParcel(Parcel in) {
            return new AsrResult(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AsrResult[] newArray(int size) {
            return new AsrResult[size];
        }
    };
    public String filePath;
    public String text;
    public String token;

    public static AsrResult fromJson(String json) {
        AsrResult asrResult = new AsrResult();
        try {
            JSONObject object = new JSONObject(json);
            asrResult.token = object.optString("token");
            asrResult.text = object.optString("text");
            asrResult.filePath = object.optString("filePath");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return asrResult;
    }

    public AsrResult() {
    }

    protected AsrResult(Parcel in) {
        this.text = in.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}
