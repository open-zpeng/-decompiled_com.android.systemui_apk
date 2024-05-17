package com.android.systemui.volume;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.R;
import com.android.systemui.volume.Interaction;
import java.util.Objects;
/* loaded from: classes21.dex */
public class SegmentedButtons extends LinearLayout {
    private Callback mCallback;
    private final View.OnClickListener mClick;
    private final ConfigurableTexts mConfigurableTexts;
    private final Context mContext;
    protected final LayoutInflater mInflater;
    protected Object mSelectedValue;
    private static final int LABEL_RES_KEY = R.id.label;
    private static final Typeface REGULAR = Typeface.create("sans-serif", 0);
    private static final Typeface MEDIUM = Typeface.create("sans-serif-medium", 0);

    /* loaded from: classes21.dex */
    public interface Callback extends Interaction.Callback {
        void onSelected(Object obj, boolean z);
    }

    public SegmentedButtons(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mClick = new View.OnClickListener() { // from class: com.android.systemui.volume.SegmentedButtons.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SegmentedButtons.this.setSelectedValue(v.getTag(), true);
            }
        };
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
        setOrientation(0);
        this.mConfigurableTexts = new ConfigurableTexts(this.mContext);
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public Object getSelectedValue() {
        return this.mSelectedValue;
    }

    public void setSelectedValue(Object value, boolean fromClick) {
        if (Objects.equals(value, this.mSelectedValue)) {
            return;
        }
        this.mSelectedValue = value;
        for (int i = 0; i < getChildCount(); i++) {
            TextView c = (TextView) getChildAt(i);
            Object tag = c.getTag();
            boolean selected = Objects.equals(this.mSelectedValue, tag);
            c.setSelected(selected);
            setSelectedStyle(c, selected);
        }
        fireOnSelected(fromClick);
    }

    protected void setSelectedStyle(TextView textView, boolean selected) {
        textView.setTypeface(selected ? MEDIUM : REGULAR);
    }

    public Button inflateButton() {
        return (Button) this.mInflater.inflate(R.layout.segmented_button, (ViewGroup) this, false);
    }

    public void addButton(int labelResId, int contentDescriptionResId, Object value) {
        Button b = inflateButton();
        b.setTag(LABEL_RES_KEY, Integer.valueOf(labelResId));
        b.setText(labelResId);
        b.setContentDescription(getResources().getString(contentDescriptionResId));
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) b.getLayoutParams();
        if (getChildCount() == 0) {
            lp.rightMargin = 0;
            lp.leftMargin = 0;
        }
        b.setLayoutParams(lp);
        addView(b);
        b.setTag(value);
        b.setOnClickListener(this.mClick);
        Interaction.register(b, new Interaction.Callback() { // from class: com.android.systemui.volume.SegmentedButtons.1
            @Override // com.android.systemui.volume.Interaction.Callback
            public void onInteraction() {
                SegmentedButtons.this.fireInteraction();
            }
        });
        this.mConfigurableTexts.add(b, labelResId);
    }

    public void update() {
        this.mConfigurableTexts.update();
    }

    private void fireOnSelected(boolean fromClick) {
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onSelected(this.mSelectedValue, fromClick);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fireInteraction() {
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onInteraction();
        }
    }
}
