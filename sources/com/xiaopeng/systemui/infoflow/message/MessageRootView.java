package com.xiaopeng.systemui.infoflow.message;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import com.android.systemui.R;
import com.xiaopeng.speech.protocol.bean.recommend.RecommendBean;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.infoflow.message.adapter.CardStackAdapter;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.CallCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.CruiseSceneCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.ExploreSceneCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.MusicCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.NaviSceneCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.NotificationCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.PhoneCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.PushCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.RecommendCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.WeatherCardHolder;
import com.xiaopeng.systemui.infoflow.message.anim.LandItemAnimator;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.CardListData;
import com.xiaopeng.systemui.infoflow.message.helper.StartSnapHelper;
import com.xiaopeng.systemui.infoflow.message.presenter.CardsPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.PushBean;
import com.xiaopeng.systemui.infoflow.message.presenter.WeatherBean;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.OrientationUtil;
import com.xiaopeng.systemui.infoflow.widget.CardStack;
import com.xiaopeng.systemui.infoflow.widget.XLinearLayoutManager;
import com.xiaopeng.xuimanager.contextinfo.Lane;
import com.xiaopeng.xuimanager.contextinfo.Maneuver;
import com.xiaopeng.xuimanager.contextinfo.Navi;
import com.xiaopeng.xuimanager.contextinfo.RemainInfo;
import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
import java.util.List;
/* loaded from: classes24.dex */
public class MessageRootView extends RelativeLayout {
    private static final String TAG = "MessageRootView";
    private Scroller mAngleScroller;
    private CardStack mCardStack;
    private CardStackAdapter mCardStackAdapter;
    private CardListData mCardsData;
    private CardsPresenter mCardsPresenter;
    private Context mContext;
    private RecyclerView.ItemAnimator mItemAnimator;
    private LinearLayoutManager mLayoutManager;
    private MessageViewContainer mMessageViewContainer;
    private CardStackAdapter.OnItemClickListener mOnItemClickListener;

    public MessageRootView(Context context) {
        this(context, null);
    }

