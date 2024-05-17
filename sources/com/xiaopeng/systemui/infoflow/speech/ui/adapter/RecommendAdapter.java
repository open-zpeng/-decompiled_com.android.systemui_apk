package com.xiaopeng.systemui.infoflow.speech.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.speech.protocol.bean.base.ButtonBean;
import com.xiaopeng.systemui.infoflow.util.CommonUtils;
/* loaded from: classes24.dex */
public class RecommendAdapter extends BaseRecyclerAdapter<ButtonBean> {
    private static final String TAG = "RecommendAdapter";
    private String mHitText;
    private View.OnTouchListener mOnTouchListener;

    public RecommendAdapter(Context context) {
        super(context);
        this.mOnTouchListener = new View.OnTouchListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.adapter.RecommendAdapter.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    view.setAlpha(0.5f);
                    return false;
                } else if (action == 1 || action == 3) {
                    view.setAlpha(1.0f);
                    return false;
                } else {
                    return false;
                }
            }
        };
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter
    public void bindData(BaseRecyclerAdapter<ButtonBean>.BaseViewHolder holder, ButtonBean buttonBean, int position) {
        TextView recommendText = (TextView) holder.getView(R.id.recommend_item_text);
        String strRecommend = CommonUtils.getRecommendText(buttonBean);
        recommendText.setText(strRecommend);
        recommendText.setOnTouchListener(this.mOnTouchListener);
        if (!TextUtils.isEmpty(this.mHitText)) {
            recommendText.setSelected(this.mHitText.equals(strRecommend));
        } else {
            recommendText.setSelected(false);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter
    public int getItemLayoutId() {
        return R.layout.item_recommend;
    }

    public void updateCurrentHit(String hitText) {
        this.mHitText = hitText;
    }
}
