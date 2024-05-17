package com.xiaopeng.systemui.infoflow.message.anim.musiccard;

import android.view.View;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.MusicCardHolder;
/* loaded from: classes24.dex */
public abstract class ToNormalModeAnimation {
    protected long ANIMATION_DURATION = 500;
    protected View mCardView;
    public MusicCardHolder mMusicCardHolder;

    public abstract void doAnimation();

    public ToNormalModeAnimation(MusicCardHolder musicCardHolder) {
        this.mMusicCardHolder = musicCardHolder;
    }
}
