package com.android.systemui.statusbar.notification.row.wrapper;

import android.app.PendingIntent;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.internal.util.ContrastColorUtil;
import com.android.internal.widget.NotificationActionListLayout;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.ViewTransformationHelper;
import com.android.systemui.statusbar.notification.ImageTransformState;
import com.android.systemui.statusbar.notification.TransformState;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.HybridNotificationView;
/* loaded from: classes21.dex */
public class NotificationTemplateViewWrapper extends NotificationHeaderViewWrapper {
    private NotificationActionListLayout mActions;
    protected View mActionsContainer;
    private ArraySet<PendingIntent> mCancelledPendingIntents;
    private int mContentHeight;
    private final int mFullHeaderTranslation;
    private float mHeaderTranslation;
    private int mMinHeightHint;
    protected ImageView mPicture;
    private ProgressBar mProgressBar;
    private View mRemoteInputHistory;
    private ImageView mReplyAction;
    private TextView mText;
    private TextView mTitle;
    private Rect mTmpRect;
    private UiOffloadThread mUiOffloadThread;

    /* JADX INFO: Access modifiers changed from: protected */
    public NotificationTemplateViewWrapper(Context ctx, View view, ExpandableNotificationRow row) {
        super(ctx, view, row);
        this.mTmpRect = new Rect();
        this.mCancelledPendingIntents = new ArraySet<>();
        this.mTransformationHelper.setCustomTransformation(new ViewTransformationHelper.CustomTransformation() { // from class: com.android.systemui.statusbar.notification.row.wrapper.NotificationTemplateViewWrapper.1
            @Override // com.android.systemui.statusbar.ViewTransformationHelper.CustomTransformation
            public boolean transformTo(TransformState ownState, TransformableView notification, float transformationAmount) {
                if (!(notification instanceof HybridNotificationView)) {
                    return false;
                }
                TransformState otherState = notification.getCurrentState(1);
                View text = ownState.getTransformedView();
                CrossFadeHelper.fadeOut(text, transformationAmount);
                if (otherState != null) {
                    ownState.transformViewVerticalTo(otherState, this, transformationAmount);
                    otherState.recycle();
                }
                return true;
            }

            @Override // com.android.systemui.statusbar.ViewTransformationHelper.CustomTransformation
            public boolean customTransformTarget(TransformState ownState, TransformState otherState) {
                float endY = getTransformationY(ownState, otherState);
                ownState.setTransformationEndY(endY);
                return true;
            }

            @Override // com.android.systemui.statusbar.ViewTransformationHelper.CustomTransformation
            public boolean transformFrom(TransformState ownState, TransformableView notification, float transformationAmount) {
                if (!(notification instanceof HybridNotificationView)) {
                    return false;
                }
                TransformState otherState = notification.getCurrentState(1);
                View text = ownState.getTransformedView();
                CrossFadeHelper.fadeIn(text, transformationAmount);
                if (otherState != null) {
                    ownState.transformViewVerticalFrom(otherState, this, transformationAmount);
                    otherState.recycle();
                }
                return true;
            }

            @Override // com.android.systemui.statusbar.ViewTransformationHelper.CustomTransformation
            public boolean initTransformation(TransformState ownState, TransformState otherState) {
                float startY = getTransformationY(ownState, otherState);
                ownState.setTransformationStartY(startY);
                return true;
            }

            private float getTransformationY(TransformState ownState, TransformState otherState) {
                int[] otherStablePosition = otherState.getLaidOutLocationOnScreen();
                int[] ownStablePosition = ownState.getLaidOutLocationOnScreen();
                return ((otherStablePosition[1] + otherState.getTransformedView().getHeight()) - ownStablePosition[1]) * 0.33f;
            }
        }, 2);
        this.mFullHeaderTranslation = ctx.getResources().getDimensionPixelSize(17105310) - ctx.getResources().getDimensionPixelSize(17105314);
    }

