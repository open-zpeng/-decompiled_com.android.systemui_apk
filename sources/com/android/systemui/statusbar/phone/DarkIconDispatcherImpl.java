package com.android.systemui.statusbar.phone;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.util.ArrayMap;
import android.widget.ImageView;
import com.android.systemui.R;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.phone.LightBarTransitionsController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class DarkIconDispatcherImpl implements SysuiDarkIconDispatcher, LightBarTransitionsController.DarkIntensityApplier {
    private float mDarkIntensity;
    private int mDarkModeIconColorSingleTone;
    private int mLightModeIconColorSingleTone;
    private final LightBarTransitionsController mTransitionsController;
    private final Rect mTintArea = new Rect();
    private final ArrayMap<Object, DarkIconDispatcher.DarkReceiver> mReceivers = new ArrayMap<>();
    private int mIconTint = -1;

    @Inject
    public DarkIconDispatcherImpl(Context context) {
        this.mDarkModeIconColorSingleTone = context.getColor(R.color.dark_mode_icon_color_single_tone);
        this.mLightModeIconColorSingleTone = context.getColor(R.color.light_mode_icon_color_single_tone);
        this.mTransitionsController = new LightBarTransitionsController(context, this);
    }

    @Override // com.android.systemui.statusbar.phone.SysuiDarkIconDispatcher
    public LightBarTransitionsController getTransitionsController() {
        return this.mTransitionsController;
    }

    @Override // com.android.systemui.plugins.DarkIconDispatcher
    public void addDarkReceiver(DarkIconDispatcher.DarkReceiver receiver) {
        this.mReceivers.put(receiver, receiver);
        receiver.onDarkChanged(this.mTintArea, this.mDarkIntensity, this.mIconTint);
    }

    @Override // com.android.systemui.plugins.DarkIconDispatcher
    public void addDarkReceiver(final ImageView imageView) {
        DarkIconDispatcher.DarkReceiver receiver = new DarkIconDispatcher.DarkReceiver() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$DarkIconDispatcherImpl$ok51JmL9mmr4FNW4V8J0PDfHR6I
            @Override // com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver
            public final void onDarkChanged(Rect rect, float f, int i) {
                DarkIconDispatcherImpl.this.lambda$addDarkReceiver$0$DarkIconDispatcherImpl(imageView, rect, f, i);
            }
        };
        this.mReceivers.put(imageView, receiver);
        receiver.onDarkChanged(this.mTintArea, this.mDarkIntensity, this.mIconTint);
    }

    public /* synthetic */ void lambda$addDarkReceiver$0$DarkIconDispatcherImpl(ImageView imageView, Rect area, float darkIntensity, int tint) {
        imageView.setImageTintList(ColorStateList.valueOf(DarkIconDispatcher.getTint(this.mTintArea, imageView, this.mIconTint)));
    }

    @Override // com.android.systemui.plugins.DarkIconDispatcher
    public void removeDarkReceiver(DarkIconDispatcher.DarkReceiver object) {
        this.mReceivers.remove(object);
    }

    @Override // com.android.systemui.plugins.DarkIconDispatcher
    public void removeDarkReceiver(ImageView object) {
        this.mReceivers.remove(object);
    }

    @Override // com.android.systemui.plugins.DarkIconDispatcher
    public void applyDark(DarkIconDispatcher.DarkReceiver object) {
        this.mReceivers.get(object).onDarkChanged(this.mTintArea, this.mDarkIntensity, this.mIconTint);
    }

    @Override // com.android.systemui.plugins.DarkIconDispatcher
    public void setIconsDarkArea(Rect darkArea) {
        if (darkArea == null && this.mTintArea.isEmpty()) {
            return;
        }
        if (darkArea == null) {
            this.mTintArea.setEmpty();
        } else {
            this.mTintArea.set(darkArea);
        }
        applyIconTint();
    }

    @Override // com.android.systemui.statusbar.phone.LightBarTransitionsController.DarkIntensityApplier
    public void applyDarkIntensity(float darkIntensity) {
        this.mDarkIntensity = darkIntensity;
        this.mIconTint = ((Integer) ArgbEvaluator.getInstance().evaluate(darkIntensity, Integer.valueOf(this.mLightModeIconColorSingleTone), Integer.valueOf(this.mDarkModeIconColorSingleTone))).intValue();
        applyIconTint();
    }

    @Override // com.android.systemui.statusbar.phone.LightBarTransitionsController.DarkIntensityApplier
    public int getTintAnimationDuration() {
        return 120;
    }

    private void applyIconTint() {
        for (int i = 0; i < this.mReceivers.size(); i++) {
            this.mReceivers.valueAt(i).onDarkChanged(this.mTintArea, this.mDarkIntensity, this.mIconTint);
        }
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("DarkIconDispatcher: ");
        pw.println("  mIconTint: 0x" + Integer.toHexString(this.mIconTint));
        pw.println("  mDarkIntensity: " + this.mDarkIntensity + "f");
        StringBuilder sb = new StringBuilder();
        sb.append("  mTintArea: ");
        sb.append(this.mTintArea);
        pw.println(sb.toString());
    }
}
