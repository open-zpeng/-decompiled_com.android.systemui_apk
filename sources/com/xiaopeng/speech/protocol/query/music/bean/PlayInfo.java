package com.xiaopeng.speech.protocol.query.music.bean;

import com.xiaopeng.speech.common.util.DontProguardClass;
@DontProguardClass
/* loaded from: classes23.dex */
public class PlayInfo {
    public static final int ERROR_CURRENT_NOT_PLAY = 20001;
    public static final int ERROR_SUPPORT_PLAY_TYPE_GET = 20002;
    public static final int TYPE_AUDIO_BOOK = 2;
    public static final int TYPE_BT_FM = 5;
    public static final int TYPE_DAB = 7;
    public static final int TYPE_FM = 3;
    public static final int TYPE_NETWORK_FM = 4;
    public static final int TYPE_SONG = 1;
    public static final int TYPE_SPOTIFY = 8;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_USB_MUSIC = 6;
    private String album;
    private String artist;
    private int error;
    private long playTime;
    private int playType;
    private long remainingTime;
    private String songId;
    private String title;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getPlayTime() {
        return this.playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public long getRemainingTime() {
        return this.remainingTime;
    }

    public void setRemainingTime(long remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getPlayType() {
        return this.playType;
    }

    public void setPlayType(int playType) {
        this.playType = playType;
    }

    public int getError() {
        return this.error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getSongId() {
        return this.songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String toString() {
        return "PlayInfo{title='" + this.title + "', artist='" + this.artist + "', album='" + this.album + "', playTime=" + this.playTime + ", remainingTime=" + this.remainingTime + ", playType=" + this.playType + ", error=" + this.error + ", songId=" + this.songId + '}';
    }
}
