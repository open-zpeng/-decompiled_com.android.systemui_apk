package com.xiaopeng.systemui.infoflow.message.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.android.systemui.R;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.INotificationCardView;
import com.xiaopeng.systemui.infoflow.egg.utils.TimeUtils;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.NotificationList;
import com.xiaopeng.systemui.infoflow.message.presenter.NotificationCardPresenter;
import com.xiaopeng.systemui.infoflow.message.util.TimeUtil;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.utils.DataLogUtils;
import com.xiaopeng.systemui.utils.Utils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/* loaded from: classes24.dex */
public class NotificationCardPresenter extends BaseCardPresenter {
    public static final String MESSAGE_URI = "xp://notification/detail?category=system&id=";
    private static final int MSG_UPDATE_NOTIFICATION = 1;
    private static final String TAG = "NotificationCardPresent";
    private boolean mIsDefaultNotification;
    private INotificationCardView mNotificationView;
    private final long one_minute = TimeUtils.TIME_ONE_MINUTE;
    private Handler mHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.NotificationCardPresenter.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                NotificationCardPresenter.this.updateNotification();
            }
        }
    };
    private Runnable mTimeRunnable = new AnonymousClass2();
    private String noNotification = this.mContext.getString(R.string.no_notification);
    private String[] mWeekArrays = this.mContext.getResources().getStringArray(R.array.msg_week_des);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final NotificationCardPresenter sInstance = new NotificationCardPresenter();

        private SingleHolder() {
        }
    }

    public static NotificationCardPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public class DateChangedBroadcast extends BroadcastReceiver {
        private DateChangedBroadcast() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.TIME_TICK".equals(intent.getAction())) {
                Logger.d(NotificationCardPresenter.TAG, "receiver ACTION_TIME_TICK");
                ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.NotificationCardPresenter.DateChangedBroadcast.1
                    @Override // java.lang.Runnable
                    public void run() {
                        NotificationCardPresenter.this.mHandler.sendEmptyMessage(1);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNotification() {
        INotificationCardView iNotificationCardView;
        if (isDefaultNotification(this.mCardData) && (iNotificationCardView = this.mNotificationView) != null) {
            iNotificationCardView.setNotificationCardStatus(false, getCurrentTime(this.mWeekArrays));
        }
    }

    protected NotificationCardPresenter() {
        registerDateChangedBroadCast();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected int getCardType() {
        return 1;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void bindDataImpl(CardEntry cardEntry) {
        this.mIsDefaultNotification = false;
        if (isDefaultNotification(this.mCardData)) {
            Logger.d(TAG, "bindData default notification");
            this.mIsDefaultNotification = true;
            INotificationCardView iNotificationCardView = this.mNotificationView;
            if (iNotificationCardView != null) {
                iNotificationCardView.setNotificationCardStatus(false, getCurrentTime(this.mWeekArrays));
            }
            stopCountTime();
            return;
        }
        INotificationCardView iNotificationCardView2 = this.mNotificationView;
        if (iNotificationCardView2 != null) {
            iNotificationCardView2.setNotificationCardStatus(true, null);
        }
        startCountTime();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithInfoflowView() {
        this.mNotificationView = this.mInfoflowView;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithCardHolder() {
        this.mNotificationView = (INotificationCardView) this.mCardHolder;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardClicked() {
        openNotificationCenter();
        DataLogUtils.sendInfoDataLog("P00004", this.mIsDefaultNotification ? "B001" : "B002", isAppForeground() ? "1" : "0");
    }

    private void openNotificationCenter() {
        Logger.d(TAG, "openNotificationCenter");
        CardEntry showEntry = NotificationList.getInstance().getShowEntry();
        if (showEntry == null) {
            return;
        }
        String showEntryReallyId = NotificationList.getInstance().getShowEntryReallyId();
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(MESSAGE_URI + showEntryReallyId));
        PackageHelper.startActivity(this.mContext, intent, (Bundle) null);
    }

    private void startCountTime() {
        ThreadUtils.runOnMainThreadDelay(this.mTimeRunnable, TimeUtils.TIME_ONE_MINUTE);
        setNotificationCardSubDesc(this.mContext.getString(R.string.passed_time_just_now));
    }

    private void stopCountTime() {
        ThreadUtils.removeRunnable(this.mTimeRunnable);
        setNotificationCardSubDesc("");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTime() {
        long currentTime = SystemClock.elapsedRealtime();
        long ellipseTimeMillis = currentTime - this.mCardData.when;
        setNotificationCardSubDesc(TimeUtil.getCardElapsedTimeDes(this.mContext, ellipseTimeMillis));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDefaultNotification(CardEntry cardEntry) {
        if (cardEntry == null) {
            return false;
        }
        return this.noNotification.equals(cardEntry.title);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.systemui.infoflow.message.presenter.NotificationCardPresenter$2  reason: invalid class name */
    /* loaded from: classes24.dex */
    public class AnonymousClass2 implements Runnable {
        AnonymousClass2() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (NotificationCardPresenter.this.mCardData != null) {
                NotificationCardPresenter notificationCardPresenter = NotificationCardPresenter.this;
                if (!notificationCardPresenter.isDefaultNotification(notificationCardPresenter.mCardData)) {
                    NotificationCardPresenter.this.updateTime();
                    ThreadUtils.runOnMainThreadDelay(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.-$$Lambda$AvHLELiDk2OtcVSNo1XIu03HyYw
                        @Override // java.lang.Runnable
                        public final void run() {
                            NotificationCardPresenter.AnonymousClass2.this.run();
                        }
                    }, TimeUtils.TIME_ONE_MINUTE);
                    return;
                }
            }
            Logger.d(NotificationCardPresenter.TAG, "mTimeRunnable call default notification");
            NotificationCardPresenter.this.setNotificationCardSubDesc("");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNotificationCardSubDesc(String desc) {
        INotificationCardView iNotificationCardView = this.mNotificationView;
        if (iNotificationCardView != null) {
            iNotificationCardView.setNotificationCardSubDesc(desc);
        }
    }

    private void registerDateChangedBroadCast() {
        DateChangedBroadcast dateChangedBroadcast = new DateChangedBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TIME_SET");
        filter.addAction("android.intent.action.TIME_TICK");
        this.mContext.registerReceiver(dateChangedBroadcast, filter);
    }

    public static String getCurrentTime() {
        String[] weekArrays = ContextUtils.getContext().getResources().getStringArray(R.array.msg_week_des);
        return getCurrentTime(weekArrays);
    }

    public static String getCurrentTime(String[] weekArrays) {
        Calendar calendar = Calendar.getInstance();
        int way = calendar.get(7);
        String weekString = weekArrays[way];
        if (Utils.isChineseLanguage()) {
            int month = calendar.get(2) + 1;
            int day = calendar.get(5);
            String result = ContextUtils.getContext().getString(R.string.notification_time_des, Integer.valueOf(month), Integer.valueOf(day), weekString);
            return result;
        }
        SimpleDateFormat formatDay = new SimpleDateFormat("MMM d");
        String dateString = formatDay.format(new Date());
        String result2 = weekString + ", " + dateString;
        return result2;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public String getCardPackageName() {
        return "com.xiaopeng.aiassistant";
    }
}
