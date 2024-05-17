package com.xiaopeng.systemui.infoflow;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.android.systemui.R;
import com.xiaopeng.speech.protocol.bean.recommend.RecommendBean;
import com.xiaopeng.systemui.IMusicPlayerView;
import com.xiaopeng.systemui.infoflow.MessageViewParent;
import com.xiaopeng.systemui.infoflow.helper.AnimationHelper;
import com.xiaopeng.systemui.infoflow.message.MessageRootView;
import com.xiaopeng.systemui.infoflow.message.MessageViewContainer;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.presenter.PushBean;
import com.xiaopeng.systemui.infoflow.message.presenter.WeatherBean;
import com.xiaopeng.systemui.infoflow.music.MusicRootView;
import com.xiaopeng.systemui.infoflow.music.MusicViewContainer;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.xuimanager.contextinfo.Lane;
import com.xiaopeng.xuimanager.contextinfo.Maneuver;
import com.xiaopeng.xuimanager.contextinfo.Navi;
import com.xiaopeng.xuimanager.contextinfo.RemainInfo;
import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
import java.util.List;
/* loaded from: classes24.dex */
public class MessageViewParent extends RelativeLayout implements IMusicPlayerView {
    private static final String TAG = "ImmerseViewContainer";
    private AnimationHelper mAnimationHelper;
    private MessageRootView mMessageRootView;
    MessageViewContainer mMessageViewContainer;
    private MessageViewParentContainer mMessageViewParentContainer;
    private MusicRootView mMusicRootView;
    MusicViewContainer mMusicViewContainer;

    public MessageViewParent(Context context) {
        this(context, null);
    }

    public MessageViewParent(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MessageViewParent(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.mMessageViewContainer = new MessageViewContainer() { // from class: com.xiaopeng.systemui.infoflow.MessageViewParent.1
            @Override // com.xiaopeng.systemui.infoflow.message.MessageViewContainer
            public void addBigCard(CardEntry cardEntry) {
                if (MessageViewParent.this.mMessageViewParentContainer != null) {
                    MessageViewParent.this.mMessageViewParentContainer.addBigCard(cardEntry);
                }
            }

            @Override // com.xiaopeng.systemui.infoflow.message.MessageViewContainer
            public void removeBigCard(CardEntry cardEntry) {
                if (MessageViewParent.this.mMessageViewParentContainer != null) {
                    MessageViewParent.this.mMessageViewParentContainer.removeBigCard(cardEntry);
                }
            }
        };
        this.mMusicViewContainer = new AnonymousClass2();
        init();
    }

    private void init() {
        this.mAnimationHelper = new AnimationHelper(this.mContext);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mMessageRootView = (MessageRootView) findViewById(R.id.view_message_root);
        this.mMusicRootView = (MusicRootView) findViewById(R.id.view_music_root);
        this.mMessageRootView.setupWithContainer(this.mMessageViewContainer);
        this.mMusicRootView.setupWithContainer(this.mMusicViewContainer);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mAnimationHelper.destroyAnimation();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showMusicView() {
        MusicRootView musicRootView = this.mMusicRootView;
        if (musicRootView != null) {
            this.mAnimationHelper.showCard(musicRootView);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.systemui.infoflow.MessageViewParent$2  reason: invalid class name */
    /* loaded from: classes24.dex */
    public class AnonymousClass2 implements MusicViewContainer {
        AnonymousClass2() {
        }

        @Override // com.xiaopeng.systemui.infoflow.music.MusicViewContainer
        public void enterMusicMode() {
            Logger.d(MessageViewParent.TAG, "enterMusicMode() called");
            if (MessageViewParent.this.mMessageRootView == null || MessageViewParent.this.mMessageRootView.getVisibility() != 0) {
                MessageViewParent.this.showMusicView();
            } else {
                MessageViewParent.this.mAnimationHelper.hideCard(MessageViewParent.this.mMessageRootView, new AnimationHelper.CardAnimationListener() { // from class: com.xiaopeng.systemui.infoflow.-$$Lambda$MessageViewParent$2$qETxtvWrwdzCxw03nWT-bI6K_QY
                    @Override // com.xiaopeng.systemui.infoflow.helper.AnimationHelper.CardAnimationListener
                    public final void onAnimationEnd() {
                        MessageViewParent.AnonymousClass2.this.lambda$enterMusicMode$0$MessageViewParent$2();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$enterMusicMode$0$MessageViewParent$2() {
            Logger.d(MessageViewParent.TAG, "enterMusicMode() called");
            MessageViewParent.this.showMusicView();
        }

        @Override // com.xiaopeng.systemui.infoflow.music.MusicViewContainer
        public void exitMusicMode() {
            MessageViewParent.this.showMessageModule();
        }
    }

    private void showMessageView() {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            this.mAnimationHelper.showCard(messageRootView);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showMessageModule() {
        MusicRootView musicRootView = this.mMusicRootView;
        if (musicRootView != null && musicRootView.getVisibility() == 0) {
            this.mAnimationHelper.hideCard(this.mMusicRootView, new AnimationHelper.CardAnimationListener() { // from class: com.xiaopeng.systemui.infoflow.-$$Lambda$MessageViewParent$Up4vNTnSf1cMpij90yV6rn8p53M
                @Override // com.xiaopeng.systemui.infoflow.helper.AnimationHelper.CardAnimationListener
                public final void onAnimationEnd() {
                    MessageViewParent.this.lambda$showMessageModule$0$MessageViewParent();
                }
            });
        } else {
            showMessageView();
        }
    }

    public /* synthetic */ void lambda$showMessageModule$0$MessageViewParent() {
        Logger.d(TAG, "showMessageModule() called");
        showMessageView();
    }

    public View getMusicCardPosition(Rect rect) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            return messageRootView.getMusicCardPosition(rect);
        }
        return null;
    }

    public void refreshList(List<CardEntry> entries) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.refreshList(entries);
        }
    }

    public void updateCallCardContent(String content) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.updateCallCardContent(content);
        }
    }

    public void setExploreSceneCardManeuverData(Maneuver maneuverData) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.setExploreSceneCardManeuverData(maneuverData);
        }
    }

    public void setExploreSceneCardNaviData(Navi navi) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.setExploreSceneCardNaviData(navi);
        }
    }

