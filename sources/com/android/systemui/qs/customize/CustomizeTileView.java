package com.android.systemui.qs.customize;

import android.content.Context;
import android.widget.TextView;
import com.android.systemui.plugins.qs.QSIconView;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileView;
/* loaded from: classes21.dex */
public class CustomizeTileView extends QSTileView {
    private boolean mShowAppLabel;

    public CustomizeTileView(Context context, QSIconView icon) {
        super(context, icon);
    }

    public void setShowAppLabel(boolean showAppLabel) {
        this.mShowAppLabel = showAppLabel;
        this.mSecondLine.setVisibility(showAppLabel ? 0 : 8);
        this.mLabel.setSingleLine(showAppLabel);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileView, com.android.systemui.qs.tileimpl.QSTileBaseView
    public void handleStateChanged(QSTile.State state) {
        super.handleStateChanged(state);
        this.mSecondLine.setVisibility(this.mShowAppLabel ? 0 : 8);
    }

    public TextView getAppLabel() {
        return this.mSecondLine;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileBaseView
    protected boolean animationsEnabled() {
        return false;
    }
}
