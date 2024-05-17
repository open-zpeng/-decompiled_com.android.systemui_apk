package com.xiaopeng.systemui.qs.views;

import android.car.Car;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import com.airbnb.lottie.LottieAnimationView;
import com.android.systemui.R;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.systemui.controller.ThemeController;
import com.xiaopeng.systemui.qs.QsPanelSetting;
import com.xiaopeng.systemui.qs.TileState;
import com.xiaopeng.systemui.qs.XpTileFactory;
import com.xiaopeng.systemui.qs.tilemodels.XpTileModel;
import com.xiaopeng.systemui.qs.widgets.XTileButton;
import com.xiaopeng.systemui.utils.IntervalControl;
import com.xiaopeng.xui.widget.XImageView;
/* loaded from: classes24.dex */
public class ButtonView extends TileView implements View.OnClickListener, ThemeController.OnThemeListener {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private LottieAnimationView mAnimationView;
    private XTileButton mButton;
    private XImageView mCarModel;
    private IntervalControl mIntervalControl;
    private int mState = 0;
    private String TAG = ButtonView.class.getSimpleName();

    public ButtonView(Context context, TileState tileState) {
        this.mContext = context;
        this.mTileState = tileState;
        this.mIntervalControl = new IntervalControl(this.mTileState.key);
        ThemeController.getInstance(this.mContext).registerThemeListener(this);
        initView();
        initTile();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (!this.mIntervalControl.isFrequently(this.mTileState.quickClickSafeTime) && view.getId() == this.mButton.getId()) {
            this.mTile.click(-1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x007d, code lost:
        if (r0 != 5) goto L32;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateViewState(int r9) {
        /*
            Method dump skipped, instructions count: 329
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.systemui.qs.views.ButtonView.updateViewState(int):void");
    }

    @Override // androidx.lifecycle.LifecycleOwner
    @NonNull
    public Lifecycle getLifecycle() {
        return this.mLifecycleRegistry;
    }

    @Override // com.xiaopeng.systemui.qs.views.TileView
    public ViewGroup getView() {
        return this.mButton;
    }

    @Override // com.xiaopeng.systemui.qs.views.TileView
    public XpTileModel getTile() {
        return this.mTile;
    }

    @Override // com.xiaopeng.systemui.qs.views.TileView
    public void initView() {
        String str = this.TAG;
        Log.i(str, "" + this.mTileState.key);
        int resIdImg = this.mContext.getResources().getIdentifier(this.mTileState.resImg, "drawable", this.mContext.getPackageName());
        int resIdTxt = this.mContext.getResources().getIdentifier(this.mTileState.resTxt, "string", this.mContext.getPackageName());
        int resIdBg = this.mContext.getResources().getIdentifier(this.mTileState.resBg, "drawable", this.mContext.getPackageName());
        int width = (this.mTileState.width * (QsPanelSetting.ATOM_WIDTH + QsPanelSetting.H_GAP)) - QsPanelSetting.H_GAP;
        int height = (this.mTileState.height * (QsPanelSetting.ATOM_WIDTH + QsPanelSetting.V_GAP)) - QsPanelSetting.V_GAP;
        this.mButton = new XTileButton(this.mContext);
        if (this.mTileState.interactType.equals("largebutton")) {
            this.mButton.setLarge();
            this.mCarModel = new XImageView(this.mContext);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-1, -1);
            this.mCarModel.setLayoutParams(lp);
            setCarModelRes();
            this.mButton.addView(this.mCarModel);
        }
        if ((this.mTileState.key.equals("dc_charging_cover_switch") || this.mTileState.key.equals("ac_charging_cover_switch")) && this.mTileState.interactType.equals("largebutton")) {
            this.mButton.setTitleRes(R.string.qs_panel_chargeport);
            this.mButton.setTitleColor(R.color.quick_menu_text_icon_normal_color);
        }
        this.mButton.setImageResource(resIdImg);
        this.mButton.setTextRes(resIdTxt);
        this.mButton.setTextColor(R.color.quick_menu_text_icon_normal_color);
        this.mButton.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        this.mButton.setOnClickListener(this);
        this.mButton.setBackgroundRes(resIdBg);
        if (this.mTileState.jumpable == 1) {
            this.mButton.setRightCornerSignShow();
        }
        this.mAnimationView = (LottieAnimationView) this.mButton.findViewById(R.id.animation_view);
        if (this.mTileState.ifAnimatedIcon) {
            int ani = this.mContext.getResources().getIdentifier(this.mTileState.resImg, "raw", this.mContext.getPackageName());
            this.mAnimationView.setAnimation(ani);
            this.mAnimationView.setVisibility(0);
            this.mButton.setImageGone(true);
        }
    }

    @Override // com.xiaopeng.systemui.qs.views.TileView
    public void initTile() {
        this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        this.mTile = XpTileFactory.createTile(this.mTileState.key);
        this.mTile.getCurrentData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.qs.views.-$$Lambda$ButtonView$YKv55GncT9tgztQokRQZgWJKpNw
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                ButtonView.this.updateViewState(((Integer) obj).intValue());
            }
        });
    }

    @Override // com.xiaopeng.systemui.controller.ThemeController.OnThemeListener
    public void onThemeChanged(boolean selfChange, Uri uri) {
        this.mButton.refreshTheme();
        setCarModelRes();
    }

    private void setCarModelRes() {
        if (this.mCarModel != null) {
            String xpCduType = Car.getXpCduType();
            char c = 65535;
            switch (xpCduType.hashCode()) {
                case 2566:
                    if (xpCduType.equals(VuiUtils.CAR_PLATFORM_Q7)) {
                        c = 2;
                        break;
                    }
                    break;
                case 2567:
                    if (xpCduType.equals("Q8")) {
                        c = 0;
                        break;
                    }
                    break;
                case 2568:
                    if (xpCduType.equals("Q9")) {
                        c = 1;
                        break;
                    }
                    break;
            }
            if (c == 0) {
                this.mCarModel.setImageResource(R.drawable.icbg_qs_charge_e28a);
            } else if (c == 1) {
                this.mCarModel.setImageResource(R.drawable.icbg_qs_charge_f30);
            } else {
                this.mCarModel.setImageResource(R.drawable.icbg_qs_charge_e38);
            }
        }
        if (this.mTileState.ifAnimatedIcon) {
            int ani = this.mContext.getResources().getIdentifier(this.mTileState.resImg, "raw", this.mContext.getPackageName());
            this.mAnimationView.setAnimation(ani);
            updateViewState(this.mState);
        }
    }
}
