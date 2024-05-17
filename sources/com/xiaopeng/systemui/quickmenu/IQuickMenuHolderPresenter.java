package com.xiaopeng.systemui.quickmenu;
/* loaded from: classes24.dex */
public interface IQuickMenuHolderPresenter {
    String getQuickMenuLayout();

    String getSecondaryQuickMenuLayout();

    void initQuickMenuPresenter();

    void onClickMediaControl();

    void onClickMediaNext();

    void onClickMediaPrev();

    void onClickTile(String str, int i);

    void onMediaSeekTo(int i);

    void registerAllTileCallback();

    void registerBrightnessCallback();

    void registerMediaControlCallback();

    void registerPsnBrightnessCallback();

    void registerPsnSoundCallback();

    void registerSoundCallback();

    void registerWindCallback();

    void unRegisterAllTileCallback();

    void unRegisterBrightnessCallback();

    void unRegisterMediaControlCallback();

    void unRegisterPsnBrightnessCallback();

    void unRegisterPsnSoundCallback();

    void unRegisterSoundCallback();

    void unRegisterWindCallback();
}
