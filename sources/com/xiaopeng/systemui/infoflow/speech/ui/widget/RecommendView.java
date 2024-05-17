package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.R;
import com.xiaopeng.speech.protocol.bean.recommend.RecommendBean;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.infoflow.BaseInfoflow2DView;
import com.xiaopeng.systemui.infoflow.message.presenter.RecommendCardPresenter;
import com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter;
import com.xiaopeng.systemui.infoflow.speech.ui.adapter.RecommendAdapter;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedLinearLayout;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
import com.xiaopeng.systemui.infoflow.theme.AnimatedImageView;
import com.xiaopeng.systemui.infoflow.theme.AnimatedTextView;
import com.xiaopeng.systemui.infoflow.widget.ShimmerLayout;
/* loaded from: classes24.dex */
public class RecommendView extends ShimmerLayout {
    private static final String TAG = RecommendView.class.getSimpleName();
    private RecommendAdapter mAdapter;
    private AnimatedImageView mRecommendCardBackground;
    private AnimatedImageView mRecommendCardShadow;
    private AlphaOptimizedLinearLayout mRecommendCardVoiceLocContainer;
    private AnimatedTextView mRecommendCardVoiceLocHintDesc;
    private AnimatedTextView mRecommendCardVoiceLocHintTitle;
    private AlphaOptimizedRelativeLayout mRecommendScoreContainer;
    private RecyclerView mRecyclerView;
    private AnimatedTextView mScoreView;
    private AnimatedTextView mTipText;

    public RecommendView(Context context) {
        super(context);
    }

    public RecommendView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        this.mTipText = (AnimatedTextView) findViewById(R.id.tv_recommend_tip);
        this.mRecommendCardShadow = (AnimatedImageView) findViewById(R.id.img_shadow);
        this.mRecommendCardBackground = (AnimatedImageView) findViewById(R.id.recommend_card_bg);
        this.mRecommendCardVoiceLocContainer = (AlphaOptimizedLinearLayout) findViewById(R.id.recommend_card_voice_loc_hint_container);
        this.mRecommendCardVoiceLocHintTitle = (AnimatedTextView) findViewById(R.id.recommend_card_voice_loc_hint_title);
        this.mRecommendCardVoiceLocHintDesc = (AnimatedTextView) findViewById(R.id.recommend_card_voice_loc_hint_desc);
        this.mRecommendScoreContainer = (AlphaOptimizedRelativeLayout) findViewById(R.id.recommend_card_score_container);
        this.mScoreView = (AnimatedTextView) findViewById(R.id.recommend_card_score);
        if (this.mAdapter == null) {
            this.mAdapter = new RecommendAdapter(getContext());
        }
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.RecommendView.1
            @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter.OnItemClickListener
            public void onItemClick(BaseRecyclerAdapter adapter, View view, int position) {
                RecommendCardPresenter.getInstance().onRecommendCardItemClicked(position);
            }
        });
    }

    private void updateScene(int type) {
        if (type == 0) {
            this.mTipText.setText(R.string.try_to_say);
            this.mRecommendCardShadow.setImageResource(R.mipmap.bg_card_shadow);
            this.mRecommendCardBackground.setImageResource(R.mipmap.bg_card_recommend_small);
        } else if (type == 1) {
            this.mTipText.setText(R.string.speech_in_all_scene);
            this.mRecommendCardShadow.setImageResource(R.mipmap.bg_card_shadow_big);
            this.mRecommendCardBackground.setImageResource(R.mipmap.bg_card_recommend_big);
        }
        BaseInfoflow2DView.updateSceneType(type, this.mRecommendCardVoiceLocContainer, this.mRecommendCardVoiceLocHintTitle, this.mRecommendCardVoiceLocHintDesc);
    }

    private void updateScore(String score) {
        if (!TextUtils.isEmpty(score)) {
            if (!this.mRecommendScoreContainer.isVisibleToUser()) {
                this.mRecommendScoreContainer.setVisibility(0);
            }
            this.mScoreView.setText(score);
            return;
        }
        this.mRecommendScoreContainer.setVisibility(8);
    }

    public void updateList(RecommendBean recommendBean) {
        if (recommendBean.isHit()) {
            String hitText = recommendBean.getHitText();
            this.mAdapter.updateCurrentHit(hitText);
        } else {
            this.mAdapter.updateCurrentHit(null);
        }
        this.mAdapter.setNewData(recommendBean.getRelateList());
        if (!CarModelsManager.getFeature().isMultiplayerVoiceSupport()) {
            updateScene(recommendBean.getCardType());
        }
        updateScore(recommendBean.getHitTips());
    }
}
