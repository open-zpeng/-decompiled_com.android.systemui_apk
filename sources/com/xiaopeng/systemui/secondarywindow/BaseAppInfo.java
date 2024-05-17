package com.xiaopeng.systemui.secondarywindow;
/* loaded from: classes24.dex */
public class BaseAppInfo {
    public static final int MODE_MAKEUP_SPACE = 3;
    public static final int MODE_MINDFULNESS_SPACE = 1;
    public static final int MODE_SCENT_SPACE = 4;
    public static final int MODE_SLEEP_SPACE = 2;
    public static final int STATUS_DOWNLOAD_FAILED = 7;
    public static final int STATUS_INIT = 0;
    public static final int STATUS_INSTALLED = 6;
    public static final int STATUS_IS_INSTALLING = 5;
    public static final int STATUS_IS_PAUSED = 3;
    public static final int STATUS_IS_PREPARING = 2;
    public static final int STATUS_IS_RUNNING = 4;
    public static final int STATUS_NOT_INSTALLED = 1;
    public static final int SYS_APP_BLUETOOTH_MUSIC = 3;
    public static final int SYS_APP_CAR_MEDIA = 2;
    public static final int SYS_APP_INVALID = 0;
    public static final int SYS_APP_MUSIC = 1;
    private int mDefaultImg;
    private String mDefaultTxt;
    private int mDownloadProgress;
    private int mIconId;
    private String mPkgName;
    private int mShadowImgId;
    private int mSubType;
    private String mSubtitle;
    private int mTextColor;
    private String mTitle;
    private int mType;
    private int mIndex = -1;
    private boolean mIsSysApp = false;
    private int mSysAppType = 0;
    private int mStatus = 0;

    public int getIndex() {
        return this.mIndex;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }

    public int getType() {
        return this.mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public int getSubType() {
        return this.mSubType;
    }

    public void setSubType(int subType) {
        this.mSubType = subType;
    }

    public int getIconId() {
        return this.mIconId;
    }

    public void setDefaultImg(int txt) {
        this.mDefaultImg = txt;
    }

    public int getDefaultImg() {
        return this.mDefaultImg;
    }

    public void setDefaultTxt(String txt) {
        this.mDefaultTxt = txt;
    }

    public String getDefaultTxt() {
        return this.mDefaultTxt;
    }

    public void setIconId(int iconId) {
        this.mIconId = iconId;
    }

    public int getShadowImgId() {
        return this.mShadowImgId;
    }

    public void setShadowImgId(int shadowImgId) {
        this.mShadowImgId = shadowImgId;
    }

    public String getPkgName() {
        return this.mPkgName;
    }

    public void setPkgName(String pkgName) {
        this.mPkgName = pkgName;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getSubtitle() {
        return this.mSubtitle;
    }

    public void setSubtitle(String subtitle) {
        this.mSubtitle = subtitle;
    }

    public boolean isSysApp() {
        return this.mIsSysApp;
    }

    public void setSysApp(boolean sysApp) {
        this.mIsSysApp = sysApp;
    }

    public int getSysAppType() {
        return this.mSysAppType;
    }

    public void setSysAppType(int sysAppType) {
        this.mSysAppType = sysAppType;
    }

    public int getTextColor() {
        return this.mTextColor;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public int getDownloadProgress() {
        return this.mDownloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.mDownloadProgress = downloadProgress;
    }
}
