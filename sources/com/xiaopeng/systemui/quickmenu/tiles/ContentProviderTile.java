package com.xiaopeng.systemui.quickmenu.tiles;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
/* loaded from: classes24.dex */
public abstract class ContentProviderTile extends XpTile {
    public static final String TAG = "ContentProviderTile";
    protected String APPLICATION_SUFFIX;
    protected String mBuriedBtnId;
    private ContentObserver mCallbackObserver;
    public Context mContext;
    protected String mCurrentStateValue;
    protected boolean mIsAlreadyRegister;

    public ContentProviderTile(String tileSpec) {
        super(tileSpec);
        this.mContext = ContextUtils.getContext();
        this.mCurrentStateValue = "";
        this.mIsAlreadyRegister = false;
        this.mBuriedBtnId = "";
        this.APPLICATION_SUFFIX = ":s";
        this.mCallbackObserver = new ContentObserver(new Handler()) { // from class: com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                String[] value;
                super.onChange(selfChange, uri);
                if (uri.equals(Settings.System.getUriFor(ContentProviderTile.this.mTileKey))) {
                    String dbValue = Settings.System.getString(ContentProviderTile.this.mContext.getContentResolver(), ContentProviderTile.this.mTileKey);
                    Log.d(ContentProviderTile.TAG, "xptile settingprovider onChange " + dbValue + " key:" + ContentProviderTile.this.mTileKey);
                    if (TextUtils.isEmpty(dbValue) || (value = dbValue.split(NavigationBarInflaterView.KEY_IMAGE_DELIM)) == null) {
                        return;
                    }
                    if (value.length == 1 || (value.length > 1 && value[0].equals(value[1]))) {
                        ContentProviderTile contentProviderTile = ContentProviderTile.this;
                        contentProviderTile.refreshState(contentProviderTile.getCurrentState());
                    }
                }
            }
        };
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void destroy() {
        Log.d(TAG, "xptiles contentprovider unregister " + this.mTileKey);
        if (this.mIsAlreadyRegister) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mCallbackObserver);
            this.mIsAlreadyRegister = false;
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void create() {
        Log.d(TAG, "xptiles contentprovider register " + this.mTileKey);
        if (!this.mIsAlreadyRegister) {
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(this.mTileKey), true, this.mCallbackObserver);
            this.mIsAlreadyRegister = true;
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        int state = getCurrentState();
        int nextState = -1;
        if (state == -1) {
            nextState = 1;
        }
        if (state == 1) {
            nextState = 2;
        } else if (state == 2) {
            nextState = 1;
        }
        QuickMenuBIHelper.sendBIData(this.mTileKey, nextState, this.mScreenId);
        saveContentProvider(state, nextState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void saveContentProvider(int state, int nextState) {
        String value = state + NavigationBarInflaterView.KEY_IMAGE_DELIM + nextState + this.APPLICATION_SUFFIX;
        Log.d(TAG, "xptile saveContentProvider  key:" + this.mTileKey + " value:" + value + " mCurrentStateValue:" + this.mCurrentStateValue);
        if (value.equals(this.mCurrentStateValue)) {
            notifyObserver();
        }
        if (getSettingValue(value).equals(getSettingValue(this.mCurrentStateValue))) {
            return;
        }
        Settings.System.putString(this.mContext.getContentResolver(), this.mTileKey, value);
        this.mCurrentStateValue = value;
    }

    private String getSettingValue(String value) {
        if (value != null && value.length() >= 3) {
            return value.substring(0, 3);
        }
        return "";
    }

    protected void notifyObserver() {
        if ("ihb_switch".equals(this.mTileKey)) {
            this.mContext.getContentResolver().notifyChange(Settings.System.getUriFor("ihb_switch"), null);
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public int getCurrentState() {
        this.mCurrentStateValue = Settings.System.getString(this.mContext.getContentResolver(), this.mTileKey);
        Log.d(TAG, "xptile contentprovider getvalue  key:" + this.mTileKey + " value:" + this.mCurrentStateValue);
        if (!TextUtils.isEmpty(this.mCurrentStateValue)) {
            String[] value = this.mCurrentStateValue.split(NavigationBarInflaterView.KEY_IMAGE_DELIM);
            return Integer.parseInt(value[0]);
        }
        return -1;
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    int convertState(int state) {
        return state;
    }
}
