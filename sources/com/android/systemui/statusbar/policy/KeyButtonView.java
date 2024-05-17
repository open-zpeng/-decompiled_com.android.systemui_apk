package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.input.InputManager;
import android.media.AudioManager;
import android.metrics.LogMaker;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.phone.ButtonInterface;
import com.android.systemui.statusbar.policy.KeyButtonRipple;
import com.xiaopeng.speech.speechwidget.ListWidget;
/* loaded from: classes21.dex */
public class KeyButtonView extends ImageView implements ButtonInterface {
    private static final String TAG = KeyButtonView.class.getSimpleName();
    private AudioManager mAudioManager;
    private final Runnable mCheckLongPress;
    private int mCode;
    private int mContentDescriptionRes;
    private float mDarkIntensity;
    private long mDownTime;
    private boolean mGestureAborted;
    private boolean mHasOvalBg;
    private final InputManager mInputManager;
    private boolean mIsVertical;
    private boolean mLongClicked;
    private final MetricsLogger mMetricsLogger;
    private View.OnClickListener mOnClickListener;
    private final Paint mOvalBgPaint;
    private final OverviewProxyService mOverviewProxyService;
    private final boolean mPlaySounds;
    private final KeyButtonRipple mRipple;
    private int mTouchDownX;
    private int mTouchDownY;

