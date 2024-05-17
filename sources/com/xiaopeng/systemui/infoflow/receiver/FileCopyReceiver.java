package com.xiaopeng.systemui.infoflow.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.utils.FileUtil;
import java.io.File;
/* loaded from: classes24.dex */
public class FileCopyReceiver extends BroadcastReceiver {
    private static final String ACTION_COPY_FILE = "com.xiaopeng.systemui.copyFile";
    private static final String ACTION_COPY_FILE_SUCCESS = "com.xiaopeng.systemui.copyFile.SUCCESS";
    private static final String TAG = "FileCopyReceiver";

    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_COPY_FILE);
        context.registerReceiver(this, filter);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        Logger.d(TAG, "onReceive action -- " + action);
        if (action.equals(ACTION_COPY_FILE)) {
            final String id = intent.getStringExtra("id");
            final int type = intent.getIntExtra(VuiConstants.ELEMENT_TYPE, 0);
            final String rsc_name = intent.getStringExtra("rsc_name");
            final String rsc_path = intent.getStringExtra("rsc_path");
            ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.receiver.FileCopyReceiver.1
                @Override // java.lang.Runnable
                public void run() {
                    Log.i(FileCopyReceiver.TAG, "begin copy id:" + id + " &type:" + type + " &rsc_name:" + rsc_name + " &rsc_path:" + rsc_path);
                    File file = new File(rsc_path);
                    StringBuilder sb = new StringBuilder();
                    sb.append("/data/xuiservice/rsc/llu/");
                    sb.append(file.getName());
                    File destfile = new File(sb.toString());
                    FileUtil.copyfile(file, destfile);
                    FileCopyReceiver.this.sendCopySuccess(context, id, type, file.getName(), destfile.getPath());
                    Log.i(FileCopyReceiver.TAG, "end copy");
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendCopySuccess(Context context, String id, int type, String rsc_name, String rsc_path) {
        Intent intent = new Intent(ACTION_COPY_FILE_SUCCESS);
        intent.putExtra("id", id);
        intent.putExtra(VuiConstants.ELEMENT_TYPE, type);
        intent.putExtra("rsc_name", rsc_name);
        intent.putExtra("rsc_path", rsc_path);
        context.sendBroadcast(intent);
    }
}
