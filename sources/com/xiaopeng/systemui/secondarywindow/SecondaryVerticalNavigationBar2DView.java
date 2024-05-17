package com.xiaopeng.systemui.secondarywindow;

import android.view.View;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.systemui.navigationbar.INavigationBarPresenter;
import com.xiaopeng.systemui.navigationbar.VerticalNavigationBar2DView;
import com.xiaopeng.systemui.ui.widget.AnimatedProgressBar;
import com.xiaopeng.systemui.ui.widget.AnimatedTextView;
import com.xiaopeng.systemui.ui.widget.HvacComboView;
import com.xiaopeng.systemui.ui.widget.NavigationItemView;
import com.xiaopeng.systemui.ui.widget.TemperatureTextView;
import com.xiaopeng.systemui.ui.window.NavigationBarWindow;
import com.xiaopeng.systemui.utils.Utils;
import com.xiaopeng.xui.widget.XImageView;
/* loaded from: classes24.dex */
public class SecondaryVerticalNavigationBar2DView extends VerticalNavigationBar2DView implements View.OnClickListener {
    private static final String TAG = "SecondaryVerticalNavigationBar2DView";
    private NavigationItemView mBottom1;
    private NavigationItemView mBottom2;
    private XImageView mIvSeatHeat;
    private XImageView mIvSeatVent;
    private NavigationItemView mTop1;
    private NavigationItemView mTop2;

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected void addNavigationBar() {
        WindowHelper.addSecondaryNavigationBar(this.mWindowManager, this.mNavigationBarWindow);
    }

    @Override // com.xiaopeng.systemui.navigationbar.VerticalNavigationBar2DView, com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected NavigationBarWindow inflateNavigation() {
        return (NavigationBarWindow) View.inflate(this.mContext, R.layout.secondary_navigation_bar_vertical, null);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected INavigationBarPresenter getNavigationBarPresenter() {
        return PresenterCenter.getInstance().getNavigationBarPresenter(1);
    }

    @Override // com.xiaopeng.systemui.navigationbar.VerticalNavigationBar2DView, com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected void initItemViews() {
        if (this.mNavigationBarView != null) {
            this.mTop2 = (NavigationItemView) this.mNavigationBarView.findViewById(R.id.top2);
            this.mTop1 = (NavigationItemView) this.mNavigationBarView.findViewById(R.id.top1);
            this.mBottom1 = (NavigationItemView) this.mNavigationBarView.findViewById(R.id.bottom1);
            this.mBottom2 = (NavigationItemView) this.mNavigationBarView.findViewById(R.id.bottom2);
            this.mIvSeatVent = (XImageView) this.mNavigationBarView.findViewById(R.id.btn_seatvent);
            this.mIvSeatHeat = (XImageView) this.mNavigationBarView.findViewById(R.id.btn_seatheat);
            this.mTop2.setOnClickListener(this);
            this.mBottom1.setOnClickListener(this);
            if (CarModelsManager.getConfig().isSeatMessageSupport()) {
                this.mBottom2.setVisibility(0);
                this.mBottom2.setOnClickListener(this);
            }
            if (CarModelsManager.getConfig().isSeatHeatSupport()) {
                this.mTop1.setVisibility(0);
                this.mTop1.setOnClickListener(this);
            }
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.VerticalNavigationBar2DView, com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView, android.view.View.OnClickListener
    public void onClick(View view) {
        Logger.d(TAG, "onClick : id = " + view.getId());
        int id = view.getId();
        if (Utils.isFastClick()) {
            Logger.d(TAG, "isFastClick");
            return;
        }
        switch (id) {
            case R.id.bottom1 /* 2131362026 */:
                this.mNavigationBarPresenter.onNavigationButtonClicked(2);
                return;
            case R.id.bottom2 /* 2131362027 */:
                this.mNavigationBarPresenter.onNavigationButtonClicked(3);
                return;
            case R.id.top1 /* 2131363229 */:
                this.mNavigationBarPresenter.onNavigationButtonClicked(1);
                return;
            case R.id.top2 /* 2131363230 */:
                this.mNavigationBarPresenter.onNavigationButtonClicked(0);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.VerticalNavigationBar2DView, com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected TemperatureTextView getDriverTemperature() {
        return null;
    }

    @Override // com.xiaopeng.systemui.navigationbar.VerticalNavigationBar2DView, com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected AnimatedTextView getDriverSyncView() {
        return null;
    }

    @Override // com.xiaopeng.systemui.navigationbar.VerticalNavigationBar2DView, com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected AnimatedProgressBar getDriverProgressBar() {
        return null;
    }

    @Override // com.xiaopeng.systemui.navigationbar.VerticalNavigationBar2DView, com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected HvacComboView getDriverComboView() {
        return null;
    }

    @Override // com.xiaopeng.systemui.navigationbar.VerticalNavigationBar2DView, com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected TemperatureTextView getPassengerTemperature() {
        return (TemperatureTextView) this.mNavigationBarView.findViewById(R.id.quick_temperature);
    }

    @Override // com.xiaopeng.systemui.navigationbar.VerticalNavigationBar2DView, com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected AnimatedProgressBar getPassengerProgressBar() {
        return (AnimatedProgressBar) this.mNavigationBarView.findViewById(R.id.hvac_progress_bar);
    }

    @Override // com.xiaopeng.systemui.navigationbar.VerticalNavigationBar2DView, com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected AnimatedTextView getPassengerSyncView() {
        return (AnimatedTextView) this.mNavigationBarView.findViewById(R.id.havc_synchronized);
    }

    @Override // com.xiaopeng.systemui.navigationbar.VerticalNavigationBar2DView, com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected HvacComboView getPassengerComboView() {
        return (HvacComboView) this.mNavigationBarView.findViewById(R.id.hvac_view);
    }

    @Override // com.xiaopeng.systemui.navigationbar.VerticalNavigationBar2DView, com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView, com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setQuickTemperature(float temperature) {
        this.mPassengerTemperature.setText(temperature);
    }

    @Override // com.xiaopeng.systemui.navigationbar.VerticalNavigationBar2DView, com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setPsnSeatHeatLevel(int heatLevel) {
        XImageView xImageView = this.mIvSeatHeat;
        if (xImageView != null) {
            xImageView.setImageLevel(heatLevel);
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.VerticalNavigationBar2DView, com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setPsnSeatVentLevel(int ventLevel) {
        XImageView xImageView = this.mIvSeatVent;
        if (xImageView != null) {
            xImageView.setImageLevel(ventLevel);
        }
    }
}
