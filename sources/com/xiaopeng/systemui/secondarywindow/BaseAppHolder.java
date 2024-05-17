package com.xiaopeng.systemui.secondarywindow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.R;
import com.xiaopeng.systemui.AppDownloadPresenter;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.xui.widget.XCircularProgressBar;
import com.xiaopeng.xui.widget.XImageView;
import com.xiaopeng.xui.widget.XTextView;
/* loaded from: classes24.dex */
public class BaseAppHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "BaseAppHolder";
    private XImageView mAppIcon;
    private XImageView mAppIconShadow;
    private BaseAppInfo mAppInfo;
    private XImageView mBgDownloadProgress;
    private Context mContext;
    private XImageView mDefaultImg;
    private XTextView mDefaultTxt;
    private XCircularProgressBar mDownloadProgress;
    private XImageView mIvDownloaded;
    private XTextView mSubtitle;
    private XTextView mTitle;

    public BaseAppHolder(View itemView) {
        super(itemView);
        this.mContext = ContextUtils.getContext();
        this.mAppIcon = (XImageView) itemView.findViewById(R.id.app_icon);
        this.mAppIconShadow = (XImageView) itemView.findViewById(R.id.app_icon_shadow);
        this.mTitle = (XTextView) itemView.findViewById(R.id.tv_title);
        this.mSubtitle = (XTextView) itemView.findViewById(R.id.tv_subtitle);
        this.mDefaultImg = (XImageView) itemView.findViewById(R.id.app_default);
        this.mDefaultTxt = (XTextView) itemView.findViewById(R.id.tv_default);
        this.mDownloadProgress = (XCircularProgressBar) itemView.findViewById(R.id.download_progress);
        this.mBgDownloadProgress = (XImageView) itemView.findViewById(R.id.bg_download_progress);
        XCircularProgressBar xCircularProgressBar = this.mDownloadProgress;
        if (xCircularProgressBar != null) {
            xCircularProgressBar.setOnClickListener(this);
        }
        this.mIvDownloaded = (XImageView) itemView.findViewById(R.id.iv_downloaded);
    }

    public void bindData(BaseAppInfo appInfo) {
        this.mAppInfo = appInfo;
        this.mAppIcon.setImageResource(appInfo.getIconId());
        XImageView xImageView = this.mAppIconShadow;
        if (xImageView != null) {
            xImageView.setImageResource(appInfo.getShadowImgId());
        }
        XTextView xTextView = this.mTitle;
        if (xTextView != null) {
            xTextView.setText(appInfo.getTitle());
        }
        XTextView xTextView2 = this.mSubtitle;
        if (xTextView2 != null) {
            xTextView2.setText(appInfo.getSubtitle());
        }
        XImageView xImageView2 = this.mDefaultImg;
        if (xImageView2 != null) {
            xImageView2.setImageResource(appInfo.getDefaultImg());
        }
        XTextView xTextView3 = this.mDefaultTxt;
        if (xTextView3 != null) {
            xTextView3.setText(appInfo.getDefaultTxt());
        }
        showAppStatus(appInfo);
        int textColor = appInfo.getTextColor();
        if (textColor != 0) {
            this.mTitle.setTextColor(this.mContext.getColor(textColor));
        }
    }

    private void showAppStatus(BaseAppInfo appInfo) {
        int status = appInfo.getStatus();
        XImageView xImageView = this.mIvDownloaded;
        if (xImageView != null) {
            xImageView.setVisibility(status == 6 ? 0 : 8);
        }
        XTextView xTextView = this.mSubtitle;
        if (xTextView != null) {
            xTextView.setVisibility(status == 6 ? 8 : 0);
        }
        if (status == 1) {
            showNotInstalled();
        } else if (status == 5) {
            XTextView xTextView2 = this.mSubtitle;
            if (xTextView2 != null) {
                xTextView2.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
                this.mSubtitle.setText(R.string.is_installing);
            }
        } else if (status != 6) {
            if (status == 7) {
                showDownloadFail();
            } else {
                updateDownloadState(appInfo.getStatus(), appInfo.getDownloadProgress());
            }
        } else {
            XTextView xTextView3 = this.mSubtitle;
            if (xTextView3 != null) {
                xTextView3.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            }
        }
    }

    private void updateDownloadState(int status, int progress) {
        if (this.mDownloadProgress == null) {
            return;
        }
        if (status == 2) {
            showDownloadProgress(true);
            this.mDownloadProgress.setIndeterminate(true);
            showDownloading();
        } else if (status == 3) {
            showDownloadProgress(true);
            this.mDownloadProgress.setIndeterminate(false);
            this.mDownloadProgress.setIndicatorType(0);
            this.mDownloadProgress.setProgress(progress);
            showDownloading();
        } else if (status == 4) {
            showDownloadProgress(true);
            this.mDownloadProgress.setIndeterminate(false);
            this.mDownloadProgress.setProgress(progress);
            this.mDownloadProgress.setIndicatorType(2);
            showDownloading();
        }
    }

    private void showDownloading() {
        XTextView xTextView = this.mSubtitle;
        if (xTextView != null) {
            xTextView.setText(R.string.is_downloading);
        }
    }

    private void showNotInstalled() {
        XTextView xTextView = this.mSubtitle;
        if (xTextView != null) {
            xTextView.setText(R.string.not_installed);
            this.mSubtitle.setCompoundDrawablePadding(12);
            this.mSubtitle.setCompoundDrawablesWithIntrinsicBounds(this.mContext.getDrawable(R.drawable.ic_not_installed), (Drawable) null, (Drawable) null, (Drawable) null);
        }
    }

    private void showDownloadFail() {
    }

    private void showDownloadProgress(boolean visible) {
        XImageView xImageView = this.mBgDownloadProgress;
        if (xImageView != null) {
            xImageView.setVisibility(visible ? 0 : 8);
        }
        XCircularProgressBar xCircularProgressBar = this.mDownloadProgress;
        if (xCircularProgressBar != null) {
            xCircularProgressBar.setVisibility(visible ? 0 : 8);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        BaseAppInfo baseAppInfo;
        if (v.getId() == R.id.download_progress && (baseAppInfo = this.mAppInfo) != null) {
            int status = baseAppInfo.getStatus();
            if (status == 3) {
                AppDownloadPresenter.getInstance().resumeDownloadApp(this.mAppInfo.getPkgName());
            } else if (status == 4) {
                AppDownloadPresenter.getInstance().pauseDownloadApp(this.mAppInfo.getPkgName());
            }
        }
    }
}