    private void resolveTemplateViews(StatusBarNotification notification) {
        this.mPicture = (ImageView) this.mView.findViewById(16909395);
        ImageView imageView = this.mPicture;
        if (imageView != null) {
            imageView.setTag(ImageTransformState.ICON_TAG, notification.getNotification().getLargeIcon());
        }
        this.mTitle = (TextView) this.mView.findViewById(16908310);
        this.mText = (TextView) this.mView.findViewById(16909530);
        View progress = this.mView.findViewById(16908301);
        if (progress instanceof ProgressBar) {
            this.mProgressBar = (ProgressBar) progress;
        } else {
            this.mProgressBar = null;
        }
        this.mActionsContainer = this.mView.findViewById(16908794);
        this.mActions = this.mView.findViewById(16908793);
        this.mReplyAction = (ImageView) this.mView.findViewById(16909385);
        this.mRemoteInputHistory = this.mView.findViewById(16909267);
        updatePendingIntentCancellations();
    }

    private void updatePendingIntentCancellations() {
        NotificationActionListLayout notificationActionListLayout = this.mActions;
        if (notificationActionListLayout != null) {
            int numActions = notificationActionListLayout.getChildCount();
            for (int i = 0; i < numActions; i++) {
                final Button action = (Button) this.mActions.getChildAt(i);
                performOnPendingIntentCancellation(action, new Runnable() { // from class: com.android.systemui.statusbar.notification.row.wrapper.-$$Lambda$NotificationTemplateViewWrapper$JRq0wlJLDK40PaCOgvvnny6lB0w
                    @Override // java.lang.Runnable
                    public final void run() {
                        NotificationTemplateViewWrapper.this.lambda$updatePendingIntentCancellations$0$NotificationTemplateViewWrapper(action);
                    }
                });
            }
        }
        ImageView imageView = this.mReplyAction;
        if (imageView != null) {
            imageView.setEnabled(true);
            performOnPendingIntentCancellation(this.mReplyAction, new Runnable() { // from class: com.android.systemui.statusbar.notification.row.wrapper.-$$Lambda$NotificationTemplateViewWrapper$Znytf0R_oPxyrIENjI1T5rfvZf4
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationTemplateViewWrapper.this.lambda$updatePendingIntentCancellations$1$NotificationTemplateViewWrapper();
                }
            });
        }
    }

    public /* synthetic */ void lambda$updatePendingIntentCancellations$0$NotificationTemplateViewWrapper(Button action) {
        if (action.isEnabled()) {
            action.setEnabled(false);
            ColorStateList textColors = action.getTextColors();
            int[] colors = textColors.getColors();
            int[] newColors = new int[colors.length];
            float disabledAlpha = this.mView.getResources().getFloat(17105299);
            for (int j = 0; j < colors.length; j++) {
                int color = colors[j];
                newColors[j] = blendColorWithBackground(color, disabledAlpha);
            }
            ColorStateList newColorStateList = new ColorStateList(textColors.getStates(), newColors);
            action.setTextColor(newColorStateList);
        }
    }

    public /* synthetic */ void lambda$updatePendingIntentCancellations$1$NotificationTemplateViewWrapper() {
        ImageView imageView = this.mReplyAction;
        if (imageView != null && imageView.isEnabled()) {
            this.mReplyAction.setEnabled(false);
            Drawable drawable = this.mReplyAction.getDrawable().mutate();
            PorterDuffColorFilter colorFilter = (PorterDuffColorFilter) drawable.getColorFilter();
            float disabledAlpha = this.mView.getResources().getFloat(17105299);
            if (colorFilter != null) {
                int color = colorFilter.getColor();
                drawable.mutate().setColorFilter(blendColorWithBackground(color, disabledAlpha), colorFilter.getMode());
                return;
            }
            this.mReplyAction.setAlpha(disabledAlpha);
        }
    }

    private int blendColorWithBackground(int color, float alpha) {
        return ContrastColorUtil.compositeColors(Color.argb((int) (255.0f * alpha), Color.red(color), Color.green(color), Color.blue(color)), resolveBackgroundColor());
    }

    private void performOnPendingIntentCancellation(View view, final Runnable cancellationRunnable) {
        final PendingIntent pendingIntent = (PendingIntent) view.getTag(16909320);
        if (pendingIntent == null) {
            return;
        }
        if (this.mCancelledPendingIntents.contains(pendingIntent)) {
            cancellationRunnable.run();
            return;
        }
        final PendingIntent.CancelListener listener = new PendingIntent.CancelListener() { // from class: com.android.systemui.statusbar.notification.row.wrapper.-$$Lambda$NotificationTemplateViewWrapper$JW7SqyfmhP6HCTJ8F1p53b90n6s
            public final void onCancelled(PendingIntent pendingIntent2) {
                NotificationTemplateViewWrapper.this.lambda$performOnPendingIntentCancellation$3$NotificationTemplateViewWrapper(pendingIntent, cancellationRunnable, pendingIntent2);
            }
        };
        if (this.mUiOffloadThread == null) {
            this.mUiOffloadThread = (UiOffloadThread) Dependency.get(UiOffloadThread.class);
        }
        if (view.isAttachedToWindow()) {
            this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.statusbar.notification.row.wrapper.-$$Lambda$NotificationTemplateViewWrapper$qLtzjAQEVXJmd7CTS0Q7hmNVWkU
                @Override // java.lang.Runnable
                public final void run() {
                    pendingIntent.registerCancelListener(listener);
                }
            });
        }
        view.addOnAttachStateChangeListener(new AnonymousClass2(pendingIntent, listener));
    }

    public /* synthetic */ void lambda$performOnPendingIntentCancellation$3$NotificationTemplateViewWrapper(final PendingIntent pendingIntent, final Runnable cancellationRunnable, PendingIntent intent) {
        this.mView.post(new Runnable() { // from class: com.android.systemui.statusbar.notification.row.wrapper.-$$Lambda$NotificationTemplateViewWrapper$W5E5gGqEebINqbELkmQR7ZZYP8Q
            @Override // java.lang.Runnable
            public final void run() {
                NotificationTemplateViewWrapper.this.lambda$performOnPendingIntentCancellation$2$NotificationTemplateViewWrapper(pendingIntent, cancellationRunnable);
            }
        });
    }

    public /* synthetic */ void lambda$performOnPendingIntentCancellation$2$NotificationTemplateViewWrapper(PendingIntent pendingIntent, Runnable cancellationRunnable) {
        this.mCancelledPendingIntents.add(pendingIntent);
        cancellationRunnable.run();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.notification.row.wrapper.NotificationTemplateViewWrapper$2  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass2 implements View.OnAttachStateChangeListener {
        final /* synthetic */ PendingIntent.CancelListener val$listener;
        final /* synthetic */ PendingIntent val$pendingIntent;

        AnonymousClass2(PendingIntent pendingIntent, PendingIntent.CancelListener cancelListener) {
            this.val$pendingIntent = pendingIntent;
            this.val$listener = cancelListener;
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
            UiOffloadThread uiOffloadThread = NotificationTemplateViewWrapper.this.mUiOffloadThread;
            final PendingIntent pendingIntent = this.val$pendingIntent;
            final PendingIntent.CancelListener cancelListener = this.val$listener;
            uiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.statusbar.notification.row.wrapper.-$$Lambda$NotificationTemplateViewWrapper$2$GihuSx3OPFqk-7UFX7W5ZofmkRI
                @Override // java.lang.Runnable
                public final void run() {
                    pendingIntent.registerCancelListener(cancelListener);
                }
            });
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            UiOffloadThread uiOffloadThread = NotificationTemplateViewWrapper.this.mUiOffloadThread;
            final PendingIntent pendingIntent = this.val$pendingIntent;
            final PendingIntent.CancelListener cancelListener = this.val$listener;
            uiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.statusbar.notification.row.wrapper.-$$Lambda$NotificationTemplateViewWrapper$2$YHJcr04bTyX63VZ5BMhNHsutz1Y
                @Override // java.lang.Runnable
                public final void run() {
                    pendingIntent.unregisterCancelListener(cancelListener);
                }
            });
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public boolean disallowSingleClick(float x, float y) {
        ImageView imageView = this.mReplyAction;
        if (imageView != null && imageView.getVisibility() == 0 && (isOnView(this.mReplyAction, x, y) || isOnView(this.mPicture, x, y))) {
            return true;
        }
        return super.disallowSingleClick(x, y);
    }

    private boolean isOnView(View view, float x, float y) {
        for (View searchView = (View) view.getParent(); searchView != null && !(searchView instanceof ExpandableNotificationRow); searchView = (View) searchView.getParent()) {
            searchView.getHitRect(this.mTmpRect);
            x -= this.mTmpRect.left;
            y -= this.mTmpRect.top;
        }
        view.getHitRect(this.mTmpRect);
        return this.mTmpRect.contains((int) x, (int) y);
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationHeaderViewWrapper, com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public void onContentUpdated(ExpandableNotificationRow row) {
        resolveTemplateViews(row.getStatusBarNotification());
        super.onContentUpdated(row);
        if (row.getHeaderVisibleAmount() != 1.0f) {
            setHeaderVisibleAmount(row.getHeaderVisibleAmount());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationHeaderViewWrapper
    public void updateTransformedTypes() {
        super.updateTransformedTypes();
        if (this.mTitle != null) {
            this.mTransformationHelper.addTransformedView(1, this.mTitle);
        }
        if (this.mText != null) {
            this.mTransformationHelper.addTransformedView(2, this.mText);
        }
        if (this.mPicture != null) {
            this.mTransformationHelper.addTransformedView(3, this.mPicture);
        }
        if (this.mProgressBar != null) {
            this.mTransformationHelper.addTransformedView(4, this.mProgressBar);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public void setContentHeight(int contentHeight, int minHeightHint) {
        super.setContentHeight(contentHeight, minHeightHint);
        this.mContentHeight = contentHeight;
        this.mMinHeightHint = minHeightHint;
        updateActionOffset();
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public boolean shouldClipToRounding(boolean topRounded, boolean bottomRounded) {
        View view;
        if (super.shouldClipToRounding(topRounded, bottomRounded)) {
            return true;
        }
        return (!bottomRounded || (view = this.mActionsContainer) == null || view.getVisibility() == 8) ? false : true;
    }

    private void updateActionOffset() {
        if (this.mActionsContainer != null) {
            int constrainedContentHeight = Math.max(this.mContentHeight, this.mMinHeightHint);
            this.mActionsContainer.setTranslationY((constrainedContentHeight - this.mView.getHeight()) - getHeaderTranslation(false));
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public int getHeaderTranslation(boolean forceNoHeader) {
        return forceNoHeader ? this.mFullHeaderTranslation : (int) this.mHeaderTranslation;
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public void setHeaderVisibleAmount(float headerVisibleAmount) {
        super.setHeaderVisibleAmount(headerVisibleAmount);
        this.mNotificationHeader.setAlpha(headerVisibleAmount);
        this.mHeaderTranslation = (1.0f - headerVisibleAmount) * this.mFullHeaderTranslation;
        this.mView.setTranslationY(this.mHeaderTranslation);
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public int getExtraMeasureHeight() {
        int extra = 0;
        NotificationActionListLayout notificationActionListLayout = this.mActions;
        if (notificationActionListLayout != null) {
            extra = notificationActionListLayout.getExtraMeasureHeight();
        }
        View view = this.mRemoteInputHistory;
        if (view != null && view.getVisibility() != 8) {
            extra += this.mRow.getContext().getResources().getDimensionPixelSize(R.dimen.remote_input_history_extra_height);
        }
        return super.getExtraMeasureHeight() + extra;
    }
}
