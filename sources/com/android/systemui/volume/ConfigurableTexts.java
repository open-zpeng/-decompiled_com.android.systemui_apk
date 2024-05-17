package com.android.systemui.volume;

import android.content.Context;
import android.content.res.Resources;
import android.util.ArrayMap;
import android.view.View;
import android.widget.TextView;
/* loaded from: classes21.dex */
public class ConfigurableTexts {
    private final Context mContext;
    private final ArrayMap<TextView, Integer> mTexts = new ArrayMap<>();
    private final ArrayMap<TextView, Integer> mTextLabels = new ArrayMap<>();
    private final Runnable mUpdateAll = new Runnable() { // from class: com.android.systemui.volume.ConfigurableTexts.2
        @Override // java.lang.Runnable
        public void run() {
            for (int i = 0; i < ConfigurableTexts.this.mTexts.size(); i++) {
                ConfigurableTexts configurableTexts = ConfigurableTexts.this;
                configurableTexts.setTextSizeH((TextView) configurableTexts.mTexts.keyAt(i), ((Integer) ConfigurableTexts.this.mTexts.valueAt(i)).intValue());
            }
            for (int i2 = 0; i2 < ConfigurableTexts.this.mTextLabels.size(); i2++) {
                ConfigurableTexts configurableTexts2 = ConfigurableTexts.this;
                configurableTexts2.setTextLabelH((TextView) configurableTexts2.mTextLabels.keyAt(i2), ((Integer) ConfigurableTexts.this.mTextLabels.valueAt(i2)).intValue());
            }
        }
    };

    public ConfigurableTexts(Context context) {
        this.mContext = context;
    }

    public int add(TextView text) {
        return add(text, -1);
    }

    public int add(final TextView text, int labelResId) {
        if (text == null) {
            return 0;
        }
        Resources res = this.mContext.getResources();
        float fontScale = res.getConfiguration().fontScale;
        float density = res.getDisplayMetrics().density;
        float px = text.getTextSize();
        final int sp = (int) ((px / fontScale) / density);
        this.mTexts.put(text, Integer.valueOf(sp));
        text.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.android.systemui.volume.ConfigurableTexts.1
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View v) {
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View v) {
                ConfigurableTexts.this.setTextSizeH(text, sp);
            }
        });
        this.mTextLabels.put(text, Integer.valueOf(labelResId));
        return sp;
    }

    public void update() {
        if (this.mTexts.isEmpty()) {
            return;
        }
        this.mTexts.keyAt(0).post(this.mUpdateAll);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTextSizeH(TextView text, int sp) {
        text.setTextSize(2, sp);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTextLabelH(TextView text, int labelResId) {
        if (labelResId >= 0) {
            try {
                Util.setText(text, this.mContext.getString(labelResId));
            } catch (Resources.NotFoundException e) {
            }
        }
    }
}
