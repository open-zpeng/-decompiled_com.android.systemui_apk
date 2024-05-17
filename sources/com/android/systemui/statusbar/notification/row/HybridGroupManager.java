package com.android.systemui.statusbar.notification.row;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class HybridGroupManager {
    private final Context mContext;
    private int mOverflowNumberColor;
    private int mOverflowNumberPadding;
    private float mOverflowNumberSize;
    private final ViewGroup mParent;

    public HybridGroupManager(Context ctx, ViewGroup parent) {
        this.mContext = ctx;
        this.mParent = parent;
        initDimens();
    }

    public void initDimens() {
        Resources res = this.mContext.getResources();
        this.mOverflowNumberSize = res.getDimensionPixelSize(R.dimen.group_overflow_number_size);
        this.mOverflowNumberPadding = res.getDimensionPixelSize(R.dimen.group_overflow_number_padding);
    }

    private HybridNotificationView inflateHybridViewWithStyle(int style) {
        LayoutInflater inflater = (LayoutInflater) new ContextThemeWrapper(this.mContext, style).getSystemService(LayoutInflater.class);
        HybridNotificationView hybrid = (HybridNotificationView) inflater.inflate(R.layout.hybrid_notification, this.mParent, false);
        this.mParent.addView(hybrid);
        return hybrid;
    }

    private TextView inflateOverflowNumber() {
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(LayoutInflater.class);
        TextView numberView = (TextView) inflater.inflate(R.layout.hybrid_overflow_number, this.mParent, false);
        this.mParent.addView(numberView);
        updateOverFlowNumberColor(numberView);
        return numberView;
    }

    private void updateOverFlowNumberColor(TextView numberView) {
        numberView.setTextColor(this.mOverflowNumberColor);
    }

    public void setOverflowNumberColor(TextView numberView, int colorRegular) {
        this.mOverflowNumberColor = colorRegular;
        if (numberView != null) {
            updateOverFlowNumberColor(numberView);
        }
    }

    public HybridNotificationView bindFromNotification(HybridNotificationView reusableView, Notification notification) {
        return bindFromNotificationWithStyle(reusableView, notification, R.style.HybridNotification);
    }

    private HybridNotificationView bindFromNotificationWithStyle(HybridNotificationView reusableView, Notification notification, int style) {
        if (reusableView == null) {
            reusableView = inflateHybridViewWithStyle(style);
        }
        CharSequence titleText = resolveTitle(notification);
        CharSequence contentText = resolveText(notification);
        reusableView.bind(titleText, contentText);
        return reusableView;
    }

    private CharSequence resolveText(Notification notification) {
        CharSequence contentText = notification.extras.getCharSequence("android.text");
        if (contentText == null) {
            return notification.extras.getCharSequence("android.bigText");
        }
        return contentText;
    }

    private CharSequence resolveTitle(Notification notification) {
        CharSequence titleText = notification.extras.getCharSequence("android.title");
        if (titleText == null) {
            return notification.extras.getCharSequence("android.title.big");
        }
        return titleText;
    }

    public TextView bindOverflowNumber(TextView reusableView, int number) {
        if (reusableView == null) {
            reusableView = inflateOverflowNumber();
        }
        String text = this.mContext.getResources().getString(R.string.notification_group_overflow_indicator, Integer.valueOf(number));
        if (!text.equals(reusableView.getText())) {
            reusableView.setText(text);
        }
        String contentDescription = String.format(this.mContext.getResources().getQuantityString(R.plurals.notification_group_overflow_description, number), Integer.valueOf(number));
        reusableView.setContentDescription(contentDescription);
        reusableView.setTextSize(0, this.mOverflowNumberSize);
        reusableView.setPaddingRelative(reusableView.getPaddingStart(), reusableView.getPaddingTop(), this.mOverflowNumberPadding, reusableView.getPaddingBottom());
        updateOverFlowNumberColor(reusableView);
        return reusableView;
    }
}
