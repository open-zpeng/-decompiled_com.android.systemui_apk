package com.xiaopeng.systemui.infoflow;

import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
import com.xiaopeng.xuimanager.mediacenter.lyric.LyricInfo;
/* loaded from: classes24.dex */
public interface IMusicCardView {
    void setMusicCardMediaInfo(int i, MediaInfo mediaInfo);

    void setMusicCardPlayStatus(int i, int i2);

    void setMusicCardPosition(int i, String str, String str2);

    void setMusicCardProgress(int i, int i2);

    void showMusicCardProgress(int i, boolean z);

    default void setMusicCardLyricInfo(int displayId, LyricInfo info) {
    }
}
