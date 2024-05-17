package com.xiaopeng.speech.protocol.query.music;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.QueryMusicEvent;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class MusicQuery extends SpeechQuery<IMusicQueryCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = "music.info.query")
    public String getPlayInfo(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).getPlayInfo();
    }

    protected String getPlaylistHistory(String event, String data) {
        int count = 1;
        try {
            JSONObject jsonObject = new JSONObject(data);
            count = jsonObject.optInt("count", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((IMusicQueryCaller) this.mQueryCaller).getHistoryPlayInfo(count);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = "music.info.query.title")
    public String getInfoTite(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).getPlayTitle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = "music.info.query.artist")
    public String getInfoArtist(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).getPlayArtist();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = "music.info.query.album")
    public String getInfoAlbum(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).getPlayAlbum();
    }

    protected String getInfoLyric(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).getPlayLyric();
    }

    protected String getInfoCategory(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).getPlayCategory();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = "music.playtype")
    public int getPlayType(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).getPlayType();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = "music.is.playing")
    public boolean isPlaying(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).isPlaying();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = "music.has.bluetooth.musiclist")
    public boolean hasBluetoothMusicList(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).hasBluetoothMusicList();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = "music.is.history.empty")
    public boolean isHistoryEmpty(String event, String data) {
        int type = 0;
        try {
            JSONObject jsonObject = new JSONObject(data);
            type = jsonObject.optInt(VuiConstants.ELEMENT_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((IMusicQueryCaller) this.mQueryCaller).isHistoryEmpty(type);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = "music.is.play.similar")
    public boolean isPlaySimilar(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).isPlaySimilar();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = "music.is.collect.empty")
    public boolean isCollectListEmpty(String event, String data) {
        int type = 0;
        try {
            JSONObject jsonObject = new JSONObject(data);
            type = jsonObject.optInt(VuiConstants.ELEMENT_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((IMusicQueryCaller) this.mQueryCaller).isCollectListEmpty(type);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = "music.is.can.collected")
    public boolean isCanCollected(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).isCanCollected();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = "music.is.bt.connected")
    public boolean isBtConnected(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).isBtConnected();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = "music.is.kugou.authed")
    public boolean isKuGouAuthed(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).isKuGouAuthed();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = "music.get.usb.state")
    public int getUsbState(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).getUsbState();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMusicEvent.MUSIC_ACCOUNT_LOGIN)
    public boolean isMusicAccountLogin(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).isMusicAccountLogin();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMusicEvent.IS_CAN_OPEN_QUALITY_PAGE)
    public boolean isQualityPageOpend(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).isQualityPageOpend();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMusicEvent.IS_XIMALAYA_ACCOUNT_LOGIN)
    public boolean isXimalayaAccountLogin(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).isXimalayaAccountLogin();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMusicEvent.IS_PLAY_COLLECT)
    public boolean isPlayCollect(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).isPlayCollect(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMusicEvent.IS_PLAY_HISTORY)
    public boolean isPlayHistory(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).isPlayHistory(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMusicEvent.GET_SEARCH_RESULT)
    public String getSearchResult(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).getSearchResult(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMusicEvent.IS_PLAY_PODCAST)
    public boolean isPlayPodcast(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).isPlayPodcast(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMusicEvent.IS_SUPPORT_BT_PLAY)
    public boolean isSupportBtPlay(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).isSupportBtPlay();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMusicEvent.IS_SUPPORT_SPOTIFY_PLAY)
    public boolean isSupportSpotifyPlay(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).isSupportSpotifyPlay();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMusicEvent.GET_SPEED)
    public double getPlaySpeed(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).getPlaySpeed();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMusicEvent.IS_SUPPORT_SPEED)
    public boolean isSupportSpeed(String event, String data) {
        int value = 0;
        try {
            JSONObject jsonObject = new JSONObject(data);
            value = jsonObject.optInt(VuiConstants.ELEMENT_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((IMusicQueryCaller) this.mQueryCaller).isSupportSpeed(value);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMusicEvent.IS_SUPPORT_RADIO_PLAY)
    public boolean isSupportRadioPlay(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).isSupportRadioPlay();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMusicEvent.IS_SUPPORT_SETTIME)
    public boolean isSupportSettime(String event, String data) {
        return ((IMusicQueryCaller) this.mQueryCaller).isSupportSettime();
    }
}
