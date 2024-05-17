package com.xiaopeng.systemui.secondarywindow;

import android.content.Context;
import com.xiaopeng.systemui.navigationbar.NavigationBar;
/* loaded from: classes24.dex */
public class SecondaryNavigationBar extends NavigationBar {
    public SecondaryNavigationBar(Context context) {
        super(context);
    }

    @Override // com.xiaopeng.systemui.navigationbar.NavigationBar
    protected void createNavigation() {
        this.mNavigation = new SecondaryVerticalNavigationImpl(this.mContext);
    }
}
