package com.android.systemui.chooser;

import android.app.Activity;
import android.os.Bundle;
/* loaded from: classes21.dex */
public final class ChooserActivity extends Activity {
    private static final String TAG = "ChooserActivity";

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChooserHelper.onChoose(this);
        finish();
    }
}
