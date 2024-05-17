package com.xiaopeng.speech.protocol.node.navi.bean;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class PoiLocation implements Parcelable {
    public static final Parcelable.Creator<PoiLocation> CREATOR = new Parcelable.Creator<PoiLocation>() { // from class: com.xiaopeng.speech.protocol.node.navi.bean.PoiLocation.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PoiLocation createFromParcel(Parcel source) {
            return new PoiLocation(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PoiLocation[] newArray(int size) {
            return new PoiLocation[size];
        }
    };
    private double latitude;
    private double longitude;

    public PoiLocation() {
    }

    public PoiLocation(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    public static PoiLocation fromJson(String fromJson) {
        PoiLocation poiLocation = new PoiLocation();
        try {
            JSONObject json = new JSONObject(fromJson);
            if (json.has("latitude")) {
                Double lat = Double.valueOf(json.optDouble("latitude"));
                if (lat != null) {
                    poiLocation.latitude = lat.doubleValue();
                } else {
                    poiLocation.latitude = 0.0d;
                }
            }
            if (json.has("longitude")) {
                Double lon = Double.valueOf(json.optDouble("longitude"));
                if (lon != null) {
                    poiLocation.longitude = lon.doubleValue();
                } else {
                    poiLocation.longitude = 0.0d;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return poiLocation;
    }

    public PoiLocation(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("latitude", this.latitude);
        json.put("longitude", this.longitude);
        return json;
    }

    public String toString() {
        return "PoiLocation{latitude=" + this.latitude + ", longitude=" + this.longitude + '}';
    }
}
