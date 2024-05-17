package com.android.systemui.statusbar.notification.stack;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.util.Preconditions;
import com.android.systemui.R;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
/* loaded from: classes21.dex */
public class SectionHeaderView extends ActivatableNotificationView {
    private ImageView mClearAllButton;
    private ViewGroup mContents;
    private TextView mLabelView;
    private View.OnClickListener mOnClearClickListener;
    private final RectF mTmpRect;

    public SectionHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mOnClearClickListener = null;
        this.mTmpRect = new RectF();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView, android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContents = (ViewGroup) Preconditions.checkNotNull((ViewGroup) findViewById(R.id.content));
        bindContents();
    }

    private void bindContents() {
        this.mLabelView = (TextView) Preconditions.checkNotNull((TextView) findViewById(R.id.header_label));
        this.mClearAllButton = (ImageView) Preconditions.checkNotNull((ImageView) findViewById(R.id.btn_clear_all));
        View.OnClickListener onClickListener = this.mOnClearClickListener;
        if (onClickListener != null) {
            this.mClearAllButton.setOnClickListener(onClickListener);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    protected View getContentView() {
        return this.mContents;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reinflateContents() {
        this.mContents.removeAllViews();
        LayoutInflater.from(getContext()).inflate(R.layout.status_bar_notification_section_header_contents, this.mContents);
        bindContents();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public boolean isTransparent() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onUiModeChanged() {
        updateBackgroundColors();
        this.mLabelView.setTextColor(getContext().getColor(R.color.notification_section_header_label_color));
        this.mClearAllButton.setImageResource(R.drawable.status_bar_notification_section_header_clear_btn);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAreThereDismissableGentleNotifs(boolean areThereDismissableGentleNotifs) {
        this.mClearAllButton.setVisibility(areThereDismissableGentleNotifs ? 0 : 8);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    public boolean disallowSingleClick(MotionEvent event) {
        this.mTmpRect.set(this.mClearAllButton.getLeft(), this.mClearAllButton.getTop(), this.mClearAllButton.getLeft() + this.mClearAllButton.getWidth(), this.mClearAllButton.getTop() + this.mClearAllButton.getHeight());
        return this.mTmpRect.contains(event.getX(), event.getY());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnHeaderClickListener(View.OnClickListener listener) {
        this.mContents.setOnClickListener(listener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnClearAllClickListener(View.OnClickListener listener) {
        this.mOnClearClickListener = listener;
        this.mClearAllButton.setOnClickListener(listener);
    }
}
