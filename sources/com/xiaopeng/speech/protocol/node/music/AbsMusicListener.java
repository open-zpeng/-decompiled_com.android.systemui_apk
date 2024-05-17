package com.xiaopeng.speech.protocol.node.music;

import com.xiaopeng.speech.protocol.node.music.bean.CollectHistoryMusic;
import com.xiaopeng.speech.protocol.node.music.bean.SearchMusic;
/* loaded from: classes23.dex */
public abstract class AbsMusicListener implements MusicListener {
    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onPlay() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onSearch(String event, SearchMusic searchKeys) {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onPause() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onResume() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onPrev() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onNext() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onStop() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onExit() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onControlCollect() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onMusicForward(int second) {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onMusicBackward(int second) {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onMusicSettime(int second) {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onMusicSpeedUp() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onMusicSpeedDown() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onMusicSpeedSet(float speed) {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onPlayMode(String mode) {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onPlayBluetooth() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onSupportPlayModeChange(String event) {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onAudioBookPlay(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onMusicListPlay(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onTwelveNovelPlay(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onAudioBookSubscribe() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onSoundEffectStereo() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onSoundEffectLive() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onSoundEffectVocal() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onSoundEffectSuperbass() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onDelCollect() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onPlayCollect() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onPlayCollect(CollectHistoryMusic collectHistoryMusic) {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onPlaySimilar() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onCancelPlaySimilar() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onPlayHistoryList(CollectHistoryMusic collectHistoryMusic) {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onMusicNewsPlay() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onMusicDailyrecPlay() {
    }

    @Override // com.xiaopeng.speech.protocol.node.music.MusicListener
    public void onPlayUsb() {
    }
}
