package com.xiaopeng.speech.protocol.node.music.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.speech.common.bean.BaseBean;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SearchMusic extends BaseBean {
    public static final Parcelable.Creator<SearchMusic> CREATOR = new Parcelable.Creator<SearchMusic>() { // from class: com.xiaopeng.speech.protocol.node.music.bean.SearchMusic.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SearchMusic createFromParcel(Parcel source) {
            return new SearchMusic(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SearchMusic[] newArray(int size) {
            return new SearchMusic[size];
        }
    };
    private String age;
    private String album;
    private String artist;
    private String genre;
    private String language;
    private String mode;
    private String module;
    private String mood;
    private String region;
    private String theme;
    private String title;
    private String type;

    public SearchMusic() {
    }

    protected SearchMusic(Parcel in) {
        super(in);
        this.artist = in.readString();
        this.title = in.readString();
        this.album = in.readString();
        this.module = in.readString();
        this.genre = in.readString();
        this.age = in.readString();
        this.region = in.readString();
        this.mood = in.readString();
        this.theme = in.readString();
        this.language = in.readString();
        this.type = in.readString();
        this.mode = in.readString();
    }

    public static SearchMusic fromJson(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return fromJson(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SearchMusic fromJson(JSONObject jsonObject) {
        SearchMusic searchMusic = new SearchMusic();
        try {
            searchMusic.setAlbum(jsonObject.optString("album"));
            searchMusic.setArtist(jsonObject.optString("artist"));
            searchMusic.setTitle(jsonObject.optString(SpeechWidget.WIDGET_TITLE));
            searchMusic.setModule(jsonObject.optString("module"));
            searchMusic.setAge(jsonObject.optString("age"));
            searchMusic.setGenre(jsonObject.optString("gen"));
            searchMusic.setMood(jsonObject.optString("mood"));
            searchMusic.setRegion(jsonObject.optString("region"));
            searchMusic.setTheme(jsonObject.optString(ThemeManager.AttributeSet.THEME));
            searchMusic.setLanguage(jsonObject.optString("lan"));
            searchMusic.setType(jsonObject.optString("typ"));
            searchMusic.setMode(jsonObject.optString("mode"));
            if (TextUtils.isEmpty(searchMusic.getAlbum()) && TextUtils.isEmpty(searchMusic.getArtist()) && TextUtils.isEmpty(searchMusic.getTitle()) && TextUtils.isEmpty(searchMusic.getModule()) && TextUtils.isEmpty(searchMusic.getAge()) && TextUtils.isEmpty(searchMusic.getGenre()) && TextUtils.isEmpty(searchMusic.getMood()) && TextUtils.isEmpty(searchMusic.getRegion()) && TextUtils.isEmpty(searchMusic.getTheme()) && TextUtils.isEmpty(searchMusic.getLanguage()) && TextUtils.isEmpty(searchMusic.getType())) {
                throw new IllegalArgumentException("Album, artist, module, title and age are all empty string !");
            }
            return searchMusic;
        } catch (IllegalArgumentException e) {
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

    @Override // com.xiaopeng.speech.common.bean.BaseBean
    public String getTitle() {
        return this.title;
    }

    @Override // com.xiaopeng.speech.common.bean.BaseBean
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getGenre() {
        return this.genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAge() {
        return this.age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getMood() {
        return this.mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getTheme() {
        return this.theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SpeechWidget.WIDGET_TITLE, this.title);
            jsonObject.put("artist", this.artist);
            jsonObject.put("album", this.album);
            jsonObject.put("module", this.module);
            jsonObject.put("gen", this.genre);
            jsonObject.put("age", this.age);
            jsonObject.put("mood", this.mood);
            jsonObject.put("region", this.region);
            jsonObject.put(ThemeManager.AttributeSet.THEME, this.theme);
            jsonObject.put("lan", this.language);
            jsonObject.put("typ", this.type);
            jsonObject.put("mode", this.mode);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toString() {
        return "SearchMusic{artist='" + this.artist + "', title='" + this.title + "', album='" + this.album + "', module='" + this.module + "', genre='" + this.genre + "', language='" + this.language + "', age='" + this.age + "', region='" + this.region + "', mood='" + this.mood + "', theme='" + this.theme + "', type='" + this.type + "', mode='" + this.mode + "'}";
    }

    @Override // com.xiaopeng.speech.common.bean.BaseBean, android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // com.xiaopeng.speech.common.bean.BaseBean, android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.artist);
        dest.writeString(this.title);
        dest.writeString(this.album);
        dest.writeString(this.module);
        dest.writeString(this.genre);
        dest.writeString(this.language);
        dest.writeString(this.age);
        dest.writeString(this.region);
        dest.writeString(this.mood);
        dest.writeString(this.theme);
        dest.writeString(this.type);
        dest.writeString(this.mode);
    }
}
