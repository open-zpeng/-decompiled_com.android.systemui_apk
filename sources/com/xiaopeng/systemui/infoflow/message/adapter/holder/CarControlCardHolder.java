package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.view.View;
import android.view.ViewStub;
import com.android.systemui.R;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.carmanager.impl.VcuControllerWrapper;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.checking.CarCheckHelper;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.ui.widget.XTabLayout;
import com.xiaopeng.systemui.utils.Utils;
/* loaded from: classes24.dex */
public class CarControlCardHolder extends BaseCardHolder {
    private View mBtnOpenAc;
    private View mBtnOpenDc;
    private View mBtnOpenTrunk;
    private CarController mCarController;
    private XTabLayout mDriveModeSelector;
    private View mViewGearNotP;
    private View mViewGearP;
    private ViewStub mViewStubGearNotP;
    private ViewStub mViewStubGearP;

    public CarControlCardHolder(View itemView) {
        super(itemView);
        this.mCarController = CarController.getInstance(this.mContext);
        this.mCarController.addCallback(new CarController.CarCallback() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.CarControlCardHolder.1
            @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
            public void onCarControlChanged(int type, final Object newValue) {
                if (type == 3106) {
                    ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.CarControlCardHolder.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            CarControlCardHolder.this.selectDriveMode(((Integer) newValue).intValue());
                        }
                    });
                }
            }

            @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
            public void onCarServiceChanged(int type, Object newValue) {
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.-$$Lambda$9KcRo21dLau4-2Gr_ZnICntadbQ
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CarControlCardHolder.this.onClick(view);
            }
        });
        this.mViewStubGearP = (ViewStub) itemView.findViewById(R.id.stub_card_car_control_gear_p);
        this.mViewStubGearNotP = (ViewStub) itemView.findViewById(R.id.stub_card_car_control_gear_not_p);
        checkToShowCarControlView();
        VcuControllerWrapper.getInstance().addListener(new VcuControllerWrapper.Listener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.CarControlCardHolder.2
            @Override // com.xiaopeng.systemui.carmanager.impl.VcuControllerWrapper.Listener
            public void onGearChanged(int gear) {
                ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.CarControlCardHolder.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        CarControlCardHolder.this.checkToShowCarControlView();
                    }
                });
            }
        });
    }

    private int getTabIndexByDriveMode(int driveMode) {
        if (driveMode != 0) {
            if (driveMode != 1) {
                return driveMode != 2 ? -1 : 1;
            }
            return 2;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkToShowCarControlView() {
        if (CarCheckHelper.isGearParking()) {
            View view = this.mViewGearP;
            if (view == null) {
                this.mViewGearP = this.mViewStubGearP.inflate();
                this.mBtnOpenDc = this.mViewGearP.findViewById(R.id.btn_open_dc);
                this.mBtnOpenAc = this.mViewGearP.findViewById(R.id.btn_open_ac);
                this.mBtnOpenTrunk = this.mViewGearP.findViewById(R.id.btn_open_trunk);
                this.mBtnOpenTrunk.setOnClickListener(this);
                this.mBtnOpenDc.setOnClickListener(this);
                this.mBtnOpenAc.setOnClickListener(this);
            } else {
                view.setVisibility(0);
            }
            View view2 = this.mViewGearNotP;
            if (view2 != null) {
                view2.setVisibility(8);
                return;
            }
            return;
        }
        View view3 = this.mViewGearNotP;
        if (view3 == null) {
            this.mViewGearNotP = this.mViewStubGearNotP.inflate();
            this.mDriveModeSelector = (XTabLayout) this.mViewGearNotP.findViewById(R.id.drive_mode_selector);
            this.mDriveModeSelector.setOnTabChangeListener(new XTabLayout.OnTabChangeListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.CarControlCardHolder.3
                @Override // com.xiaopeng.systemui.ui.widget.XTabLayout.OnTabChangeListener
                public boolean onInterceptTabChange(XTabLayout tabLayout, int index, boolean tabChange, boolean fromUser) {
                    if (fromUser && tabChange) {
                        if (index == 0) {
                            CarControlCardHolder.this.mCarController.setDriveMode(0);
                        } else if (index == 1) {
                            CarControlCardHolder.this.mCarController.setDriveMode(2);
                        } else if (index == 2) {
                            CarControlCardHolder.this.mCarController.setDriveMode(1);
                        }
                    }
                    return fromUser;
                }

                @Override // com.xiaopeng.systemui.ui.widget.XTabLayout.OnTabChangeListener
                public void onTabChangeStart(XTabLayout tabLayout, int index, boolean tabChange, boolean fromUser) {
                }

                @Override // com.xiaopeng.systemui.ui.widget.XTabLayout.OnTabChangeListener
                public void onTabChangeEnd(XTabLayout tabLayout, int index, boolean tabChange, boolean fromUser) {
                }
            });
        } else {
            view3.setVisibility(0);
        }
        View view4 = this.mViewGearP;
        if (view4 != null) {
            view4.setVisibility(8);
        }
        selectDriveMode(this.mCarController.getCarControlAdapter().getDrivingMode());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectDriveMode(int driveMode) {
        if (this.mDriveModeSelector != null) {
            int index = getTabIndexByDriveMode(driveMode);
            if (index == -1) {
                this.mDriveModeSelector.selectedNoneTab(false, false);
            } else {
                this.mDriveModeSelector.selectTab(index);
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public String getCardPackageName() {
        return VuiConstants.CARCONTROL;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder, android.view.View.OnClickListener
    public void onClick(View view) {
        if (Utils.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_open_ac /* 2131362075 */:
                CarController.getInstance(this.mContext).openChargePort(true);
                return;
            case R.id.btn_open_dc /* 2131362076 */:
                CarController.getInstance(this.mContext).openChargePort(false);
                return;
            case R.id.btn_open_trunk /* 2131362077 */:
                CarController.getInstance(this.mContext).openRearTrunk();
                return;
            default:
                PackageHelper.startCarControl(this.mContext);
                return;
        }
    }
}
