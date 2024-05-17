package com.xiaopeng.systemui.infoflow.aissistant;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import com.xiaopeng.systemui.infoflow.theme.AnimatedImageView;
/* loaded from: classes24.dex */
public class MsgIconView extends AnimatedImageView {
    public MsgIconView(Context context) {
        super(context);
        updateImageAlpha();
    }

    public MsgIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        updateImageAlpha();
    }

    public MsgIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        updateImageAlpha();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AnimatedImageView, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateImageAlpha();
    }

    private void updateImageAlpha() {
        boolean night = isNight(getContext());
        if (night) {
            setImageAlpha(229);
        } else {
            setImageAlpha(255);
        }
    }

    public static boolean isNight(Context context) {
        if (context == null) {
            return false;
        }
        Configuration config = context.getResources().getConfiguration();
        return (config.uiMode & 48) == 32;
    }
}
