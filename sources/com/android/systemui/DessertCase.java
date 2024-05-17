package com.android.systemui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.util.Slog;
import com.android.systemui.DessertCaseView;
/* loaded from: classes21.dex */
public class DessertCase extends Activity {
    DessertCaseView mView;

    @Override // android.app.Activity
    public void onStart() {
        super.onStart();
        PackageManager pm = getPackageManager();
        ComponentName cn = new ComponentName(this, DessertCaseDream.class);
        if (pm.getComponentEnabledSetting(cn) != 1) {
            Slog.v("DessertCase", "ACHIEVEMENT UNLOCKED");
            pm.setComponentEnabledSetting(cn, 1, 1);
        }
        this.mView = new DessertCaseView(this);
        DessertCaseView.RescalingContainer container = new DessertCaseView.RescalingContainer(this);
        container.setView(this.mView);
        setContentView(container);
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        this.mView.postDelayed(new Runnable() { // from class: com.android.systemui.DessertCase.1
            @Override // java.lang.Runnable
            public void run() {
                DessertCase.this.mView.start();
            }
        }, 1000L);
    }

    @Override // android.app.Activity
    public void onPause() {
        super.onPause();
        this.mView.stop();
    }
}
