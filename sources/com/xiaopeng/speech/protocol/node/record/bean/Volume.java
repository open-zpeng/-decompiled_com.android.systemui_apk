package com.xiaopeng.speech.protocol.node.record.bean;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class Volume implements Parcelable {
    public static final Parcelable.Creator<Volume> CREATOR = new Parcelable.Creator<Volume>() { // from class: com.xiaopeng.speech.protocol.node.record.bean.Volume.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Volume createFromParcel(Parcel in) {
            return new Volume(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Volume[] newArray(int size) {
            return new Volume[size];
        }
    };
    public double volume;

    public static Volume fromJson(String json) {
        Volume volume = new Volume();
        try {
            JSONObject object = new JSONObject(json);
            volume.volume = object.optDouble("volume");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return volume;
    }

    public Volume() {
    }

    protected Volume(Parcel in) {
        this.volume = in.readDouble();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.volume);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}