    public MessageRootView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MessageRootView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.mOnItemClickListener = new CardStackAdapter.OnItemClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.MessageRootView.1
            @Override // com.xiaopeng.systemui.infoflow.message.adapter.CardStackAdapter.OnItemClickListener
            public void onItemClick(CardEntry cardEntry) {
                if (MessageRootView.this.mCardsPresenter != null) {
                    MessageRootView.this.mCardsPresenter.removeCardEntry(cardEntry);
                }
            }
        };
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.mCardsData = CardListData.getInstance();
        this.mCardsPresenter = PresenterCenter.getInstance().getCardsPresenter();
        this.mCardStackAdapter = new CardStackAdapter();
        this.mCardStackAdapter.setOnItemClickListener(this.mOnItemClickListener);
        this.mLayoutManager = new XLinearLayoutManager(this.mContext);
        if (OrientationUtil.isLandscapeScreen(this.mContext)) {
            this.mLayoutManager.setOrientation(1);
            this.mItemAnimator = new LandItemAnimator();
        } else {
            this.mItemAnimator = null;
            this.mLayoutManager.setOrientation(0);
        }
        this.mAngleScroller = new Scroller(getContext());
    }

    public void setupWithContainer(MessageViewContainer messageViewContainer) {
        this.mMessageViewContainer = messageViewContainer;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Logger.d(TAG, "touch event--" + ev.toString());
        return super.dispatchTouchEvent(ev);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mCardStack = (CardStack) findViewById(R.id.cardStack);
        this.mCardStack.setNeedStopScrollOnTouchUp(true);
        configCardStack();
    }

    private void configCardStack() {
        this.mCardStack.setLayoutManager(this.mLayoutManager);
        this.mCardStack.setAdapter(this.mCardStackAdapter);
        this.mCardStack.getRecycledViewPool().setMaxRecycledViews(17, 0);
        this.mCardStack.getRecycledViewPool().setMaxRecycledViews(24, 0);
        this.mCardStack.getRecycledViewPool().setMaxRecycledViews(27, 0);
        if (OrientationUtil.isLandscapeScreen(this.mContext)) {
            SnapHelper snapHelper = new StartSnapHelper();
            snapHelper.attachToRecyclerView(this.mCardStack);
            this.mCardStack.setItemAnimator(this.mItemAnimator);
        }
        this.mCardStackAdapter.submitList(this.mCardsData.getCards());
    }

    public void refreshList(List<CardEntry> entries) {
        this.mCardStackAdapter.submitList(entries);
    }

    private RecommendCardHolder findRecommendCardHolder() {
        RecyclerView.ViewHolder viewHolder = getViewHolder(26);
        if (viewHolder instanceof RecommendCardHolder) {
            return (RecommendCardHolder) viewHolder;
        }
        return null;
    }

    private CallCardHolder findCallCardHolder() {
        RecyclerView.ViewHolder viewHolder = getViewHolder(4);
        if (viewHolder instanceof CallCardHolder) {
            return (CallCardHolder) viewHolder;
        }
        return null;
    }

    private PhoneCardHolder findPhoneCardHolder() {
        RecyclerView.ViewHolder viewHolder = getViewHolder(5);
        if (viewHolder instanceof PhoneCardHolder) {
            return (PhoneCardHolder) viewHolder;
        }
        return null;
    }

    private PushCardHolder findPushCardHolder() {
        RecyclerView.ViewHolder viewHolder = getViewHolder(17);
        if (viewHolder instanceof PushCardHolder) {
            return (PushCardHolder) viewHolder;
        }
        return null;
    }

    private WeatherCardHolder findWeatherCardHolder() {
        RecyclerView.ViewHolder viewHolder = getViewHolder(17);
        if (viewHolder instanceof WeatherCardHolder) {
            return (WeatherCardHolder) viewHolder;
        }
        return null;
    }

    private ExploreSceneCardHolder findExploreSceneCardHolder() {
        RecyclerView.ViewHolder viewHolder = getViewHolder(27);
        if (viewHolder instanceof ExploreSceneCardHolder) {
            return (ExploreSceneCardHolder) viewHolder;
        }
        return null;
    }

    private MusicCardHolder findMusicCardHolder() {
        RecyclerView.ViewHolder viewHolder = getViewHolder(9);
        if (viewHolder instanceof MusicCardHolder) {
            return (MusicCardHolder) viewHolder;
        }
        return null;
    }

    private NaviSceneCardHolder findNaviSceneCardHolder() {
        RecyclerView.ViewHolder viewHolder = getViewHolder(24);
        if (viewHolder instanceof NaviSceneCardHolder) {
            return (NaviSceneCardHolder) viewHolder;
        }
        return null;
    }

    private CruiseSceneCardHolder findCruiseSceneCardHolder() {
        RecyclerView.ViewHolder viewHolder = getViewHolder(25);
        if (viewHolder instanceof CruiseSceneCardHolder) {
            return (CruiseSceneCardHolder) viewHolder;
        }
        return null;
    }

    private NotificationCardHolder findNotificationCardHolder() {
        RecyclerView.ViewHolder viewHolder = getViewHolder(1);
        if (viewHolder instanceof NotificationCardHolder) {
            return (NotificationCardHolder) viewHolder;
        }
        return null;
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mAngleScroller.computeScrollOffset()) {
            CardStack cardStack = this.mCardStack;
            if (cardStack != null) {
                cardStack.changeAngle(this.mAngleScroller.getCurrX(), this.mAngleScroller.getCurrY());
            }
            postInvalidate();
        }
    }

    public View getMusicCardPosition(Rect rect) {
        CardStack cardStack = this.mCardStack;
        if (cardStack != null) {
            return cardStack.getMusicCardPosition(rect);
        }
        return null;
    }

    private RecyclerView.ViewHolder getViewHolder(int cardType) {
        int index;
        CardEntry cardEntry = this.mCardsData.getCard(cardType);
        if (cardEntry != null && (index = CardListData.getInstance().getCards().indexOf(cardEntry)) >= 0) {
            return this.mCardStack.findViewHolderForAdapterPosition(index);
        }
        return null;
    }

    public void updateCallCardContent(String content) {
        CallCardHolder callCardHolder = findCallCardHolder();
        if (callCardHolder != null) {
            callCardHolder.updateContent(content);
        }
    }

    public void setExploreSceneCardManeuverData(Maneuver maneuverData) {
        ExploreSceneCardHolder exploreSceneCardHolder = findExploreSceneCardHolder();
        if (exploreSceneCardHolder != null) {
            exploreSceneCardHolder.setExploreSceneCardManeuverData(maneuverData);
        }
    }

    public void setExploreSceneCardNaviData(Navi navi) {
        ExploreSceneCardHolder exploreSceneCardHolder = findExploreSceneCardHolder();
        if (exploreSceneCardHolder != null) {
            exploreSceneCardHolder.setExploreSceneCardNaviData(navi);
        }
    }

    public void setExploreSceneCardLaneData(Lane lane) {
        ExploreSceneCardHolder exploreSceneCardHolder = findExploreSceneCardHolder();
        if (exploreSceneCardHolder != null) {
            exploreSceneCardHolder.setExploreSceneCardLaneData(lane);
        }
    }

    public void updateMusicCardMediaInfo(MediaInfo mediaInfo) {
        MusicCardHolder musicCardHolder = findMusicCardHolder();
        if (musicCardHolder != null) {
            musicCardHolder.setMusicCardMediaInfo(0, mediaInfo);
        }
    }

    public void updateMusicCardPlayStatus(int playStatus) {
        MusicCardHolder musicCardHolder = findMusicCardHolder();
        if (musicCardHolder != null) {
            musicCardHolder.setMusicCardPlayStatus(0, playStatus);
        }
    }

    public void updateMusicCardProgress(int progress) {
        MusicCardHolder musicCardHolder = findMusicCardHolder();
        if (musicCardHolder != null) {
            musicCardHolder.setMusicCardProgress(0, progress);
        }
    }

    public void showMusicCardProgress(boolean show) {
        MusicCardHolder musicCardHolder = findMusicCardHolder();
        if (musicCardHolder != null) {
            musicCardHolder.showMusicCardProgress(0, show);
        }
    }

    public void setNaviSceneCardLaneData(Lane lane) {
        NaviSceneCardHolder naviSceneCardHolder = findNaviSceneCardHolder();
        if (naviSceneCardHolder != null) {
            naviSceneCardHolder.setNaviSceneCardLaneData(lane);
        }
    }

    public void setNaviSceneCardManeuverData(Maneuver maneuver) {
        NaviSceneCardHolder naviSceneCardHolder = findNaviSceneCardHolder();
        if (naviSceneCardHolder != null) {
            naviSceneCardHolder.setNaviSceneCardManeuverData(maneuver);
        }
    }

    public void setNaviSceneCardNaviData(Navi navi) {
        NaviSceneCardHolder naviSceneCardHolder = findNaviSceneCardHolder();
        if (naviSceneCardHolder != null) {
            naviSceneCardHolder.setNaviSceneCardNaviData(navi);
        }
    }

    public void setNaviSceneCardRemainInfoData(RemainInfo remainInfo) {
        NaviSceneCardHolder naviSceneCardHolder = findNaviSceneCardHolder();
        if (naviSceneCardHolder != null) {
            naviSceneCardHolder.setNaviSceneCardRemainInfoData(remainInfo);
        }
    }

    public void setNotificationCardStatus(boolean hasNotification, String currentTime) {
        NotificationCardHolder notificationCardHolder = findNotificationCardHolder();
        if (notificationCardHolder != null) {
            notificationCardHolder.setNotificationCardStatus(hasNotification, currentTime);
        }
    }

    public void setNotificationCardSubDesc(String desc) {
        NotificationCardHolder notificationCardHolder = findNotificationCardHolder();
        if (notificationCardHolder != null) {
            notificationCardHolder.setNotificationCardSubDesc(desc);
        }
    }

    public void setInfoflowCardFocused(int cardType, boolean focused) {
        RecyclerView.ViewHolder viewHolder = getViewHolder(cardType);
        if (viewHolder instanceof BaseCardHolder) {
            ((BaseCardHolder) viewHolder).setFocused(focused);
        }
    }

    public void hidePushCardNotTip() {
        PushCardHolder pushCardHolder = findPushCardHolder();
        if (pushCardHolder != null) {
            pushCardHolder.hidePushCardNotTip();
        }
    }

    public void updatePushCardView(PushBean pushBean) {
        PushCardHolder pushCardHolder = findPushCardHolder();
        if (pushCardHolder != null) {
            pushCardHolder.setPushCardContent(pushBean);
        }
    }

    public void updateRecommendCardList(RecommendBean recommendBean) {
        RecommendCardHolder recommendCardHolder = findRecommendCardHolder();
        if (recommendCardHolder != null) {
            recommendCardHolder.setRecommendCardContent(recommendBean);
        }
    }

    public void updateWeatherCardView(WeatherBean weatherBean) {
        WeatherCardHolder weatherCardHolder = findWeatherCardHolder();
        if (weatherCardHolder != null) {
            weatherCardHolder.setWeatherCardContent(weatherBean);
        }
    }

    public void hideWeatherCardNotTip() {
        WeatherCardHolder weatherCardHolder = findWeatherCardHolder();
        if (weatherCardHolder != null) {
            weatherCardHolder.hideWeatherCardNotTip();
        }
    }
}
