package com.xiaopeng.systemui.infoflow.message.presenter;

import com.xiaopeng.systemui.ViewFactory;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class SecondaryMusicCardPresenter extends MusicCardPresenter {
    private static final String TAG = "SecondaryMusicCardPresenter";

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final SecondaryMusicCardPresenter sInstance = new SecondaryMusicCardPresenter();

        private SingleHolder() {
        }
    }

    public static SecondaryMusicCardPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.MusicCardPresenter
    protected void registerMediaCenter() {
        Logger.d(TAG, "registerMediaCenter : " + this.mMediaManager);
        if (this.mMediaManager != null) {
            this.mMediaManager.addOnSecondaryMediaInfoChangedListener(this.mMediaInfoChangedListener);
            this.mMediaManager.addOnSecondaryPlayStatusChangedListener(this.mPlayStatusChangedListener);
            this.mMediaManager.addOnSecondaryPlayPositionChangedListener(this.mPositionChangedListener);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.MusicCardPresenter, com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected int getCardType() {
        return 30;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.MusicCardPresenter
    protected int getDisplayId() {
        return 1;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.MusicCardPresenter, com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void bindDataImpl(CardEntry cardEntry) {
        setMusicCardMediaInfo(this.mMediaManager.getCurrentMediaInfo(1));
        setMusicCardPlayStatus(this.mMediaManager.getCurrentPlayStatus(1));
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.MusicCardPresenter, com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithInfoflowView() {
        Logger.d(TAG, "fillViewWithInfoflowView : " + this.mInfoflowView);
        if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
            this.mMusicCardView = this.mInfoflowView;
        } else {
            this.mMusicCardView = ViewFactory.getLandscapeInfoflowView();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.MusicCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IMusicCardPresenter
    public void onMusicCardPlayPauseClicked() {
        Logger.d(TAG, "onMusicPlayPauseClicked");
        this.mMediaManager.pause(1);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.MusicCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IMusicCardPresenter
    public void onMusicCardPrevClicked() {
        Logger.d(TAG, "onMusicPrevClicked");
        this.mMediaManager.previous(1);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.MusicCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IMusicCardPresenter
    public void onMusicCardNextClicked() {
        Logger.d(TAG, "onMusicNextClicked");
        this.mMediaManager.next(1);
    }
}
