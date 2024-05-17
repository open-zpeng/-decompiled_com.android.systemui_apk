package com.xiaopeng.systemui;

import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
/* loaded from: classes24.dex */
public interface IMusicPlayerView {
    void setMusicCardPosition(String str, String str2);

    void showMusicCardProgress(boolean z);

    void updateMusicCardMediaInfo(MediaInfo mediaInfo);

    void updateMusicCardPlayStatus(int i);

    void updateMusicCardProgress(int i);
}
