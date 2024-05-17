package com.android.systemui.statusbar.notification;

import android.text.Layout;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Pools;
import android.view.View;
import android.widget.TextView;
import com.android.systemui.statusbar.notification.TransformState;
/* loaded from: classes21.dex */
public class TextViewTransformState extends TransformState {
    private static Pools.SimplePool<TextViewTransformState> sInstancePool = new Pools.SimplePool<>(40);
    private TextView mText;

    @Override // com.android.systemui.statusbar.notification.TransformState
    public void initFrom(View view, TransformState.TransformInfo transformInfo) {
        super.initFrom(view, transformInfo);
        this.mText = (TextView) view;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.TransformState
    public boolean sameAs(TransformState otherState) {
        if (super.sameAs(otherState)) {
            return true;
        }
        if (otherState instanceof TextViewTransformState) {
            TextViewTransformState otherTvs = (TextViewTransformState) otherState;
            if (TextUtils.equals(otherTvs.mText.getText(), this.mText.getText())) {
                int ownEllipsized = getEllipsisCount();
                int otherEllipsized = otherTvs.getEllipsisCount();
                return ownEllipsized == otherEllipsized && this.mText.getLineCount() == otherTvs.mText.getLineCount() && hasSameSpans(otherTvs);
            }
        }
        return false;
    }

    private boolean hasSameSpans(TextViewTransformState otherTvs) {
        TextView textView = this.mText;
        boolean hasSpans = textView instanceof Spanned;
        boolean otherHasSpans = otherTvs.mText instanceof Spanned;
        if (hasSpans != otherHasSpans) {
            return false;
        }
        if (!hasSpans) {
            return true;
        }
        Spanned ownSpanned = (Spanned) textView;
        Object[] spans = ownSpanned.getSpans(0, ownSpanned.length(), Object.class);
        Spanned otherSpanned = (Spanned) otherTvs.mText;
        Object[] otherSpans = otherSpanned.getSpans(0, otherSpanned.length(), Object.class);
        if (spans.length != otherSpans.length) {
            return false;
        }
        for (int i = 0; i < spans.length; i++) {
            Object span = spans[i];
            Object otherSpan = otherSpans[i];
            if (!span.getClass().equals(otherSpan.getClass()) || ownSpanned.getSpanStart(span) != otherSpanned.getSpanStart(otherSpan) || ownSpanned.getSpanEnd(span) != otherSpanned.getSpanEnd(otherSpan)) {
                return false;
            }
        }
        return true;
    }

    @Override // com.android.systemui.statusbar.notification.TransformState
    protected boolean transformScale(TransformState otherState) {
        int lineCount;
        if (otherState instanceof TextViewTransformState) {
            TextViewTransformState otherTvs = (TextViewTransformState) otherState;
            return TextUtils.equals(this.mText.getText(), otherTvs.mText.getText()) && (lineCount = this.mText.getLineCount()) == 1 && lineCount == otherTvs.mText.getLineCount() && getEllipsisCount() == otherTvs.getEllipsisCount() && getViewHeight() != otherTvs.getViewHeight();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.TransformState
    public int getViewWidth() {
        Layout l = this.mText.getLayout();
        if (l != null) {
            return (int) l.getLineWidth(0);
        }
        return super.getViewWidth();
    }

    @Override // com.android.systemui.statusbar.notification.TransformState
    protected int getViewHeight() {
        return this.mText.getLineHeight();
    }

    private int getInnerHeight(TextView text) {
        return (text.getHeight() - text.getPaddingTop()) - text.getPaddingBottom();
    }

    private int getEllipsisCount() {
        Layout l = this.mText.getLayout();
        if (l != null) {
            int lines = l.getLineCount();
            if (lines > 0) {
                return l.getEllipsisCount(0);
            }
        }
        return 0;
    }

    public static TextViewTransformState obtain() {
        TextViewTransformState instance = (TextViewTransformState) sInstancePool.acquire();
        if (instance != null) {
            return instance;
        }
        return new TextViewTransformState();
    }

    @Override // com.android.systemui.statusbar.notification.TransformState
    public void recycle() {
        super.recycle();
        sInstancePool.release(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.TransformState
    public void reset() {
        super.reset();
        this.mText = null;
    }
}
