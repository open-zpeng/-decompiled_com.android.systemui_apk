package com.xiaopeng.systemui.qs.tilemodels;

import android.content.ContentResolver;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes24.dex */
public class AutoWiperSpeedTileModel extends ContentProviderTileModel {
    private static final String KEY_WIPER_GEAR_AUTO_EXIST = "wiper_gear_auto_exist_switch";

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface State {
        public static final int AUTO = 0;
        public static final int FAST = 3;
        public static final int INIT = -1;
        public static final int LOW = 1;
        public static final int MID = 2;
        public static final int VERY_FAST = 4;
    }

    public AutoWiperSpeedTileModel(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.ContentProviderTileModel, com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        int state = getCurrentState();
        int nextState = -1;
        if (state == -1) {
            nextState = 1;
        }
        if (state == 0) {
            nextState = 1;
        } else if (state == 1) {
            nextState = 2;
        } else if (state == 2) {
            nextState = 3;
        } else if (state == 3) {
            nextState = 4;
        } else if (state == 4) {
            nextState = 1 ^ hasWiperGearAuto();
        }
        QuickMenuBIHelper.sendBIData(this.mTileKey, nextState, this.mScreenId);
        saveContentProvider(state, nextState);
    }

    private boolean hasWiperGearAuto() {
        ContentResolver resolver = this.mContext.getContentResolver();
        String value = Settings.System.getString(resolver, KEY_WIPER_GEAR_AUTO_EXIST);
        Logger.d("hasWiperGearAuto field: wiper_gear_auto_exist_switch  value: " + value);
        if (!TextUtils.isEmpty(value)) {
            String[] array = value.split(NavigationBarInflaterView.KEY_IMAGE_DELIM);
            if (array.length > 1 && "1".equals(array[0])) {
                return true;
            }
        }
        return false;
    }
}
