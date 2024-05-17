package com.android.keyguard;

import android.content.res.ColorStateList;
import android.view.MotionEvent;
import com.android.internal.widget.LockPatternUtils;
/* loaded from: classes19.dex */
public interface KeyguardSecurityView {
    public static final int PROMPT_REASON_AFTER_LOCKOUT = 5;
    public static final int PROMPT_REASON_DEVICE_ADMIN = 3;
    public static final int PROMPT_REASON_NONE = 0;
    public static final int PROMPT_REASON_RESTART = 1;
    public static final int PROMPT_REASON_TIMEOUT = 2;
    public static final int PROMPT_REASON_USER_REQUEST = 4;
    public static final int SCREEN_ON = 1;
    public static final int VIEW_REVEALED = 2;

    KeyguardSecurityCallback getCallback();

    CharSequence getTitle();

    boolean needsInput();

    void onPause();

    void onResume(int i);

    void reset();

    void setKeyguardCallback(KeyguardSecurityCallback keyguardSecurityCallback);

    void setLockPatternUtils(LockPatternUtils lockPatternUtils);

    void showMessage(CharSequence charSequence, ColorStateList colorStateList);

    void showPromptReason(int i);

    void showUsabilityHint();

    void startAppearAnimation();

    boolean startDisappearAnimation(Runnable runnable);

    default boolean disallowInterceptTouch(MotionEvent event) {
        return false;
    }
}
