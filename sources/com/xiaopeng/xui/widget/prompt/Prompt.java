package com.xiaopeng.xui.widget.prompt;

import android.content.Context;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import com.xiaopeng.xpui.R;
import java.util.ArrayList;
/* loaded from: classes25.dex */
public abstract class Prompt {
    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 0;
    private static final String TAG = "XPrompt";
    private Animation mChangeAnim;
    private Context mContext;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private boolean mIsQueueShow;
    private boolean mIsUseExitAnim;
    private int mLongTime;
    private OnPromptActionListener mOnPromptActionListener;
    private int mShortTime;
    private XPromptMessage mXPromptMessage;
    XPromptView mXPromptView;
    private Handler mHandler = new Handler();
    private ArrayList<XPromptMessage> mMessages = new ArrayList<>();
    private Runnable mRemoveRunnable = new Runnable() { // from class: com.xiaopeng.xui.widget.prompt.Prompt.2
        @Override // java.lang.Runnable
        public void run() {
            Prompt.this.removeView();
        }
    };
    private Runnable mShowRunnable = new Runnable() { // from class: com.xiaopeng.xui.widget.prompt.Prompt.3
        @Override // java.lang.Runnable
        public void run() {
            if (!Prompt.this.showNext()) {
                Prompt.this.cancel();
            }
        }
    };

    protected abstract boolean addView();

    protected abstract void removeView();

    public Prompt(@NonNull Context context) {
        this.mContext = context;
        this.mXPromptView = new XPromptView(context);
        this.mXPromptView.setPrompt(this);
        this.mShortTime = context.getResources().getInteger(R.integer.x_prompt_shortTime);
        this.mLongTime = context.getResources().getInteger(R.integer.x_prompt_longTime);
        initAnim();
    }

    private void initAnim() {
        this.mIsUseExitAnim = true;
        this.mEnterAnim = AnimationUtils.loadAnimation(this.mContext, R.anim.x_prompt_enter_anim);
        this.mExitAnim = AnimationUtils.loadAnimation(this.mContext, R.anim.x_prompt_exit_anim);
        this.mExitAnim.setAnimationListener(new Animation.AnimationListener() { // from class: com.xiaopeng.xui.widget.prompt.Prompt.1
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                Prompt.this.mHandler.post(Prompt.this.mRemoveRunnable);
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }
        });
        this.mChangeAnim = AnimationUtils.loadAnimation(this.mContext, R.anim.x_prompt_change_anim);
    }

    private void setXPromptMessage(XPromptMessage XPromptMessage) {
        this.mXPromptMessage = XPromptMessage;
    }

    public Prompt setQueueShow(boolean isQueueShow) {
        this.mIsQueueShow = isQueueShow;
        return this;
    }

    public boolean isQueueShow() {
        return this.mIsQueueShow;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addMessage(XPromptMessage message) {
        if (!this.mIsQueueShow) {
            this.mMessages.clear();
        }
        this.mMessages.add(message);
    }

    @UiThread
    public void show(@NonNull CharSequence text) {
        show(text, 0);
    }

    @UiThread
    public void show(@NonNull CharSequence text, int duration) {
        show(new XPromptMessage(duration, text));
    }

    @UiThread
    public void show(@NonNull CharSequence text, @DrawableRes int actionIcon, String actionId) {
        show(text, actionIcon, actionId, 1);
    }

    @UiThread
    public void show(@NonNull CharSequence text, @DrawableRes int actionIcon, String actionId, int duration) {
        show(new XPromptMessage(duration, text, actionIcon, actionId));
    }

    @UiThread
    public void show(@NonNull CharSequence text, CharSequence actionButton, String actionId) {
        show(text, actionButton, actionId, 1);
    }

    @UiThread
    public void show(@NonNull CharSequence text, CharSequence actionButton, String actionId, int duration) {
        show(new XPromptMessage(duration, text, actionButton, actionId));
    }

    @UiThread
    public void show(@NonNull XPromptMessage message) {
        addMessage(message);
        enqueuePrompt();
    }

    @UiThread
    public void show() {
        enqueuePrompt();
    }

    private void enqueuePrompt() {
        if (this.mXPromptView.getParent() == null || !this.mIsQueueShow) {
            showNext();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean showNext() {
        if (this.mMessages.size() > 0) {
            XPromptMessage message = this.mMessages.remove(0);
            setXPromptMessage(message);
            return showCurrent();
        }
        return false;
    }

    private boolean showCurrent() {
        if (this.mXPromptMessage == null) {
            return false;
        }
        this.mHandler.removeCallbacks(this.mRemoveRunnable);
        if (this.mXPromptView.getParent() == null) {
            if (!addView()) {
                return false;
            }
            this.mXPromptView.getContentView().startAnimation(this.mEnterAnim);
        } else {
            this.mXPromptView.getContentView().startAnimation(this.mChangeAnim);
        }
        this.mHandler.removeCallbacks(this.mShowRunnable);
        this.mHandler.postDelayed(this.mShowRunnable, this.mXPromptMessage.getDuration() == 0 ? this.mShortTime : this.mLongTime);
        this.mXPromptView.show(this.mXPromptMessage.getText(), this.mXPromptMessage.getIcon(), this.mXPromptMessage.getButton());
        return true;
    }

    @UiThread
    public void cancel() {
        this.mMessages.clear();
        this.mHandler.removeCallbacks(this.mShowRunnable);
        if (this.mIsUseExitAnim) {
            this.mXPromptView.getContentView().startAnimation(this.mExitAnim);
        } else {
            this.mHandler.post(this.mRemoveRunnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Prompt setUseExitAnim(boolean useExitAnim) {
        this.mIsUseExitAnim = useExitAnim;
        return this;
    }

    public Prompt setOnPromptActionClickListener(OnPromptActionListener l) {
        this.mOnPromptActionListener = l;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onAction(int which) {
        OnPromptActionListener onPromptActionListener = this.mOnPromptActionListener;
        if (onPromptActionListener != null) {
            onPromptActionListener.onPromptAction(this.mXPromptMessage.getId(), which);
        }
        this.mShowRunnable.run();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearQueue() {
        this.mHandler.removeCallbacks(this.mShowRunnable);
    }
}
