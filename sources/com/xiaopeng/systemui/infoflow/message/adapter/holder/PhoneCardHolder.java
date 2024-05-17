package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.egg.utils.TimeUtils;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.PhoneCardHolder;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.bean.BluetoothInfo;
import com.xiaopeng.systemui.infoflow.message.presenter.PhoneCardPresenter;
import com.xiaopeng.systemui.infoflow.message.util.TimeUtil;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.utils.DataLogUtils;
/* loaded from: classes24.dex */
public class PhoneCardHolder extends BaseCardHolder {
    public static final String PHONE_CARD_KEY = "com.xiaopeng.systemui.phone_card_entry_key";
    private static final String TAG = PhoneCardHolder.class.getSimpleName();
    private TextView mContent;
    private boolean mIsConnected;
    private boolean mIsMissCall;
    private ImageView mMainIcon;
    private Runnable mTimeRunnable;
    private TextView mTimeTv;
    private TextView mTitle;
    private final String missed_call_des;
    private final long one_hour;
    private final long one_minute;

    public PhoneCardHolder(View view) {
        super(view);
        this.one_minute = TimeUtils.TIME_ONE_MINUTE;
        this.one_hour = TimeUtils.TIME_ONE_HOUR;
        this.mIsMissCall = false;
        this.mIsConnected = false;
        this.mTimeRunnable = new AnonymousClass1();
        view.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.-$$Lambda$-mZmdw_tl5-OLYiwKlShjUAYh_M
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhoneCardHolder.this.onClick(view2);
            }
        });
        this.mTitle = (TextView) view.findViewById(R.id.tv_title);
        this.mContent = (TextView) view.findViewById(R.id.tv_des);
        this.mTimeTv = (TextView) view.findViewById(R.id.tv_sub_des);
        this.mMainIcon = (ImageView) view.findViewById(R.id.img_phone_icon);
        this.missed_call_des = view.getContext().getString(R.string.missed_call);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public void bindData(CardEntry cardEntry) {
        String str = TAG;
        Logger.d(str, "bindData : content = " + cardEntry.content);
        this.itemView.setTag("phone");
        super.bindData(cardEntry);
        this.mIsMissCall = false;
        this.mData = cardEntry;
        String title = cardEntry.title;
        this.mTitle.setText(title);
        this.mContent.setText(cardEntry.content);
        if (isMissedCall(cardEntry.content)) {
            Logger.d(TAG, "missed call");
            this.mIsMissCall = true;
            this.mIsConnected = true;
            this.mTimeTv.setVisibility(0);
            this.mMainIcon.setImageResource(R.mipmap.ic_card_phone_miss);
            startCountTime();
        } else if (!TextUtils.isEmpty(cardEntry.extraData)) {
            this.mIsConnected = true;
            stopCountTime();
            this.mTimeTv.setVisibility(4);
            BluetoothInfo bluetoothInfo = (BluetoothInfo) GsonUtil.fromJson(cardEntry.extraData, (Class<Object>) BluetoothInfo.class);
            int batteryLevel = bluetoothInfo.batteryLevel;
            String str2 = TAG;
            Logger.d(str2, "battery level--" + batteryLevel);
            this.mMainIcon.setImageResource(R.mipmap.ic_card_phone_dial);
        } else {
            this.mIsConnected = false;
            this.mMainIcon.setImageResource(R.mipmap.ic_card_btphone);
            stopCountTime();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public int getCardType() {
        return 5;
    }

    private void startCountTime() {
        stopCountTime();
        this.mTimeTv.setText(R.string.passed_time_just_now);
        ThreadUtils.runOnMainThreadDelay(this.mTimeRunnable, TimeUtils.TIME_ONE_MINUTE);
    }

    private void stopCountTime() {
        ThreadUtils.removeRunnable(this.mTimeRunnable);
        this.mTimeTv.setText("");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.systemui.infoflow.message.adapter.holder.PhoneCardHolder$1  reason: invalid class name */
    /* loaded from: classes24.dex */
    public class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
        }

        @Override // java.lang.Runnable
        public void run() {
            PhoneCardHolder.this.updateTime();
            ThreadUtils.runOnMainThreadDelay(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.-$$Lambda$Rt0NFHW8Yyjgnvr4dNj5hnbxOmE
                @Override // java.lang.Runnable
                public final void run() {
                    PhoneCardHolder.AnonymousClass1.this.run();
                }
            }, TimeUtils.TIME_ONE_MINUTE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTime() {
        long currentTime = System.currentTimeMillis();
        long elipseTimeMillis = currentTime - this.mData.when;
        this.mTimeTv.setText(TimeUtil.getCardElapsedTimeDes(this.mContext, elipseTimeMillis));
    }

    private boolean isMissedCall(String title) {
        if (TextUtils.isEmpty(title)) {
            return false;
        }
        return title.contains(this.missed_call_des);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder, android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.mIsConnected) {
            DataLogUtils.sendInfoDataLog(DataLogUtils.INFO_BT_PAGE_ID, "B003", "", PhoneCardPresenter.getInstance().isAppForeground() ? "1" : "0", this.mIsMissCall ? "1" : "0");
        } else {
            DataLogUtils.sendInfoDataLog(DataLogUtils.INFO_BT_PAGE_ID, "B002");
        }
        Intent intent = new Intent();
        intent.setAction(this.mData.action);
        intent.setData(Uri.parse("tel:"));
        PackageHelper.startActivity(view.getContext(), intent, (Bundle) null);
    }
}
