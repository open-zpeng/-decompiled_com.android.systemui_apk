package com.xiaopeng.systemui.quickmenu.tiles;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes24.dex */
public class BackBoxTile extends ContentProviderTile {
    private static final String KEY = "open_close_back_box";
    private ContentObserver mCallbackObserver;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface State {
        public static final int CLOSED = 1;
        public static final int CLOSING = 4;
        public static final int CLOSING_PAUSE = 6;
        public static final int INIT = -1;
        public static final int OPENED = 2;
        public static final int OPENING = 3;
        public static final int OPENING_PAUSE = 5;
        public static final int PREPARE = 7;
    }

    public BackBoxTile(String tileSpec) {
        super(tileSpec);
        this.mCallbackObserver = new ContentObserver(new Handler()) { // from class: com.xiaopeng.systemui.quickmenu.tiles.BackBoxTile.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                String[] value;
                super.onChange(selfChange, uri);
                if (uri.equals(Settings.System.getUriFor("open_close_back_box"))) {
                    String dbValue = Settings.System.getString(BackBoxTile.this.mContext.getContentResolver(), "open_close_back_box");
                    Log.d("quickmenu", "xptile settingprovider onChange " + dbValue + " key:" + BackBoxTile.this.mTileKey);
                    if (!TextUtils.isEmpty(dbValue) && (value = dbValue.split(NavigationBarInflaterView.KEY_IMAGE_DELIM)) != null && value.length > 1 && value[0].equals(value[1])) {
                        BackBoxTile backBoxTile = BackBoxTile.this;
                        backBoxTile.refreshState(backBoxTile.getCurrentState());
                    }
                }
            }
        };
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void destroy() {
        Log.d("quickmenu", "xptiles contentprovider unregister " + this.mTileKey);
        if (this.mIsAlreadyRegister) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mCallbackObserver);
            this.mIsAlreadyRegister = false;
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void create() {
        Log.d("quickmenu", "xptiles contentprovider register " + this.mTileKey);
        if (!this.mIsAlreadyRegister) {
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("open_close_back_box"), true, this.mCallbackObserver);
            this.mIsAlreadyRegister = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile
    public void saveContentProvider(int state, int nextState) {
        String value = state + NavigationBarInflaterView.KEY_IMAGE_DELIM + nextState + this.APPLICATION_SUFFIX;
        Log.d("quickmenu", "xptile saveContentProvider  key:" + this.mTileKey + " value:" + value + " mCurrentStateValue:" + this.mCurrentStateValue);
        Settings.System.putString(this.mContext.getContentResolver(), "open_close_back_box", value);
        this.mCurrentStateValue = value;
        Log.d("quickmenu", "xptile contentprovider setvalue  key:" + this.mTileKey + " value:" + value);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public int getCurrentState() {
        this.mCurrentStateValue = Settings.System.getString(this.mContext.getContentResolver(), "open_close_back_box");
        Log.d("quickmenu", "xptile contentprovider getvalue  key:" + this.mTileKey + " value:" + this.mCurrentStateValue);
        if (!TextUtils.isEmpty(this.mCurrentStateValue)) {
            String[] value = this.mCurrentStateValue.split(NavigationBarInflaterView.KEY_IMAGE_DELIM);
            return Integer.parseInt(value[0]);
        }
        return -1;
    }
}
