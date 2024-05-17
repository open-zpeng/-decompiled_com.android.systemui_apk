package com.android.systemui.statusbar.policy;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import com.android.systemui.statusbar.policy.NextAlarmController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NextAlarmControllerImpl extends BroadcastReceiver implements NextAlarmController {
    private AlarmManager mAlarmManager;
    private final ArrayList<NextAlarmController.NextAlarmChangeCallback> mChangeCallbacks = new ArrayList<>();
    private AlarmManager.AlarmClockInfo mNextAlarm;

    @Inject
    public NextAlarmControllerImpl(Context context) {
        this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.USER_SWITCHED");
        filter.addAction("android.app.action.NEXT_ALARM_CLOCK_CHANGED");
        context.registerReceiverAsUser(this, UserHandle.ALL, filter, null, null);
        updateNextAlarm();
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("NextAlarmController state:");
        pw.print("  mNextAlarm=");
        pw.println(this.mNextAlarm);
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(NextAlarmController.NextAlarmChangeCallback cb) {
        this.mChangeCallbacks.add(cb);
        cb.onNextAlarmChanged(this.mNextAlarm);
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(NextAlarmController.NextAlarmChangeCallback cb) {
        this.mChangeCallbacks.remove(cb);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.intent.action.USER_SWITCHED") || action.equals("android.app.action.NEXT_ALARM_CLOCK_CHANGED")) {
            updateNextAlarm();
        }
    }

    private void updateNextAlarm() {
        this.mNextAlarm = this.mAlarmManager.getNextAlarmClock(-2);
        fireNextAlarmChanged();
    }

    private void fireNextAlarmChanged() {
        int n = this.mChangeCallbacks.size();
        for (int i = 0; i < n; i++) {
            this.mChangeCallbacks.get(i).onNextAlarmChanged(this.mNextAlarm);
        }
    }
}
