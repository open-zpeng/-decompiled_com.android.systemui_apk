package com.xiaopeng.speech.protocol.query.music;

import com.xiaopeng.speech.IQueryCaller;
/* loaded from: classes23.dex */
public interface IMusicQueryCaller extends IQueryCaller {
    String getHistoryPlayInfo(int i);

    String getPlayAlbum();

    String getPlayArtist();

    String getPlayCategory();

    String getPlayInfo();

    String getPlayLyric();

    String getPlayTitle();

    int getPlayType();

    int getUsbState();

    boolean hasBluetoothMusicList();

    boolean isBtConnected();

    boolean isCanCollected();

    boolean isCollectListEmpty(int i);

    boolean isHistoryEmpty(int i);

    boolean isKuGouAuthed();

    boolean isPlaySimilar();

    boolean isPlaying();

    default boolean isMusicAccountLogin() {
        return false;
    }

    default boolean isQualityPageOpend() {
        return false;
    }

    default boolean isXimalayaAccountLogin() {
        return false;
    }

    default boolean isPlayCollect(String data) {
        return false;
    }

    default boolean isPlayHistory(String data) {
        return false;
    }

    default String getSearchResult(String data) {
        return null;
    }

    default boolean isPlayPodcast(String data) {
        return false;
    }

    default boolean isSupportBtPlay() {
        return false;
    }

    default boolean isSupportSpotifyPlay() {
        return false;
    }

    default double getPlaySpeed() {
        return 0.0d;
    }

    default boolean isSupportSpeed(double value) {
        return false;
    }

    default boolean isSupportRadioPlay() {
        return false;
    }

    default boolean isSupportSettime() {
        return false;
    }
}
