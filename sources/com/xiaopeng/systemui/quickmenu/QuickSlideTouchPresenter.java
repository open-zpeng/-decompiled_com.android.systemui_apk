package com.xiaopeng.systemui.quickmenu;

import android.content.ContentResolver;
import android.provider.Settings;
import com.alibaba.fastjson.JSON;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes24.dex */
public class QuickSlideTouchPresenter {
    public static final String KEY_SLIDE_TOUCH_EVENT = "key_slide_touch_event";
    private static final String TAG = "QuickSlideTouchPresenter";
    private boolean primaryScreen;
    private boolean secondScreen;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final QuickSlideTouchPresenter sInstance = new QuickSlideTouchPresenter();

        private SingleHolder() {
        }
    }

    public static QuickSlideTouchPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    private QuickSlideTouchPresenter() {
    }

    public void attachQuickMenu(int screenId) {
        if (screenId == 0) {
            this.primaryScreen = true;
        } else if (screenId == 1) {
            this.secondScreen = true;
        }
        handleTouch();
    }

    public void dismissQuickMenu(int screenId) {
        if (screenId == 0) {
            this.primaryScreen = false;
        } else if (screenId == 1) {
            this.secondScreen = false;
        }
        handleTouch();
    }

    private void handleTouch() {
        int enable;
        Logger.i(TAG, "handleTouch " + this.primaryScreen + " ," + this.secondScreen);
        if (this.primaryScreen || this.secondScreen) {
            enable = 0;
        } else {
            enable = 1;
        }
        int screenIndex = 0;
        boolean z = this.primaryScreen;
        boolean z2 = this.secondScreen;
        if (z & z2) {
            screenIndex = -1;
        } else if (z) {
            screenIndex = 0;
        } else if (z2) {
            screenIndex = 1;
        }
        ContentResolver resolver = ContextUtils.getContext().getContentResolver();
        Map<String, Integer> map = new HashMap<>();
        map.put("sharedId", Integer.valueOf(screenIndex));
        map.put("enable", Integer.valueOf(enable));
        String value = JSON.toJSONString(map);
        Settings.Secure.putString(resolver, KEY_SLIDE_TOUCH_EVENT, value);
    }
}
