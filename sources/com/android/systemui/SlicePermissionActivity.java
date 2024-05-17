package com.android.systemui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.slice.SliceManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.BidiFormatter;
import android.util.EventLog;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.slice.compat.SliceProviderCompat;
/* loaded from: classes21.dex */
public class SlicePermissionActivity extends Activity implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private static final String TAG = "SlicePermissionActivity";
    private CheckBox mAllCheckbox;
    private String mCallingPkg;
    private String mProviderPkg;
    private Uri mUri;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mUri = (Uri) getIntent().getParcelableExtra(SliceProviderCompat.EXTRA_BIND_URI);
        this.mCallingPkg = getIntent().getStringExtra(SliceProviderCompat.EXTRA_PKG);
        if (this.mUri == null) {
            Log.e(TAG, "slice_uri wasn't provided");
            finish();
            return;
        }
        try {
            PackageManager pm = getPackageManager();
            this.mProviderPkg = pm.resolveContentProvider(this.mUri.getAuthority(), 128).applicationInfo.packageName;
            verifyCallingPkg();
            CharSequence app1 = BidiFormatter.getInstance().unicodeWrap(pm.getApplicationInfo(this.mCallingPkg, 0).loadSafeLabel(pm, 500.0f, 5).toString());
            CharSequence app2 = BidiFormatter.getInstance().unicodeWrap(pm.getApplicationInfo(this.mProviderPkg, 0).loadSafeLabel(pm, 500.0f, 5).toString());
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.slice_permission_title, new Object[]{app1, app2})).setView(R.layout.slice_permission_request).setNegativeButton(R.string.slice_permission_deny, this).setPositiveButton(R.string.slice_permission_allow, this).setOnDismissListener(this).create();
            dialog.getWindow().addSystemFlags(524288);
            dialog.show();
            TextView t1 = (TextView) dialog.getWindow().getDecorView().findViewById(R.id.text1);
            t1.setText(getString(R.string.slice_permission_text_1, new Object[]{app2}));
            TextView t2 = (TextView) dialog.getWindow().getDecorView().findViewById(R.id.text2);
            t2.setText(getString(R.string.slice_permission_text_2, new Object[]{app2}));
            this.mAllCheckbox = (CheckBox) dialog.getWindow().getDecorView().findViewById(R.id.slice_permission_checkbox);
            this.mAllCheckbox.setText(getString(R.string.slice_permission_checkbox, new Object[]{app1}));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Couldn't find package", e);
            finish();
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        if (which == -1) {
            ((SliceManager) getSystemService(SliceManager.class)).grantPermissionFromUser(this.mUri, this.mCallingPkg, this.mAllCheckbox.isChecked());
        }
        finish();
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        finish();
    }

    private void verifyCallingPkg() {
        String providerPkg = getIntent().getStringExtra(SliceProviderCompat.EXTRA_PROVIDER_PKG);
        if (providerPkg == null || this.mProviderPkg.equals(providerPkg)) {
            return;
        }
        String callingPkg = getCallingPkg();
        EventLog.writeEvent(1397638484, "159145361", Integer.valueOf(getUid(callingPkg)));
    }

    private String getCallingPkg() {
        Uri referrer = getReferrer();
        if (referrer == null) {
            return null;
        }
        return referrer.getHost();
    }

    private int getUid(String pkg) {
        if (pkg == null) {
            return -1;
        }
        try {
            return getPackageManager().getApplicationInfo(pkg, 0).uid;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }
}
