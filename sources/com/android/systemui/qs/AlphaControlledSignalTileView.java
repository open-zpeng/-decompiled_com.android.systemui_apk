package com.android.systemui.qs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import com.android.systemui.qs.tileimpl.SlashImageView;
/* loaded from: classes21.dex */
public class AlphaControlledSignalTileView extends SignalTileView {
    public AlphaControlledSignalTileView(Context context) {
        super(context);
    }

    @Override // com.android.systemui.qs.SignalTileView
    protected SlashImageView createSlashImageView(Context context) {
        return new AlphaControlledSlashImageView(context);
    }

    /* loaded from: classes21.dex */
    public static class AlphaControlledSlashImageView extends SlashImageView {
        public AlphaControlledSlashImageView(Context context) {
            super(context);
        }

        public void setFinalImageTintList(ColorStateList tint) {
            super.setImageTintList(tint);
            SlashDrawable slash = getSlash();
            if (slash != null) {
                ((AlphaControlledSlashDrawable) slash).setFinalTintList(tint);
            }
        }

        @Override // com.android.systemui.qs.tileimpl.SlashImageView
        protected void ensureSlashDrawable() {
            if (getSlash() == null) {
                SlashDrawable slash = new AlphaControlledSlashDrawable(getDrawable());
                setSlash(slash);
                slash.setAnimationEnabled(getAnimationEnabled());
                setImageViewDrawable(slash);
            }
        }
    }

    /* loaded from: classes21.dex */
    public static class AlphaControlledSlashDrawable extends SlashDrawable {
        AlphaControlledSlashDrawable(Drawable d) {
            super(d);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.systemui.qs.SlashDrawable
        public void setDrawableTintList(ColorStateList tint) {
        }

        public void setFinalTintList(ColorStateList tint) {
            super.setDrawableTintList(tint);
        }
    }
}
