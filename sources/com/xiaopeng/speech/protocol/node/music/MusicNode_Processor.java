package com.xiaopeng.speech.protocol.node.music;

import com.alibaba.fastjson.parser.JSONLexer;
import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.MusicEvent;
import kotlin.text.Typography;
/* loaded from: classes23.dex */
public class MusicNode_Processor implements ICommandProcessor {
    private MusicNode mTarget;

    public MusicNode_Processor(MusicNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -2013000544:
                if (event.equals("command://music.control.loop.all")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -2002174621:
                if (event.equals("command://music.speed.set")) {
                    c = 31;
                    break;
                }
                c = 65535;
                break;
            case -1938308159:
                if (event.equals("command://music.speed.down")) {
                    c = 30;
                    break;
                }
                c = 65535;
                break;
            case -1829687228:
                if (event.equals("native://music.playmode.support")) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case -1756385993:
                if (event.equals(MusicEvent.PLAY_LOOP_CLOSE)) {
                    c = Typography.dollar;
                    break;
                }
                c = 65535;
                break;
            case -1728394409:
                if (event.equals("command://music.settime")) {
                    c = 28;
                    break;
                }
                c = 65535;
                break;
            case -1627989588:
                if (event.equals("command://music.list.play")) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case -1425904452:
                if (event.equals("command://music.control.next")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -1425838851:
                if (event.equals("command://music.control.play")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1425832964:
                if (event.equals("command://music.control.prev")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -1425741365:
                if (event.equals("command://music.control.stop")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -1353272389:
                if (event.equals("command://music.backward")) {
                    c = 27;
                    break;
                }
                c = 65535;
                break;
            case -1311512198:
                if (event.equals("command://music.speed.up")) {
                    c = 29;
                    break;
                }
                c = 65535;
                break;
            case -1292964230:
                if (event.equals("command://music.control.sim.cancel")) {
                    c = 24;
                    break;
                }
                c = 65535;
                break;
            case -1251639987:
                if (event.equals("command://music.control.pause")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -1236424121:
                if (event.equals("command://music.control.collect.cancel")) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case -1071838543:
                if (event.equals(MusicEvent.PLAY_SPOTIFY)) {
                    c = '#';
                    break;
                }
                c = 65535;
                break;
            case -782232511:
                if (event.equals("command://music.control.playlist.history.play")) {
                    c = 25;
                    break;
                }
                c = 65535;
                break;
            case -679814637:
                if (event.equals("command://music.dailyrec.play")) {
                    c = '!';
                    break;
                }
                c = 65535;
                break;
            case -496865250:
                if (event.equals("command://music.soundeffect.stereo")) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case -447517996:
                if (event.equals("command://music.control.bluetooth.play.random")) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -362899903:
                if (event.equals("command://music.control.collect.play")) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case -151956081:
                if (event.equals("command://music.soundeffect.vocal")) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case -102819689:
                if (event.equals("command://music.news.play")) {
                    c = ' ';
                    break;
                }
                c = 65535;
                break;
            case -96514067:
                if (event.equals("command://music.forward")) {
                    c = JSONLexer.EOI;
                    break;
                }
                c = 65535;
                break;
            case -89098164:
                if (event.equals("command://music.control.random")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -85238858:
                if (event.equals("command://music.control.resume")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -28153286:
                if (event.equals("command://music.1212.novel.play")) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 554746164:
                if (event.equals("native://music.search")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 720689622:
                if (event.equals(MusicEvent.PLAY_RANDOM_CLOSE)) {
                    c = '%';
                    break;
                }
                c = 65535;
                break;
            case 899591924:
                if (event.equals("command://music.soundeffect.superbass")) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case 1260718851:
                if (event.equals("command://music.audiobook.subscribe")) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 1313205723:
                if (event.equals("command://music.audiobook.play")) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 1504460481:
                if (event.equals("command://music.control.collect")) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 1616575982:
                if (event.equals("command://music.control.sim")) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case 1657363090:
                if (event.equals("command://music.soundeffect.live")) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case 1841863913:
                if (event.equals("command://music.control.loop.single")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1903252755:
                if (event.equals("command://music.control.play.usb")) {
                    c = Typography.quote;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                this.mTarget.onPlay(event, data);
                return;
            case 1:
                this.mTarget.onPlayLoopSingle(event, data);
                return;
            case 2:
                this.mTarget.onPlayLoopAll(event, data);
                return;
            case 3:
                this.mTarget.onPlayLoopRandom(event, data);
                return;
            case 4:
                this.mTarget.onSearch(event, data);
                return;
            case 5:
                this.mTarget.onPause(event, data);
                return;
            case 6:
                this.mTarget.onResume(event, data);
                return;
            case 7:
                this.mTarget.onPrev(event, data);
                return;
            case '\b':
                this.mTarget.onNext(event, data);
                return;
            case '\t':
                this.mTarget.onStop(event, data);
                return;
            case '\n':
                this.mTarget.onPlayBlueTooth(event, data);
                return;
            case 11:
                this.mTarget.onSupportPlayModeChange(event, data);
                return;
            case '\f':
                this.mTarget.onAudioBookPlay(event, data);
                return;
            case '\r':
                this.mTarget.onMusicListPlay(event, data);
                return;
            case 14:
                this.mTarget.onTwelveNovelPlay(event, data);
                return;
            case 15:
                this.mTarget.onControlCollect(event, data);
                return;
            case 16:
                this.mTarget.onAudioBookSubscribe(event, data);
                return;
            case 17:
                this.mTarget.onSoundEffectStereo(event, data);
                return;
            case 18:
                this.mTarget.onSoundEffectLive(event, data);
                return;
            case 19:
                this.mTarget.onSoundEffectVocal(event, data);
                return;
            case 20:
                this.mTarget.onSoundEffectSuperbass(event, data);
                return;
            case 21:
                this.mTarget.onDelCollect(event, data);
                return;
            case 22:
                this.mTarget.onPlayCollect(event, data);
                return;
            case 23:
                this.mTarget.onPlaySimilar(event, data);
                return;
            case 24:
                this.mTarget.onCancelPlaySimilar(event, data);
                return;
            case 25:
                this.mTarget.onPlayHistoryList(event, data);
                return;
            case 26:
                this.mTarget.onMusicForward(event, data);
                return;
            case 27:
                this.mTarget.onMusicBackward(event, data);
                return;
            case 28:
                this.mTarget.onMusicSettime(event, data);
                return;
            case 29:
                this.mTarget.onMusicSpeedUp(event, data);
                return;
            case 30:
                this.mTarget.onMusicSpeedDown(event, data);
                return;
            case 31:
                this.mTarget.onMusicSpeedSet(event, data);
                return;
            case ' ':
                this.mTarget.onMusicNewsPlay(event, data);
                return;
            case '!':
                this.mTarget.onMusicDailyrecPlay(event, data);
                return;
            case '\"':
                this.mTarget.onPlayUsb(event, data);
                return;
            case '#':
                this.mTarget.onPlaySpotify(event, data);
                return;
            case '$':
                this.mTarget.onPlayLoopClose(event, data);
                return;
            case '%':
                this.mTarget.onPlayRandomClose(event, data);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{"command://music.control.play", "command://music.control.loop.single", "command://music.control.loop.all", "command://music.control.random", "native://music.search", "command://music.control.pause", "command://music.control.resume", "command://music.control.prev", "command://music.control.next", "command://music.control.stop", "command://music.control.bluetooth.play.random", "native://music.playmode.support", "command://music.audiobook.play", "command://music.list.play", "command://music.1212.novel.play", "command://music.control.collect", "command://music.audiobook.subscribe", "command://music.soundeffect.stereo", "command://music.soundeffect.live", "command://music.soundeffect.vocal", "command://music.soundeffect.superbass", "command://music.control.collect.cancel", "command://music.control.collect.play", "command://music.control.sim", "command://music.control.sim.cancel", "command://music.control.playlist.history.play", "command://music.forward", "command://music.backward", "command://music.settime", "command://music.speed.up", "command://music.speed.down", "command://music.speed.set", "command://music.news.play", "command://music.dailyrec.play", "command://music.control.play.usb", MusicEvent.PLAY_SPOTIFY, MusicEvent.PLAY_LOOP_CLOSE, MusicEvent.PLAY_RANDOM_CLOSE};
    }
}
