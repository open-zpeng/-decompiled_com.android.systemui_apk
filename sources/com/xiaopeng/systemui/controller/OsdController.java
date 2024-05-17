package com.xiaopeng.systemui.controller;

import android.app.INotificationManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.ViewFactory;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.ui.widget.IOsdView;
import com.xiaopeng.systemui.ui.widget.OsdView;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class OsdController {
    private static final int MSG_OSD_HIDE = 1001;
    private static final int MSG_OSD_SHOW = 1001;
    private static final String TAG = "OsdController";
    private final TN mTN;
    private static List<Toast> sToastQueue = new ArrayList();
    private static OsdController sOsdController = null;
    public static boolean sIsMute = false;
    private static final Runnable sToastQueueRunnable = new Runnable() { // from class: com.xiaopeng.systemui.controller.OsdController.2
        @Override // java.lang.Runnable
        public void run() {
            OsdController.queueToast();
        }
    };
    private boolean mInTouchMode = false;
    private boolean mShow = false;
    private int mType = 0;
    private Handler mHandler = new Handler() { // from class: com.xiaopeng.systemui.controller.OsdController.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1001) {
                OsdController.this.hideOsd();
            }
        }
    };
    private IOsdView mOsdView = ViewFactory.getOsdView();

    private OsdController(Context context) {
        this.mTN = new TN(context, Looper.myLooper());
    }

    public static OsdController getInstance(Context context) {
        if (sOsdController == null) {
            synchronized (OsdController.class) {
                if (sOsdController == null) {
                    sOsdController = new OsdController(context);
                }
            }
        }
        return sOsdController;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        IOsdView iOsdView = this.mOsdView;
        if (iOsdView != null) {
            iOsdView.dispatchConfigurationChanged(newConfig);
        }
    }

    public static void notifyOsdNotification(Context context, int type, int streamType, CharSequence title, int titleColor, Icon left, Icon right, Icon icon, CharSequence content, int progress, int progressMin, int progressMax) {
        if (context != null && !TextUtils.isEmpty(title)) {
            NotificationManager manager = (NotificationManager) context.getSystemService("notification");
            NotificationChannel channel = new NotificationChannel("channel_id_osd", "channel_name_osd", 3);
            manager.createNotificationChannel(channel);
            Notification.Builder builder = new Notification.Builder(context, "channel_id_osd");
            builder.setSmallIcon(17301595);
            Bundle extra = builder.getExtras();
            extra.putInt("android.osd.type", type);
            extra.putInt("android.osd.stream.type", streamType);
            extra.putCharSequence("android.osd.title", title);
            extra.putInt("android.osd.title.color", titleColor);
            extra.putParcelable("android.osd.title.icon.left", left);
            extra.putParcelable("android.osd.title.icon.right", right);
            extra.putParcelable("android.osd.icon", icon);
            extra.putCharSequence("android.osd.content", content);
            extra.putInt("android.osd.progress", progress);
            extra.putInt("android.osd.progress.min", progressMin);
            extra.putInt("android.osd.progress.max", progressMax);
            extra.putInt("android.displayFlag", 2);
            builder.setExtras(extra);
            manager.notify(100, builder.build());
        }
    }

    public static void notifyOsd(Context context, OsdParams params) {
        if (context != null && params != null) {
            if (!CarModelsManager.getFeature().isOsdReduceSelfUse()) {
                notifyOsdNotification(context, params.mType, params.mStreamType, params.mTitle, params.mTitleColor, params.mTitleLeft, params.mTitleRight, params.mIcon, params.mContent, params.mProgress, params.mProgressMin, params.mProgressMax);
                return;
            }
            INotificationManager service = NotificationManager.getService();
            try {
                if (service.isOsdEnable()) {
                    getInstance(context).showOsd(params);
                } else {
                    Logger.i(TAG, "notifyOsd isOsdEnable = false");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyMuteOsd(Context context, int volume, int volumeMax, boolean mute) {
        Logger.d(TAG, "notifyMuteOsd volume=" + volume + " ,mute:" + mute + " ,volumeMax:" + volumeMax);
        OsdParams params = new OsdParams();
        params.mProgress = volume;
        params.mProgressMin = 0;
        params.mProgressMax = volumeMax;
        if (mute) {
            params.mTitle = context.getText(R.string.osd_title_volume_mute);
            params.mType = 0;
            params.mIcon = Icon.createWithResource(context, (int) R.drawable.ic_sysui_osd_volume_mute);
        } else {
            params.mTitle = context.getText(R.string.osd_title_volume);
            params.mType = 1;
            params.mIcon = Icon.createWithResource(context, (int) R.drawable.ic_sysui_osd_volume_media);
        }
        sIsMute = mute;
        notifyOsd(context, params);
    }

    public static void notifyVolumeOsd(Context context, int streamType, int volume, int volumeMax) {
        Logger.d(TAG, "notifyVolumeOsd volume=" + volume + " ,streamType:" + streamType);
        OsdParams params = new OsdParams();
        params.mIcon = getVolumeIcon(context, streamType);
        params.mType = getOsdType(context, streamType);
        params.mStreamType = streamType;
        params.mProgress = volume;
        params.mProgressMin = 0;
        params.mProgressMax = volumeMax;
        params.mTitle = getVolumeTitle(context, streamType);
        notifyOsd(context, params);
    }

    public int getOsdType() {
        return this.mType;
    }

    public static int getOsdType(Context context, int streamType) {
        if (CarModelsManager.getFeature().isOsdReduceSelfUse()) {
            return (streamType == 3 || streamType == 6 || streamType == 11) ? 1 : 0;
        } else if (streamType != -1) {
            return (streamType == 0 || streamType == 2 || streamType == 6) ? 0 : 1;
        } else {
            boolean isMute = AudioController.getInstance(context).isStreamMute();
            Logger.d(TAG, "getOsdType isMute=" + isMute + " ,streamType:" + streamType);
            return !isMute;
        }
    }

    public static Icon getVolumeIcon(Context context, int streamType) {
        if (streamType == 0 || streamType == 2 || streamType == 6) {
            Icon icon = Icon.createWithResource(context, (int) R.drawable.ic_sysui_osd_volume_ring);
            return icon;
        }
        switch (streamType) {
            case 9:
                Icon icon2 = Icon.createWithResource(context, (int) R.drawable.ic_sysui_osd_volume_tts);
                return icon2;
            case 10:
                Icon icon3 = Icon.createWithResource(context, (int) R.drawable.ic_sysui_osd_volume_voice);
                return icon3;
            case 11:
                Icon icon4 = Icon.createWithResource(context, (int) R.drawable.ic_sysui_osd_volume_avas);
                return icon4;
            default:
                boolean isMute = AudioController.getInstance(context).isStreamMute();
                Logger.d(TAG, "getVolumeIcon isMute=" + isMute + " , streamType : " + streamType);
                if (isMute) {
                    Icon icon5 = Icon.createWithResource(context, (int) R.drawable.ic_sysui_osd_volume_mute);
                    return icon5;
                }
                Icon icon6 = Icon.createWithResource(context, (int) R.drawable.ic_sysui_osd_volume_media);
                return icon6;
        }
    }

    public static String getVolumeTitle(Context context, int streamType) {
        if (streamType == 0 || streamType == 2 || streamType == 6) {
            String title = context.getString(R.string.osd_title_volume_call);
            return title;
        }
        switch (streamType) {
            case 9:
                String title2 = context.getString(R.string.osd_title_volume_navi);
                return title2;
            case 10:
                String title3 = context.getString(R.string.osd_title_volume_report);
                return title3;
            case 11:
                String title4 = context.getString(R.string.osd_title_volume_avas);
                return title4;
            default:
                boolean isMute = AudioController.getInstance(context).isStreamMute();
                if (isMute) {
                    String title5 = context.getString(R.string.osd_title_volume_mute);
                    return title5;
                }
                String title6 = context.getString(R.string.osd_title_volume);
                return title6;
        }
    }

    public static OsdParams getOsdParams(Notification n) {
        OsdParams params = new OsdParams();
        if (n != null) {
            params.mType = n.extras.getInt("android.osd.type", 0);
            params.mStreamType = n.extras.getInt("android.osd.stream.type", 3);
            params.mTitle = n.extras.getCharSequence("android.osd.title", "");
            params.mTitleColor = n.extras.getInt("android.osd.title.color", R.color.osdTextColor);
            params.mTitleLeft = (Icon) n.extras.getParcelable("android.osd.title.icon.left");
            params.mTitleRight = (Icon) n.extras.getParcelable("android.osd.title.icon.right");
            params.mIcon = (Icon) n.extras.getParcelable("android.osd.icon");
            params.mContent = n.extras.getCharSequence("android.osd.content", "");
            params.mProgress = n.extras.getInt("android.osd.progress", 0);
            params.mProgressMin = n.extras.getInt("android.osd.progress.min", 0);
            params.mProgressMax = n.extras.getInt("android.osd.progress.max", 100);
            params.mScreenId = n.extras.getInt("android.osd.shared.id", -1);
            Logger.d(TAG, "getOsdParams params=" + params.toString());
        }
        return params;
    }

    public void showOsd(OsdParams params) {
        Logger.d(TAG, "showOsd " + params);
        this.mType = params.mType;
        this.mOsdView.showOsd(params);
        TN tn = this.mTN;
        tn.mView = this.mOsdView;
        tn.mDuration = TN.DURATION_TIMEOUT_LONG;
        tn.show();
        if (!this.mInTouchMode) {
            startAutoHideOsd();
        }
    }

    public void stopAutoHideOsd() {
        Logger.d(TAG, "stopAutoHideOsd");
        this.mHandler.removeMessages(1001);
    }

    public void startAutoHideOsd() {
        Logger.d(TAG, "startAutoHideOsd");
        this.mHandler.removeMessages(1001);
        this.mHandler.sendEmptyMessageDelayed(1001, this.mTN.mDuration);
    }

    public void hideOsd() {
        Logger.d(TAG, "hideOsd");
        this.mTN.hide();
    }

    public boolean isOsdShown() {
        return this.mShow;
    }

    public void setOsdShow(boolean show) {
        this.mShow = show;
    }

    public void setInTouchMode(boolean b) {
        this.mInTouchMode = b;
    }

    public static void showToast(Context context, OsdParams params) {
        Toast toast = new Toast(context);
        View view = new OsdView(context, params);
        toast.setDuration(1);
        toast.setGravity(17, 0, 0);
        toast.setView(view);
        toast.show();
        handleToastQueue(toast);
    }

    private static void handleToastQueue(Toast toast) {
        if (toast != null) {
            sToastQueue.add(toast);
            toast.getView().removeCallbacks(sToastQueueRunnable);
            toast.getView().postDelayed(sToastQueueRunnable, 4000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void queueToast() {
        for (Toast t : sToastQueue) {
            if (t != null) {
                t.cancel();
            }
        }
        sToastQueue.clear();
    }

    /* loaded from: classes24.dex */
    public static class OsdParams {
        public static final int OSD_TYPE_INTERACTIVE = 1;
        public static final int OSD_TYPE_NORMAL = 0;
        public int mProgress;
        public int mType = 0;
        public int mStreamType = 3;
        public Icon mIcon = null;
        public Icon mTitleLeft = null;
        public Icon mTitleRight = null;
        public CharSequence mTitle = "";
        public CharSequence mContent = "";
        public int mProgressMin = 0;
        public int mProgressMax = 100;
        public int mTitleColor = R.color.osdTextColor;
        public int mScreenId = -1;

        public String toString() {
            return "OsdParams{mType=" + this.mType + ", mStreamType=" + this.mStreamType + ", mIcon=" + this.mIcon + ", mTitle=" + ((Object) this.mTitle) + ", mProgress=" + this.mProgress + ", mScreenId=" + this.mScreenId + '}';
        }
    }

    /* loaded from: classes24.dex */
    public static class TN {
        public static final long DURATION_TIMEOUT_LONG = 2500;
        public static final long DURATION_TIMEOUT_SHORT = 2000;
        private static final int HIDE = 1;
        private static final int SHOW = 0;
        private Context mContext;
        public long mDuration;
        private final Handler mHandler;
        public IOsdView mView;

        TN(Context context, Looper looper) {
            this.mContext = context;
            if (looper == null && (looper = Looper.myLooper()) == null) {
                throw new RuntimeException("Can't osd on a thread that has not called Looper.prepare()");
            }
            this.mHandler = new Handler(looper, null) { // from class: com.xiaopeng.systemui.controller.OsdController.TN.1
                @Override // android.os.Handler
                public void handleMessage(Message msg) {
                    int i = msg.what;
                    if (i == 0) {
                        TN.this.handleShow();
                    } else if (i == 1) {
                        TN.this.handleHide();
                    }
                }
            };
        }

        public void show() {
            this.mHandler.removeMessages(0);
            this.mHandler.obtainMessage(0).sendToTarget();
        }

        public void hide() {
            this.mHandler.obtainMessage(1).sendToTarget();
        }

        public void handleShow() {
            IOsdView iOsdView;
            if (!this.mHandler.hasMessages(1) && (iOsdView = this.mView) != null) {
                iOsdView.showOsd(true);
                OsdController.getInstance(this.mContext).setOsdShow(true);
            }
        }

        public void handleHide() {
            IOsdView iOsdView = this.mView;
            if (iOsdView != null) {
                iOsdView.showOsd(false);
                OsdController.getInstance(this.mContext).setOsdShow(false);
            }
        }
    }
}
