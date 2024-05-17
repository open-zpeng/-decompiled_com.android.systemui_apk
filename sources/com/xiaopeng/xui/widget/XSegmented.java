package com.xiaopeng.xui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.vui.commons.model.VuiEvent;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.drawable.XIndicatorDrawable;
import com.xiaopeng.xui.theme.XThemeManager;
import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.vui.VuiView;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
/* loaded from: classes25.dex */
public class XSegmented extends XLinearLayout implements IVuiElementListener, View.OnClickListener {
    private static final String TAG = XSegmented.class.getSimpleName();
    @DrawableRes
    private int[] mIconRes;
    private final XIndicatorDrawable mIndicatorDrawable;
    private final boolean mIsInit;
    private boolean mIsPlayingAnim;
    private SegmentListener mSegmentListener;
    private int mSelection;
    @ColorRes
    private int mTextColorRes;
    private float mTextSize;

    /* loaded from: classes25.dex */
    public interface SegmentListener {
        boolean onInterceptChange(XSegmented xSegmented, int i);

        void onSelectionChange(XSegmented xSegmented, int i, boolean z);
    }

    public XSegmented(Context context) {
        this(context, null);
    }

    public XSegmented(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XSegmented(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "CustomViewStyleable"})
    public XSegmented(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mIndicatorDrawable = new XIndicatorDrawable();
        this.mIndicatorDrawable.inflateAttrs(getResources(), attrs, context.getTheme());
        this.mIndicatorDrawable.setCallback(this);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.XSegmented, defStyleAttr, defStyleRes);
        CharSequence[] vuiLabels = typedArray.getTextArray(R.styleable.XSegmented_segment_vui_labels);
        int selection = typedArray.getInt(R.styleable.XSegmented_segment_selected_pos, -1);
        if (typedArray.getBoolean(R.styleable.XSegmented_segment_anim_disable_click, false)) {
            this.mIndicatorDrawable.setOnAnimationListener(new XIndicatorDrawable.OnAnimationListener() { // from class: com.xiaopeng.xui.widget.XSegmented.1
                @Override // com.xiaopeng.xui.drawable.XIndicatorDrawable.OnAnimationListener
                public void onStart() {
                    XSegmented.this.mIsPlayingAnim = true;
                }

                @Override // com.xiaopeng.xui.drawable.XIndicatorDrawable.OnAnimationListener
                public void onEnd() {
                    XSegmented.this.mIsPlayingAnim = false;
                }
            });
        }
        if (typedArray.hasValue(R.styleable.XSegmented_segment_texts)) {
            this.mTextSize = typedArray.getDimension(R.styleable.XSegmented_segment_text_size, R.dimen.x_font_title_03_size);
            this.mTextColorRes = typedArray.getResourceId(R.styleable.XSegmented_segment_text_color, R.color.x_tabs_layout_title_color);
            CharSequence[] mTitleTexts = typedArray.getTextArray(R.styleable.XSegmented_segment_texts);
            addSegmentView(mTitleTexts, vuiLabels);
        } else {
            int iconRes = typedArray.getResourceId(R.styleable.XSegmented_segment_icons, 0);
            TypedArray iconArray = getResources().obtainTypedArray(iconRes);
            this.mIconRes = new int[iconArray.length()];
            for (int i = 0; i < iconArray.length(); i++) {
                this.mIconRes[i] = iconArray.getResourceId(i, 0);
            }
            iconArray.recycle();
            addSegmentView(this.mIconRes, vuiLabels);
        }
        typedArray.recycle();
        setSelection(selection, false, false, false);
        setGravity(17);
        setWillNotDraw(false);
        this.mIsInit = true;
    }

    public void addSegmentView(CharSequence[] titles, CharSequence[] vuiLabels) {
        if (titles != null) {
            int i = 0;
            while (i < titles.length) {
                CharSequence c = (vuiLabels == null || vuiLabels.length <= i) ? null : vuiLabels[i];
                addSegmentView(titles[i], c);
                i++;
            }
        }
    }

    public void addSegmentView(CharSequence title, CharSequence vuiLabel) {
        addSegmentView(title, vuiLabel, getChildCount());
    }

    public void addSegmentView(CharSequence title, CharSequence vuiLabel, int index) {
        if (title != null) {
            XTextView textView = new XTextView(getContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0f));
            textView.setGravity(17);
            textView.setText(title);
            textView.setTextSize(this.mTextSize);
            textView.setTextColor(getContext().getColorStateList(this.mTextColorRes));
            if (vuiLabel != null) {
                textView.setVuiLabel(vuiLabel.toString());
            }
            textView.setVuiElementType(VuiElementType.TEXTVIEW);
            textView.setSoundEffectsEnabled(isSoundEffectsEnabled());
            textView.setOnClickListener(this);
            addView(textView, index);
            int selection = this.mSelection;
            if (index <= selection) {
                selection++;
            }
            setSelection(selection, true, this.mIsInit);
        }
    }

    public void setTitleTextSize(float size) {
        this.mTextSize = size;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextSize(size);
            }
        }
    }

    public void setTitleTextColor(@ColorRes int colorRes) {
        this.mTextColorRes = colorRes;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(getContext().getColorStateList(colorRes));
            }
        }
    }

    public void setTitleText(CharSequence text, CharSequence vuiLabel, int index) {
        if (getChildCount() > index) {
            View view = getChildAt(index);
            if (view instanceof XTextView) {
                XTextView textView = (XTextView) view;
                textView.setText(text);
                textView.setVuiLabel(vuiLabel.toString());
                return;
            }
            XLogUtils.w(TAG, "setTitleText failed, view is not XTextView");
            return;
        }
        XLogUtils.w(TAG, "setTitleText failed, index > child count");
    }

    public void addSegmentView(@DrawableRes int[] icons, CharSequence[] vuiLabels) {
        this.mIconRes = icons;
        int i = 0;
        while (i < icons.length) {
            CharSequence c = (vuiLabels == null || vuiLabels.length <= i) ? null : vuiLabels[i];
            addSegmentView(icons[i], c);
            i++;
        }
    }

    public void addSegmentView(@DrawableRes int icon, CharSequence vuiLabel) {
        addSegmentView(icon, vuiLabel, getChildCount());
    }

    @SuppressLint({"UseCompatLoadingForDrawables"})
    public void addSegmentView(@DrawableRes int icon, CharSequence vuiLabel, int index) {
        if (this.mIconRes == null) {
            this.mIconRes = new int[0];
        }
        if (index < 0) {
            index = 0;
        }
        if (index >= this.mIconRes.length) {
            int[] iconRes = this.mIconRes;
            this.mIconRes = new int[iconRes.length + 1];
            System.arraycopy(iconRes, 0, this.mIconRes, 0, iconRes.length);
            this.mIconRes[iconRes.length] = icon;
        } else if (index < getChildCount()) {
            int[] iconRes2 = this.mIconRes;
            this.mIconRes = new int[iconRes2.length + 1];
            System.arraycopy(iconRes2, 0, this.mIconRes, 0, index);
            int[] iArr = this.mIconRes;
            iArr[index] = icon;
            System.arraycopy(iconRes2, index, iArr, index + 1, iconRes2.length - index);
        }
        XImageView imageView = new XImageView(getContext());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0f));
        if (icon != 0) {
            imageView.setImageDrawable(getContext().getDrawable(icon));
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        if (vuiLabel != null) {
            imageView.setVuiLabel(vuiLabel.toString());
        }
        imageView.setVuiElementType(VuiElementType.TEXTVIEW);
        imageView.setSoundEffectsEnabled(isSoundEffectsEnabled());
        imageView.setOnClickListener(this);
        addView(imageView, index);
        int selection = this.mSelection;
        if (index <= selection) {
            selection++;
        }
        setSelection(selection, true, this.mIsInit);
    }

    @SuppressLint({"UseCompatLoadingForDrawables"})
    public void setSegmentIcon(@DrawableRes int res, CharSequence vuiLabel, int index) {
        if (getChildCount() > index) {
            View view = getChildAt(index);
            if (view instanceof XImageView) {
                XImageView xImageView = (XImageView) view;
                xImageView.setImageDrawable(getContext().getDrawable(res));
                if (vuiLabel != null) {
                    xImageView.setVuiLabel(vuiLabel.toString());
                }
                int[] iArr = this.mIconRes;
                if (iArr != null && iArr.length > index) {
                    iArr[index] = res;
                    return;
                }
                return;
            }
            XLogUtils.w(TAG, "setSegmentIcon failed, view is not XImageView");
            return;
        }
        XLogUtils.w(TAG, "setSegmentIcon failed, index > child count");
    }

    public void removeSegment(int index) {
        if (getChildCount() > index) {
            View view = getChildAt(index);
            if ((view instanceof ImageView) && this.mIconRes != null) {
                int[] iconRes = this.mIconRes;
                this.mIconRes = new int[iconRes.length - 1];
                System.arraycopy(iconRes, 0, this.mIconRes, 0, index);
                int[] iArr = this.mIconRes;
                System.arraycopy(iconRes, index + 1, iArr, index, iArr.length - index);
            }
            removeViewAt(index);
            int selection = this.mSelection;
            if (index == selection) {
                selection = -1;
            } else if (index < selection || selection >= getChildCount()) {
                selection--;
            }
            setSelection(selection, true, true);
        }
    }

    public void clearSegment() {
        removeAllViews();
        this.mIconRes = new int[0];
        setSelection(-1);
    }

    public void setSelection(int index) {
        setSelection(index, true, false, false);
    }

    private void setSelection(int index, boolean playAnimation, boolean isEdt) {
        setSelection(index, playAnimation, false, isEdt);
    }

    public void setSelection(int index, boolean playAnimation) {
        setSelection(index, playAnimation, false, false);
    }

    private void setSelection(int index, boolean playAnimation, boolean fromUser, boolean isEdt) {
        int i;
        int current = this.mSelection;
        this.mSelection = index;
        int i2 = this.mSelection;
        if (i2 < 0 || i2 >= getChildCount()) {
            this.mSelection = -1;
        }
        int i3 = 0;
        while (i3 < getChildCount()) {
            getChildAt(i3).setSelected(this.mSelection == i3);
            i3++;
        }
        XIndicatorDrawable xIndicatorDrawable = this.mIndicatorDrawable;
        if (xIndicatorDrawable != null) {
            xIndicatorDrawable.setSelection(getChildCount(), this.mSelection, playAnimation);
        }
        changeIndicatorEnable();
        SegmentListener segmentListener = this.mSegmentListener;
        if (segmentListener != null && current != (i = this.mSelection)) {
            segmentListener.onSelectionChange(this, i, fromUser);
        }
        if (current != this.mSelection || isEdt) {
            updateVui(this);
        }
    }

    public int getSelection() {
        return this.mSelection;
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (int i = 0; i < getChildCount(); i++) {
            setEnabled(enabled, i);
        }
    }

    public void setEnabled(boolean enabled, int index) {
        if (index >= 0 && index < getChildCount()) {
            getChildAt(index).setEnabled(enabled);
            changeIndicatorEnable();
        }
    }

    public boolean isEnable(int index) {
        if (index >= 0 && index < getChildCount()) {
            return getChildAt(index).isEnabled();
        }
        return false;
    }

    private void changeIndicatorEnable() {
        int i;
        if (this.mIndicatorDrawable != null && (i = this.mSelection) >= 0 && i < getChildCount()) {
            this.mIndicatorDrawable.setEnable(getChildAt(this.mSelection).isEnabled());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XLinearLayout, android.view.View
    @SuppressLint({"UseCompatLoadingForDrawables"})
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (XThemeManager.isThemeChanged(newConfig)) {
            setTitleTextColor(this.mTextColorRes);
            int[] iArr = this.mIconRes;
            if (iArr != null && iArr.length == getChildCount()) {
                int i = 0;
                while (true) {
                    int[] iArr2 = this.mIconRes;
                    if (i >= iArr2.length) {
                        break;
                    }
                    setSegmentIcon(iArr2[i], null, i);
                    i++;
                }
            }
            XIndicatorDrawable xIndicatorDrawable = this.mIndicatorDrawable;
            if (xIndicatorDrawable != null) {
                xIndicatorDrawable.onConfigurationChanged(getResources(), getContext().getTheme());
            }
        }
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    @SuppressLint({"DrawAllocation"})
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        XIndicatorDrawable xIndicatorDrawable = this.mIndicatorDrawable;
        if (xIndicatorDrawable != null) {
            xIndicatorDrawable.setBounds(new Rect(l, t, r, b));
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        XIndicatorDrawable xIndicatorDrawable = this.mIndicatorDrawable;
        if (xIndicatorDrawable != null) {
            xIndicatorDrawable.setState(getDrawableState());
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        XIndicatorDrawable xIndicatorDrawable = this.mIndicatorDrawable;
        if (xIndicatorDrawable != null) {
            xIndicatorDrawable.draw(canvas);
        }
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(@NonNull Drawable drawable) {
        super.invalidateDrawable(drawable);
        if (drawable instanceof XIndicatorDrawable) {
            invalidate();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (this.mIsPlayingAnim) {
            return;
        }
        int index = indexOfChild(v);
        SegmentListener segmentListener = this.mSegmentListener;
        if (segmentListener == null || !segmentListener.onInterceptChange(this, index)) {
            setSelection(indexOfChild(v), true, true, false);
        }
    }

    public void setSegmentListener(SegmentListener segmentListener) {
        this.mSegmentListener = segmentListener;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public VuiElement onBuildVuiElement(String vuiElementId, IVuiElementBuilder builder) {
        setVuiValue(Integer.valueOf(this.mSelection));
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView instanceof VuiView) {
                VuiView xView = (VuiView) childView;
                xView.setVuiPosition(i);
                xView.setVuiElementId(vuiElementId + "_" + i);
                String str = TAG;
                XLogUtils.d(str, "onBuildVuiElement:" + vuiElementId);
            }
        }
        return null;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public boolean onVuiElementEvent(final View view, VuiEvent event) {
        final Double value;
        XLogUtils.d("Segmented onVuiElementEvent");
        if (view == null || (value = (Double) event.getEventValue(event)) == null) {
            return false;
        }
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XSegmented$ER8BZntFAAkVbBNKoz8rSSxFn8I
            @Override // java.lang.Runnable
            public final void run() {
                XSegmented.this.lambda$onVuiElementEvent$0$XSegmented(value, view);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$onVuiElementEvent$0$XSegmented(Double value, View view) {
        View elementView;
        if (value.intValue() < getChildCount()) {
            elementView = getChildAt(value.intValue());
        } else {
            elementView = view;
        }
        VuiFloatingLayerManager.show(elementView);
        setPerformVuiAction(true);
        setSelection(value.intValue(), true, true, false);
        setPerformVuiAction(false);
    }
}
