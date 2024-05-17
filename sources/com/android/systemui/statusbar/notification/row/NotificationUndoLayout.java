package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class NotificationUndoLayout extends FrameLayout {
    private View mConfirmationTextView;
    private boolean mIsMultiline;
    private int mMultilineTopMargin;
    private View mUndoView;

    public NotificationUndoLayout(Context context) {
        this(context, null);
    }

    public NotificationUndoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationUndoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIsMultiline = false;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mConfirmationTextView = findViewById(R.id.confirmation_text);
        this.mUndoView = findViewById(R.id.undo);
        this.mMultilineTopMargin = getResources().getDimensionPixelOffset(17105313);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        FrameLayout.LayoutParams confirmationLayoutParams = (FrameLayout.LayoutParams) this.mConfirmationTextView.getLayoutParams();
        FrameLayout.LayoutParams undoLayoutParams = (FrameLayout.LayoutParams) this.mUndoView.getLayoutParams();
        int measuredWidth = getMeasuredWidth();
        int requiredWidth = this.mConfirmationTextView.getMeasuredWidth() + confirmationLayoutParams.rightMargin + confirmationLayoutParams.leftMargin + this.mUndoView.getMeasuredWidth() + undoLayoutParams.rightMargin;
        if (requiredWidth > measuredWidth) {
            this.mIsMultiline = true;
            int updatedHeight = this.mMultilineTopMargin + this.mConfirmationTextView.getMeasuredHeight() + this.mUndoView.getMeasuredHeight() + undoLayoutParams.topMargin + undoLayoutParams.bottomMargin;
            setMeasuredDimension(measuredWidth, updatedHeight);
            return;
        }
        this.mIsMultiline = false;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int undoViewLeft;
        if (this.mIsMultiline) {
            int parentBottom = getMeasuredHeight();
            int parentRight = getMeasuredWidth();
            FrameLayout.LayoutParams confirmationLayoutParams = (FrameLayout.LayoutParams) this.mConfirmationTextView.getLayoutParams();
            FrameLayout.LayoutParams undoLayoutParams = (FrameLayout.LayoutParams) this.mUndoView.getLayoutParams();
            this.mConfirmationTextView.layout(confirmationLayoutParams.leftMargin, this.mMultilineTopMargin, confirmationLayoutParams.leftMargin + this.mConfirmationTextView.getMeasuredWidth(), this.mMultilineTopMargin + this.mConfirmationTextView.getMeasuredHeight());
            if (getLayoutDirection() == 1) {
                undoViewLeft = undoLayoutParams.rightMargin;
            } else {
                undoViewLeft = (parentRight - this.mUndoView.getMeasuredWidth()) - undoLayoutParams.rightMargin;
            }
            View view = this.mUndoView;
            view.layout(undoViewLeft, (parentBottom - view.getMeasuredHeight()) - undoLayoutParams.bottomMargin, this.mUndoView.getMeasuredWidth() + undoViewLeft, parentBottom - undoLayoutParams.bottomMargin);
            return;
        }
        super.onLayout(changed, left, top, right, bottom);
    }
}
