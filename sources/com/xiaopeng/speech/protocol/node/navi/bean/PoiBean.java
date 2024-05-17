package com.xiaopeng.speech.protocol.node.navi.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.xiaopeng.speech.common.util.DontProguardClass;
import org.json.JSONException;
import org.json.JSONObject;
@DontProguardClass
/* loaded from: classes23.dex */
public class PoiBean implements Parcelable {
    protected static final Parcelable.Creator<PoiBean> CREATOR = new Parcelable.Creator<PoiBean>() { // from class: com.xiaopeng.speech.protocol.node.navi.bean.PoiBean.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PoiBean createFromParcel(Parcel source) {
            return new PoiBean(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PoiBean[] newArray(int size) {
            return new PoiBean[size];
        }
    };
    private String address;
    private String blCustomName;
    private int category;
    private String categoryExtra;
    private String cityName;
    private String displayDistance;
    private long distance;
    private String dst_name;
    private PoiLocation entrLocation;
    private PoiLocation exitLocation;
    private double latitude;
    private double longitude;
    private String name;
    private double naviLat;
    private double naviLon;
    private PoiExtraBean poiExtra;
    private String poiId;
    private int scenario;
    private String source;
    private String telephone;
    private String typeCode;

    public PoiExtraBean getPoiExtra() {
        return this.poiExtra;
    }

    public void setPoiExtra(PoiExtraBean poiExtra) {
        this.poiExtra = poiExtra;
    }

    public PoiBean() {
        this.entrLocation = new PoiLocation(0.0d, 0.0d);
        this.exitLocation = new PoiLocation(0.0d, 0.0d);
    }

    public static PoiBean fromJson(String fromJson) {
        try {
            PoiBean poiBean = new PoiBean();
            try {
                JSONObject json = new JSONObject(fromJson);
                if (json.has("poiId")) {
                    poiBean.poiId = json.optString("poiId");
                }
                if (json.has("name")) {
                    poiBean.name = json.optString("name");
                }
                if (json.has("dst_name")) {
                    poiBean.dst_name = json.optString("dst_name");
                }
                if (json.has("latitude")) {
                    poiBean.latitude = json.optDouble("latitude", 0.0d);
                }
                if (json.has("longitude")) {
                    poiBean.longitude = json.optDouble("longitude", 0.0d);
                }
                if (json.has("address")) {
                    poiBean.address = json.optString("address");
                }
                if (json.has("distance")) {
                    poiBean.distance = json.optLong("distance");
                }
                if (json.has("displayDistance")) {
                    poiBean.displayDistance = json.optString("displayDistance");
                }
                if (json.has("tel")) {
                    poiBean.telephone = json.optString("tel");
                }
                if (json.has("category")) {
                    poiBean.category = json.optInt("category");
                }
                if (json.has("categoryExtra")) {
                    poiBean.categoryExtra = json.optString("categoryExtra");
                }
                if (json.has("naviLon")) {
                    poiBean.naviLon = json.optDouble("naviLon", 0.0d);
                }
                if (json.has("naviLat")) {
                    poiBean.naviLat = json.optDouble("naviLat", 0.0d);
                }
                if (json.has("typeCode")) {
                    poiBean.typeCode = json.optString("typeCode");
                }
                if (json.has("source")) {
                    poiBean.source = json.optString("source");
                }
                if (json.has("scenario")) {
                    poiBean.scenario = json.optInt("scenario");
                }
                if (json.has("entrLocation")) {
                    poiBean.entrLocation = PoiLocation.fromJson(json.optString("entrLocation"));
                }
                if (json.has("exitLocation")) {
                    poiBean.exitLocation = PoiLocation.fromJson(json.optString("exitLocation"));
                }
                if (json.has("poiExtra")) {
                    poiBean.poiExtra = PoiExtraBean.fromJson(json.optString("poiExtra"));
                }
                if (json.has("blCustomName")) {
                    poiBean.blCustomName = json.optString("blCustomName");
                }
                return poiBean;
            } catch (JSONException e) {
                e = e;
                e.printStackTrace();
                return null;
            }
        } catch (JSONException e2) {
            e = e2;
        }
    }

    public PoiBean(Parcel in) {
        this.entrLocation = new PoiLocation(0.0d, 0.0d);
        this.exitLocation = new PoiLocation(0.0d, 0.0d);
        this.name = in.readString();
        this.dst_name = in.readString();
        this.poiId = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.address = in.readString();
        this.distance = in.readLong();
        this.telephone = in.readString();
        this.displayDistance = in.readString();
        this.cityName = in.readString();
        this.category = in.readInt();
        this.categoryExtra = in.readString();
        this.naviLon = in.readDouble();
        this.naviLat = in.readDouble();
        this.typeCode = in.readString();
        this.source = in.readString();
        this.scenario = in.readInt();
        this.entrLocation = (PoiLocation) in.readParcelable(PoiLocation.class.getClassLoader());
        this.exitLocation = (PoiLocation) in.readParcelable(PoiLocation.class.getClassLoader());
        this.poiExtra = (PoiExtraBean) in.readParcelable(PoiExtraBean.class.getClassLoader());
        this.blCustomName = in.readString();
    }

    public String getCityName() {
        return String.valueOf(this.cityName);
    }

    public int getCategory() {
        return this.category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getCategoryExtra() {
        return this.categoryExtra;
    }

    public void setCategoryExtra(String categorys) {
        this.categoryExtra = categorys;
    }

    public String getTypeCode() {
        return this.typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public PoiLocation getEntrLocation() {
        return this.entrLocation;
    }

    public void setEntrLocation(PoiLocation entrLocation) {
        this.entrLocation = entrLocation;
    }

    public PoiLocation getExitLocation() {
        return this.exitLocation;
    }

    public void setExitLocation(PoiLocation exitLocation) {
        this.exitLocation = exitLocation;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getScenario() {
        return this.scenario;
    }

    public void setScenario(int scenario) {
        this.scenario = scenario;
    }

    public String getBlCustomName() {
        return this.blCustomName;
    }

    public void setBlCustomName(String blCustomName) {
        this.blCustomName = blCustomName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDisplayDistance() {
        return String.valueOf(this.displayDistance);
    }

    public void setDisplayDistance(String display) {
        this.displayDistance = display;
    }

    public String getPoiId() {
        return String.valueOf(this.poiId);
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
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

    public String getAddress() {
        return String.valueOf(this.address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getDistance() {
        return this.distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public String getTelephone() {
        return String.valueOf(this.telephone);
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getName() {
        return String.valueOf(this.name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDstName() {
        return this.dst_name;
    }

    public void setDstName(String dst_name) {
        this.dst_name = dst_name;
    }

    public double getNaviLon() {
        return this.naviLon;
    }

    public void setNaviLon(double naviLon) {
        this.naviLon = naviLon;
    }

    public double getNaviLat() {
        return this.naviLat;
    }

    public void setNaviLat(double naviLat) {
        this.naviLat = naviLat;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", getName());
        json.put("dst_name", getDstName());
        json.put("latitude", this.latitude);
        json.put("longitude", this.longitude);
        json.put("address", this.address);
        json.put("distance", this.distance);
        json.put("displayDistance", this.displayDistance);
        json.put("tel", this.telephone);
        json.put("category", this.category);
        json.put("categoryExtra", this.categoryExtra);
        json.put("poiId", this.poiId);
        json.put("naviLon", this.naviLon);
        json.put("naviLat", this.naviLat);
        json.put("typeCode", this.typeCode);
        json.put("source", this.source);
        json.put("scenario", this.scenario);
        PoiLocation poiLocation = this.entrLocation;
        if (poiLocation != null) {
            json.put("entrLocation", poiLocation.toJson());
        }
        PoiLocation poiLocation2 = this.exitLocation;
        if (poiLocation2 != null) {
            json.put("exitLocation", poiLocation2.toJson());
        }
        PoiExtraBean poiExtraBean = this.poiExtra;
        if (poiExtraBean != null) {
            json.put("poiExtra", poiExtraBean.toJson());
        }
        json.put("blCustomName", this.blCustomName);
        return json;
    }

    public String toString() {
        return "PoiBean{name='" + this.name + "', dst_name='" + this.dst_name + "', poiId='" + this.poiId + "', latitude=" + this.latitude + ", longitude=" + this.longitude + ", address='" + this.address + "', distance=" + this.distance + ", categoryExtra='" + this.categoryExtra + "', telephone='" + this.telephone + "', displayDistance='" + this.displayDistance + "', cityName='" + this.cityName + "', naviLon=" + this.naviLon + ", naviLat=" + this.naviLat + ", category=" + this.category + ", typeCode='" + this.typeCode + "', source='" + this.source + "', scenario='" + this.scenario + "', entrLocation=" + this.entrLocation + ", exitLocation=" + this.exitLocation + ", poiExtra=" + this.poiExtra + ", blCustomName=" + this.blCustomName + '}';
    }

    public static PoiBean gcj02_To_Bd09(double lat, double lng) {
        double z = Math.sqrt((lng * lng) + (lat * lat)) + (Math.sin(((3.141592653589793d * 3000.0d) / 180.0d) * lat) * 2.0E-5d);
        double theta = Math.atan2(lat, lng) + (Math.cos(((3000.0d * 3.141592653589793d) / 180.0d) * lng) * 3.0E-6d);
        double bd_lon = (Math.cos(theta) * z) + 0.0065d;
        double bd_lat = (Math.sin(theta) * z) + 0.006d;
        PoiBean b = new PoiBean();
        b.setLatitude(bd_lat);
        b.setLongitude(bd_lon);
        return b;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.dst_name);
        dest.writeString(this.poiId);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.address);
        dest.writeLong(this.distance);
        dest.writeString(this.telephone);
        dest.writeString(this.displayDistance);
        dest.writeString(this.cityName);
        dest.writeInt(this.category);
        dest.writeString(this.categoryExtra);
        dest.writeDouble(this.naviLon);
        dest.writeDouble(this.naviLat);
        dest.writeString(this.typeCode);
        dest.writeString(this.source);
        dest.writeInt(this.scenario);
        dest.writeParcelable(this.entrLocation, 0);
        dest.writeParcelable(this.exitLocation, 0);
        dest.writeString(this.blCustomName);
    }
}
