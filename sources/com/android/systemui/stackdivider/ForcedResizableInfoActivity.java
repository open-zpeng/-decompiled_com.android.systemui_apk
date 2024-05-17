package com.android.systemui.stackdivider;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class ForcedResizableInfoActivity extends Activity implements View.OnTouchListener {
    private static final long DISMISS_DELAY = 2500;
    public static final String EXTRA_FORCED_RESIZEABLE_REASON = "extra_forced_resizeable_reason";
    private final Runnable mFinishRunnable = new Runnable() { // from class: com.android.systemui.stackdivider.ForcedResizableInfoActivity.1
        @Override // java.lang.Runnable
        public void run() {
            ForcedResizableInfoActivity.this.finish();
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        String text;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forced_resizable_activity);
        TextView tv = (TextView) findViewById(16908299);
        int reason = getIntent().getIntExtra(EXTRA_FORCED_RESIZEABLE_REASON, -1);
        if (reason == 1) {
            text = getString(R.string.dock_forced_resizable);
        } else if (reason == 2) {
            text = getString(R.string.forced_resizable_secondary_display);
        } else {
            throw new IllegalArgumentException("Unexpected forced resizeable reason: " + reason);
        }
        tv.setText(text);
        getWindow().setTitle(text);
        getWindow().getDecorView().setOnTouchListener(this);
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        getWindow().getDecorView().postDelayed(this.mFinishRunnable, 2500L);
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        finish();
        return true;
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return true;
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.forced_resizable_exit);
    }

    @Override // android.app.Activity
    public void setTaskDescription(ActivityManager.TaskDescription taskDescription) {
    }
}
