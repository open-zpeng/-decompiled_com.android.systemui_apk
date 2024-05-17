package com.xiaopeng.systemui.navigationbar;

import android.view.View;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.ItemController;
import com.xiaopeng.systemui.ui.widget.AnimatedProgressBar;
import com.xiaopeng.systemui.ui.widget.AnimatedTextView;
import com.xiaopeng.systemui.ui.widget.HvacComboView;
import com.xiaopeng.systemui.ui.widget.NavigationItemView;
import com.xiaopeng.systemui.ui.widget.TemperatureTextView;
import com.xiaopeng.systemui.ui.window.NavigationBarWindow;
import com.xiaopeng.systemui.utils.Utils;
/* loaded from: classes24.dex */
public class VerticalNavigationBar2DView extends AbstractNavigationBar2DView implements View.OnClickListener {
    private static final String TAG = "VerticalNavigationBar2D";

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected View getBackDefrostView() {
        return null;
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected View getFrontDefrostView() {
        return null;
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected AnimatedTextView getDriverSyncView() {
        return (AnimatedTextView) this.mNavigationBarView.findViewById(R.id.havc_synchronized);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected AnimatedTextView getPassengerSyncView() {
        return null;
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected AnimatedProgressBar getDriverProgressBar() {
        return (AnimatedProgressBar) this.mNavigationBarView.findViewById(R.id.hvac_progress_bar);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected AnimatedProgressBar getPassengerProgressBar() {
        return null;
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected TemperatureTextView getDriverTemperature() {
        return (TemperatureTextView) this.mNavigationBarView.findViewById(R.id.quick_temperature);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected TemperatureTextView getPassengerTemperature() {
        return null;
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected HvacComboView getDriverComboView() {
        return (HvacComboView) this.mNavigationBarView.findViewById(R.id.hvac_view);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected HvacComboView getPassengerComboView() {
        return null;
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected NavigationBarWindow inflateNavigation() {
        return (NavigationBarWindow) View.inflate(this.mContext, R.layout.navigation_bar_vertical, null);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView, android.view.View.OnClickListener
    public void onClick(View view) {
        Logger.d(TAG, "onClick : id = " + view.getId());
        int id = view.getId();
        if (Utils.isFastClick()) {
            Logger.d(TAG, "isFastClick");
            return;
        }
        if (view.isSelected() && id != R.id.havc_synchronized) {
            view.setSelected(false);
        }
        switch (id) {
            case R.id.action /* 2131361873 */:
            case R.id.navigation_bar /* 2131362756 */:
                this.mNavigationBarPresenter.onNavigationBarClicked();
                return;
            case R.id.bottom1 /* 2131362026 */:
                this.mNavigationBarPresenter.onNavigationButtonClicked(3);
                return;
            case R.id.bottom2 /* 2131362027 */:
                this.mNavigationBarPresenter.onNavigationButtonClicked(4);
                return;
            case R.id.havc_synchronized /* 2131362359 */:
                this.mNavigationBarPresenter.onHvacSynchronizedClicked();
                return;
            case R.id.top1 /* 2131363229 */:
                this.mNavigationBarPresenter.onNavigationButtonClicked(2);
                return;
            case R.id.top2 /* 2131363230 */:
                this.mNavigationBarPresenter.onNavigationButtonClicked(1);
                return;
            case R.id.top3 /* 2131363231 */:
                this.mNavigationBarPresenter.onNavigationButtonClicked(0);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected void initItemViews() {
        if (this.mNavigationBarView != null) {
            NavigationItemView top3 = (NavigationItemView) this.mNavigationBarView.findViewById(R.id.top3);
            NavigationItemView top2 = (NavigationItemView) this.mNavigationBarView.findViewById(R.id.top2);
            NavigationItemView top1 = (NavigationItemView) this.mNavigationBarView.findViewById(R.id.top1);
            NavigationItemView bottom1 = (NavigationItemView) this.mNavigationBarView.findViewById(R.id.bottom1);
            NavigationItemView bottom2 = (NavigationItemView) this.mNavigationBarView.findViewById(R.id.bottom2);
            top3.setTag(this.mContext.getString(R.string.component_app));
            top2.setTag(this.mContext.getString(ItemController.VerticalNavigationItem.sTop2.tagId));
            top1.setTag(this.mContext.getString(ItemController.VerticalNavigationItem.sTop1.tagId));
            bottom1.setTag(this.mContext.getString(ItemController.VerticalNavigationItem.sBottom1.tagId));
            bottom2.setTag(this.mContext.getString(ItemController.VerticalNavigationItem.sBottom2.tagId));
            this.mItemViews.add(top3);
            this.mItemViews.add(top2);
            this.mItemViews.add(top1);
            this.mItemViews.add(bottom1);
            this.mItemViews.add(bottom2);
            top3.setOnClickListener(this);
            top2.setOnClickListener(this);
            top1.setOnClickListener(this);
            bottom1.setOnClickListener(this);
            bottom2.setOnClickListener(this);
            top3.setImageResource(R.drawable.ic_navbar_item_app_list);
            top2.setImageResource(ItemController.VerticalNavigationItem.sTop2.resId);
            top1.setImageResource(ItemController.VerticalNavigationItem.sTop1.resId);
            bottom1.setImageResource(ItemController.VerticalNavigationItem.sBottom1.resId);
            bottom2.setImageResource(ItemController.VerticalNavigationItem.sBottom2.resId);
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView, com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setQuickTemperature(float temperature) {
        this.mDriverTemperature.setText(temperature);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView, com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setPurgeMode(boolean purgeMode, boolean isHvacAuto, int hvacQuality) {
        Logger.i(TAG, "updatePurgeMode : " + purgeMode);
        this.mDriverComboView.setShowWind(purgeMode ^ true);
        this.mDriverComboView.setShowAuto(purgeMode ^ true);
        this.mAirPurgeView.setVisibility(purgeMode ? 0 : 8);
        this.mDriverComboView.setWindVisibility(purgeMode ? 8 : 0);
        this.mDriverComboView.setAuto(isHvacAuto);
        this.mAirPurgeView.setQualityInner(hvacQuality);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setAutoDefog(boolean autoDefog) {
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setPsnSeatHeatLevel(int heatLevel) {
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setPsnSeatVentLevel(int ventLevel) {
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView, com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setInnerQuality(int innerQuality, String qualityContent) {
        if (this.mAirPurgeView != null && this.mAirPurgeView.getVisibility() == 0) {
            this.mAirPurgeView.setQualityInner(innerQuality);
        }
    }
}
