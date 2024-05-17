package com.android.systemui.pip.tv;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ParceledListSlice;
import android.os.Bundle;
import com.android.systemui.R;
import com.android.systemui.pip.tv.PipManager;
import java.util.Collections;
/* loaded from: classes21.dex */
public class PipMenuActivity extends Activity implements PipManager.Listener {
    static final String EXTRA_CUSTOM_ACTIONS = "custom_actions";
    private static final String TAG = "PipMenuActivity";
    private Animator mFadeInAnimation;
    private Animator mFadeOutAnimation;
    private PipControlsView mPipControlsView;
    private final PipManager mPipManager = PipManager.getInstance();
    private boolean mRestorePipSizeWhenClose;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!this.mPipManager.isPipShown()) {
            finish();
        }
        setContentView(R.layout.tv_pip_menu);
        this.mPipManager.addListener(this);
        this.mRestorePipSizeWhenClose = true;
        this.mPipControlsView = (PipControlsView) findViewById(R.id.pip_controls);
        this.mFadeInAnimation = AnimatorInflater.loadAnimator(this, R.anim.tv_pip_menu_fade_in_animation);
        this.mFadeInAnimation.setTarget(this.mPipControlsView);
        this.mFadeOutAnimation = AnimatorInflater.loadAnimator(this, R.anim.tv_pip_menu_fade_out_animation);
        this.mFadeOutAnimation.setTarget(this.mPipControlsView);
        onPipMenuActionsChanged((ParceledListSlice) getIntent().getParcelableExtra(EXTRA_CUSTOM_ACTIONS));
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onPipMenuActionsChanged((ParceledListSlice) getIntent().getParcelableExtra(EXTRA_CUSTOM_ACTIONS));
    }

    private void restorePipAndFinish() {
        if (this.mRestorePipSizeWhenClose) {
            this.mPipManager.resizePinnedStack(1);
        }
        finish();
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        this.mFadeInAnimation.start();
    }

    @Override // android.app.Activity
    public void onPause() {
        super.onPause();
        this.mFadeOutAnimation.start();
        restorePipAndFinish();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.mPipManager.removeListener(this);
        this.mPipManager.resumePipResizing(1);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        restorePipAndFinish();
    }

    @Override // com.android.systemui.pip.tv.PipManager.Listener
    public void onPipEntered() {
    }

    @Override // com.android.systemui.pip.tv.PipManager.Listener
    public void onPipActivityClosed() {
        finish();
    }

    @Override // com.android.systemui.pip.tv.PipManager.Listener
    public void onPipMenuActionsChanged(ParceledListSlice actions) {
        boolean hasCustomActions = (actions == null || actions.getList().isEmpty()) ? false : true;
        this.mPipControlsView.setActions(hasCustomActions ? actions.getList() : Collections.EMPTY_LIST);
    }

    @Override // com.android.systemui.pip.tv.PipManager.Listener
    public void onShowPipMenu() {
    }

    @Override // com.android.systemui.pip.tv.PipManager.Listener
    public void onMoveToFullscreen() {
        this.mRestorePipSizeWhenClose = false;
        finish();
    }

    @Override // com.android.systemui.pip.tv.PipManager.Listener
    public void onPipResizeAboutToStart() {
        finish();
        this.mPipManager.suspendPipResizing(1);
    }
}
