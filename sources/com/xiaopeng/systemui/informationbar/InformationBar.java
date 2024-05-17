package com.xiaopeng.systemui.informationbar;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.DropmenuController;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.ui.widget.AnimatedImageView;
import com.xiaopeng.systemui.ui.widget.AnimatedTextView;
import com.xiaopeng.systemui.ui.window.StatusBarWindow;
import com.xiaopeng.systemui.utils.DataLogUtils;
/* loaded from: classes24.dex */
public class InformationBar implements StatusBarWindow.OnViewListener, View.OnClickListener {
    private static final int HOME_STATE_PAUSED = 4;
    private static final int HOME_STATE_RESUMED = 3;
    private static final String KEY_HOME_STATE = "key_system_home_state";
    private static final String TAG = "InformationBar";
    private AnimatedImageView mBtnExchange;
    private AnimatedTextView mBtnHome;
    private ContentObserver mContentObserver;
    private Context mContext;
    private FrameLayout mInformationBar;
    private boolean mShowExchange = false;
    private StatusBarWindow mStatusBarWindow;
    private WindowManager mWindowManager;

    public InformationBar(Context context, StatusBarWindow viewGroup) {
        this.mContext = context;
        this.mStatusBarWindow = viewGroup;
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        initInformationBar();
        this.mContentObserver = new ContentObserver(new Handler()) { // from class: com.xiaopeng.systemui.informationbar.InformationBar.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                if (uri.equals(Settings.Secure.getUriFor(InformationBar.KEY_HOME_STATE))) {
                    try {
                        int homeState = Settings.Secure.getInt(InformationBar.this.mContext.getContentResolver(), InformationBar.KEY_HOME_STATE);
                        int i = 0;
                        boolean showHomeButton = homeState == 4;
                        if (InformationBar.this.mStatusBarWindow != null) {
                            StatusBarWindow statusBarWindow = InformationBar.this.mStatusBarWindow;
                            if (!showHomeButton) {
                                i = 8;
                            }
                            statusBarWindow.setVisibility(i);
                        }
                        if (InformationBar.this.mInformationBar != null) {
                            InformationBar.this.mInformationBar.setEnabled(showHomeButton);
                        }
                    } catch (Settings.SettingNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(KEY_HOME_STATE), false, this.mContentObserver);
    }

    private void initInformationBar() {
        StatusBarWindow statusBarWindow = this.mStatusBarWindow;
        if (statusBarWindow != null) {
            this.mInformationBar = (FrameLayout) statusBarWindow.findViewById(R.id.information_bar);
            this.mStatusBarWindow.addListener(this);
            this.mInformationBar.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.informationbar.-$$Lambda$IN1jeTs1qYtHIU430ddbn6K26_Q
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    InformationBar.this.onClick(view);
                }
            });
            this.mInformationBar.setOnTouchListener(new View.OnTouchListener() { // from class: com.xiaopeng.systemui.informationbar.InformationBar.2
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if (action == 0) {
                        InformationBar.this.mInformationBar.getBackground().setAlpha(143);
                        return false;
                    } else if (action == 1) {
                        InformationBar.this.mInformationBar.getBackground().setAlpha(255);
                        return false;
                    } else {
                        return false;
                    }
                }
            });
            this.mBtnHome = (AnimatedTextView) this.mInformationBar.findViewById(R.id.btn_home);
            this.mBtnExchange = (AnimatedImageView) this.mInformationBar.findViewById(R.id.btn_exchange);
        }
    }

    @Override // com.xiaopeng.systemui.ui.window.StatusBarWindow.OnViewListener
    public void onFinishInflate() {
    }

    @Override // com.xiaopeng.systemui.ui.window.StatusBarWindow.OnViewListener
    public void onAttachedToWindow() {
    }

    @Override // com.xiaopeng.systemui.ui.window.StatusBarWindow.OnViewListener
    public void dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == 0 || action == 1 || action == 3) {
            DropmenuController.getInstance(this.mContext).onOutsideTouched(ev);
        }
    }

    private void setTypeface(TextView view) {
        if (view != null) {
            Typeface tf = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/xpeng-regular.ttf");
            view.setTypeface(tf);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        Logger.d(TAG, "onClick view=" + view);
        if (id == R.id.information_bar) {
            if (this.mShowExchange) {
                this.mWindowManager.setSharedEvent(0, 1);
                return;
            }
            DataLogUtils.sendDataLog(DataLogUtils.SYSTEMUI_PAGE_ID, DataLogUtils.BACK_TO_HOME_ID);
            PackageHelper.gotoHome(this.mContext);
        }
    }

    public void onActivityChanged(String topPackage, String primaryTopPackage) {
        boolean showInformationBar = true;
        this.mShowExchange = !TextUtils.isEmpty(primaryTopPackage);
        this.mBtnExchange.setVisibility(this.mShowExchange ? 0 : 8);
        boolean showHomeBtn = (this.mShowExchange || TextUtils.isEmpty(topPackage) || topPackage.equals(PackageHelper.getInstance().getMapPkgName())) ? false : true;
        this.mBtnHome.setVisibility(showHomeBtn ? 0 : 8);
        if (this.mBtnExchange.getVisibility() != 0 && this.mBtnHome.getVisibility() != 0) {
            showInformationBar = false;
        }
        showInformationBar(showInformationBar);
        Logger.d(TAG, "onActivityChanged : topPackage = " + topPackage + " primaryTopPackage = " + primaryTopPackage + " mShowExchange = " + this.mShowExchange + " showHomeBtn = " + showHomeBtn + " showInformationBar = " + showInformationBar);
    }

    private void showInformationBar(boolean show) {
        StatusBarWindow statusBarWindow = this.mStatusBarWindow;
        if (statusBarWindow != null) {
            statusBarWindow.setVisibility(show ? 0 : 8);
            this.mInformationBar.setEnabled(show);
        }
    }
}
