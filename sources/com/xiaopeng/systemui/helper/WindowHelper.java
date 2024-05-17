package com.xiaopeng.systemui.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.ServiceManager;
import android.view.IWindowManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.alibaba.fastjson.asm.Opcodes;
import com.android.settingslib.wifi.AccessPoint;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarFragment;
import com.android.systemui.statusbar.phone.StatusBar;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.NgpWarningView;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.VoiceWaveViewContainer;
import com.xiaopeng.systemui.statusbar.QuickMenuGuide;
/* loaded from: classes24.dex */
public class WindowHelper {
    private static final String TAG = "WindowHelper";
    public static final int TYPE_INFOFLOW_BAR = 2039;
    public static final int TYPE_INFORMATION_BAR = 2046;
    public static final int TYPE_NAVIGATION_BAR = 2019;
    public static final int TYPE_NAVIGATION_BAR_PANEL = 2024;
    public static final int TYPE_OSD = 2043;
    public static final int TYPE_QUICK_PANEL = 2053;
    public static final int TYPE_SPECTRUM = 2041;
    public static final int TYPE_STATUS_BAR = 2000;
    public static final int TYPE_STATUS_BAR_PANEL = 2014;
    public static final int TYPE_VUI = 2049;
    public static final int TYPE_VUI_OVERLAY = 2052;
    public static final int TYPE_WATER_MARK = 2045;
    private static View sNavigationBarWindow = null;
    private static View sStatusBarWindow = null;

