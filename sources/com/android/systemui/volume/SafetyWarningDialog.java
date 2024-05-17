package com.android.systemui.volume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.xiaopeng.systemui.controller.CarController;
/* loaded from: classes21.dex */
public abstract class SafetyWarningDialog extends SystemUIDialog implements DialogInterface.OnDismissListener, DialogInterface.OnClickListener {
    private static final int KEY_CONFIRM_ALLOWED_AFTER = 1000;
    private static final String TAG = Util.logTag(SafetyWarningDialog.class);
    private final AudioManager mAudioManager;
    private final Context mContext;
    private boolean mDisableOnVolumeUp;
    private boolean mNewVolumeUp;
    private final BroadcastReceiver mReceiver;
    private long mShowTime;

    protected abstract void cleanUp();

    public SafetyWarningDialog(Context context, AudioManager audioManager) {
        super(context);
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.systemui.volume.SafetyWarningDialog.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
                    if (D.BUG) {
                        Log.d(SafetyWarningDialog.TAG, "Received ACTION_CLOSE_SYSTEM_DIALOGS");
                    }
                    SafetyWarningDialog.this.cancel();
                    SafetyWarningDialog.this.cleanUp();
                }
            }
        };
        this.mContext = context;
        this.mAudioManager = audioManager;
        try {
            this.mDisableOnVolumeUp = this.mContext.getResources().getBoolean(17891507);
        } catch (Resources.NotFoundException e) {
            this.mDisableOnVolumeUp = true;
        }
        getWindow().setType(CarController.TYPE_CAR_NEDC_DRIVE_DISTANCE);
        setShowForAllUsers(true);
        setMessage(this.mContext.getString(17040973));
        setButton(-1, this.mContext.getString(17039379), this);
        setButton(-2, this.mContext.getString(17039369), (DialogInterface.OnClickListener) null);
        setOnDismissListener(this);
        IntentFilter filter = new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        context.registerReceiver(this.mReceiver, filter);
    }

    @Override // android.app.AlertDialog, android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.mDisableOnVolumeUp && keyCode == 24 && event.getRepeatCount() == 0) {
            this.mNewVolumeUp = true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.app.AlertDialog, android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 24 && this.mNewVolumeUp && System.currentTimeMillis() - this.mShowTime > 1000) {
            if (D.BUG) {
                Log.d(TAG, "Confirmed warning via VOLUME_UP");
            }
            this.mAudioManager.disableSafeMediaVolume();
            dismiss();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        this.mAudioManager.disableSafeMediaVolume();
    }

    @Override // android.app.Dialog
    protected void onStart() {
        super.onStart();
        this.mShowTime = System.currentTimeMillis();
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface unused) {
        this.mContext.unregisterReceiver(this.mReceiver);
        cleanUp();
    }
}
