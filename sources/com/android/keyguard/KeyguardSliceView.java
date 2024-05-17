package com.android.keyguard;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Trace;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.SliceViewManager;
import androidx.slice.core.SliceQuery;
import androidx.slice.widget.ListContent;
import androidx.slice.widget.RowContent;
import androidx.slice.widget.SliceContent;
import androidx.slice.widget.SliceLiveData;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.graphics.ColorUtils;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.keyguard.KeyguardSliceProvider;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.wakelock.KeepAwakeAnimationListener;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.infoflow.message.define.CardExtra;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
/* loaded from: classes19.dex */
public class KeyguardSliceView extends LinearLayout implements View.OnClickListener, Observer<Slice>, TunerService.Tunable, ConfigurationController.ConfigurationListener {
    public static final int DEFAULT_ANIM_DURATION = 550;
    private static final String TAG = "KeyguardSliceView";
    private final ActivityStarter mActivityStarter;
    private final HashMap<View, PendingIntent> mClickActions;
    private final ConfigurationController mConfigurationController;
    private Runnable mContentChangeListener;
    private float mDarkAmount;
    private int mDisplayId;
    private boolean mHasHeader;
    private int mIconSize;
    private int mIconSizeWithHeader;
    private Uri mKeyguardSliceUri;
    private final LayoutTransition mLayoutTransition;
    private LiveData<Slice> mLiveData;
    private Row mRow;
    private final int mRowPadding;
    private float mRowTextSize;
    private final int mRowWithHeaderPadding;
    private float mRowWithHeaderTextSize;
    private Slice mSlice;
    private int mTextColor;
    @VisibleForTesting
    TextView mTitle;

