package com.xiaopeng.speech.protocol.node.music;

import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.protocol.SpeechUtils;
import com.xiaopeng.speech.protocol.event.MusicEvent;
import com.xiaopeng.speech.protocol.node.context.AbsContextListener;
import com.xiaopeng.speech.protocol.node.context.ContextNode;
import com.xiaopeng.speech.protocol.node.music.bean.CollectHistoryMusic;
import com.xiaopeng.speech.protocol.node.music.bean.SearchMusic;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import java.math.BigDecimal;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class MusicNode extends SpeechNode<MusicListener> {
    private static final String TAG = "MusicNode";

    public MusicNode() {
        SpeechUtils.subscribe(ContextNode.class, new AbsContextListener() { // from class: com.xiaopeng.speech.protocol.node.music.MusicNode.1
            @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
            public void onWidgetText(String data) {
                JSONObject extraJson;
                super.onWidgetText(data);
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String function = jsonObject.optString("function");
                    if ("music_list_play".equals(function) && (extraJson = jsonObject.optJSONObject(SpeechWidget.WIDGET_EXTRA)) != null) {
                        MusicNode.this.onMusicListPlay("command://music.list.play", extraJson.optString("param"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.control.play")
    public void onPlay(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onPlay();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.control.loop.single")
    public void onPlayLoopSingle(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onPlayMode("single");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.control.loop.all")
    public void onPlayLoopAll(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onPlayMode("order");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.control.random")
    public void onPlayLoopRandom(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onPlayMode("random");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "native://music.search")
    public void onSearch(String event, String data) {
        SearchMusic searchMusic = SearchMusic.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onSearch(event, searchMusic);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.control.pause")
    public void onPause(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onPause();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.control.resume")
    public void onResume(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onResume();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.control.prev")
    public void onPrev(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onPrev();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.control.next")
    public void onNext(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onNext();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.control.stop")
    public void onStop(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onStop();
            }
        }
    }

    protected void onExit(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onExit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.control.bluetooth.play.random")
    public void onPlayBlueTooth(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onPlayBluetooth();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "native://music.playmode.support")
    public void onSupportPlayModeChange(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onSupportPlayModeChange(event);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.audiobook.play")
    public void onAudioBookPlay(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onAudioBookPlay(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.list.play")
    public void onMusicListPlay(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (SpeechUtils.isJson(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                if (jsonObject.has("from")) {
                    String from = jsonObject.optString("from");
                    if ("dui_xp".equals(from)) {
                        LogUtils.i(TAG, "is from dui_xp");
                        return;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for (Object obj : listenerList) {
            ((MusicListener) obj).onMusicListPlay(data);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.1212.novel.play")
    public void onTwelveNovelPlay(String event, String data) {
        String audioBookBean = null;
        try {
            JSONObject jsonObject = new JSONObject(data);
            audioBookBean = jsonObject.optString("param");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onTwelveNovelPlay(audioBookBean);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.control.collect")
    public void onControlCollect(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onControlCollect();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.audiobook.subscribe")
    public void onAudioBookSubscribe(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onAudioBookSubscribe();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.soundeffect.stereo")
    public void onSoundEffectStereo(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onSoundEffectStereo();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.soundeffect.live")
    public void onSoundEffectLive(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onSoundEffectLive();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.soundeffect.vocal")
    public void onSoundEffectVocal(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onSoundEffectVocal();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.soundeffect.superbass")
    public void onSoundEffectSuperbass(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onSoundEffectSuperbass();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.control.collect.cancel")
    public void onDelCollect(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onDelCollect();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.control.collect.play")
    public void onPlayCollect(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        CollectHistoryMusic music = CollectHistoryMusic.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onPlayCollect(music);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.control.sim")
    public void onPlaySimilar(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onPlaySimilar();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.control.sim.cancel")
    public void onCancelPlaySimilar(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onCancelPlaySimilar();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "command://music.control.playlist.history.play")
    public void onPlayHistoryList(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        CollectHistoryMusic music = CollectHistoryMusic.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onPlayHistoryList(music);
            }
        }
    }

    public void startMusicSearch() {
        SpeechClient.instance().getWakeupEngine().startDialogFrom("music");
    }

    @SpeechAnnotation(event = "command://music.forward")
    public void onMusicForward(String event, String data) {
        try {
            Object[] listenerList = this.mListenerList.collectCallbacks();
            JSONObject json = new JSONObject(data);
            int second = json.optInt(VuiConstants.ELEMENT_VALUE);
            if (listenerList != null) {
                for (Object obj : listenerList) {
                    ((MusicListener) obj).onMusicForward(second);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SpeechAnnotation(event = "command://music.backward")
    public void onMusicBackward(String event, String data) {
        try {
            Object[] listenerList = this.mListenerList.collectCallbacks();
            JSONObject json = new JSONObject(data);
            int second = json.optInt(VuiConstants.ELEMENT_VALUE);
            if (listenerList != null) {
                for (Object obj : listenerList) {
                    ((MusicListener) obj).onMusicBackward(second);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SpeechAnnotation(event = "command://music.settime")
    public void onMusicSettime(String event, String data) {
        try {
            Object[] listenerList = this.mListenerList.collectCallbacks();
            JSONObject json = new JSONObject(data);
            int second = json.optInt(VuiConstants.ELEMENT_VALUE);
            if (listenerList != null) {
                for (Object obj : listenerList) {
                    ((MusicListener) obj).onMusicSettime(second);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SpeechAnnotation(event = "command://music.speed.up")
    public void onMusicSpeedUp(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onMusicSpeedUp();
            }
        }
    }

    @SpeechAnnotation(event = "command://music.speed.down")
    public void onMusicSpeedDown(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onMusicSpeedDown();
            }
        }
    }

    @SpeechAnnotation(event = "command://music.speed.set")
    public void onMusicSpeedSet(String event, String data) {
        try {
            Object[] listenerList = this.mListenerList.collectCallbacks();
            JSONObject json = new JSONObject(data);
            float speed = BigDecimal.valueOf(json.optDouble(VuiConstants.ELEMENT_VALUE)).floatValue();
            if (listenerList != null) {
                for (Object obj : listenerList) {
                    ((MusicListener) obj).onMusicSpeedSet(speed);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SpeechAnnotation(event = "command://music.news.play")
    public void onMusicNewsPlay(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onMusicNewsPlay();
            }
        }
    }

    @SpeechAnnotation(event = "command://music.dailyrec.play")
    public void onMusicDailyrecPlay(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onMusicDailyrecPlay();
            }
        }
    }

    @SpeechAnnotation(event = "command://music.control.play.usb")
    public void onPlayUsb(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onPlayUsb();
            }
        }
    }

    @SpeechAnnotation(event = MusicEvent.PLAY_SPOTIFY)
    public void onPlaySpotify(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onPlaySpotify();
            }
        }
    }

    @SpeechAnnotation(event = MusicEvent.PLAY_LOOP_CLOSE)
    public void onPlayLoopClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onPlayLoopClose();
            }
        }
    }

    @SpeechAnnotation(event = MusicEvent.PLAY_RANDOM_CLOSE)
    public void onPlayRandomClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MusicListener) obj).onPlayRandomClose();
            }
        }
    }
}
