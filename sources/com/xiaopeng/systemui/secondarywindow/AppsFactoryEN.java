package com.xiaopeng.systemui.secondarywindow;

import android.content.Context;
import com.android.systemui.R;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class AppsFactoryEN extends AbstractAppsFactory {
    private Context mContext = ContextUtils.getContext();

    @Override // com.xiaopeng.systemui.secondarywindow.AbstractAppsFactory
    public List<BaseAppInfo> createLargeApps() {
        List<BaseAppInfo> largeApps = new ArrayList<>();
        CarMediaAppInfo appInfo = new CarMediaAppInfo();
        appInfo.setTabIndex(0);
        appInfo.setIconId(R.drawable.spotify);
        appInfo.setSmallIconId(R.drawable.spotify_logo);
        appInfo.setPkgName(this.mContext.getString(R.string.component_spotify));
        appInfo.setTitle("Spotify");
        appInfo.setSubtitle("");
        appInfo.setDesc("");
        largeApps.add(appInfo);
        CarMediaAppInfo appInfo2 = new CarMediaAppInfo();
        appInfo2.setTabIndex(3);
        appInfo2.setIconId(R.drawable.amazon_music);
        appInfo2.setSmallIconId(R.drawable.amazonmusic_logo);
        appInfo2.setPkgName(this.mContext.getString(R.string.component_amazon_music));
        appInfo2.setTitle("Amazon\nmusic");
        appInfo2.setSubtitle("");
        appInfo2.setDesc("");
        largeApps.add(appInfo2);
        CarMediaAppInfo appInfo3 = new CarMediaAppInfo();
        appInfo3.setTabIndex(4);
        appInfo3.setIconId(R.drawable.tidal);
        appInfo3.setSmallIconId(R.drawable.tidal_logo);
        appInfo3.setPkgName(this.mContext.getString(R.string.component_tidal));
        appInfo3.setTitle("Tidal");
        appInfo3.setSubtitle("");
        appInfo3.setDesc("");
        largeApps.add(appInfo3);
        CarMediaAppInfo appInfo4 = new CarMediaAppInfo();
        appInfo4.setTabIndex(5);
        appInfo4.setIconId(R.drawable.tune_in);
        appInfo4.setSmallIconId(R.drawable.tune_in_logo);
        appInfo4.setPkgName(this.mContext.getString(R.string.component_tunein));
        appInfo4.setTitle("TuneIn");
        appInfo4.setSubtitle("");
        appInfo4.setDesc("");
        largeApps.add(appInfo4);
        return largeApps;
    }

    @Override // com.xiaopeng.systemui.secondarywindow.AbstractAppsFactory
    public List<BaseAppInfo> createSmallApps() {
        List<BaseAppInfo> smallApps = new ArrayList<>();
        if (CarModelsManager.getConfig().getCfcVehicleLevel() >= 0) {
            BaseAppInfo appInfo = new BaseAppInfo();
            appInfo.setTitle(this.mContext.getString(R.string.mindfulness_space));
            appInfo.setType(3);
            appInfo.setSubType(1);
            appInfo.setIconId(R.drawable.mindfulness_space);
            appInfo.setShadowImgId(R.drawable.mindfulness_space_shadow);
            appInfo.setTextColor(R.color.psn_title);
            smallApps.add(appInfo);
            BaseAppInfo appInfo2 = new BaseAppInfo();
            appInfo2.setTitle(this.mContext.getString(R.string.sleep_space));
            appInfo2.setType(3);
            appInfo2.setSubType(2);
            appInfo2.setIconId(R.drawable.sleep_space);
            appInfo2.setTextColor(R.color.psn_white);
            appInfo2.setShadowImgId(R.drawable.sleep_space_shadow);
            smallApps.add(appInfo2);
        } else {
            BaseAppInfo appInfo3 = new BaseAppInfo();
            appInfo3.setTitle(this.mContext.getString(R.string.mindfulness_space));
            appInfo3.setType(3);
            appInfo3.setSubType(1);
            appInfo3.setIconId(R.drawable.mindfulness_space_big);
            appInfo3.setShadowImgId(R.drawable.mindfulness_space_big_shadow);
            appInfo3.setTextColor(R.color.psn_title);
            smallApps.add(appInfo3);
        }
        BaseAppInfo appInfo4 = new BaseAppInfo();
        appInfo4.setTitle(this.mContext.getString(R.string.makeup_space));
        appInfo4.setTextColor(R.color.psn_title);
        appInfo4.setType(3);
        appInfo4.setSubType(3);
        appInfo4.setIconId(R.drawable.makeup_space);
        appInfo4.setShadowImgId(R.drawable.makeup_space_shadow);
        smallApps.add(appInfo4);
        BaseAppInfo appInfo5 = new BaseAppInfo();
        appInfo5.setTitle(this.mContext.getString(R.string.scent_space));
        appInfo5.setTextColor(R.color.psn_title);
        appInfo5.setType(3);
        appInfo5.setSubType(4);
        appInfo5.setIconId(R.drawable.scent_space);
        appInfo5.setShadowImgId(R.drawable.scent_space_shadow);
        smallApps.add(appInfo5);
        return smallApps;
    }
}
