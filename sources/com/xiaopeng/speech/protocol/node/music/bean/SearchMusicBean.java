package com.xiaopeng.speech.protocol.node.music.bean;

import android.os.Parcel;
import com.xiaopeng.speech.common.bean.BaseBean;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SearchMusicBean extends BaseBean {
    private boolean canDownload;
    private boolean canPlay;
    private String singers;
    private String songName;
    private boolean vip;

    public static SearchMusicBean fromJson(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return fromJson(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SearchMusicBean fromJson(JSONObject jsonObject) {
        SearchMusicBean musicBean = new SearchMusicBean();
        if (jsonObject != null) {
            musicBean.setSingers(jsonObject.optString("singers"));
            musicBean.setSongName(jsonObject.optString("songName"));
            musicBean.setVip(jsonObject.optBoolean("vip"));
            musicBean.setCanDownload(jsonObject.optBoolean("canDownload"));
            musicBean.setCanPlay(jsonObject.optBoolean("canPlay"));
        }
        return musicBean;
    }

    @Override // com.xiaopeng.speech.common.bean.BaseBean, android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.singers);
        dest.writeString(this.songName);
        dest.writeByte(this.vip ? (byte) 1 : (byte) 0);
        dest.writeByte(this.canDownload ? (byte) 1 : (byte) 0);
        dest.writeByte(this.canPlay ? (byte) 1 : (byte) 0);
    }

    public String getSingers() {
        return this.singers;
    }

    public void setSingers(String singers) {
        this.singers = singers;
    }

    public String getSongName() {
        return this.songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public boolean isVip() {
        return this.vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public boolean isCanDownload() {
        return this.canDownload;
    }

    public void setCanDownload(boolean canDownload) {
        this.canDownload = canDownload;
    }

    public boolean isCanPlay() {
        return this.canPlay;
    }

    public void setCanPlay(boolean canPlay) {
        this.canPlay = canPlay;
    }

    public String toString() {
        return "MusicBean{singers='" + this.singers + "', songName='" + this.songName + "', vip='" + this.vip + "', canDownload='" + this.canDownload + "', canPlay='" + this.canPlay + "'}";
    }
}
