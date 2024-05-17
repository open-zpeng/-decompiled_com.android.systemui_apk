package com.xiaopeng.systemui.qs.views;

import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import com.airbnb.lottie.LottieAnimationView;
import com.android.systemui.R;
import com.xiaopeng.systemui.controller.ThemeController;
import com.xiaopeng.systemui.qs.QsPanelSetting;
import com.xiaopeng.systemui.qs.TileState;
import com.xiaopeng.systemui.qs.XpTileFactory;
import com.xiaopeng.systemui.qs.XpTilesConfig;
import com.xiaopeng.systemui.qs.tilemodels.XpTileModel;
import com.xiaopeng.systemui.qs.widgets.QcSliderLayout;
import com.xiaopeng.xui.widget.XImageView;
import com.xiaopeng.xui.widget.XTextView;
/* loaded from: classes24.dex */
public class SliderView extends TileView implements QcSliderLayout.OnSlideChangeListener, ThemeController.OnThemeListener {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private LottieAnimationView mAnimationView;
    private XImageView mImageView;
    private QcSliderLayout mSlider;
    private XTextView mTextView;
    private XpTileModel mVolumeTypeTile;

    public SliderView(Context context, TileState tileState) {
        this.mContext = context;
        this.mTileState = tileState;
        ThemeController.getInstance(this.mContext).registerThemeListener(this);
        initView();
        initTile();
        this.mSlider.setProgress(this.mTile.getCurrentData().getValue().intValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateViewState(int state) {
        QcSliderLayout qcSliderLayout = this.mSlider;
        if (qcSliderLayout != null) {
            qcSliderLayout.setProgress(state);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMediaType(int state) {
        XImageView xImageView = this.mImageView;
        if (xImageView != null) {
            xImageView.setImageLevel(state);
        }
        XTextView xTextView = this.mTextView;
        if (xTextView != null) {
            if (state == 0) {
                xTextView.setText(R.string.qs_panel_volume_0);
            } else if (state == 1) {
                xTextView.setText(R.string.qs_panel_volume_1);
            } else if (state == 2) {
                xTextView.setText(R.string.qs_panel_volume_2);
            }
        }
    }

    @Override // androidx.lifecycle.LifecycleOwner
    @NonNull
    public Lifecycle getLifecycle() {
        return this.mLifecycleRegistry;
    }

    @Override // com.xiaopeng.systemui.qs.widgets.QcSliderLayout.OnSlideChangeListener
    public void onProgressChanged(QcSliderLayout qcSliderLayout, int progress) {
        this.mTile.click(progress);
    }

    @Override // com.xiaopeng.systemui.qs.widgets.QcSliderLayout.OnSlideChangeListener
    public void onStartTrackingTouch(QcSliderLayout qcSliderLayout) {
        this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    @Override // com.xiaopeng.systemui.qs.widgets.QcSliderLayout.OnSlideChangeListener
    public void onStopTrackingTouch(QcSliderLayout qcSliderLayout) {
        LottieAnimationView lottieAnimationView;
        this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        if (this.mTileState.ifAnimatedIcon && (lottieAnimationView = this.mAnimationView) != null) {
            lottieAnimationView.playAnimation();
        }
    }

    @Override // com.xiaopeng.systemui.qs.views.TileView
    public ViewGroup getView() {
        return this.mSlider;
    }

    @Override // com.xiaopeng.systemui.qs.views.TileView
    public XpTileModel getTile() {
        return this.mTile;
    }

    @Override // com.xiaopeng.systemui.qs.views.TileView
    public void initView() {
        int resIdImg = this.mContext.getResources().getIdentifier(this.mTileState.resImg, "drawable", this.mContext.getPackageName());
        int resIdTxt = this.mContext.getResources().getIdentifier(this.mTileState.resTxt, "string", this.mContext.getPackageName());
        int width = (this.mTileState.width * (QsPanelSetting.ATOM_WIDTH + QsPanelSetting.H_GAP)) - QsPanelSetting.H_GAP;
        int height = (this.mTileState.height * (QsPanelSetting.ATOM_WIDTH + QsPanelSetting.V_GAP)) - QsPanelSetting.V_GAP;
        this.mTextView = new XTextView(this.mContext);
        this.mSlider = new QcSliderLayout(this.mContext, null, R.style.QcSliderLayout8155, R.style.QcSliderLayout8155);
        this.mSlider.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        this.mSlider.setColor(R.color.quick_menu_ui_background, R.color.quick_menu_ui_slider);
        this.mTextView.setText(resIdTxt);
        this.mTextView.setTextSize(2.13116672E9f);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        lp.setMargins(32, 145, -1, -1);
        this.mTextView.setLayoutParams(lp);
        this.mTextView.setTextSize(this.mContext.getResources().getDimensionPixelSize(R.dimen.x_font_body_02_size));
        this.mTextView.setTextColor(this.mContext.getResources().getColor(R.color.quick_menu_text_icon_normal_color, null));
        this.mSlider.addView(this.mTextView);
        this.mSlider.setOnSlideChangeListener(this);
        if (this.mTileState.ifAnimatedIcon) {
            this.mAnimationView = new LottieAnimationView(this.mContext);
            int ani = this.mContext.getResources().getIdentifier(this.mTileState.resImg, "raw", this.mContext.getPackageName());
            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(-2, -2);
            lp2.setMargins(32, 32, -1, -1);
            this.mAnimationView.setLayoutParams(lp2);
            this.mAnimationView.setAnimation(ani);
            this.mSlider.addView(this.mAnimationView);
            return;
        }
        this.mImageView = new XImageView(this.mContext);
        this.mImageView.setImageResource(resIdImg);
        RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(-2, -2);
        lp3.setMargins(32, 32, -1, -1);
        this.mImageView.setLayoutParams(lp3);
        this.mSlider.addView(this.mImageView);
    }

    @Override // com.xiaopeng.systemui.qs.views.TileView
    public void initTile() {
        this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        this.mTile = XpTileFactory.createTile(this.mTileState.key);
        this.mTile.getCurrentData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.qs.views.-$$Lambda$SliderView$DNLNcWE7qrlIpckPQWYWr8uwpJc
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                SliderView.this.updateViewState(((Integer) obj).intValue());
            }
        });
        if (this.mTileState.key.equals("passenger_volume_adjustment")) {
            this.mVolumeTypeTile = XpTileFactory.createTile(XpTilesConfig.MEDIA_TYPE);
            this.mVolumeTypeTile.getCurrentData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.qs.views.-$$Lambda$SliderView$765M-UsEYKJDCI1CLA7lJ7l26pc
                @Override // androidx.lifecycle.Observer
                public final void onChanged(Object obj) {
                    SliderView.this.updateMediaType(((Integer) obj).intValue());
                }
            });
        }
    }

    @Override // com.xiaopeng.systemui.controller.ThemeController.OnThemeListener
    public void onThemeChanged(boolean selfChange, Uri uri) {
        this.mTextView.setTextColor(this.mContext.getResources().getColor(R.color.quick_menu_text_icon_normal_color, null));
        int resIdImg = this.mContext.getResources().getIdentifier(this.mTileState.resImg, "drawable", this.mContext.getPackageName());
        if (this.mTileState.ifAnimatedIcon) {
            int ani = this.mContext.getResources().getIdentifier(this.mTileState.resImg, "raw", this.mContext.getPackageName());
            this.mAnimationView.setAnimation(ani);
        } else {
            this.mImageView.setImageResource(resIdImg);
        }
        this.mSlider.refreshTheme();
    }
}
