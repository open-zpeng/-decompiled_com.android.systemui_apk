package com.xiaopeng.systemui.speech.component;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import com.xiaopeng.systemui.speech.component.IComponentListener;
import com.xiaopeng.systemui.speech.model.AnimationListenerExUtils;
/* loaded from: classes24.dex */
public abstract class AreaWidget<T extends IComponentListener> {
    private final int mArea;
    private T mComponentListener;
    private View mContentView;
    private final Context mContext;
    private boolean mFirst;
    private final Animation mInAnimation = new AlphaAnimation(0.0f, 1.0f);
    private final Animation mOutAnimation;

    protected abstract View initView(View view);

    protected abstract String logTag();

    public AreaWidget(Context context, int area) {
        this.mContext = context;
        this.mArea = area;
        this.mInAnimation.setDuration(300L);
        this.mOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        this.mOutAnimation.setDuration(300L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Context getContext() {
        return this.mContext;
    }

    protected int getArea() {
        return this.mArea;
    }

    protected View getContentView() {
        return this.mContentView;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isShowing() {
        View view = this.mContentView;
        return view != null && view.getVisibility() == 0;
    }

    public boolean isFirst() {
        return this.mFirst;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void hide(boolean anim) {
        final View v = this.mContentView;
        if (v == null) {
            return;
        }
        if (anim) {
            AnimationListenerExUtils.start(v, this.mOutAnimation, new Runnable() { // from class: com.xiaopeng.systemui.speech.component.-$$Lambda$AreaWidget$NbSy8kkSrTVh2G0Tn_Wy9YW9Mxw
                @Override // java.lang.Runnable
                public final void run() {
                    v.setVisibility(8);
                }
            });
        } else {
            v.setVisibility(8);
        }
        this.mFirst = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean createAndShow(Object data, boolean anim) {
        if (data == null) {
            return false;
        }
        if (this.mContentView == null) {
            createContentView();
        }
        if (this.mContentView == null) {
            return false;
        }
        IComponentListener listener = this.mComponentListener;
        if (listener != null) {
            listener.onShow(this.mArea);
        }
        if (this.mContentView.getVisibility() != 0) {
            this.mContentView.setVisibility(0);
            if (anim) {
                AnimationListenerExUtils.start(this.mContentView, this.mInAnimation, null);
            }
        }
        this.mFirst = false;
        return true;
    }

    private void createContentView() {
        IComponentListener listener = this.mComponentListener;
        if (listener != null) {
            View rootView = listener.getView(this.mArea);
            if (rootView != null) {
                this.mContentView = initView(rootView);
            }
            View view = this.mContentView;
            if (view == null) {
                return;
            }
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.xiaopeng.systemui.speech.component.AreaWidget.1
                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewAttachedToWindow(View v) {
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewDetachedFromWindow(View v) {
                    AreaWidget.this.mFirst = true;
                    v.setVisibility(8);
                }
            });
        }
    }

    public void setListener(T listener) {
        this.mComponentListener = listener;
    }

    protected T getComponentListener() {
        return this.mComponentListener;
    }
}