    public void setExploreSceneCardLaneData(Lane lane) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.setExploreSceneCardLaneData(lane);
        }
    }

    @Override // com.xiaopeng.systemui.IMusicPlayerView
    public void updateMusicCardMediaInfo(MediaInfo mediaInfo) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.updateMusicCardMediaInfo(mediaInfo);
        }
    }

    @Override // com.xiaopeng.systemui.IMusicPlayerView
    public void updateMusicCardPlayStatus(int playStatus) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.updateMusicCardPlayStatus(playStatus);
        }
    }

    @Override // com.xiaopeng.systemui.IMusicPlayerView
    public void updateMusicCardProgress(int progress) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.updateMusicCardProgress(progress);
        }
    }

    @Override // com.xiaopeng.systemui.IMusicPlayerView
    public void showMusicCardProgress(boolean show) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.showMusicCardProgress(show);
        }
    }

    @Override // com.xiaopeng.systemui.IMusicPlayerView
    public void setMusicCardPosition(String position, String duration) {
    }

    public void setNaviSceneCardLaneData(Lane lane) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.setNaviSceneCardLaneData(lane);
        }
    }

    public void setNaviSceneCardManeuverData(Maneuver maneuver) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.setNaviSceneCardManeuverData(maneuver);
        }
    }

    public void setNaviSceneCardNaviData(Navi navi) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.setNaviSceneCardNaviData(navi);
        }
    }

    public void setNaviSceneCardRemainInfoData(RemainInfo remainInfo) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.setNaviSceneCardRemainInfoData(remainInfo);
        }
    }

    public void setNotificationCardStatus(boolean hasNotification, String currentTime) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.setNotificationCardStatus(hasNotification, currentTime);
        }
    }

    public void setNotificationCardSubDesc(String desc) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.setNotificationCardSubDesc(desc);
        }
    }

    public void setInfoflowCardFocused(int cardType, boolean focused) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.setInfoflowCardFocused(cardType, focused);
        }
    }

    public void hidePushCardNotTip() {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.hidePushCardNotTip();
        }
    }

    public void updatePushCardView(PushBean pushBean) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.updatePushCardView(pushBean);
        }
    }

    public void updateRecommendCardList(RecommendBean recommendBean) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.updateRecommendCardList(recommendBean);
        }
    }

    public void updateWeatherCardView(WeatherBean weatherBean) {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.updateWeatherCardView(weatherBean);
        }
    }

    public void hideWeatherCardNotTip() {
        MessageRootView messageRootView = this.mMessageRootView;
        if (messageRootView != null) {
            messageRootView.hideWeatherCardNotTip();
        }
    }
}