    public KeyButtonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyButtonView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, InputManager.getInstance());
    }

    @VisibleForTesting
    public KeyButtonView(Context context, AttributeSet attrs, int defStyle, InputManager manager) {
        super(context, attrs);
        this.mMetricsLogger = (MetricsLogger) Dependency.get(MetricsLogger.class);
        this.mOvalBgPaint = new Paint(3);
        this.mHasOvalBg = false;
        this.mCheckLongPress = new Runnable() { // from class: com.android.systemui.statusbar.policy.KeyButtonView.1
            @Override // java.lang.Runnable
            public void run() {
                if (KeyButtonView.this.isPressed()) {
                    if (KeyButtonView.this.isLongClickable()) {
                        KeyButtonView.this.performLongClick();
                        KeyButtonView.this.mLongClicked = true;
                        return;
                    }
                    KeyButtonView.this.sendEvent(0, 128);
                    KeyButtonView.this.sendAccessibilityEvent(2);
                    KeyButtonView.this.mLongClicked = true;
                }
            }
        };
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KeyButtonView, defStyle, 0);
        this.mCode = a.getInteger(R.styleable.KeyButtonView_keyCode, 0);
        this.mPlaySounds = a.getBoolean(R.styleable.KeyButtonView_playSound, true);
        TypedValue value = new TypedValue();
        if (a.getValue(R.styleable.KeyButtonView_android_contentDescription, value)) {
            this.mContentDescriptionRes = value.resourceId;
        }
        a.recycle();
        setClickable(true);
        this.mAudioManager = (AudioManager) context.getSystemService(ListWidget.EXTRA_TYPE_AUDIO);
        this.mRipple = new KeyButtonRipple(context, this);
        this.mOverviewProxyService = (OverviewProxyService) Dependency.get(OverviewProxyService.class);
        this.mInputManager = manager;
        setBackground(this.mRipple);
        setWillNotDraw(false);
        forceHasOverlappingRendering(false);
    }

    @Override // android.view.View
    public boolean isClickable() {
        return this.mCode != 0 || super.isClickable();
    }

    public void setCode(int code) {
        this.mCode = code;
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener onClickListener) {
        super.setOnClickListener(onClickListener);
        this.mOnClickListener = onClickListener;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.android.systemui.statusbar.policy.KeyButtonView$2] */
    public void loadAsync(Icon icon) {
        new AsyncTask<Icon, Void, Drawable>() { // from class: com.android.systemui.statusbar.policy.KeyButtonView.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Drawable doInBackground(Icon... params) {
                return params[0].loadDrawable(KeyButtonView.this.mContext);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Drawable drawable) {
                KeyButtonView.this.setImageDrawable(drawable);
            }
        }.execute(icon);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.mContentDescriptionRes != 0) {
            setContentDescription(this.mContext.getString(this.mContentDescriptionRes));
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (this.mCode != 0) {
            info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, null));
            if (isLongClickable()) {
                info.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, null));
            }
        }
    }

    @Override // android.view.View
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility != 0) {
            jumpDrawablesToCurrentState();
        }
    }

    public boolean performAccessibilityActionInternal(int action, Bundle arguments) {
        if (action == 16 && this.mCode != 0) {
            sendEvent(0, 0, SystemClock.uptimeMillis());
            sendEvent(1, 0);
            sendAccessibilityEvent(1);
            playSoundEffect(0);
            return true;
        } else if (action == 32 && this.mCode != 0) {
            sendEvent(0, 128);
            sendEvent(1, 0);
            sendAccessibilityEvent(2);
            return true;
        } else {
            return super.performAccessibilityActionInternal(action, arguments);
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        boolean doIt;
        boolean doHapticFeedback;
        View.OnClickListener onClickListener;
        boolean showSwipeUI = this.mOverviewProxyService.shouldShowSwipeUpUI();
        int action = ev.getAction();
        if (action == 0) {
            this.mGestureAborted = false;
        }
        if (this.mGestureAborted) {
            setPressed(false);
            return false;
        }
        if (action != 0) {
            if (action == 1) {
                if (!isPressed() || this.mLongClicked) {
                    doIt = false;
                } else {
                    doIt = true;
                }
                setPressed(false);
                if (SystemClock.uptimeMillis() - this.mDownTime <= 150) {
                    doHapticFeedback = false;
                } else {
                    doHapticFeedback = true;
                }
                if (showSwipeUI) {
                    if (doIt) {
                        performHapticFeedback(1);
                        playSoundEffect(0);
                    }
                } else if (doHapticFeedback && !this.mLongClicked) {
                    performHapticFeedback(8);
                }
                if (this.mCode != 0) {
                    if (doIt) {
                        sendEvent(1, 0);
                        sendAccessibilityEvent(1);
                    } else {
                        sendEvent(1, 32);
                    }
                } else if (doIt && (onClickListener = this.mOnClickListener) != null) {
                    onClickListener.onClick(this);
                    sendAccessibilityEvent(1);
                }
                removeCallbacks(this.mCheckLongPress);
            } else if (action == 2) {
                int x = (int) ev.getRawX();
                int y = (int) ev.getRawY();
                float slop = QuickStepContract.getQuickStepTouchSlopPx(getContext());
                if (Math.abs(x - this.mTouchDownX) > slop || Math.abs(y - this.mTouchDownY) > slop) {
                    setPressed(false);
                    removeCallbacks(this.mCheckLongPress);
                }
            } else if (action == 3) {
                setPressed(false);
                if (this.mCode != 0) {
                    sendEvent(1, 32);
                }
                removeCallbacks(this.mCheckLongPress);
            }
        } else {
            this.mDownTime = SystemClock.uptimeMillis();
            this.mLongClicked = false;
            setPressed(true);
            this.mTouchDownX = (int) ev.getRawX();
            this.mTouchDownY = (int) ev.getRawY();
            if (this.mCode != 0) {
                sendEvent(0, 0, this.mDownTime);
            } else {
                performHapticFeedback(1);
            }
            if (!showSwipeUI) {
                playSoundEffect(0);
            }
            removeCallbacks(this.mCheckLongPress);
            postDelayed(this.mCheckLongPress, ViewConfiguration.getLongPressTimeout());
        }
        return true;
    }

    @Override // android.widget.ImageView, com.android.systemui.statusbar.phone.ButtonInterface
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (drawable == null) {
            return;
        }
        KeyButtonDrawable keyButtonDrawable = (KeyButtonDrawable) drawable;
        keyButtonDrawable.setDarkIntensity(this.mDarkIntensity);
        this.mHasOvalBg = keyButtonDrawable.hasOvalBg();
        if (this.mHasOvalBg) {
            this.mOvalBgPaint.setColor(keyButtonDrawable.getDrawableBackgroundColor());
        }
        this.mRipple.setType(keyButtonDrawable.hasOvalBg() ? KeyButtonRipple.Type.OVAL : KeyButtonRipple.Type.ROUNDED_RECT);
    }

    @Override // android.view.View
    public void playSoundEffect(int soundConstant) {
        if (this.mPlaySounds) {
            this.mAudioManager.playSoundEffect(soundConstant, ActivityManager.getCurrentUser());
        }
    }

    public void sendEvent(int action, int flags) {
        sendEvent(action, flags, SystemClock.uptimeMillis());
    }

    private void sendEvent(int action, int flags, long when) {
        this.mMetricsLogger.write(new LogMaker(931).setType(4).setSubtype(this.mCode).addTaggedData(933, Integer.valueOf(action)).addTaggedData(932, Integer.valueOf(flags)));
        if (this.mCode == 4 && flags != 128) {
            String str = TAG;
            Log.i(str, "Back button event: " + KeyEvent.actionToString(action));
            if (action == 1) {
                this.mOverviewProxyService.notifyBackAction((flags & 32) == 0, -1, -1, true, false);
            }
        }
        int repeatCount = (flags & 128) != 0 ? 1 : 0;
        KeyEvent ev = new KeyEvent(this.mDownTime, when, action, this.mCode, repeatCount, 0, -1, 0, flags | 8 | 64, 257);
        int displayId = -1;
        if (getDisplay() != null) {
            displayId = getDisplay().getDisplayId();
        }
        BubbleController bubbleController = (BubbleController) Dependency.get(BubbleController.class);
        int bubbleDisplayId = bubbleController.getExpandedDisplayId(this.mContext);
        if (this.mCode == 4 && bubbleDisplayId != -1) {
            displayId = bubbleDisplayId;
        }
        if (displayId != -1) {
            ev.setDisplayId(displayId);
        }
        this.mInputManager.injectInputEvent(ev, 0);
    }

    @Override // com.android.systemui.statusbar.phone.ButtonInterface
    public void abortCurrentGesture() {
        setPressed(false);
        this.mRipple.abortDelayedRipple();
        this.mGestureAborted = true;
    }

    @Override // com.android.systemui.statusbar.phone.ButtonInterface
    public void setDarkIntensity(float darkIntensity) {
        this.mDarkIntensity = darkIntensity;
        Drawable drawable = getDrawable();
        if (drawable != null) {
            ((KeyButtonDrawable) drawable).setDarkIntensity(darkIntensity);
            invalidate();
        }
        this.mRipple.setDarkIntensity(darkIntensity);
    }

    @Override // com.android.systemui.statusbar.phone.ButtonInterface
    public void setDelayTouchFeedback(boolean shouldDelay) {
        this.mRipple.setDelayTouchFeedback(shouldDelay);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        if (this.mHasOvalBg) {
            canvas.save();
            int cx = (getLeft() + getRight()) / 2;
            int cy = (getTop() + getBottom()) / 2;
            canvas.translate(cx, cy);
            int d = Math.min(getWidth(), getHeight());
            int r = d / 2;
            canvas.drawOval(-r, -r, r, r, this.mOvalBgPaint);
            canvas.restore();
        }
        super.draw(canvas);
    }

    @Override // com.android.systemui.statusbar.phone.ButtonInterface
    public void setVertical(boolean vertical) {
        this.mIsVertical = vertical;
    }
}
