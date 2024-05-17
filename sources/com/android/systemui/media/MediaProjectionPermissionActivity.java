package com.android.systemui.media;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.projection.IMediaProjection;
import android.media.projection.IMediaProjectionManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.BidiFormatter;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.android.systemui.R;
import com.android.systemui.util.Utils;
/* loaded from: classes21.dex */
public class MediaProjectionPermissionActivity extends Activity implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
    private static final String ELLIPSIS = "…";
    private static final float MAX_APP_NAME_SIZE_PX = 500.0f;
    private static final String TAG = "MediaProjectionPermissionActivity";
    private AlertDialog mDialog;
    private String mPackageName;
    private IMediaProjectionManager mService;
    private int mUid;

    @Override // android.app.Activity
    public void onCreate(Bundle icicle) {
        CharSequence dialogText;
        super.onCreate(icicle);
        this.mPackageName = getCallingPackage();
        IBinder b = ServiceManager.getService("media_projection");
        this.mService = IMediaProjectionManager.Stub.asInterface(b);
        if (this.mPackageName == null) {
            finish();
            return;
        }
        PackageManager packageManager = getPackageManager();
        try {
            ApplicationInfo aInfo = packageManager.getApplicationInfo(this.mPackageName, 0);
            this.mUid = aInfo.uid;
            try {
                if (this.mService.hasProjectionPermission(this.mUid, this.mPackageName)) {
                    setResult(-1, getMediaProjectionIntent(this.mUid, this.mPackageName));
                    finish();
                    return;
                }
                TextPaint paint = new TextPaint();
                paint.setTextSize(42.0f);
                if (Utils.isHeadlessRemoteDisplayProvider(packageManager, this.mPackageName)) {
                    dialogText = getString(R.string.media_projection_dialog_service_text);
                } else {
                    String label = aInfo.loadLabel(packageManager).toString();
                    int labelLength = label.length();
                    int offset = 0;
                    while (offset < labelLength) {
                        int codePoint = label.codePointAt(offset);
                        int type = Character.getType(codePoint);
                        if (type == 13 || type == 15 || type == 14) {
                            label = label.substring(0, offset) + ELLIPSIS;
                            break;
                        }
                        offset += Character.charCount(codePoint);
                    }
                    if (label.isEmpty()) {
                        label = this.mPackageName;
                    }
                    String unsanitizedAppName = TextUtils.ellipsize(label, paint, MAX_APP_NAME_SIZE_PX, TextUtils.TruncateAt.END).toString();
                    String appName = BidiFormatter.getInstance().unicodeWrap(unsanitizedAppName);
                    String actionText = getString(R.string.media_projection_dialog_text, new Object[]{appName});
                    SpannableString message = new SpannableString(actionText);
                    int appNameIndex = actionText.indexOf(appName);
                    if (appNameIndex >= 0) {
                        message.setSpan(new StyleSpan(1), appNameIndex, appNameIndex + appName.length(), 0);
                    }
                    dialogText = message;
                }
                String dialogTitle = getString(R.string.media_projection_dialog_title);
                View dialogTitleView = View.inflate(this, R.layout.media_projection_dialog_title, null);
                TextView titleText = (TextView) dialogTitleView.findViewById(R.id.dialog_title);
                titleText.setText(dialogTitle);
                this.mDialog = new AlertDialog.Builder(this).setCustomTitle(dialogTitleView).setMessage(dialogText).setPositiveButton(R.string.media_projection_action_text, this).setNegativeButton(17039360, this).setOnCancelListener(this).create();
                this.mDialog.create();
                this.mDialog.getButton(-1).setFilterTouchesWhenObscured(true);
                Window w = this.mDialog.getWindow();
                w.setType(2003);
                w.addSystemFlags(524288);
                this.mDialog.show();
            } catch (RemoteException e) {
                Log.e(TAG, "Error checking projection permissions", e);
                finish();
            }
        } catch (PackageManager.NameNotFoundException e2) {
            Log.e(TAG, "unable to look up package name", e2);
            finish();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x001f, code lost:
        if (r0 == null) goto L6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0038, code lost:
        return;
     */
    @Override // android.content.DialogInterface.OnClickListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onClick(android.content.DialogInterface r4, int r5) {
        /*
            r3 = this;
            r0 = -1
            if (r5 != r0) goto L2d
            int r1 = r3.mUid     // Catch: java.lang.Throwable -> Lf android.os.RemoteException -> L11
            java.lang.String r2 = r3.mPackageName     // Catch: java.lang.Throwable -> Lf android.os.RemoteException -> L11
            android.content.Intent r1 = r3.getMediaProjectionIntent(r1, r2)     // Catch: java.lang.Throwable -> Lf android.os.RemoteException -> L11
            r3.setResult(r0, r1)     // Catch: java.lang.Throwable -> Lf android.os.RemoteException -> L11
            goto L2d
        Lf:
            r0 = move-exception
            goto L22
        L11:
            r0 = move-exception
            java.lang.String r1 = "MediaProjectionPermissionActivity"
            java.lang.String r2 = "Error granting projection permission"
            android.util.Log.e(r1, r2, r0)     // Catch: java.lang.Throwable -> Lf
            r1 = 0
            r3.setResult(r1)     // Catch: java.lang.Throwable -> Lf
            android.app.AlertDialog r0 = r3.mDialog
            if (r0 == 0) goto L34
            goto L31
        L22:
            android.app.AlertDialog r1 = r3.mDialog
            if (r1 == 0) goto L29
            r1.dismiss()
        L29:
            r3.finish()
            throw r0
        L2d:
            android.app.AlertDialog r0 = r3.mDialog
            if (r0 == 0) goto L34
        L31:
            r0.dismiss()
        L34:
            r3.finish()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.media.MediaProjectionPermissionActivity.onClick(android.content.DialogInterface, int):void");
    }

    private Intent getMediaProjectionIntent(int uid, String packageName) throws RemoteException {
        IMediaProjection projection = this.mService.createProjection(uid, packageName, 0, false);
        Intent intent = new Intent();
        intent.putExtra("android.media.projection.extra.EXTRA_MEDIA_PROJECTION", projection.asBinder());
        return intent;
    }

    @Override // android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialog) {
        finish();
    }
}
