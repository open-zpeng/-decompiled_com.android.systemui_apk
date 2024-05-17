package com.xiaopeng.xui.widget.quicksidebar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.XRelativeLayout;
import java.lang.ref.WeakReference;
/* loaded from: classes25.dex */
public class XQuickSideBarTipsView extends XRelativeLayout {
    private static final int SIDEBAR_TIPS_DELAY_CODE = 1;
    private boolean isAnimationHideTips;
    private DelayHandler mDelayHandler;
    private long mDelayedTime;
    private XQuickSideBarTipsItemView mTipsView;

    public XQuickSideBarTipsView(Context context) {
        this(context, null);
    }

    public XQuickSideBarTipsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XQuickSideBarTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.isAnimationHideTips = true;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mDelayHandler = new DelayHandler(this);
        if (attrs != null) {
            this.mTipsView = new XQuickSideBarTipsItemView(context, attrs);
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.XQuickSideBarView);
            this.mDelayedTime = a.getInteger(R.styleable.XQuickSideBarView_sidebarTipsDelayTime, 500);
            a.recycle();
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
            addView(this.mTipsView, layoutParams);
        }
    }

    public void setAnimationHideTips(boolean animationHideTips) {
        this.isAnimationHideTips = animationHideTips;
    }

    public void setText(String text) {
        this.mTipsView.setText(text);
    }

    public void setDelayedTime(long delayedTime) {
        this.mDelayedTime = delayedTime;
    }

    public void display(boolean visible) {
        if (!visible) {
            this.mDelayHandler.sendEmptyMessageDelayed(1, this.mDelayedTime);
            return;
        }
        setVisibility(0);
        this.mDelayHandler.removeMessages(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes25.dex */
    public static final class DelayHandler extends Handler {
        private final WeakReference<XQuickSideBarTipsView> reference;

        public DelayHandler(XQuickSideBarTipsView context) {
            this.reference = new WeakReference<>(context);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            XQuickSideBarTipsView model = this.reference.get();
            if (model != null && msg.what == 1) {
                model.hideView(model.isAnimationHideTips);
                model.mDelayHandler.removeMessages(1);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideView(boolean isAnimate) {
        if (isAnimate) {
            animate().alpha(0.0f).setDuration(200L).setListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.xui.widget.quicksidebar.XQuickSideBarTipsView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    XQuickSideBarTipsView.this.setVisibility(4);
                    XQuickSideBarTipsView.this.setAlpha(1.0f);
                }
            });
        } else {
            setVisibility(4);
        }
    }
}
