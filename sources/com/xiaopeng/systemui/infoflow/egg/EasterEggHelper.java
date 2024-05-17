package com.xiaopeng.systemui.infoflow.egg;

import android.net.Uri;
import android.os.RemoteException;
import com.xiaopeng.lib.apirouter.ApiRouter;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.lib.utils.ThreadUtils;
/* loaded from: classes24.dex */
public class EasterEggHelper {
    private static final String TAG = "EasterEggHelper";

    public static boolean hasEasterEggShow() {
        return hasEasterEggShow(false);
    }

    public static boolean hasEasterEggShow(boolean isGetOffScene) {
        long currentTime = System.currentTimeMillis();
        boolean hasEgg = HolidayEventManager.hasHolidayEvent(isGetOffScene);
        LogUtils.e(TAG, "hasEasterEggShow : " + hasEgg + ", cost time : " + (System.currentTimeMillis() - currentTime) + "ms");
        if (isGetOffScene) {
            notifyHasEgg(hasEgg);
        }
        return hasEgg;
    }

    private static void notifyHasEgg(final boolean hasHoliday) {
        LogUtils.i(TAG, "notifyShowGetOffEggStatus");
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.egg.EasterEggHelper.1
            @Override // java.lang.Runnable
            public void run() {
                Uri.Builder builder = new Uri.Builder();
                builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("hasGetOffHoliday").appendQueryParameter("hasHoliday", String.valueOf(hasHoliday));
                try {
                    ApiRouter.route(builder.build());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
