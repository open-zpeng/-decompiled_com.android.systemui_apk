package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.systemui.R;
import com.android.systemui.statusbar.notification.row.StackScrollerDecorView;
import com.android.systemui.statusbar.notification.stack.ExpandableViewState;
/* loaded from: classes21.dex */
public class EmptyShadeView extends StackScrollerDecorView {
    private TextView mEmptyText;
    private int mText;

    public EmptyShadeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mText = R.string.empty_shade_text;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mEmptyText.setText(this.mText);
    }

    @Override // com.android.systemui.statusbar.notification.row.StackScrollerDecorView
    protected View findContentView() {
        return findViewById(R.id.no_notifications);
    }

    @Override // com.android.systemui.statusbar.notification.row.StackScrollerDecorView
    protected View findSecondaryView() {
        return null;
    }

    public void setTextColor(int color) {
        this.mEmptyText.setTextColor(color);
    }

    public void setText(int text) {
        this.mText = text;
        this.mEmptyText.setText(this.mText);
    }

    public int getTextResource() {
        return this.mText;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.row.StackScrollerDecorView, android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mEmptyText = (TextView) findContentView();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public ExpandableViewState createExpandableViewState() {
        return new EmptyShadeViewState();
    }

    /* loaded from: classes21.dex */
    public class EmptyShadeViewState extends ExpandableViewState {
        public EmptyShadeViewState() {
        }

        @Override // com.android.systemui.statusbar.notification.stack.ExpandableViewState, com.android.systemui.statusbar.notification.stack.ViewState
        public void applyToView(View view) {
            super.applyToView(view);
            if (view instanceof EmptyShadeView) {
                EmptyShadeView emptyShadeView = (EmptyShadeView) view;
                boolean z = true;
                boolean visible = ((float) this.clipTopAmount) <= ((float) EmptyShadeView.this.mEmptyText.getPaddingTop()) * 0.6f;
                if (!visible || !emptyShadeView.isVisible()) {
                    z = false;
                }
                emptyShadeView.setContentVisible(z);
            }
        }
    }
}
