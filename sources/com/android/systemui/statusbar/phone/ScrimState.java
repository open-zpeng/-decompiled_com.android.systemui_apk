package com.android.systemui.statusbar.phone;

import android.graphics.Color;
import android.os.Trace;
import android.support.v4.media.session.PlaybackStateCompat;
import com.android.systemui.statusbar.ScrimView;
/* loaded from: classes21.dex */
public enum ScrimState {
    UNINITIALIZED(-1),
    KEYGUARD(0) { // from class: com.android.systemui.statusbar.phone.ScrimState.1
        @Override // com.android.systemui.statusbar.phone.ScrimState
        public void prepare(ScrimState previousState) {
            this.mBlankScreen = false;
            if (previousState == ScrimState.AOD) {
                this.mAnimationDuration = 500L;
                if (this.mDisplayRequiresBlanking) {
                    this.mBlankScreen = true;
                }
            } else if (previousState == ScrimState.KEYGUARD) {
                this.mAnimationDuration = 500L;
            } else {
                this.mAnimationDuration = 220L;
            }
            this.mCurrentInFrontTint = -16777216;
            this.mCurrentBehindTint = -16777216;
            this.mCurrentBehindAlpha = this.mScrimBehindAlphaKeyguard;
            this.mCurrentInFrontAlpha = 0.0f;
        }
    },
    BOUNCER(1) { // from class: com.android.systemui.statusbar.phone.ScrimState.2
        @Override // com.android.systemui.statusbar.phone.ScrimState
        public void prepare(ScrimState previousState) {
            this.mCurrentBehindAlpha = 0.7f;
            this.mCurrentInFrontAlpha = 0.0f;
        }
    },
    BOUNCER_SCRIMMED(2) { // from class: com.android.systemui.statusbar.phone.ScrimState.3
        @Override // com.android.systemui.statusbar.phone.ScrimState
        public void prepare(ScrimState previousState) {
            this.mCurrentBehindAlpha = 0.0f;
            this.mCurrentInFrontAlpha = 0.7f;
        }
    },
    BRIGHTNESS_MIRROR(3) { // from class: com.android.systemui.statusbar.phone.ScrimState.4
        @Override // com.android.systemui.statusbar.phone.ScrimState
        public void prepare(ScrimState previousState) {
            this.mCurrentBehindAlpha = 0.0f;
            this.mCurrentInFrontAlpha = 0.0f;
        }
    },
    AOD(4) { // from class: com.android.systemui.statusbar.phone.ScrimState.5
        @Override // com.android.systemui.statusbar.phone.ScrimState
        public void prepare(ScrimState previousState) {
            boolean alwaysOnEnabled = this.mDozeParameters.getAlwaysOn();
            this.mBlankScreen = this.mDisplayRequiresBlanking;
            this.mCurrentInFrontAlpha = alwaysOnEnabled ? this.mAodFrontScrimAlpha : 1.0f;
            this.mCurrentInFrontTint = -16777216;
            this.mCurrentBehindTint = -16777216;
            this.mAnimationDuration = 1000L;
            this.mAnimateChange = this.mDozeParameters.shouldControlScreenOff();
        }

        @Override // com.android.systemui.statusbar.phone.ScrimState
        public float getBehindAlpha() {
            return (!this.mWallpaperSupportsAmbientMode || this.mHasBackdrop) ? 1.0f : 0.0f;
        }

        @Override // com.android.systemui.statusbar.phone.ScrimState
        public boolean isLowPowerState() {
            return true;
        }
    },
    PULSING(5) { // from class: com.android.systemui.statusbar.phone.ScrimState.6
        @Override // com.android.systemui.statusbar.phone.ScrimState
        public void prepare(ScrimState previousState) {
            this.mCurrentInFrontAlpha = this.mAodFrontScrimAlpha;
            this.mCurrentBehindTint = -16777216;
            this.mCurrentInFrontTint = -16777216;
            this.mBlankScreen = this.mDisplayRequiresBlanking;
            this.mAnimationDuration = this.mWakeLockScreenSensorActive ? 1000L : 220L;
            if (this.mWakeLockScreenSensorActive && previousState == AOD) {
                updateScrimColor(this.mScrimBehind, 1.0f, -16777216);
            }
        }

        @Override // com.android.systemui.statusbar.phone.ScrimState
        public float getBehindAlpha() {
            if (this.mWakeLockScreenSensorActive) {
                return 0.6f;
            }
            return AOD.getBehindAlpha();
        }
    },
    UNLOCKED(6) { // from class: com.android.systemui.statusbar.phone.ScrimState.7
        @Override // com.android.systemui.statusbar.phone.ScrimState
        public void prepare(ScrimState previousState) {
            long j;
            this.mCurrentBehindAlpha = 0.0f;
            this.mCurrentInFrontAlpha = 0.0f;
            if (this.mKeyguardFadingAway) {
                j = this.mKeyguardFadingAwayDuration;
            } else {
                j = 300;
            }
            this.mAnimationDuration = j;
            this.mAnimateChange = !this.mLaunchingAffordanceWithPreview;
            if (previousState == ScrimState.AOD) {
                updateScrimColor(this.mScrimInFront, 1.0f, -16777216);
                updateScrimColor(this.mScrimBehind, 1.0f, -16777216);
                this.mCurrentInFrontTint = -16777216;
                this.mCurrentBehindTint = -16777216;
                this.mBlankScreen = true;
                return;
            }
            this.mCurrentInFrontTint = 0;
            this.mCurrentBehindTint = 0;
            this.mBlankScreen = false;
        }
    },
    BUBBLE_EXPANDED(7) { // from class: com.android.systemui.statusbar.phone.ScrimState.8
        @Override // com.android.systemui.statusbar.phone.ScrimState
        public void prepare(ScrimState previousState) {
            this.mCurrentInFrontTint = 0;
            this.mCurrentBehindTint = 0;
            this.mAnimationDuration = 220L;
            this.mCurrentBehindAlpha = 0.7f;
            this.mBlankScreen = false;
        }
    };
    
