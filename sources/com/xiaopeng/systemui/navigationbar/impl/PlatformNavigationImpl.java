package com.xiaopeng.systemui.navigationbar.impl;

import android.content.Context;
import android.text.TextUtils;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.ViewFactory;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.systemui.controller.ItemController;
import com.xiaopeng.systemui.navigationbar.HvacInfo;
import com.xiaopeng.systemui.navigationbar.INavigationBarView;
import com.xiaopeng.systemui.utils.BIHelper;
/* loaded from: classes24.dex */
public class PlatformNavigationImpl extends AbstractNavigationImpl {
    private static final String TAG = "PlatformNavigationImpl";
    private ItemController.ItemInfo mLeftItemInfo1;
    private ItemController.ItemInfo mLeftItemInfo2;

    @Override // com.xiaopeng.systemui.navigationbar.impl.AbstractNavigationImpl
    protected INavigationBarView createNavigationBarView() {
        return ViewFactory.getPlatformNavigationBarView();
    }

    public PlatformNavigationImpl(Context context) {
        super(context);
        ItemController.NavigationItemFactory navigationItemFactory = ItemController.NavigationItemFactory.getInstance();
        this.mLeftItemInfo1 = navigationItemFactory.getLeftItemInfo(1);
        this.mLeftItemInfo2 = navigationItemFactory.getLeftItemInfo(2);
    }

    private void setQuickTemperature(int type, Object value) {
        if (type == 2104) {
            float driverTemperature = this.mHvacViewModel.getHvacDriverTemperature();
            if (value != null) {
                try {
                    driverTemperature = Float.parseFloat(value.toString());
                } catch (Exception e) {
                }
            }
            this.mNavigationBarView.setDriverTemperature(driverTemperature);
        } else if (type == 2106) {
            float passengerTemperature = this.mHvacViewModel.getHvacPassengerTemperature();
            if (value != null) {
                try {
                    passengerTemperature = Float.parseFloat(value.toString());
                } catch (Exception e2) {
                }
            }
            this.mNavigationBarView.setPassengerTemperature(passengerTemperature);
        }
    }

    private void updateInnerQuality(int innerQuality) {
        int innerQuality2 = innerQuality < 0 ? 1023 : innerQuality;
        String quality = innerQuality2 == 1023 ? "--" : String.valueOf(innerQuality2);
        String content = quality;
        if (innerQuality2 != 1023) {
            content = content + " " + CarController.getInnerQualityContent(this.mContext, innerQuality2);
        }
        this.mNavigationBarView.setInnerQuality(innerQuality2, content);
    }

    @Override // com.xiaopeng.systemui.navigationbar.impl.AbstractNavigationImpl, com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public void onDockHvacLongClicked() {
        this.mHvacHandler.setPower(!this.mHvacViewModel.isHvacPowerOn());
    }

    private void onHvacItemStateChanged(int type, Object value) {
        Logger.d(TAG, "onHvacItemStateChanged type=" + type + " value=" + value);
        if (value != null) {
            try {
                if (!TextUtils.isEmpty(value.toString())) {
                    int wind = this.mHvacViewModel.getHvacWindSpeed();
                    boolean powerOn = this.mHvacViewModel.isHvacPowerOn();
                    boolean isHvacAuto = this.mHvacViewModel.isHvacAuto();
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("onHvacItemStateChanged");
                    buffer.append(" wind=" + wind);
                    buffer.append(" powerOn=" + powerOn);
                    buffer.append(" isHvacAuto=" + isHvacAuto);
                    Logger.d(TAG, buffer.toString());
                    if (type == 2101 || type == 2102 || type == 2110) {
                        HvacInfo hvacInfo = new HvacInfo();
                        hvacInfo.isAuto = isHvacAuto && powerOn;
                        hvacInfo.isPowerOn = powerOn;
                        this.mNavigationBarView.setHvacInfo(hvacInfo);
                    } else if (type == 2111) {
                        updateInnerQuality(Integer.parseInt(value.toString()));
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public void onNavigationButtonClicked(int btnIndex) {
        if (btnIndex == 1) {
            this.mLeftItemInfo2.onClicked(this.mContext);
        } else if (btnIndex == 2) {
            this.mLeftItemInfo1.onClicked(this.mContext);
        } else if (btnIndex == 6) {
            this.mHvacHandler.setDefrost(true, !this.mHvacViewModel.isHvacFrontDefrostOn());
            BIHelper.sendBIData(BIHelper.ID.defrost, BIHelper.Type.dock, this.mHvacViewModel.isHvacFrontDefrostOn() ? BIHelper.Action.close : BIHelper.Action.open, BIHelper.Screen.main);
        } else if (btnIndex == 7) {
            this.mHvacHandler.setDefrost(false, true ^ this.mHvacViewModel.isHvacBackDefrostOn());
            BIHelper.sendBIData(BIHelper.ID.mirror_heat, BIHelper.Type.dock, this.mHvacViewModel.isHvacBackDefrostOn() ? BIHelper.Action.close : BIHelper.Action.open, BIHelper.Screen.main);
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public String getHvacInfo() {
        return null;
    }

    @Override // com.xiaopeng.systemui.navigationbar.impl.AbstractNavigationImpl, com.xiaopeng.systemui.navigationbar.NavigationHvacHandler.OnHvacListener
    public void onHvacItemChanged(int type, Object value) {
        if (type != 2101 && type != 2102) {
            if (type == 2104 || type == 2106) {
                setQuickTemperature(type, value);
                return;
            } else if (type != 2114 && type != 2110 && type != 2111) {
                return;
            }
        }
        onHvacItemStateChanged(type, value);
    }
}
