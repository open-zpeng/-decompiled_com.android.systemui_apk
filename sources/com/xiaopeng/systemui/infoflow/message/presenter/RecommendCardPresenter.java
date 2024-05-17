package com.xiaopeng.systemui.infoflow.message.presenter;

import android.os.Handler;
import android.os.Message;
import com.google.gson.Gson;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.jarvisproto.ContextInput;
import com.xiaopeng.speech.protocol.bean.base.ButtonBean;
import com.xiaopeng.speech.protocol.bean.recommend.RecommendBean;
import com.xiaopeng.speech.protocol.bean.stats.RecommendStatBean;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.infoflow.IRecommendCardView;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.CardListData;
import com.xiaopeng.systemui.infoflow.message.define.CardKey;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.Logger;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class RecommendCardPresenter extends BaseCardPresenter implements IRecommendCardPresenter {
    private static final int HIT_SHOW_TIME = 1000;
    private static final int MSG_NOTIFY_CARD_STATE = 0;
    private static final String TAG = "RecommendCardPresenter";
    private Handler mHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.RecommendCardPresenter.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                CardState cardState = (CardState) msg.obj;
                RecommendCardPresenter.this.notifyCardState(cardState);
            }
        }
    };
    private RecommendBean mRecommendBean;
    private IRecommendCardView mRecommendCardView;
    private int mSceneType;
    private RecommendStatBean recommendStatBean;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final RecommendCardPresenter sInstance = new RecommendCardPresenter();

        private SingleHolder() {
        }
    }

    public static RecommendCardPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class CardState {
        String mHitText;
        boolean mIsAnimEnd;
        boolean mIsHidden;

        private CardState() {
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected int getCardType() {
        return 26;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
        if (this.mCardData != null) {
            refreshCard(this.mCardData.content);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
        this.mHandler.removeCallbacksAndMessages(null);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithInfoflowView() {
        this.mRecommendCardView = this.mInfoflowView;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithCardHolder() {
        this.mRecommendCardView = (IRecommendCardView) this.mCardHolder;
    }

    public void refreshCard(String content) {
        Logger.d(TAG, "refreshCard");
        CardEntry cardEntry = CardListData.getInstance().getCard(CardKey.RECOMMEND_ENTRY_KEY);
        if (cardEntry != null) {
            cardEntry.content = content;
            RecommendBean recommendBean = (RecommendBean) GsonUtil.fromJson(cardEntry.content, (Class<Object>) RecommendBean.class);
            this.mSceneType = recommendBean.getCardType();
            setDataList(recommendBean);
        }
    }

    public void removeCard() {
        CardEntry cardEntry = CardListData.getInstance().getCard(CardKey.RECOMMEND_ENTRY_KEY);
        if (cardEntry != null) {
            PresenterCenter.getInstance().getCardsPresenter().removeCardEntry(cardEntry);
            sendMsgToNotifyCardState(true, false, "", 0);
        }
    }

    public void setDataList(RecommendBean recommendBean) {
        Logger.d(TAG, "setDataList");
        if (recommendBean == null) {
            return;
        }
        this.mRecommendBean = recommendBean;
        this.recommendStatBean = new RecommendStatBean();
        this.recommendStatBean.setType(recommendBean.getType());
        this.recommendStatBean.setSubType(recommendBean.getSubType());
        this.recommendStatBean.setMsgId(recommendBean.getMsgId());
        this.recommendStatBean.setRefMsgId(recommendBean.getRefMsgId());
        this.recommendStatBean.setShowTime(System.currentTimeMillis());
        this.recommendStatBean.setStartTime(System.currentTimeMillis());
        List<ButtonBean> relateList = recommendBean.getRelateList();
        for (int i = 0; i < relateList.size(); i++) {
            ButtonBean buttonBean = relateList.get(i);
            if (i == 0) {
                this.recommendStatBean.setFirstText(buttonBean.text);
            } else if (i == 1) {
                this.recommendStatBean.setSecondText(buttonBean.text);
            } else if (i == 2) {
                this.recommendStatBean.setThirdText(buttonBean.text);
            }
            if (i > 4) {
                break;
            }
        }
        SpeechClient.instance().getAgent().sendInfoFlowStatData(401, new Gson().toJson(this.recommendStatBean));
        if (recommendBean.isHit()) {
            String hitText = recommendBean.getHitText();
            sendMsgToNotifyCardState(false, true, hitText, 1000);
        }
        IRecommendCardView iRecommendCardView = this.mRecommendCardView;
        if (iRecommendCardView != null) {
            iRecommendCardView.setRecommendCardContent(recommendBean);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IRecommendCardPresenter
    public void onRecommendCardItemClicked(int position) {
        ButtonBean buttonBean = this.mRecommendBean.getRelateList().get(position);
        Logger.v(TAG, "buttonBean.action.type = " + buttonBean.action.type);
        this.recommendStatBean.setClickTime(System.currentTimeMillis());
        this.recommendStatBean.setClickText(buttonBean.text);
        this.recommendStatBean.setClickPosition(position + 1);
        this.recommendStatBean.setStartTime(System.currentTimeMillis());
        SpeechClient.instance().getAgent().sendInfoFlowStatData(401, new Gson().toJson(this.recommendStatBean));
        if ("server".equals(buttonBean.action.type)) {
            ContextInput contextInput = new ContextInput();
            contextInput.text = buttonBean.text;
            SpeechClient.instance().getAgent().sendEvent(ContextInput.EVENT, contextInput.getJsonData());
            SpeechClient.instance().getAgent().sendText(buttonBean.text);
        } else if ("client".equals(buttonBean.action.type) && "command://tpl".equals(buttonBean.action.data.code)) {
            SpeechClient.instance().getAgent().sendScript(buttonBean.action.data.param.antlr);
        }
    }

    private void sendMsgToNotifyCardState(boolean isHidden, boolean isAnimEnd, String hitText, int delayTime) {
        this.mHandler.removeMessages(0);
        Message msg = this.mHandler.obtainMessage(0);
        CardState cardState = new CardState();
        cardState.mIsHidden = isHidden;
        cardState.mIsAnimEnd = isAnimEnd;
        cardState.mHitText = hitText;
        msg.obj = cardState;
        this.mHandler.sendMessageDelayed(msg, delayTime);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyCardState(CardState cardState) {
        try {
            JSONObject cardStateInfo = new JSONObject();
            cardStateInfo.put("isHiden", cardState.mIsHidden);
            cardStateInfo.put("isHitAnimEnd", cardState.mIsAnimEnd);
            cardStateInfo.put("hitText", cardState.mHitText);
            Logger.i(TAG, "notifyCardState : " + cardStateInfo.toString());
            SpeechClient.instance().getAgent().sendInfoFlowCardState(cardStateInfo.toString(), this.mSceneType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
