package com.xiaopeng.systemui.secondarywindow;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.R;
import com.xiaopeng.systemui.AppDownloadPresenter;
import com.xiaopeng.systemui.carmanager.XuiClientWrapper;
import com.xiaopeng.systemui.helper.PackageHelper;
import java.util.List;
/* loaded from: classes24.dex */
public class AppsAdapter extends RecyclerView.Adapter<BaseAppHolder> implements View.OnClickListener {
    private static final String TAG = "AppsAdapter";
    public static final int VIEW_TYPE_LARGE = 1;
    public static final int VIEW_TYPE_MODE = 3;
    public static final int VIEW_TYPE_SMALL = 2;
    private List<BaseAppInfo> mAppInfos;
    private Context mContext;

    public AppsAdapter(Context context, List<BaseAppInfo> appInfos) {
        this.mAppInfos = appInfos;
        this.mContext = context;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    @NonNull
    public BaseAppHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View largeAppView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_large_app, parent, false);
            largeAppView.setOnClickListener(this);
            return new LargeAppHolder(largeAppView);
        } else if (viewType == 2) {
            View smallAppView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_small_app, parent, false);
            smallAppView.setOnClickListener(this);
            return new SmallAppHolder(smallAppView);
        } else if (viewType == 3) {
            View modeEntryView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mode_entry, parent, false);
            modeEntryView.setOnClickListener(this);
            return new BaseAppHolder(modeEntryView);
        } else {
            return null;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(@NonNull BaseAppHolder holder, int position) {
        holder.bindData(this.mAppInfos.get(position));
        holder.itemView.setTag(Integer.valueOf(position));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mAppInfos.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int position) {
        return this.mAppInfos.get(position).getType();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        BaseAppInfo baseAppInfo = this.mAppInfos.get(((Integer) v.getTag()).intValue());
        if (baseAppInfo != null) {
            if (baseAppInfo.isSysApp()) {
                int sysAppType = baseAppInfo.getSysAppType();
                if (sysAppType == 1) {
                    PackageHelper.startXpMusic(this.mContext, 1);
                    return;
                } else if (sysAppType != 2) {
                    if (sysAppType == 3) {
                        PackageHelper.startCarMusic(this.mContext, 1, false, true);
                        return;
                    }
                    return;
                } else {
                    CarMediaAppInfo carMediaAppInfo = (CarMediaAppInfo) baseAppInfo;
                    if (checkAppInstalled(carMediaAppInfo)) {
                        PackageHelper.startCarMedia(this.mContext, 1, carMediaAppInfo.getTabIndex());
                        return;
                    }
                    return;
                }
            }
            int type = baseAppInfo.getType();
            if (type != 1 && type != 2) {
                if (type == 3) {
                    handleModeClick(baseAppInfo.getSubType());
                    return;
                }
                return;
            }
            String pkgName = baseAppInfo.getPkgName();
            if (PackageHelper.isAppInstalled(this.mContext, pkgName)) {
                PackageHelper.startSecondaryApp(this.mContext, pkgName);
            } else {
                AppDownloadPresenter.getInstance().startDownloadApp(pkgName, baseAppInfo.getTitle());
            }
        }
    }

    private boolean checkAppInstalled(CarMediaAppInfo carMediaAppInfo) {
        String pkgName = carMediaAppInfo.getPkgName();
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }
        if (PackageHelper.isAppInstalled(this.mContext, pkgName)) {
            return true;
        }
        if (carMediaAppInfo.getStatus() == 1) {
            AppDownloadPresenter.getInstance().startDownloadApp(pkgName, carMediaAppInfo.getTitle());
        }
        return false;
    }

    private void handleModeClick(int subType) {
        if (subType == 1) {
            XuiClientWrapper.getInstance().startMeditationMode();
        } else if (subType == 2) {
            XuiClientWrapper.getInstance().startSleepMode();
        } else if (subType == 3) {
            XuiClientWrapper.getInstance().startMakeupMode();
        } else if (subType == 4) {
            PackageHelper.startScentSpace(this.mContext, 1);
        }
    }

    public void notifyDownloadInfo(BaseAppInfo appInfo) {
        for (int i = 0; i < this.mAppInfos.size(); i++) {
            BaseAppInfo baseAppInfo = this.mAppInfos.get(i);
            String pkgName = baseAppInfo.getPkgName();
            if (pkgName.equals(appInfo.getPkgName())) {
                baseAppInfo.setStatus(appInfo.getStatus());
                baseAppInfo.setDownloadProgress(appInfo.getDownloadProgress());
                notifyItemChanged(i);
                return;
            }
        }
    }
}
