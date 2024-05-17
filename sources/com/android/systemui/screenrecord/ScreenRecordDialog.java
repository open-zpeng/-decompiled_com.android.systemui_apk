package com.android.systemui.screenrecord;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class ScreenRecordDialog extends Activity {
    private static final int REQUEST_CODE_PERMISSIONS = 299;
    private static final int REQUEST_CODE_PERMISSIONS_AUDIO = 399;
    private static final int REQUEST_CODE_VIDEO_AUDIO = 300;
    private static final int REQUEST_CODE_VIDEO_AUDIO_TAPS = 301;
    private static final int REQUEST_CODE_VIDEO_ONLY = 200;
    private static final int REQUEST_CODE_VIDEO_TAPS = 201;
    private static final String TAG = "ScreenRecord";
    private boolean mShowTaps;
    private boolean mUseAudio;

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_record_dialog);
        final CheckBox micCheckBox = (CheckBox) findViewById(R.id.checkbox_mic);
        final CheckBox tapsCheckBox = (CheckBox) findViewById(R.id.checkbox_taps);
        Button recordButton = (Button) findViewById(R.id.record_button);
        recordButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.screenrecord.-$$Lambda$ScreenRecordDialog$H-9qHbhSc2WYgQqs87Jfr3hmOoA
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ScreenRecordDialog.this.lambda$onCreate$0$ScreenRecordDialog(micCheckBox, tapsCheckBox, view);
            }
        });
    }

    public /* synthetic */ void lambda$onCreate$0$ScreenRecordDialog(CheckBox micCheckBox, CheckBox tapsCheckBox, View v) {
        this.mUseAudio = micCheckBox.isChecked();
        this.mShowTaps = tapsCheckBox.isChecked();
        Log.d(TAG, "Record button clicked: audio " + this.mUseAudio + ", taps " + this.mShowTaps);
        if (this.mUseAudio && checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
            Log.d(TAG, "Requesting permission for audio");
            requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, REQUEST_CODE_PERMISSIONS_AUDIO);
            return;
        }
        requestScreenCapture();
    }

    private void requestScreenCapture() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService("media_projection");
        Intent permissionIntent = mediaProjectionManager.createScreenCaptureIntent();
        if (this.mUseAudio) {
            startActivityForResult(permissionIntent, this.mShowTaps ? 301 : 300);
        } else {
            startActivityForResult(permissionIntent, this.mShowTaps ? 201 : 200);
        }
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean z = true;
        this.mShowTaps = requestCode == 201 || requestCode == 301;
        if (requestCode != 200 && requestCode != 201) {
            if (requestCode != REQUEST_CODE_PERMISSIONS_AUDIO) {
                switch (requestCode) {
                    case REQUEST_CODE_PERMISSIONS /* 299 */:
                        int permission = checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
                        if (permission != 0) {
                            Toast.makeText(this, getResources().getString(R.string.screenrecord_permission_error), 0).show();
                            finish();
                            return;
                        }
                        requestScreenCapture();
                        return;
                    case 300:
                    case 301:
                        break;
                    default:
                        return;
                }
            } else {
                int videoPermission = checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
                int audioPermission = checkSelfPermission("android.permission.RECORD_AUDIO");
                if (videoPermission != 0 || audioPermission != 0) {
                    Toast.makeText(this, getResources().getString(R.string.screenrecord_permission_error), 0).show();
                    finish();
                    return;
                }
                requestScreenCapture();
                return;
            }
        }
        if (resultCode == -1) {
            if (requestCode != 300 && requestCode != 301) {
                z = false;
            }
            this.mUseAudio = z;
            startForegroundService(RecordingService.getStartIntent(this, resultCode, data, this.mUseAudio, this.mShowTaps));
        } else {
            Toast.makeText(this, getResources().getString(R.string.screenrecord_permission_error), 0).show();
        }
        finish();
    }
}
