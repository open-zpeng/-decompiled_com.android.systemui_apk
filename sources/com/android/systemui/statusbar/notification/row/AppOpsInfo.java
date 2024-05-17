package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R;
import com.android.systemui.statusbar.notification.row.NotificationGuts;
/* loaded from: classes21.dex */
public class AppOpsInfo extends LinearLayout implements NotificationGuts.GutsContent {
    private static final String TAG = "AppOpsGuts";
    private String mAppName;
    private ArraySet<Integer> mAppOps;
    private int mAppUid;
    private NotificationGuts mGutsContainer;
    private MetricsLogger mMetricsLogger;
    private View.OnClickListener mOnOk;
    private OnSettingsClickListener mOnSettingsClickListener;
    private String mPkg;
    private PackageManager mPm;
    private StatusBarNotification mSbn;

    /* loaded from: classes21.dex */
    public interface OnSettingsClickListener {
        void onClick(View view, String str, int i, ArraySet<Integer> arraySet);
    }

    public AppOpsInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mOnOk = new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$AppOpsInfo$zS48CwL7b6UcUOuxgx7Zkw4dC1A
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AppOpsInfo.this.lambda$new$0$AppOpsInfo(view);
            }
        };
    }

    public void bindGuts(PackageManager pm, OnSettingsClickListener onSettingsClick, StatusBarNotification sbn, ArraySet<Integer> activeOps) {
        this.mPkg = sbn.getPackageName();
        this.mSbn = sbn;
        this.mPm = pm;
        this.mAppName = this.mPkg;
        this.mOnSettingsClickListener = onSettingsClick;
        this.mAppOps = activeOps;
        bindHeader();
        bindPrompt();
        bindButtons();
        this.mMetricsLogger = new MetricsLogger();
        this.mMetricsLogger.visibility(1345, true);
    }

    private void bindHeader() {
        Drawable pkgicon = null;
        try {
            ApplicationInfo info = this.mPm.getApplicationInfo(this.mPkg, 795136);
            if (info != null) {
                this.mAppUid = this.mSbn.getUid();
                this.mAppName = String.valueOf(this.mPm.getApplicationLabel(info));
                pkgicon = this.mPm.getApplicationIcon(info);
            }
        } catch (PackageManager.NameNotFoundException e) {
            pkgicon = this.mPm.getDefaultActivityIcon();
        }
        ((ImageView) findViewById(R.id.pkgicon)).setImageDrawable(pkgicon);
        ((TextView) findViewById(R.id.pkgname)).setText(this.mAppName);
    }

    private void bindPrompt() {
        TextView prompt = (TextView) findViewById(R.id.prompt);
        prompt.setText(getPrompt());
    }

    private void bindButtons() {
        View settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$AppOpsInfo$MC_PUe5w52BX3b0kt9URHDzbSUA
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AppOpsInfo.this.lambda$bindButtons$1$AppOpsInfo(view);
            }
        });
        TextView ok = (TextView) findViewById(R.id.ok);
        ok.setOnClickListener(this.mOnOk);
    }

    public /* synthetic */ void lambda$bindButtons$1$AppOpsInfo(View view) {
        this.mOnSettingsClickListener.onClick(view, this.mPkg, this.mAppUid, this.mAppOps);
    }

    private String getPrompt() {
        ArraySet<Integer> arraySet = this.mAppOps;
        if (arraySet == null || arraySet.size() == 0) {
            return "";
        }
        if (this.mAppOps.size() == 1) {
            if (this.mAppOps.contains(26)) {
                return this.mContext.getString(R.string.appops_camera);
            }
            if (this.mAppOps.contains(27)) {
                return this.mContext.getString(R.string.appops_microphone);
            }
            return this.mContext.getString(R.string.appops_overlay);
        } else if (this.mAppOps.size() == 2) {
            if (this.mAppOps.contains(26)) {
                if (this.mAppOps.contains(27)) {
                    return this.mContext.getString(R.string.appops_camera_mic);
                }
                return this.mContext.getString(R.string.appops_camera_overlay);
            }
            return this.mContext.getString(R.string.appops_mic_overlay);
        } else {
            return this.mContext.getString(R.string.appops_camera_mic_overlay);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (this.mGutsContainer != null && event.getEventType() == 32) {
            if (this.mGutsContainer.isExposed()) {
                event.getText().add(this.mContext.getString(R.string.notification_channel_controls_opened_accessibility, this.mAppName));
            } else {
                event.getText().add(this.mContext.getString(R.string.notification_channel_controls_closed_accessibility, this.mAppName));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: closeControls */
    public void lambda$new$0$AppOpsInfo(View v) {
        this.mMetricsLogger.visibility(1345, false);
        int[] parentLoc = new int[2];
        int[] targetLoc = new int[2];
        this.mGutsContainer.getLocationOnScreen(parentLoc);
        v.getLocationOnScreen(targetLoc);
        int centerX = v.getWidth() / 2;
        int centerY = v.getHeight() / 2;
        int x = (targetLoc[0] - parentLoc[0]) + centerX;
        int y = (targetLoc[1] - parentLoc[1]) + centerY;
        this.mGutsContainer.closeControls(x, y, false, false);
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public void setGutsParent(NotificationGuts guts) {
        this.mGutsContainer = guts;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public boolean willBeRemoved() {
        return false;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public boolean shouldBeSaved() {
        return false;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public View getContentView() {
        return this;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public boolean handleCloseControls(boolean save, boolean force) {
        return false;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public int getActualHeight() {
        return getHeight();
    }
}
