package com.xiaopeng.systemui.infoflow.montecarlo.view;

import android.content.Context;
import android.content.res.Configuration;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.TypefaceSpan;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeManager;
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
    public static final int DISTANCE_60KM = 60000;
    public static final int DIST_TYPE_1 = 1;
    public static final int DIST_TYPE_2 = 2;
    public static final int DIST_TYPE_3 = 3;
    public static final int DIST_TYPE_END = 0;
    private static final String TAG = RemainInfoView.class.getSimpleName();
    private TextView mArriveInfo;
    private TextView mRemainBatteryInfo;
    private RemainInfo mRemainInfo;
    private double mRouterRemainTime;

    public RemainBatteryInfoView(Context context) {
        super(context);
    }

    public RemainBatteryInfoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setData(RemainInfo remainInfo) {
        this.mRemainInfo = remainInfo;
        refreshArriveInfo(remainInfo);
        refreshBatteryInfo(remainInfo);
    }

    public void setRouterRemainTime(double routerRemainTime) {
        this.mRouterRemainTime = routerRemainTime;
    }

    private void refreshArriveInfo(RemainInfo remainInfo) {
        int carRemainDistance = remainInfo.getCarRemainDist();
        if (carRemainDistance < 1000) {
            this.mArriveInfo.setText(getContext().getResources().getString(R.string.unreachable_des));
            return;
        }
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

    private void showArriveTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(13, (int) this.mRouterRemainTime);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(calendar.getTime());
        String arriveTimeString = getContext().getString(R.string.arrive_time_title, time);
        SpannableString arriveTimeSpan = new SpannableString(arriveTimeString);
        arriveTimeSpan.setSpan(new AbsoluteSizeSpan(36), 2, time.length() + 2, 17);
        arriveTimeSpan.setSpan(new TypefaceSpan("xpeng-fonts-number"), 2, time.length() + 2, 17);
        this.mArriveInfo.setText(arriveTimeSpan);
    }

    private void refreshBatteryInfo(RemainInfo remainInfo) {
        int carRemainDistance = remainInfo.getCarRemainDist();
        if (carRemainDistance < 1000) {
            this.mRemainBatteryInfo.setText("");
            return;
        }
        String colorString = getContext().getString(R.string.colorBatteryNormal);
        if (carRemainDistance < 10000) {
            colorString = getContext().getString(R.string.colorBatteryWarning);
        } else if (carRemainDistance < 20000) {
            colorString = getContext().getString(R.string.colorBatteryLow);
        }
        String headString = getContext().getString(R.string.remain_battery);
        SpannableString headStringSpan = new SpannableString(headString);
        headStringSpan.setSpan(new AbsoluteSizeSpan(22), 0, headString.length(), 17);
        String distance = NaviUtil.getDistanceString(getContext(), carRemainDistance);
        SpannableString distanceSpan = NaviUtil.getDistanceSpannableStringWithColor(distance, 36, 22, colorString);
        this.mRemainBatteryInfo.setText(TextUtils.concat(headStringSpan, distanceSpan));
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
            refreshBatteryInfo(this.mRemainInfo);
        }
    }
}
