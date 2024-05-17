package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.keyguard.AlphaOptimizedLinearLayout;
import com.android.systemui.R;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.ViewTransformationHelper;
import com.android.systemui.statusbar.notification.TransformState;
/* loaded from: classes21.dex */
public class HybridNotificationView extends AlphaOptimizedLinearLayout implements TransformableView {
    protected TextView mTextView;
    protected TextView mTitleView;
    private ViewTransformationHelper mTransformationHelper;

    public HybridNotificationView(Context context) {
        this(context, null);
    }

    public HybridNotificationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HybridNotificationView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HybridNotificationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TextView getTitleView() {
        return this.mTitleView;
    }

    public TextView getTextView() {
        return this.mTextView;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTitleView = (TextView) findViewById(R.id.notification_title);
        this.mTextView = (TextView) findViewById(R.id.notification_text);
        this.mTransformationHelper = new ViewTransformationHelper();
        this.mTransformationHelper.setCustomTransformation(new ViewTransformationHelper.CustomTransformation() { // from class: com.android.systemui.statusbar.notification.row.HybridNotificationView.1
            @Override // com.android.systemui.statusbar.ViewTransformationHelper.CustomTransformation
            public boolean transformTo(TransformState ownState, TransformableView notification, float transformationAmount) {
                TransformState otherState = notification.getCurrentState(1);
                CrossFadeHelper.fadeOut(HybridNotificationView.this.mTextView, transformationAmount);
                if (otherState != null) {
                    ownState.transformViewVerticalTo(otherState, transformationAmount);
                    otherState.recycle();
                }
                return true;
            }

            @Override // com.android.systemui.statusbar.ViewTransformationHelper.CustomTransformation
            public boolean transformFrom(TransformState ownState, TransformableView notification, float transformationAmount) {
                TransformState otherState = notification.getCurrentState(1);
                CrossFadeHelper.fadeIn(HybridNotificationView.this.mTextView, transformationAmount);
                if (otherState != null) {
                    ownState.transformViewVerticalFrom(otherState, transformationAmount);
                    otherState.recycle();
                }
                return true;
            }
        }, 2);
        this.mTransformationHelper.addTransformedView(1, this.mTitleView);
        this.mTransformationHelper.addTransformedView(2, this.mTextView);
    }

    public void bind(CharSequence title) {
        bind(title, null);
    }

    public void bind(CharSequence title, CharSequence text) {
        this.mTitleView.setText(title);
        this.mTitleView.setVisibility(TextUtils.isEmpty(title) ? 8 : 0);
        if (TextUtils.isEmpty(text)) {
            this.mTextView.setVisibility(8);
            this.mTextView.setText((CharSequence) null);
        } else {
            this.mTextView.setVisibility(0);
            this.mTextView.setText(text.toString());
        }
        requestLayout();
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public TransformState getCurrentState(int fadingView) {
        return this.mTransformationHelper.getCurrentState(fadingView);
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public void transformTo(TransformableView notification, Runnable endRunnable) {
        this.mTransformationHelper.transformTo(notification, endRunnable);
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public void transformTo(TransformableView notification, float transformationAmount) {
        this.mTransformationHelper.transformTo(notification, transformationAmount);
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public void transformFrom(TransformableView notification) {
        this.mTransformationHelper.transformFrom(notification);
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public void transformFrom(TransformableView notification, float transformationAmount) {
        this.mTransformationHelper.transformFrom(notification, transformationAmount);
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public void setVisible(boolean visible) {
        setVisibility(visible ? 0 : 4);
        this.mTransformationHelper.setVisible(visible);
    }
}
