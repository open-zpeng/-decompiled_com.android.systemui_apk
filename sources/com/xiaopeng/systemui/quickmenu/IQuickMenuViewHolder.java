package com.xiaopeng.systemui.quickmenu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
/* loaded from: classes24.dex */
public interface IQuickMenuViewHolder {
    void enableMediaBtn(boolean z);

    void initSlider(int i, int i2, int i3, int i4);

    View initView(Context context, ViewGroup viewGroup);

    void openNapaAppWindow(String str);

    void themeChanged(boolean z);

    void updateControlBtn(int i);

    void updateMusicInfo(String str, String str2, String str3, int i);

    void updateMusicProgress(long j, long j2);

    void updateSoundType(int i);

    void updateViewState(String str, int i);
}
