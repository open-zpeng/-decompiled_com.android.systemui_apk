package com.android.systemui.statusbar.policy;

import android.app.AlarmManager;
import com.android.systemui.Dumpable;
/* loaded from: classes21.dex */
public interface NextAlarmController extends CallbackController<NextAlarmChangeCallback>, Dumpable {

    /* loaded from: classes21.dex */
    public interface NextAlarmChangeCallback {
        void onNextAlarmChanged(AlarmManager.AlarmClockInfo alarmClockInfo);
    }
}
