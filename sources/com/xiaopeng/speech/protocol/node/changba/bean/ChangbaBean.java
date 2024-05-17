package com.xiaopeng.speech.protocol.node.changba.bean;

import com.xiaopeng.speech.common.bean.BaseBean;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class ChangbaBean extends BaseBean {
    private String artist;
    private String song;

    public static ChangbaBean fromJson(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            ChangbaBean changbaBean = new ChangbaBean();
            changbaBean.setArtist(jsonObject.optString("artist"));
            changbaBean.setSong(jsonObject.optString("song"));
            return changbaBean;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSong() {
        return this.song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String toString() {
        return "ChangbaBean{artist='" + this.artist + "', song='" + this.song + "'}";
    }
}