    @Inject
    public KeyguardSliceView(@Named("view_context") Context context, AttributeSet attrs, ActivityStarter activityStarter, ConfigurationController configurationController) {
        super(context, attrs);
        this.mDarkAmount = 0.0f;
        this.mDisplayId = -1;
        TunerService tunerService = (TunerService) Dependency.get(TunerService.class);
        tunerService.addTunable(this, "keyguard_slice_uri");
        this.mClickActions = new HashMap<>();
        this.mRowPadding = context.getResources().getDimensionPixelSize(R.dimen.subtitle_clock_padding);
        this.mRowWithHeaderPadding = context.getResources().getDimensionPixelSize(R.dimen.header_subtitle_padding);
        this.mActivityStarter = activityStarter;
        this.mConfigurationController = configurationController;
        this.mLayoutTransition = new LayoutTransition();
        this.mLayoutTransition.setStagger(0, 275L);
        this.mLayoutTransition.setDuration(2, 550L);
        this.mLayoutTransition.setDuration(3, 275L);
        this.mLayoutTransition.disableTransitionType(0);
        this.mLayoutTransition.disableTransitionType(1);
        this.mLayoutTransition.setInterpolator(2, Interpolators.FAST_OUT_SLOW_IN);
        this.mLayoutTransition.setInterpolator(3, Interpolators.ALPHA_OUT);
        this.mLayoutTransition.setAnimateParentHierarchy(false);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTitle = (TextView) findViewById(R.id.title);
        this.mRow = (Row) findViewById(R.id.row);
        this.mTextColor = Utils.getColorAttrDefaultColor(this.mContext, R.attr.wallpaperTextColor);
        this.mIconSize = (int) this.mContext.getResources().getDimension(R.dimen.widget_icon_size);
        this.mIconSizeWithHeader = (int) this.mContext.getResources().getDimension(R.dimen.header_icon_size);
        this.mRowTextSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.widget_label_font_size);
        this.mRowWithHeaderTextSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.header_row_font_size);
        this.mTitle.setOnClickListener(this);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mDisplayId = getDisplay().getDisplayId();
        this.mLiveData.observeForever(this);
        this.mConfigurationController.addCallback(this);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mDisplayId == 0) {
            this.mLiveData.removeObserver(this);
        }
        this.mConfigurationController.removeCallback(this);
    }

    @Override // android.view.View
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        setLayoutTransition(isVisible ? this.mLayoutTransition : null);
    }

    public boolean hasHeader() {
        return this.mHasHeader;
    }

    private void showSlice() {
        LinearLayout.LayoutParams layoutParams;
        SliceContent headerContent;
        List<SliceContent> subItems;
        boolean z;
        Drawable iconDrawable;
        Trace.beginSection("KeyguardSliceView#showSlice");
        if (this.mSlice == null) {
            this.mTitle.setVisibility(8);
            this.mRow.setVisibility(8);
            this.mHasHeader = false;
            Runnable runnable = this.mContentChangeListener;
            if (runnable != null) {
                runnable.run();
            }
            Trace.endSection();
            return;
        }
        this.mClickActions.clear();
        ListContent lc = new ListContent(getContext(), this.mSlice);
        SliceContent headerContent2 = lc.getHeader();
        this.mHasHeader = (headerContent2 == null || headerContent2.getSliceItem().hasHint("list_item")) ? false : true;
        List<SliceContent> subItems2 = new ArrayList<>();
        for (int i = 0; i < lc.getRowItems().size(); i++) {
            SliceContent subItem = lc.getRowItems().get(i);
            String itemUri = subItem.getSliceItem().getSlice().getUri().toString();
            if (!KeyguardSliceProvider.KEYGUARD_ACTION_URI.equals(itemUri)) {
                subItems2.add(subItem);
            }
        }
        if (!this.mHasHeader) {
            this.mTitle.setVisibility(8);
        } else {
            this.mTitle.setVisibility(0);
            RowContent header = lc.getHeader();
            SliceItem mainTitle = header.getTitleItem();
            CharSequence title = mainTitle != null ? mainTitle.getText() : null;
            this.mTitle.setText(title);
            if (header.getPrimaryAction() != null && header.getPrimaryAction().getAction() != null) {
                this.mClickActions.put(this.mTitle, header.getPrimaryAction().getAction());
            }
        }
        int subItemsCount = subItems2.size();
        int blendedColor = getTextColor();
        boolean z2 = this.mHasHeader;
        this.mRow.setVisibility(subItemsCount > 0 ? 0 : 8);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mRow.getLayoutParams();
        layoutParams2.topMargin = this.mHasHeader ? this.mRowWithHeaderPadding : this.mRowPadding;
        this.mRow.setLayoutParams(layoutParams2);
        int startIndex = z2 ? 1 : 0;
        int i2 = startIndex;
        while (i2 < subItemsCount) {
            RowContent rc = (RowContent) subItems2.get(i2);
            SliceItem item = rc.getSliceItem();
            Uri itemTag = item.getSlice().getUri();
            KeyguardSliceButton button = (KeyguardSliceButton) this.mRow.findViewWithTag(itemTag);
            if (button == null) {
                button = new KeyguardSliceButton(this.mContext);
                button.setTextColor(blendedColor);
                button.setTag(itemTag);
                int viewIndex = i2 - (this.mHasHeader ? 1 : 0);
                this.mRow.addView(button, viewIndex);
            }
            PendingIntent pendingIntent = null;
            if (rc.getPrimaryAction() != null) {
                pendingIntent = rc.getPrimaryAction().getAction();
            }
            this.mClickActions.put(button, pendingIntent);
            SliceItem titleItem = rc.getTitleItem();
            button.setText(titleItem == null ? null : titleItem.getText());
            button.setContentDescription(rc.getContentDescription());
            ListContent lc2 = lc;
            button.setTextSize(0, this.mHasHeader ? this.mRowWithHeaderTextSize : this.mRowTextSize);
            SliceItem icon = SliceQuery.find(item.getSlice(), CardExtra.KEY_CARD_IMAGE);
            if (icon == null) {
                layoutParams = layoutParams2;
                headerContent = headerContent2;
                subItems = subItems2;
                z = false;
                iconDrawable = null;
            } else {
                int iconSize = this.mHasHeader ? this.mIconSizeWithHeader : this.mIconSize;
                layoutParams = layoutParams2;
                iconDrawable = icon.getIcon().loadDrawable(this.mContext);
                if (iconDrawable == null) {
                    headerContent = headerContent2;
                    subItems = subItems2;
                    z = false;
                } else {
                    headerContent = headerContent2;
                    int width = (int) ((iconDrawable.getIntrinsicWidth() / iconDrawable.getIntrinsicHeight()) * iconSize);
                    subItems = subItems2;
                    z = false;
                    iconDrawable.setBounds(0, 0, Math.max(width, 1), iconSize);
                }
            }
            button.setCompoundDrawables(iconDrawable, null, null, null);
            button.setOnClickListener(this);
            button.setClickable(pendingIntent != null ? true : z);
            i2++;
            lc = lc2;
            layoutParams2 = layoutParams;
            headerContent2 = headerContent;
            subItems2 = subItems;
        }
        int i3 = 0;
        while (i3 < this.mRow.getChildCount()) {
            View child = this.mRow.getChildAt(i3);
            if (!this.mClickActions.containsKey(child)) {
                this.mRow.removeView(child);
                i3--;
            }
            i3++;
        }
        Runnable runnable2 = this.mContentChangeListener;
        if (runnable2 != null) {
            runnable2.run();
        }
        Trace.endSection();
    }

    public void setDarkAmount(float darkAmount) {
        this.mDarkAmount = darkAmount;
        this.mRow.setDarkAmount(darkAmount);
        updateTextColors();
    }

    private void updateTextColors() {
        int blendedColor = getTextColor();
        this.mTitle.setTextColor(blendedColor);
        int childCount = this.mRow.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = this.mRow.getChildAt(i);
            if (v instanceof Button) {
                ((Button) v).setTextColor(blendedColor);
            }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        PendingIntent action = this.mClickActions.get(v);
        if (action != null) {
            this.mActivityStarter.startPendingIntentDismissingKeyguard(action);
        }
    }

    public void setContentChangeListener(Runnable contentChangeListener) {
        this.mContentChangeListener = contentChangeListener;
    }

    @Override // androidx.lifecycle.Observer
    public void onChanged(Slice slice) {
        this.mSlice = slice;
        showSlice();
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        setupUri(newValue);
    }

    public void setupUri(String uriString) {
        if (uriString == null) {
            uriString = KeyguardSliceProvider.KEYGUARD_SLICE_URI;
        }
        boolean wasObserving = false;
        LiveData<Slice> liveData = this.mLiveData;
        if (liveData != null && liveData.hasActiveObservers()) {
            wasObserving = true;
            this.mLiveData.removeObserver(this);
        }
        this.mKeyguardSliceUri = Uri.parse(uriString);
        this.mLiveData = SliceLiveData.fromUri(this.mContext, this.mKeyguardSliceUri);
        if (wasObserving) {
            this.mLiveData.observeForever(this);
        }
    }

    @VisibleForTesting
    int getTextColor() {
        return ColorUtils.blendARGB(this.mTextColor, -1, this.mDarkAmount);
    }

    @VisibleForTesting
    void setTextColor(int textColor) {
        this.mTextColor = textColor;
        updateTextColors();
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onDensityOrFontScaleChanged() {
        this.mIconSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.widget_icon_size);
        this.mIconSizeWithHeader = (int) this.mContext.getResources().getDimension(R.dimen.header_icon_size);
        this.mRowTextSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.widget_label_font_size);
        this.mRowWithHeaderTextSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.header_row_font_size);
    }

    public void refresh() {
        Slice slice;
        Trace.beginSection("KeyguardSliceView#refresh");
        if (KeyguardSliceProvider.KEYGUARD_SLICE_URI.equals(this.mKeyguardSliceUri.toString())) {
            KeyguardSliceProvider instance = KeyguardSliceProvider.getAttachedInstance();
            if (instance != null) {
                slice = instance.onBindSlice(this.mKeyguardSliceUri);
            } else {
                Log.w(TAG, "Keyguard slice not bound yet?");
                slice = null;
            }
        } else {
            slice = SliceViewManager.getInstance(getContext()).bindSlice(this.mKeyguardSliceUri);
        }
        onChanged(slice);
        Trace.endSection();
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        Boolean valueOf;
        pw.println("KeyguardSliceView:");
        pw.println("  mClickActions: " + this.mClickActions);
        StringBuilder sb = new StringBuilder();
        sb.append("  mTitle: ");
        TextView textView = this.mTitle;
        Object obj = "null";
        if (textView == null) {
            valueOf = "null";
        } else {
            valueOf = Boolean.valueOf(textView.getVisibility() == 0);
        }
        sb.append(valueOf);
        pw.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  mRow: ");
        Row row = this.mRow;
        if (row != null) {
            obj = Boolean.valueOf(row.getVisibility() == 0);
        }
        sb2.append(obj);
        pw.println(sb2.toString());
        pw.println("  mTextColor: " + Integer.toHexString(this.mTextColor));
        pw.println("  mDarkAmount: " + this.mDarkAmount);
        pw.println("  mSlice: " + this.mSlice);
        pw.println("  mHasHeader: " + this.mHasHeader);
    }

    /* loaded from: classes19.dex */
    public static class Row extends LinearLayout {
        private float mDarkAmount;
        private final Animation.AnimationListener mKeepAwakeListener;
        private LayoutTransition mLayoutTransition;

        public Row(Context context) {
            this(context, null);
        }

        public Row(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public Row(Context context, AttributeSet attrs, int defStyleAttr) {
            this(context, attrs, defStyleAttr, 0);
        }

        public Row(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            this.mKeepAwakeListener = new KeepAwakeAnimationListener(this.mContext);
        }

        @Override // android.view.View
        protected void onFinishInflate() {
            this.mLayoutTransition = new LayoutTransition();
            this.mLayoutTransition.setDuration(550L);
            PropertyValuesHolder left = PropertyValuesHolder.ofInt("left", 0, 1);
            PropertyValuesHolder right = PropertyValuesHolder.ofInt("right", 0, 1);
            ObjectAnimator changeAnimator = ObjectAnimator.ofPropertyValuesHolder(null, left, right);
            this.mLayoutTransition.setAnimator(0, changeAnimator);
            this.mLayoutTransition.setAnimator(1, changeAnimator);
            this.mLayoutTransition.setInterpolator(0, Interpolators.ACCELERATE_DECELERATE);
            this.mLayoutTransition.setInterpolator(1, Interpolators.ACCELERATE_DECELERATE);
            this.mLayoutTransition.setStartDelay(0, 550L);
            this.mLayoutTransition.setStartDelay(1, 550L);
            ObjectAnimator appearAnimator = ObjectAnimator.ofFloat((Object) null, ThemeManager.AttributeSet.ALPHA, 0.0f, 1.0f);
            this.mLayoutTransition.setAnimator(2, appearAnimator);
            this.mLayoutTransition.setInterpolator(2, Interpolators.ALPHA_IN);
            ObjectAnimator disappearAnimator = ObjectAnimator.ofFloat((Object) null, ThemeManager.AttributeSet.ALPHA, 1.0f, 0.0f);
            this.mLayoutTransition.setInterpolator(3, Interpolators.ALPHA_OUT);
            this.mLayoutTransition.setDuration(3, 137L);
            this.mLayoutTransition.setAnimator(3, disappearAnimator);
            this.mLayoutTransition.setAnimateParentHierarchy(false);
        }

        @Override // android.view.View
        public void onVisibilityAggregated(boolean isVisible) {
            super.onVisibilityAggregated(isVisible);
            setLayoutTransition(isVisible ? this.mLayoutTransition : null);
        }

        @Override // android.widget.LinearLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child instanceof KeyguardSliceButton) {
                    ((KeyguardSliceButton) child).setMaxWidth(width / childCount);
                }
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        public void setDarkAmount(float darkAmount) {
            boolean isAwake = darkAmount != 0.0f;
            boolean wasAwake = this.mDarkAmount != 0.0f;
            if (isAwake == wasAwake) {
                return;
            }
            this.mDarkAmount = darkAmount;
            setLayoutAnimationListener(isAwake ? null : this.mKeepAwakeListener);
        }

        @Override // android.view.View
        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes19.dex */
    public static class KeyguardSliceButton extends Button implements ConfigurationController.ConfigurationListener {
        private static int sStyleId = R.style.TextAppearance_Keyguard_Secondary;

        public KeyguardSliceButton(Context context) {
            super(context, null, 0, sStyleId);
            onDensityOrFontScaleChanged();
            setEllipsize(TextUtils.TruncateAt.END);
        }

        @Override // android.widget.TextView, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
        }

        @Override // android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            ((ConfigurationController) Dependency.get(ConfigurationController.class)).removeCallback(this);
        }

        @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
        public void onDensityOrFontScaleChanged() {
            updatePadding();
        }

        @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
        public void onOverlayChanged() {
            setTextAppearance(sStyleId);
        }

        @Override // android.widget.TextView
        public void setText(CharSequence text, TextView.BufferType type) {
            super.setText(text, type);
            updatePadding();
        }

        private void updatePadding() {
            boolean hasText = !TextUtils.isEmpty(getText());
            int horizontalPadding = ((int) getContext().getResources().getDimension(R.dimen.widget_horizontal_padding)) / 2;
            setPadding(horizontalPadding, 0, (hasText ? 1 : -1) * horizontalPadding, 0);
            setCompoundDrawablePadding((int) this.mContext.getResources().getDimension(R.dimen.widget_icon_padding));
        }

        @Override // android.widget.TextView
        public void setTextColor(int color) {
            super.setTextColor(color);
            updateDrawableColors();
        }

        @Override // android.widget.TextView
        public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
            super.setCompoundDrawables(left, top, right, bottom);
            updateDrawableColors();
            updatePadding();
        }

        private void updateDrawableColors() {
            Drawable[] compoundDrawables;
            int color = getCurrentTextColor();
            for (Drawable drawable : getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setTint(color);
                }
            }
        }
    }
}
