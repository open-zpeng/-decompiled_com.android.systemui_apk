package com.android.keyguard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.settingslib.animation.DisappearAnimationUtils;
import com.android.systemui.R;
/* loaded from: classes19.dex */
public class KeyguardPINView extends KeyguardPinBasedInputView {
    private final AppearAnimationUtils mAppearAnimationUtils;
    private ViewGroup mContainer;
    private final DisappearAnimationUtils mDisappearAnimationUtils;
    private final DisappearAnimationUtils mDisappearAnimationUtilsLocked;
    private int mDisappearYTranslation;
    private View mDivider;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private ViewGroup mRow0;
    private ViewGroup mRow1;
    private ViewGroup mRow2;
    private ViewGroup mRow3;
    private View[][] mViews;

    public KeyguardPINView(Context context) {
        this(context, null);
    }

    public KeyguardPINView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mAppearAnimationUtils = new AppearAnimationUtils(context);
        this.mDisappearAnimationUtils = new DisappearAnimationUtils(context, 125L, 0.6f, 0.45f, AnimationUtils.loadInterpolator(this.mContext, 17563663));
        this.mDisappearAnimationUtilsLocked = new DisappearAnimationUtils(context, 187L, 0.6f, 0.45f, AnimationUtils.loadInterpolator(this.mContext, 17563663));
        this.mDisappearYTranslation = getResources().getDimensionPixelSize(R.dimen.disappear_y_translation);
        this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.keyguard.KeyguardPinBasedInputView, com.android.keyguard.KeyguardAbsKeyInputView
    public void resetState() {
        super.resetState();
        if (this.mSecurityMessageDisplay != null) {
            this.mSecurityMessageDisplay.setMessage("");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    public int getPasswordTextViewId() {
        return R.id.pinEntry;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.keyguard.KeyguardPinBasedInputView, com.android.keyguard.KeyguardAbsKeyInputView, android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContainer = (ViewGroup) findViewById(R.id.container);
        this.mRow0 = (ViewGroup) findViewById(R.id.row0);
        this.mRow1 = (ViewGroup) findViewById(R.id.row1);
        this.mRow2 = (ViewGroup) findViewById(R.id.row2);
        this.mRow3 = (ViewGroup) findViewById(R.id.row3);
        this.mDivider = findViewById(R.id.divider);
        this.mViews = new View[][]{new View[]{this.mRow0, null, null}, new View[]{findViewById(R.id.key1), findViewById(R.id.key2), findViewById(R.id.key3)}, new View[]{findViewById(R.id.key4), findViewById(R.id.key5), findViewById(R.id.key6)}, new View[]{findViewById(R.id.key7), findViewById(R.id.key8), findViewById(R.id.key9)}, new View[]{findViewById(R.id.delete_button), findViewById(R.id.key0), findViewById(R.id.key_enter)}, new View[]{null, this.mEcaView, null}};
        View cancelBtn = findViewById(R.id.cancel_button);
        if (cancelBtn != null) {
            cancelBtn.setOnClickListener(new View.OnClickListener() { // from class: com.android.keyguard.-$$Lambda$KeyguardPINView$32q9EwjCzWlJ6lNiw9pw0PSsPxs
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    KeyguardPINView.this.lambda$onFinishInflate$0$KeyguardPINView(view);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onFinishInflate$0$KeyguardPINView(View view) {
        this.mCallback.reset();
        this.mCallback.onCancelClicked();
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void showUsabilityHint() {
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    public int getWrongPasswordStringId() {
        return R.string.kg_wrong_pin;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void startAppearAnimation() {
        enableClipping(false);
        setAlpha(1.0f);
        setTranslationY(this.mAppearAnimationUtils.getStartTranslation());
        AppearAnimationUtils.startTranslationYAnimation(this, 0L, 500L, 0.0f, this.mAppearAnimationUtils.getInterpolator());
        this.mAppearAnimationUtils.startAnimation2d(this.mViews, new Runnable() { // from class: com.android.keyguard.KeyguardPINView.1
            @Override // java.lang.Runnable
            public void run() {
                KeyguardPINView.this.enableClipping(true);
            }
        });
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView, com.android.keyguard.KeyguardSecurityView
    public boolean startDisappearAnimation(final Runnable finishRunnable) {
        DisappearAnimationUtils disappearAnimationUtils;
        enableClipping(false);
        setTranslationY(0.0f);
        AppearAnimationUtils.startTranslationYAnimation(this, 0L, 280L, this.mDisappearYTranslation, this.mDisappearAnimationUtils.getInterpolator());
        if (this.mKeyguardUpdateMonitor.needsSlowUnlockTransition()) {
            disappearAnimationUtils = this.mDisappearAnimationUtilsLocked;
        } else {
            disappearAnimationUtils = this.mDisappearAnimationUtils;
        }
        disappearAnimationUtils.startAnimation2d(this.mViews, new Runnable() { // from class: com.android.keyguard.KeyguardPINView.2
            @Override // java.lang.Runnable
            public void run() {
                KeyguardPINView.this.enableClipping(true);
                Runnable runnable = finishRunnable;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableClipping(boolean enable) {
        this.mContainer.setClipToPadding(enable);
        this.mContainer.setClipChildren(enable);
        this.mRow1.setClipToPadding(enable);
        this.mRow2.setClipToPadding(enable);
        this.mRow3.setClipToPadding(enable);
        setClipChildren(enable);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }
}
