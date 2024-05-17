package com.xiaopeng.systemui.statusbar;

import android.content.Context;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.ViewFactory;
import com.xiaopeng.systemui.infoflow.message.util.SharedPreferenceUtil;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
/* loaded from: classes24.dex */
public class QuickMenuGuide implements IQuickMenuGuidePresenter {
    private static final String KEY_DISPLAY_COUNT = "quick_menu_guide_display_count";
    private static final String KEY_ENABLE_QUICK_MENU_GUIDE = "quick_menu_guide_enabled";
    private static final String KEY_LAST_DISPLAY_TIME = "last_quick_menu_guide_display_time";
    private static final int MAX_DISPLAY_COUNT = 3;
    private static final String TAG = "QuickMenuGuide";
    private Context mContext;
    private boolean mDestroyed;
    private int mDisplayCount;
    private boolean mEnabled;
    private long mLastDisplayTime;
    private IQuickMenuGuideView mQuickMenuGuideView;

    public void onThemeChanged() {
        IQuickMenuGuideView iQuickMenuGuideView = this.mQuickMenuGuideView;
        if (iQuickMenuGuideView != null) {
            iQuickMenuGuideView.onThemeChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final QuickMenuGuide sInstance = new QuickMenuGuide(ContextUtils.getContext());

        private SingleHolder() {
        }
    }

    public static QuickMenuGuide getInstance() {
        return SingleHolder.sInstance;
    }

    private QuickMenuGuide(Context context) {
        this.mContext = context;
        this.mLastDisplayTime = SharedPreferenceUtil.getLong(this.mContext, SharedPreferenceUtil.PREF_FILE_NAME, KEY_LAST_DISPLAY_TIME, 0L);
        this.mDisplayCount = SharedPreferenceUtil.get(this.mContext, SharedPreferenceUtil.PREF_FILE_NAME, KEY_DISPLAY_COUNT, 0);
        this.mEnabled = SharedPreferenceUtil.getBoolean(this.mContext, SharedPreferenceUtil.PREF_FILE_NAME, KEY_ENABLE_QUICK_MENU_GUIDE, true);
        this.mDestroyed = !this.mEnabled;
        this.mQuickMenuGuideView = ViewFactory.getQuickMenuGuideView();
    }

    public void checkToDisplay() {
        boolean matchPeriod;
        long j = this.mLastDisplayTime;
        if (j == 0) {
            Logger.d(TAG, "checkToDisplay : mLastDisplayTime is 0");
            matchPeriod = true;
        } else {
            LocalDate fromDate = new Date(j).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int days = Period.between(fromDate, LocalDate.now()).getDays();
            boolean matchPeriod2 = days > 0;
            Logger.d(TAG, "checkToDisplay : days = " + days + " matchPeriod = " + matchPeriod2 + " lastDisplayTime = " + fromDate.toString());
            matchPeriod = matchPeriod2;
        }
        Logger.d(TAG, "checkToDisplay : mEnabled = " + this.mEnabled + " mDisplayCount = " + this.mDisplayCount);
        if (this.mEnabled && matchPeriod && this.mDisplayCount < 3) {
            Logger.d(TAG, "checkToDisplay : show quick menu guide");
            reset();
            this.mQuickMenuGuideView.enterQuickMenuGuide();
            this.mDisplayCount++;
        }
    }

    private void reset() {
        this.mDestroyed = false;
    }

    @Override // com.xiaopeng.systemui.statusbar.IQuickMenuGuidePresenter
    public void disableQuickMenuGuide() {
        if (this.mEnabled) {
            Logger.d(TAG, "disable");
            IQuickMenuGuideView iQuickMenuGuideView = this.mQuickMenuGuideView;
            if (iQuickMenuGuideView != null) {
                iQuickMenuGuideView.quitQuickMenuGuide();
            }
            SharedPreferenceUtil.set(this.mContext, SharedPreferenceUtil.PREF_FILE_NAME, KEY_ENABLE_QUICK_MENU_GUIDE, false);
            this.mEnabled = false;
        }
    }

    public void destroy() {
        if (this.mDestroyed) {
            return;
        }
        this.mDestroyed = true;
        Logger.d(TAG, "destroy");
        IQuickMenuGuideView iQuickMenuGuideView = this.mQuickMenuGuideView;
        if (iQuickMenuGuideView != null) {
            iQuickMenuGuideView.quitQuickMenuGuide();
        }
        SharedPreferenceUtil.set(this.mContext, SharedPreferenceUtil.PREF_FILE_NAME, KEY_LAST_DISPLAY_TIME, System.currentTimeMillis());
        SharedPreferenceUtil.set(this.mContext, SharedPreferenceUtil.PREF_FILE_NAME, KEY_DISPLAY_COUNT, this.mDisplayCount);
        if (this.mDisplayCount >= 3) {
            SharedPreferenceUtil.set(this.mContext, SharedPreferenceUtil.PREF_FILE_NAME, KEY_ENABLE_QUICK_MENU_GUIDE, false);
        }
    }
}