    public static void addStatusBar(WindowManager wm, View view, int height) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(-1, -1, 2000, 25428008, -3);
        lp.setTitle(StatusBar.TAG);
        lp.gravity = 51;
        lp.windowAnimations = 0;
        wm.addView(view, lp);
        sStatusBarWindow = view;
    }

    public static void addNavigationBar(WindowManager wm, View view, int height) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(-1, -1, TYPE_NAVIGATION_BAR, 25428072, -3);
        lp.setTitle(NavigationBarFragment.TAG);
        lp.windowAnimations = 0;
        wm.addView(view, lp);
        sNavigationBarWindow = view;
    }

    public static View getNavigationBarWindow() {
        return sNavigationBarWindow;
    }

    public static View getStatusBarWindow() {
        return sStatusBarWindow;
    }

    public static void addInformationBar(WindowManager wm, View view, int height) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(-1, -1, TYPE_INFORMATION_BAR, 8650792, -3);
        lp.setTitle(StatusBar.TAG);
        lp.gravity = 80;
        lp.windowAnimations = 0;
        wm.addView(view, lp);
    }

    public static void addQuickMenu(int screenIndex, WindowManager wm, View view) {
        if (screenIndex == 1) {
            addSecondaryQuickMenu(wm, view);
        } else {
            addQuickMenu(wm, view);
        }
    }

    private static void addQuickMenu(WindowManager wm, View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.gravity = 49;
        lp.type = TYPE_QUICK_PANEL;
        lp.flags = 17106728;
        lp.format = -2;
        lp.systemUiVisibility = 4871;
        lp.height = -1;
        lp.width = -1;
        wm.addView(view, lp);
    }

    private static void addSecondaryQuickMenu(WindowManager wm, View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.setTitle("secondaryQuickMenu");
        lp.gravity = 49;
        lp.type = TYPE_QUICK_PANEL;
        lp.flags = 16777256;
        lp.format = -2;
        lp.height = -1;
        lp.width = AccessPoint.LOWER_FREQ_24GHZ;
        lp.intentFlags = 32;
        lp.x = AccessPoint.LOWER_FREQ_24GHZ;
        wm.addView(view, lp);
    }

    public static void addSecondaryWindow(WindowManager wm, View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.setTitle("secondaryWindow");
        lp.gravity = 49;
        lp.type = 2060;
        lp.flags = 16777256;
        lp.format = -2;
        lp.height = -1;
        lp.width = -1;
        wm.addView(view, lp);
    }

    public static void addSecondaryScreensaverWindow(WindowManager wm, View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.setTitle("secondaryScreensaverWindow");
        lp.gravity = 49;
        lp.type = 2060;
        lp.flags = 16777256;
        lp.format = -2;
        lp.height = -1;
        lp.width = -1;
        wm.addView(view, lp);
    }

    public static void addStatusBarOverflowWindow(Context context, WindowManager wm, View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.gravity = 51;
        lp.type = TYPE_VUI;
        lp.flags = 17039368;
        lp.format = -2;
        lp.height = -2;
        lp.width = -2;
        lp.x = context.getResources().getDimensionPixelSize(R.dimen.status_bar_overflow_item_container_left_margin);
        lp.y = context.getResources().getDimensionPixelSize(R.dimen.status_bar_overflow_item_container_top_margin);
        wm.addView(view, lp);
    }

    public static Rect getWindowRect(Context context, int type) {
        IBinder binder = ServiceManager.getService("window");
        IWindowManager wm = binder != null ? IWindowManager.Stub.asInterface(binder) : null;
        if (wm != null) {
            try {
                return wm.getXuiRectByType(type);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static Rect getStatusBarRect(Context context) {
        return getWindowRect(context, 2000);
    }

    public static Rect getNavigationBarRect(Context context) {
        return getWindowRect(context, TYPE_NAVIGATION_BAR);
    }

    public static RelativeLayout addTopLeftAsrContainer(Context context, WindowManager wm) {
        RelativeLayout asrContainer = (RelativeLayout) View.inflate(context, R.layout.view_voice_top_left_asr_container, null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, TYPE_VUI, 8388616, -3);
        layoutParams.width = context.getResources().getDimensionPixelSize(R.dimen.speech_top_left_asr_window_width);
        layoutParams.height = -2;
        layoutParams.x = context.getResources().getDimensionPixelSize(R.dimen.speech_top_left_asr_window_margin_start);
        layoutParams.y = context.getResources().getDimensionPixelSize(R.dimen.speech_asr_window_margin_top);
        layoutParams.gravity = 8388659;
        wm.addView(asrContainer, layoutParams);
        return asrContainer;
    }

    public static VoiceWaveViewContainer addTopVoiceWaveViewContainer(Context context, WindowManager wm) {
        VoiceWaveViewContainer voiceWaveViewContainer = (VoiceWaveViewContainer) View.inflate(context, R.layout.view_voice_top_right_wave_container_by_code, null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, TYPE_VUI_OVERLAY, 8388616, -3);
        layoutParams.width = context.getResources().getDimensionPixelSize(R.dimen.speech_right_asr_window_width);
        layoutParams.height = -2;
        layoutParams.x = 0;
        layoutParams.y = context.getResources().getDimensionPixelSize(R.dimen.speech_asr_window_margin_top);
        layoutParams.gravity = 8388661;
        layoutParams.token = new Binder();
        wm.addView(voiceWaveViewContainer, layoutParams);
        return voiceWaveViewContainer;
    }

    public static VoiceWaveViewContainer addBottomLeftAsrContainer(Context context, WindowManager wm) {
        VoiceWaveViewContainer voiceWaveViewContainer = (VoiceWaveViewContainer) View.inflate(context, R.layout.view_voice_bottom_left_asr_container, null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, TYPE_VUI_OVERLAY, 8388616, -3);
        layoutParams.width = context.getResources().getDimensionPixelSize(R.dimen.speech_bottom_left_asr_window_width);
        layoutParams.height = -2;
        layoutParams.x = 0;
        layoutParams.y = context.getResources().getDimensionPixelSize(R.dimen.speech_asr_window_margin_bottom);
        layoutParams.gravity = 8388691;
        layoutParams.windowAnimations = 0;
        layoutParams.token = new Binder();
        wm.addView(voiceWaveViewContainer, layoutParams);
        return voiceWaveViewContainer;
    }

    public static VoiceWaveViewContainer addBottomMidAsrContainer(Context context, WindowManager wm) {
        VoiceWaveViewContainer voiceWaveViewContainer = (VoiceWaveViewContainer) View.inflate(context, R.layout.view_voice_bottom_mid_asr_container, null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, TYPE_VUI_OVERLAY, 8388616, -3);
        layoutParams.width = context.getResources().getDimensionPixelSize(R.dimen.speech_bottom_mid_asr_window_width);
        layoutParams.height = -2;
        layoutParams.x = 0;
        layoutParams.y = context.getResources().getDimensionPixelSize(R.dimen.speech_asr_window_margin_bottom);
        layoutParams.gravity = 81;
        layoutParams.token = new Binder();
        wm.addView(voiceWaveViewContainer, layoutParams);
        return voiceWaveViewContainer;
    }

    public static VoiceWaveViewContainer addBottomRightAsrContainer(Context context, WindowManager wm) {
        VoiceWaveViewContainer voiceWaveViewContainer = (VoiceWaveViewContainer) View.inflate(context, R.layout.view_voice_bottom_right_asr_container, null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, TYPE_VUI_OVERLAY, 8388616, -3);
        layoutParams.width = context.getResources().getDimensionPixelSize(R.dimen.speech_right_asr_window_width);
        layoutParams.height = -2;
        layoutParams.x = 0;
        layoutParams.y = context.getResources().getDimensionPixelSize(R.dimen.speech_asr_window_margin_bottom);
        layoutParams.gravity = 8388693;
        layoutParams.token = new Binder();
        wm.addView(voiceWaveViewContainer, layoutParams);
        return voiceWaveViewContainer;
    }

    public static VoiceWaveViewContainer addBottomVoiceWaveViewContainer(Context context, WindowManager wm) {
        VoiceWaveViewContainer voiceWaveViewContainer = (VoiceWaveViewContainer) View.inflate(context, R.layout.view_voice_bottom_wave_container_by_code, null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, TYPE_VUI, 8388616, -3);
        layoutParams.width = context.getResources().getDimensionPixelSize(R.dimen.speech_vui_window_width);
        layoutParams.height = -2;
        layoutParams.x = 0;
        layoutParams.y = 1200 - layoutParams.height;
        layoutParams.gravity = 17;
        layoutParams.token = new Binder();
        wm.addView(voiceWaveViewContainer, layoutParams);
        return voiceWaveViewContainer;
    }

    public static VoiceWaveViewContainer addTopVoiceWaveViewOnly(Context context, WindowManager wm) {
        VoiceWaveViewContainer voiceWaveViewContainer = (VoiceWaveViewContainer) View.inflate(context, R.layout.view_top_voice_wave_container, null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, TYPE_VUI, 8388632, -3);
        layoutParams.width = context.getResources().getDimensionPixelSize(R.dimen.speech_vui_window_width);
        layoutParams.height = context.getResources().getDimensionPixelSize(R.dimen.speech_wave_voice_container_corner_height);
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.gravity = 48;
        layoutParams.token = new Binder();
        wm.addView(voiceWaveViewContainer, layoutParams);
        return voiceWaveViewContainer;
    }

    public static VoiceWaveViewContainer addBottomVoiceWaveViewOnly(Context context, WindowManager wm) {
        VoiceWaveViewContainer voiceWaveViewContainer = (VoiceWaveViewContainer) View.inflate(context, R.layout.view_bottom_voice_wave_container, null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, TYPE_VUI, 8388632, -3);
        layoutParams.width = context.getResources().getDimensionPixelSize(R.dimen.speech_vui_window_width);
        layoutParams.height = context.getResources().getDimensionPixelSize(R.dimen.speech_wave_voice_container_corner_height);
        layoutParams.x = 0;
        layoutParams.y = 1200 - layoutParams.height;
        layoutParams.gravity = 17;
        layoutParams.token = new Binder();
        wm.addView(voiceWaveViewContainer, layoutParams);
        return voiceWaveViewContainer;
    }

    public static View addQuickMenuGuide(Context context, WindowManager wm) {
        View quickMenuGuideLayout = View.inflate(context, R.layout.layout_quick_menu_guide, null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, Opcodes.INVOKESTATIC, TYPE_VUI, 25427992, -3);
        layoutParams.gravity = 51;
        wm.addView(quickMenuGuideLayout, layoutParams);
        quickMenuGuideLayout.setOnTouchListener(new View.OnTouchListener() { // from class: com.xiaopeng.systemui.helper.WindowHelper.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                QuickMenuGuide.getInstance().destroy();
                return false;
            }
        });
        return quickMenuGuideLayout;
    }

    public static NgpWarningView addNgpWarningView(Context context, WindowManager wm) {
        NgpWarningView ngpWarningView = (NgpWarningView) View.inflate(context, R.layout.view_ngp_warning, null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, TYPE_VUI, 25165880, -3);
        layoutParams.gravity = 17;
        layoutParams.token = new Binder();
        wm.addView(ngpWarningView, layoutParams);
        return ngpWarningView;
    }

    public static void addMaskLayerView(WindowManager wm, View view, int width) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.setTitle("Watermark");
        lp.type = 2042;
        lp.flags = 25165880;
        lp.format = -2;
        lp.height = 1200;
        lp.width = width;
        wm.addView(view, lp);
    }

    public static void removeMaskLayerView(WindowManager wm, View view) {
        if (view != null) {
            wm.removeView(view);
        }
    }

    public static RelativeLayout addVuiAsrView(Context context, WindowManager wm) {
        RelativeLayout vuiAsrView = (RelativeLayout) View.inflate(context, R.layout.view_vui_asr, null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, TYPE_VUI, 8388616, -3);
        Resources res = context.getResources();
        layoutParams.width = -1;
        layoutParams.height = res.getDimensionPixelSize(R.dimen.infoflow_vui_asr_view_height);
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.gravity = 51;
        layoutParams.token = new Binder();
        wm.addView(vuiAsrView, layoutParams);
        return vuiAsrView;
    }

    public static void addSecondaryNavigationBar(WindowManager wm, View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(160, 1090, 2060, 8388712, -3);
        lp.setTitle("SecondaryNavigationBar");
        lp.gravity = 80;
        lp.windowAnimations = 0;
        wm.addView(view, lp);
    }
}
