package com.xiaopeng.systemui.infoflow.message.presenter;

import android.graphics.Bitmap;
import android.util.Log;
import com.android.systemui.R;
import com.xiaopeng.lib.utils.info.BuildInfoUtils;
import com.xiaopeng.systemui.infoflow.IMusicCardView;
import com.xiaopeng.systemui.infoflow.manager.MediaManager;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.quickmenu.widgets.MediaControlView;
import com.xiaopeng.systemui.utils.DataLogUtils;
import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
import com.xiaopeng.xuimanager.mediacenter.lyric.LyricInfo;
/* loaded from: classes24.dex */
public class MusicCardPresenter extends BaseCardPresenter implements IMusicCardPresenter {
    private static final String TAG = "MusicCardPresenter";
    protected MediaInfo mMediaInfo;
    protected MediaManager mMediaManager;
    protected int mMediaType;
    protected IMusicCardView mMusicCardView;
    protected int mStatus = 1;
    protected MediaManager.OnMediaInfoChangedListener mMediaInfoChangedListener = new MediaManager.OnMediaInfoChangedListener() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.MusicCardPresenter.1
        @Override // com.xiaopeng.systemui.infoflow.manager.MediaManager.OnMediaInfoChangedListener
        public void onInfoChanged(MediaInfo mediaInfo) {
            MusicCardPresenter musicCardPresenter = MusicCardPresenter.this;
            musicCardPresenter.mMediaInfo = mediaInfo;
            if (mediaInfo != null) {
                musicCardPresenter.mMediaType = mediaInfo.getSource();
            } else {
                musicCardPresenter.mMediaType = 0;
            }
            MusicCardPresenter.this.setMusicCardMediaInfo(mediaInfo);
        }
    };
    protected MediaManager.OnPlayStatusChangedListener mPlayStatusChangedListener = new MediaManager.OnPlayStatusChangedListener() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.MusicCardPresenter.2
        @Override // com.xiaopeng.systemui.infoflow.manager.MediaManager.OnPlayStatusChangedListener
        public void onStatusChanged(int status) {
            Logger.d(MusicCardPresenter.TAG, "onStatusChanged status = " + status);
            MusicCardPresenter.this.setMusicCardPlayStatus(status);
        }
    };
    protected MediaManager.OnPlayPositionChangedListener mPositionChangedListener = new MediaManager.OnPlayPositionChangedListener() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.MusicCardPresenter.3
        @Override // com.xiaopeng.systemui.infoflow.manager.MediaManager.OnPlayPositionChangedListener
        public void onPositionChanged(long position, long duration) {
            if (duration == 0) {
                Logger.w(MusicCardPresenter.TAG, "duration is zero");
                MusicCardPresenter.this.showMusicCardProgress(false);
            } else {
                int progress = (int) ((100 * position) / duration);
                if (progress == 0 && position > 0) {
                    progress = 1;
                }
                MusicCardPresenter.this.showMusicCardProgress(true);
                MusicCardPresenter.this.setMusicCardProgress(progress);
            }
            MusicCardPresenter.this.setMusicCardPosition(position, duration);
        }
    };
    protected MediaManager.OnLyricUpdatedListener mLyricUpdatedListener = new MediaManager.OnLyricUpdatedListener() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.MusicCardPresenter.4
        @Override // com.xiaopeng.systemui.infoflow.manager.MediaManager.OnLyricUpdatedListener
        public void onLyricUpdated(int displayId, LyricInfo info) {
            Logger.d(MusicCardPresenter.TAG, "onLyricUpdated displayId:" + displayId + " &info:" + info.toString());
            if (MusicCardPresenter.this.mMusicCardView != null) {
                MusicCardPresenter.this.mMusicCardView.setMusicCardLyricInfo(displayId, info);
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final MusicCardPresenter sInstance = new MusicCardPresenter();

        private SingleHolder() {
        }
    }

    public static MusicCardPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MusicCardPresenter() {
        Logger.d(TAG, TAG);
        this.mMediaManager = MediaManager.getInstance();
        registerMediaCenter();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected int getCardType() {
        return 9;
    }

    protected int getDisplayId() {
        return 0;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public String getCardPackageName() {
        String mPkgName;
        MediaInfo mediaInfo = this.mMediaInfo;
        return (mediaInfo == null || (mPkgName = mediaInfo.getPackageName()) == null) ? this.mContext.getString(R.string.pkg_music) : mPkgName;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardClicked() {
        Log.i(TAG, "onCardClicked");
        DataLogUtils.sendInfoDataLog(DataLogUtils.INFO_MUSIC_PAGE_ID, "B001", "0", isAppForeground() ? "1" : "0", getDataLogSource());
        this.mMediaManager.enterMusicApp();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IMusicCardPresenter
    public void onMusicCardPlayPauseClicked() {
        Logger.d(TAG, "onMusicPlayPauseClicked");
        this.mMediaManager.pause();
        if (this.mStatus == 0) {
            DataLogUtils.sendInfoDataLog(DataLogUtils.INFO_MUSIC_PAGE_ID, "B001", "3", isAppForeground() ? "1" : "0", getDataLogSource());
        } else {
            DataLogUtils.sendInfoDataLog(DataLogUtils.INFO_MUSIC_PAGE_ID, "B001", "2", isAppForeground() ? "1" : "0", getDataLogSource());
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IMusicCardPresenter
    public void onMusicCardPrevClicked() {
        Logger.d(TAG, "onMusicPrevClicked");
        this.mMediaManager.previous();
        DataLogUtils.sendInfoDataLog(DataLogUtils.INFO_MUSIC_PAGE_ID, "B001", "1", isAppForeground() ? "1" : "0", getDataLogSource());
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IMusicCardPresenter
    public void onMusicCardNextClicked() {
        Logger.d(TAG, "onMusicNextClicked");
        this.mMediaManager.next();
        DataLogUtils.sendInfoDataLog(DataLogUtils.INFO_MUSIC_PAGE_ID, "B001", BuildInfoUtils.BID_LAN, isAppForeground() ? "1" : "0", getDataLogSource());
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IMusicCardPresenter
    public void onMusicCardCollectClicked() {
        this.mMediaManager.setFavorite(!isFavor(this.mMediaInfo), this.mMediaInfo.getId());
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void bindDataImpl(CardEntry cardEntry) {
        setMusicCardMediaInfo(this.mMediaManager.getCurrentMediaInfo());
        setMusicCardPlayStatus(this.mMediaManager.getCurrentPlayStatus());
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithInfoflowView() {
        Logger.d(TAG, "fillViewWithInfoflowView : " + this.mInfoflowView);
        this.mMusicCardView = this.mInfoflowView;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithCardHolder() {
        this.mMusicCardView = (IMusicCardView) this.mCardHolder;
    }

    public MediaInfo getCurrentMediaInfo(int displayId) {
        return this.mMediaManager.getCurrentMediaInfo(displayId);
    }

    public LyricInfo getCurrentLyricInfo(int displayId) {
        return this.mMediaManager.getCurrentLyricInfo(displayId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setMusicCardMediaInfo(MediaInfo mediaInfo) {
        if (mediaInfo != null) {
            Bitmap bitmap = mediaInfo.getAlbumBitmap();
            if (bitmap != null) {
                Logger.d(TAG, "setMusicCardMediaInfo width: " + bitmap.getWidth() + " &height:" + bitmap.getHeight());
            }
            IMusicCardView iMusicCardView = this.mMusicCardView;
            if (iMusicCardView != null) {
                iMusicCardView.setMusicCardMediaInfo(getDisplayId(), mediaInfo);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setMusicCardPlayStatus(int status) {
        IMusicCardView iMusicCardView = this.mMusicCardView;
        if (iMusicCardView != null) {
            iMusicCardView.setMusicCardPlayStatus(getDisplayId(), status);
        }
    }

    protected void showMusicCardProgress(boolean show) {
        IMusicCardView iMusicCardView = this.mMusicCardView;
        if (iMusicCardView != null) {
            iMusicCardView.showMusicCardProgress(getDisplayId(), show);
        }
    }

    protected void setMusicCardProgress(int progress) {
        IMusicCardView iMusicCardView = this.mMusicCardView;
        if (iMusicCardView != null) {
            iMusicCardView.setMusicCardProgress(getDisplayId(), progress);
        }
    }

    protected void setMusicCardPosition(long position, long duration) {
        Logger.d(TAG, "setMusicCardPosition : " + this.mMusicCardView);
        IMusicCardView iMusicCardView = this.mMusicCardView;
        if (iMusicCardView != null) {
            iMusicCardView.setMusicCardPosition(getDisplayId(), MediaControlView.formatSeconds(position), MediaControlView.formatSeconds(duration));
        }
    }

    protected void registerMediaCenter() {
        MediaManager mediaManager = this.mMediaManager;
        if (mediaManager != null) {
            mediaManager.addOnMediaInfoChangedListener(this.mMediaInfoChangedListener);
            this.mMediaManager.addOnPlayStatusChangedListener(this.mPlayStatusChangedListener);
            this.mMediaManager.addOnPlayPositionChangedListener(this.mPositionChangedListener);
            this.mMediaManager.addLyricListener(this.mLyricUpdatedListener);
        }
    }

    private String getDataLogSource() {
        int i = this.mMediaType;
        if (i == 0) {
            return "0";
        }
        if (i == 3) {
            return "1";
        }
        if (i == 1) {
            return "3";
        }
        if (i == 2) {
            return BuildInfoUtils.BID_LAN;
        }
        return "2";
    }

    private boolean isFavor(MediaInfo mediaInfo) {
        return mediaInfo.getFavor() == 1;
    }
}
