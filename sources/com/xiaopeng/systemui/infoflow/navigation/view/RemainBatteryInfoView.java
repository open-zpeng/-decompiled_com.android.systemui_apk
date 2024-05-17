package com.xiaopeng.systemui.infoflow.navigation.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.carmanager.CarClientWrapper;
import com.xiaopeng.systemui.carmanager.impl.VcuController;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.systemui.infoflow.montecarlo.util.NaviUtil;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedLinearLayout;
import com.xiaopeng.xuimanager.contextinfo.RemainInfo;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/* loaded from: classes24.dex */
public class RemainBatteryInfoView extends AlphaOptimizedLinearLayout {
    public static final int DISTANCE_10KM = 10000;
    public static final int DISTANCE_1KM = 1000;
    public static final int DISTANCE_20KM = 20000;
    public static final int DISTANCE_30KM = 30000;
    public static final int DISTANCE_60KM = 60000;
    public static final int DIST_TYPE_1 = 1;
    public static final int DIST_TYPE_2 = 2;
    public static final int DIST_TYPE_3 = 3;
    public static final int DIST_TYPE_END = 0;
    private static final String TAG = RemainInfoView.class.getSimpleName();
    private TextView mArriveInfo;
    private int mCarAvailableMileage;
    private TextView mRemainBatteryInfo;
    private RemainInfo mRemainInfo;
    private double mRouterRemainTime;
    private boolean mShowArriveTime;
    private VcuController mVcuController;

    public RemainBatteryInfoView(Context context) {
        this(context, null);
    }

    public RemainBatteryInfoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mShowArriveTime = false;
        init();
    }

    private void init() {
        this.mVcuController = (VcuController) CarClientWrapper.getInstance().getController(CarClientWrapper.XP_VCU_SERVICE);
    }

    public void setData(RemainInfo remainInfo) {
        this.mRemainInfo = remainInfo;
        this.mCarAvailableMileage = getCarAvailableMileage();
        int carAvailableMeters = this.mCarAvailableMileage * 1000;
        refreshArriveInfo(remainInfo, carAvailableMeters);
        refreshBatteryInfo(remainInfo, carAvailableMeters);
    }

    public void setRouterRemainTime(double routerRemainTime) {
        this.mRouterRemainTime = routerRemainTime;
        if (this.mShowArriveTime) {
            showArriveTime();
        }
    }

    private void refreshArriveInfo(RemainInfo remainInfo, int availableMileage) {
        this.mShowArriveTime = false;
        int carRemainDistance = remainInfo.getCarRemainDist();
        if (carRemainDistance < 1000) {
            String unreachableString = getContext().getResources().getString(R.string.unreachable_des);
            String batteryLowColor = getContext().getResources().getString(R.string.colorBatteryWarning);
            SpannableString spannableString = new SpannableString(unreachableString);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor(batteryLowColor)), 0, spannableString.length(), 17);
            this.mArriveInfo.setText(spannableString);
        } else if (availableMileage < 20000) {
            String lowBatteryString = getContext().getResources().getString(R.string.low_available_mileage_des);
            String batteryLowColor2 = getContext().getResources().getString(R.string.colorBatteryLow);
            SpannableString spannableString2 = new SpannableString(lowBatteryString);
            spannableString2.setSpan(new ForegroundColorSpan(Color.parseColor(batteryLowColor2)), 0, spannableString2.length(), 17);
            this.mArriveInfo.setText(spannableString2);
        } else {
            int distType = remainInfo.getDistType();
            if (distType == 0) {
                showArriveTime();
                return;
            }
            String arriveTitle = getContext().getString(R.string.arrive_title);
            String arriveLabel = "";
            if (distType != 1) {
                if (distType == 2) {
                    arriveLabel = getContext().getString(R.string.route_label_via2_name);
                } else if (distType == 3) {
                    arriveLabel = getContext().getString(R.string.route_label_via3_name);
                }
            } else {
                arriveLabel = getContext().getString(R.string.route_label_via1_name);
            }
            String des = TextUtils.concat(arriveTitle, arriveLabel).toString();
            this.mArriveInfo.setText(des);
        }
    }

    private void showArriveTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(13, (int) this.mRouterRemainTime);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(calendar.getTime());
        String arriveTimeString = getContext().getString(R.string.arrive_time_title, time);
        SpannableString arriveTimeSpan = new SpannableString(arriveTimeString);
        this.mArriveInfo.setText(arriveTimeSpan);
        this.mShowArriveTime = true;
    }

    private void refreshBatteryInfo(RemainInfo remainInfo, int availableMileage) {
        int carRemainDistance = remainInfo.getCarRemainDist();
        if (carRemainDistance < 1000 || availableMileage < 20000) {
            this.mRemainBatteryInfo.setText("");
            return;
        }
        String colorString = getContext().getString(R.string.colorBatteryNormal);
        if (carRemainDistance < 30000) {
            colorString = getContext().getString(R.string.colorBatteryLow);
        }
        String distance = NaviUtil.getDistanceString(getContext(), remainInfo.getCarRemainDistDisplay(), remainInfo.getCarRemainDistUnitDisplay());
        String batteryString = getContext().getString(R.string.remain_battery, distance);
        this.mRemainBatteryInfo.setTextColor(Color.parseColor(colorString));
        this.mRemainBatteryInfo.setText(batteryString);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mArriveInfo = (TextView) findViewById(R.id.tv_arrive_info);
        this.mRemainBatteryInfo = (TextView) findViewById(R.id.tv_remain_battery);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedLinearLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.mRemainInfo != null && ThemeManager.isThemeChanged(newConfig)) {
            refreshBatteryInfo(this.mRemainInfo, this.mCarAvailableMileage);
        }
    }

    private int getCarAvailableMileage() {
        int carAvailableMileage = (int) CarController.getInstance(getContext()).getCarServiceAdapter().getDriveDistance();
        return carAvailableMileage;
    }
}