    boolean mAnimateChange;
    long mAnimationDuration;
    float mAodFrontScrimAlpha;
    boolean mBlankScreen;
    float mCurrentBehindAlpha;
    int mCurrentBehindTint;
    float mCurrentInFrontAlpha;
    int mCurrentInFrontTint;
    boolean mDisplayRequiresBlanking;
    DozeParameters mDozeParameters;
    boolean mHasBackdrop;
    int mIndex;
    boolean mKeyguardFadingAway;
    long mKeyguardFadingAwayDuration;
    boolean mLaunchingAffordanceWithPreview;
    ScrimView mScrimBehind;
    float mScrimBehindAlphaKeyguard;
    ScrimView mScrimInFront;
    boolean mWakeLockScreenSensorActive;
    boolean mWallpaperSupportsAmbientMode;

    ScrimState(int index) {
        this.mBlankScreen = false;
        this.mAnimationDuration = 220L;
        this.mCurrentInFrontTint = 0;
        this.mCurrentBehindTint = 0;
        this.mAnimateChange = true;
        this.mIndex = index;
    }

    public void init(ScrimView scrimInFront, ScrimView scrimBehind, DozeParameters dozeParameters) {
        this.mScrimInFront = scrimInFront;
        this.mScrimBehind = scrimBehind;
        this.mDozeParameters = dozeParameters;
        this.mDisplayRequiresBlanking = dozeParameters.getDisplayNeedsBlanking();
    }

    public void prepare(ScrimState previousState) {
    }

    public int getIndex() {
        return this.mIndex;
    }

    public float getFrontAlpha() {
        return this.mCurrentInFrontAlpha;
    }

    public float getBehindAlpha() {
        return this.mCurrentBehindAlpha;
    }

    public int getFrontTint() {
        return this.mCurrentInFrontTint;
    }

    public int getBehindTint() {
        return this.mCurrentBehindTint;
    }

    public long getAnimationDuration() {
        return this.mAnimationDuration;
    }

    public boolean getBlanksScreen() {
        return this.mBlankScreen;
    }

    public void updateScrimColor(ScrimView scrim, float alpha, int tint) {
        Trace.traceCounter(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM, scrim == this.mScrimInFront ? "front_scrim_alpha" : "back_scrim_alpha", (int) (255.0f * alpha));
        Trace.traceCounter(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM, scrim == this.mScrimInFront ? "front_scrim_tint" : "back_scrim_tint", Color.alpha(tint));
        scrim.setTint(tint);
        scrim.setViewAlpha(alpha);
    }

    public boolean getAnimateChange() {
        return this.mAnimateChange;
    }

    public void setAodFrontScrimAlpha(float aodFrontScrimAlpha) {
        this.mAodFrontScrimAlpha = aodFrontScrimAlpha;
    }

    public void setScrimBehindAlphaKeyguard(float scrimBehindAlphaKeyguard) {
        this.mScrimBehindAlphaKeyguard = scrimBehindAlphaKeyguard;
    }

    public void setWallpaperSupportsAmbientMode(boolean wallpaperSupportsAmbientMode) {
        this.mWallpaperSupportsAmbientMode = wallpaperSupportsAmbientMode;
    }

    public void setLaunchingAffordanceWithPreview(boolean launchingAffordanceWithPreview) {
        this.mLaunchingAffordanceWithPreview = launchingAffordanceWithPreview;
    }

    public boolean isLowPowerState() {
        return false;
    }

    public void setHasBackdrop(boolean hasBackdrop) {
        this.mHasBackdrop = hasBackdrop;
    }

    public void setWakeLockScreenSensorActive(boolean active) {
        this.mWakeLockScreenSensorActive = active;
    }

    public void setKeyguardFadingAway(boolean fadingAway, long duration) {
        this.mKeyguardFadingAway = fadingAway;
        this.mKeyguardFadingAwayDuration = duration;
    }
}
