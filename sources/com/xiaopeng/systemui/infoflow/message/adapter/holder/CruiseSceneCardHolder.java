package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.infoflow.ICruiseSceneCardView;
import com.xiaopeng.systemui.infoflow.widget.SplitLinearLayout;
import com.xiaopeng.xuimanager.contextinfo.HomeCompanyRouteInfo;
import java.util.List;
/* loaded from: classes24.dex */
public class CruiseSceneCardHolder extends BaseCardHolder implements ICruiseSceneCardView {
    private static final String TAG = "CruiseSceneCardHolder";
    private final int MAX_ACTION_SIZE;
    View.OnClickListener mActionBtnClickListener;
    private ImageView[] mActionBtns;
    private int mActionNum;
    private TextView mContentTv;
    private ImageView mFirstActionImage;
    private ImageView mSecondActionImage;
    private SplitLinearLayout mSplitLinearLayout;
    private ImageView mThirdActionImage;
    private TextView mTitleTv;

    public CruiseSceneCardHolder(View itemView) {
        super(itemView);
        this.MAX_ACTION_SIZE = 3;
        this.mActionBtns = new ImageView[3];
        this.mActionNum = 0;
        this.mActionBtnClickListener = new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.CruiseSceneCardHolder.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.img_action_first /* 2131362413 */:
                        CruiseSceneCardHolder.this.mInfoflowCardPresenter.onActionClicked(1);
                        return;
                    case R.id.img_action_second /* 2131362414 */:
                        CruiseSceneCardHolder.this.mInfoflowCardPresenter.onActionClicked(2);
                        return;
                    case R.id.img_action_third /* 2131362415 */:
                        CruiseSceneCardHolder.this.mInfoflowCardPresenter.onActionClicked(3);
                        return;
                    default:
                        return;
                }
            }
        };
        Logger.d(TAG, TAG);
        itemView.setTag("cruiseScene");
        initView(itemView);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public int getCardType() {
        return 25;
    }

    private void initView(View view) {
        this.mTitleTv = (TextView) view.findViewById(R.id.tv_title);
        this.mContentTv = (TextView) view.findViewById(R.id.tv_des);
        this.mSplitLinearLayout = (SplitLinearLayout) view.findViewById(R.id.split_action_view);
        this.mFirstActionImage = (ImageView) view.findViewById(R.id.img_action_first);
        this.mSecondActionImage = (ImageView) view.findViewById(R.id.img_action_second);
        this.mThirdActionImage = (ImageView) view.findViewById(R.id.img_action_third);
        this.mFirstActionImage.setOnClickListener(this.mActionBtnClickListener);
        this.mSecondActionImage.setOnClickListener(this.mActionBtnClickListener);
        this.mThirdActionImage.setOnClickListener(this.mActionBtnClickListener);
        ImageView[] imageViewArr = this.mActionBtns;
        imageViewArr[0] = this.mFirstActionImage;
        imageViewArr[1] = this.mSecondActionImage;
        imageViewArr[2] = this.mThirdActionImage;
    }

    @Override // com.xiaopeng.systemui.infoflow.ICruiseSceneCardView
    public void setCruiseSceneCardBtnImages(List<String> images) {
        if (this.mTitleTv != null && this.mData != null) {
            this.mTitleTv.setText(this.mData.title);
            this.mContentTv.setText(this.mData.content);
        }
        for (ImageView imageView : this.mActionBtns) {
            imageView.setVisibility(8);
        }
        if (images != null && images.size() > 1) {
            for (int i = 0; i < images.size(); i++) {
                ImageView imageView2 = this.mActionBtns[i];
                imageView2.setVisibility(0);
                byte[] imageBytes = Base64.decode(images.get(i), 0);
                if (imageBytes != null) {
                    imageView2.setImageBitmap(getBitmap(imageBytes));
                }
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.ICruiseSceneCardView
    public void setCruiseSceneCardRouteInfo(HomeCompanyRouteInfo routeInfo) {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder, android.view.View.OnClickListener
    public void onClick(View view) {
        Log.i(TAG, "on Click");
        if (this.mActionNum > 0) {
            this.mInfoflowCardPresenter.onActionClicked(0);
        } else {
            this.mInfoflowCardPresenter.onCardClicked();
        }
    }

    private Bitmap getBitmap(byte[] data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        return bitmap;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public void enableItemView(boolean appOpen) {
    }

    @Override // com.xiaopeng.systemui.infoflow.ICruiseSceneCardView
    public void setCruiseSceneCardActionNum(int actionNum) {
        this.mActionNum = actionNum;
        this.mSplitLinearLayout.setActionNum(actionNum);
    }
}
