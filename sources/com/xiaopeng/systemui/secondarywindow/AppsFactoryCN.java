package com.xiaopeng.systemui.secondarywindow;

import android.content.Context;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class AppsFactoryCN extends AbstractAppsFactory {
    private Context mContext = ContextUtils.getContext();

    @Override // com.xiaopeng.systemui.secondarywindow.AbstractAppsFactory
    public List<BaseAppInfo> createLargeApps() {
        List<BaseAppInfo> largeApps = new ArrayList<>();
        LargeAppInfo appInfo = new LargeAppInfo();
        appInfo.setType(1);
        appInfo.setIconId(R.drawable.bg_aiqiyi);
        appInfo.setSmallIconId(R.drawable.ic_applist_aiqiyi);
        appInfo.setTitle("专属影院");
        appInfo.setSubtitle("Love, Death & Wardddd");
        appInfo.setDesc("科幻、剧情");
        appInfo.setPkgName(this.mContext.getString(R.string.component_aiqiyi));
        largeApps.add(appInfo);
        LargeAppInfo appInfo2 = new LargeAppInfo();
        appInfo2.setType(1);
        appInfo2.setIconId(R.drawable.bg_music);
        appInfo2.setSmallIconId(R.drawable.ic_applist_music);
        appInfo2.setPkgName(this.mContext.getString(R.string.component_music_secondary));
        appInfo2.setTitle("每日推荐");
        appInfo2.setSubtitle("Sugar");
        appInfo2.setDesc("Maroon 5");
        appInfo2.setSysApp(true);
        appInfo2.setSysAppType(1);
        largeApps.add(appInfo2);
        LargeAppInfo appInfo3 = new LargeAppInfo();
        appInfo3.setType(1);
        appInfo3.setIconId(R.drawable.bg_play);
        appInfo3.setSmallIconId(R.drawable.ic_applist_play);
        appInfo3.setPkgName(this.mContext.getString(R.string.component_game));
        appInfo3.setTitle("热门排行");
        appInfo3.setSubtitle("守卫者");
        appInfo3.setDesc("角色扮演");
        largeApps.add(appInfo3);
        LargeAppInfo appInfo4 = new LargeAppInfo();
        appInfo4.setType(1);
        appInfo4.setIconId(R.drawable.bg_changba);
        appInfo4.setSmallIconId(R.drawable.ic_applist_changba);
        appInfo4.setPkgName(this.mContext.getString(R.string.component_changba));
        appInfo4.setTitle("猜你喜欢");
        appInfo4.setSubtitle("安静");
        appInfo4.setDesc("周杰伦");
        largeApps.add(appInfo4);
        return largeApps;
    }

    @Override // com.xiaopeng.systemui.secondarywindow.AbstractAppsFactory
    public List<BaseAppInfo> createSmallApps() {
        List<BaseAppInfo> smallApps = new ArrayList<>();
        BaseAppInfo appInfo = new BaseAppInfo();
        appInfo.setType(2);
        appInfo.setIconId(R.drawable.ic_applist_bilibili);
        appInfo.setTitle("哔哩哔哩");
        appInfo.setSubtitle("你感兴趣的视频都在B站");
        appInfo.setPkgName(this.mContext.getString(R.string.component_bilibili));
        smallApps.add(appInfo);
        BaseAppInfo appInfo2 = new BaseAppInfo();
        appInfo2.setType(2);
        appInfo2.setIconId(R.drawable.ic_applist_tencent);
        appInfo2.setTitle("腾讯视频");
        appInfo2.setSubtitle("发现音乐新世界");
        appInfo2.setPkgName(this.mContext.getString(R.string.component_ktcp_video));
        smallApps.add(appInfo2);
        BaseAppInfo appInfo3 = new BaseAppInfo();
        appInfo3.setType(2);
        appInfo3.setIconId(R.drawable.ic_applist_youku);
        appInfo3.setTitle("优酷视频");
        appInfo3.setSubtitle("精彩内容，尽在优酷");
        appInfo3.setPkgName(this.mContext.getString(R.string.component_youku));
        smallApps.add(appInfo3);
        BaseAppInfo appInfo4 = new BaseAppInfo();
        appInfo4.setType(2);
        appInfo4.setIconId(R.drawable.ic_applist_qqmusic);
        appInfo4.setTitle("QQ音乐");
        appInfo4.setSubtitle("发现音乐新世界");
        appInfo4.setPkgName(this.mContext.getString(R.string.component_qqmusic));
        smallApps.add(appInfo4);
        return smallApps;
    }
}
