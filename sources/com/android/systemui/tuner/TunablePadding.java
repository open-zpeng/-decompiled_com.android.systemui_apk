package com.android.systemui.tuner;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import com.android.systemui.Dependency;
import com.android.systemui.tuner.TunerService;
import javax.inject.Inject;
import javax.inject.Singleton;
/* loaded from: classes21.dex */
public class TunablePadding implements TunerService.Tunable {
    public static final int FLAG_BOTTOM = 8;
    public static final int FLAG_END = 2;
    public static final int FLAG_START = 1;
    public static final int FLAG_TOP = 4;
    private final int mDefaultSize;
    private final float mDensity;
    private final int mFlags;
    private final TunerService mTunerService;
    private final View mView;

    private TunablePadding(String key, int def, int flags, View view, TunerService tunerService) {
        this.mDefaultSize = def;
        this.mFlags = flags;
        this.mView = view;
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) view.getContext().getSystemService(WindowManager.class)).getDefaultDisplay().getMetrics(metrics);
        this.mDensity = metrics.density;
        this.mTunerService = tunerService;
        this.mTunerService.addTunable(this, key);
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        int dimen = this.mDefaultSize;
        if (newValue != null) {
            try {
                dimen = (int) (Integer.parseInt(newValue) * this.mDensity);
            } catch (NumberFormatException e) {
            }
        }
        int left = this.mView.isLayoutRtl() ? 2 : 1;
        int right = this.mView.isLayoutRtl() ? 1 : 2;
        this.mView.setPadding(getPadding(dimen, left), getPadding(dimen, 4), getPadding(dimen, right), getPadding(dimen, 8));
    }

    private int getPadding(int dimen, int flag) {
        if ((this.mFlags & flag) != 0) {
            return dimen;
        }
        return 0;
    }

    public void destroy() {
        this.mTunerService.removeTunable(this);
    }

    @Singleton
    /* loaded from: classes21.dex */
    public static class TunablePaddingService {
        private final TunerService mTunerService;

        @Inject
        public TunablePaddingService(TunerService tunerService) {
            this.mTunerService = tunerService;
        }

        public TunablePadding add(View view, String key, int defaultSize, int flags) {
            if (view == null) {
                throw new IllegalArgumentException();
            }
            return new TunablePadding(key, defaultSize, flags, view, this.mTunerService);
        }
    }

    public static TunablePadding addTunablePadding(View view, String key, int defaultSize, int flags) {
        return ((TunablePaddingService) Dependency.get(TunablePaddingService.class)).add(view, key, defaultSize, flags);
    }
}
