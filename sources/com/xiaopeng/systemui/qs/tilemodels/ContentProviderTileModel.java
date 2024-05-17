package com.xiaopeng.systemui.qs.tilemodels;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.systemui.qs.QuickMenuBIHelper;
import java.util.Objects;
/* loaded from: classes24.dex */
public class ContentProviderTileModel extends XpTileModel {
    public static final String TAG = ContentProviderTileModel.class.getSimpleName();
    protected String APPLICATION_SUFFIX;
    private final ContentObserver mCallbackObserver;
    protected String mCurrentOriginData;

    public ContentProviderTileModel(String tileSpec) {
        super(tileSpec);
        this.APPLICATION_SUFFIX = "s";
        this.mCurrentOriginData = "";
        this.mCallbackObserver = new ContentObserver(new Handler()) { // from class: com.xiaopeng.systemui.qs.tilemodels.ContentProviderTileModel.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                if (uri.equals(Settings.System.getUriFor(ContentProviderTileModel.this.mTileKey))) {
                    String dbValue = Settings.System.getString(ContentProviderTileModel.this.mContext.getContentResolver(), ContentProviderTileModel.this.mTileKey);
                    String str = ContentProviderTileModel.TAG;
                    Log.d(str, "xptile settingprovider onChange " + dbValue + " key:" + ContentProviderTileModel.this.mTileKey);
                    ContentProviderTileModel.this.mCurrentOriginData = dbValue;
                    if (!TextUtils.isEmpty(dbValue)) {
                        String[] value = dbValue.split(NavigationBarInflaterView.KEY_IMAGE_DELIM);
                        if (value.length == 1 || (value.length > 1 && value[0].equals(value[1]))) {
                            ContentProviderTileModel.this.mCurrentState = Integer.parseInt(value[0]);
                            MutableLiveData<Integer> mutableLiveData = ContentProviderTileModel.this.mCurrentLivedata;
                            ContentProviderTileModel contentProviderTileModel = ContentProviderTileModel.this;
                            mutableLiveData.setValue(Integer.valueOf(contentProviderTileModel.convertState(contentProviderTileModel.mCurrentState)));
                        }
                    }
                }
            }
        };
        this.mCurrentOriginData = Settings.System.getString(this.mContext.getContentResolver(), this.mTileKey);
        String str = TAG;
        Log.d(str, "init-tile key: " + this.mTileKey + "; mCurrentOriginData: " + this.mCurrentOriginData);
        String str2 = this.mCurrentOriginData;
        if (str2 != null && !Objects.equals(str2, "")) {
            this.mCurrentState = Integer.parseInt(this.mCurrentOriginData.split(NavigationBarInflaterView.KEY_IMAGE_DELIM)[0]);
        }
        this.mCurrentLivedata.setValue(Integer.valueOf(convertState(this.mCurrentState)));
        registerXpTile();
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        int state = this.mCurrentState;
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
        String value = String.format("%d:%d:%s", Integer.valueOf(state), Integer.valueOf(nextState), this.APPLICATION_SUFFIX);
        String str = TAG;
        Log.d(str, "xptile saveContentProvider  key:" + this.mTileKey + " value:" + value + " mCurrentOriginData:" + this.mCurrentOriginData);
        if (value.equals(this.mCurrentOriginData)) {
            notifyObserver();
        }
        Settings.System.putString(this.mContext.getContentResolver(), this.mTileKey, value);
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

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public int getCurrentState() {
        this.mCurrentOriginData = Settings.System.getString(this.mContext.getContentResolver(), this.mTileKey);
        String str = TAG;
        Log.d(str, "xptile contentprovider getvalue  key:" + this.mTileKey + " value:" + this.mCurrentOriginData);
        if (!TextUtils.isEmpty(this.mCurrentOriginData)) {
            String[] value = this.mCurrentOriginData.split(NavigationBarInflaterView.KEY_IMAGE_DELIM);
            if (this.mCurrentState != Integer.parseInt(value[0])) {
                this.mCurrentState = Integer.parseInt(value[0]);
                this.mCurrentLivedata.setValue(Integer.valueOf(convertState(this.mCurrentState)));
            }
            return this.mCurrentState;
        }
        return -1;
    }

    private void registerXpTile() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(this.mTileKey), true, this.mCallbackObserver);
    }
}
