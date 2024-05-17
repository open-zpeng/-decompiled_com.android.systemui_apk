package com.xiaopeng.xui.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
@Deprecated
/* loaded from: classes25.dex */
public class XFullscreenActivity extends AppCompatActivity {
    public static final int FEATURE_XUI_FULLSCREEN = 14;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enterFullscreen(this, 14);
    }

    private static void enterFullscreen(Activity activity, int feature) {
        if (feature > 0) {
            activity.requestWindowFeature(feature);
        }
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(5894);
    }

    private static void exitFullscreen(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(-1);
    }
}
