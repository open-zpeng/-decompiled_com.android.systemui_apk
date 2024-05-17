package com.xiaopeng.speech.protocol.node.music.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.xiaopeng.speech.common.bean.BaseBean;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class MusicBean extends BaseBean {
    public static final Parcelable.Creator<MusicBean> CREATOR = new Parcelable.Creator<MusicBean>() { // from class: com.xiaopeng.speech.protocol.node.music.bean.MusicBean.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MusicBean createFromParcel(Parcel source) {
            return new MusicBean(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MusicBean[] newArray(int size) {
            return new MusicBean[size];
        }
    };
    private String extData;
    private String keyWord;
    private int listed;
    private String metaDataList;
    private String metadata;
    private String page;
    private String params;
    private int searchType;
    private String source;
    private String tracks;

    public MusicBean() {
    }

    protected MusicBean(Parcel in) {
        super(in);
        this.params = in.readString();
        this.tracks = in.readString();
        this.page = in.readString();
        this.metadata = in.readString();
        this.keyWord = in.readString();
        this.source = in.readString();
        this.searchType = in.readInt();
        this.listed = in.readInt();
        this.extData = in.readString();
        this.metaDataList = in.readString();
    }

    public static MusicBean fromJson(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has("param")) {
                jsonObject = new JSONObject(jsonObject.optString("param"));
            } else if (jsonObject.has("from")) {
                String from = jsonObject.optString("from");
                if ("dui_xp".equals(from)) {
                    return null;
                }
            }
            return fromJson(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MusicBean fromJson(JSONObject jsonObject) {
        MusicBean musicBean = new MusicBean();
        if (jsonObject != null) {
            musicBean.setParams(jsonObject.optString("params"));
            musicBean.setTracks(jsonObject.optString("tracks"));
            musicBean.setPage(jsonObject.optString("page"));
            musicBean.setMetadata(jsonObject.optString("metadata"));
            musicBean.setSource(jsonObject.optString("source"));
            musicBean.setKeyWord(jsonObject.optString("keyWord"));
            musicBean.setSearchType(jsonObject.optInt("searchType"));
            musicBean.setListed(jsonObject.optInt("listed"));
            musicBean.setExtData(jsonObject.optString("extData"));
            musicBean.setMetaDataList(jsonObject.optString("metaDataList"));
        }
        return musicBean;
    }

    @Override // com.xiaopeng.speech.common.bean.BaseBean, android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.params);
        dest.writeString(this.tracks);
        dest.writeString(this.page);
        dest.writeString(this.metadata);
        dest.writeString(this.keyWord);
        dest.writeString(this.source);
        dest.writeInt(this.searchType);
        dest.writeInt(this.listed);
        dest.writeString(this.extData);
        dest.writeString(this.metaDataList);
    }

    public String getParams() {
        return this.params;
    }

    public String getTracks() {
        return this.tracks;
    }

    public String getPage() {
        return this.page;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public void setTracks(String tracks) {
        this.tracks = tracks;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getKeyWord() {
        return this.keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public int getSearchType() {
        return this.searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getListed() {
        return this.listed;
    }

    public void setListed(int listed) {
        this.listed = listed;
    }

    public String getExtData() {
        return this.extData;
    }

    public void setExtData(String extData) {
        this.extData = extData;
    }

    public String getMetaDataList() {
        return this.metaDataList;
    }

    public void setMetaDataList(String metaDataList) {
        this.metaDataList = metaDataList;
    }

    public String toString() {
        return "MusicBean{params='" + this.params + "', tracks='" + this.tracks + "', page='" + this.page + "', metadata='" + this.metadata + "', keyWord='" + this.keyWord + "', source='" + this.source + "', searchType=" + this.searchType + ", listed=" + this.listed + ", extData=" + this.extData + '}';
    }
}
