package com.xiaopeng.systemui;

import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.infoflow.IInfoflowView;
import com.xiaopeng.systemui.infoflow.Infoflow3DView;
import com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView;
import com.xiaopeng.systemui.infoflow.VerticalInfoflow2DView;
import com.xiaopeng.systemui.infoflow.speech.Asr2DView;
import com.xiaopeng.systemui.infoflow.speech.Asr3DView;
import com.xiaopeng.systemui.infoflow.speech.IAsrView;
import com.xiaopeng.systemui.navigationbar.INavigationBarView;
import com.xiaopeng.systemui.navigationbar.NavigationBar3DView;
import com.xiaopeng.systemui.navigationbar.PlatformNavigationBar2DView;
import com.xiaopeng.systemui.navigationbar.VerticalNavigationBar2DView;
import com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder;
import com.xiaopeng.systemui.quickmenu.QuickMenu3DView;
import com.xiaopeng.systemui.quickmenu.QuickMenuVerticalViewHolder;
import com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView;
import com.xiaopeng.systemui.secondarywindow.SecondaryVerticalNavigationBar2DView;
import com.xiaopeng.systemui.secondarywindow.SecondaryWindow2DView;
import com.xiaopeng.systemui.secondarywindow.SecondaryWindow3DView;
import com.xiaopeng.systemui.statusbar.IQuickMenuGuideView;
import com.xiaopeng.systemui.statusbar.IStatusbarView;
import com.xiaopeng.systemui.statusbar.QuickMenuGuide2DView;
import com.xiaopeng.systemui.statusbar.QuickMenuGuide3DView;
import com.xiaopeng.systemui.statusbar.Statusbar2DView;
import com.xiaopeng.systemui.statusbar.Statusbar3DView;
import com.xiaopeng.systemui.ui.widget.IOsdView;
import com.xiaopeng.systemui.ui.widget.Osd2DView;
import com.xiaopeng.systemui.ui.widget.Osd2DView2;
/* loaded from: classes24.dex */
public class ViewFactory {
    private static IInfoflowView mLandscapeInfoflowView;
    private static IQuickMenuViewHolder mNapaView;
    private static ISecondaryWindowView mSecondaryWindowView;

    public static IStatusbarView getStatusbarView() {
        if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
            IStatusbarView statusbarView = new Statusbar3DView();
            return statusbarView;
        }
        IStatusbarView statusbarView2 = new Statusbar2DView();
        return statusbarView2;
    }

    public static IQuickMenuGuideView getQuickMenuGuideView() {
        if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
            IQuickMenuGuideView quickMenuGuideView = new QuickMenuGuide3DView();
            return quickMenuGuideView;
        }
        IQuickMenuGuideView quickMenuGuideView2 = new QuickMenuGuide2DView();
        return quickMenuGuideView2;
    }

    public static INavigationBarView getVerticalNavigationBarView() {
        if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
            INavigationBarView navigationBarView = new NavigationBar3DView();
            return navigationBarView;
        }
        INavigationBarView navigationBarView2 = new VerticalNavigationBar2DView();
        return navigationBarView2;
    }

    public static INavigationBarView getSecondaryVerticalNavigationBarView() {
        if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
            INavigationBarView navigationBarView = new NavigationBar3DView();
            return navigationBarView;
        }
        INavigationBarView navigationBarView2 = new SecondaryVerticalNavigationBar2DView();
        return navigationBarView2;
    }

    public static INavigationBarView getPlatformNavigationBarView() {
        if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
            INavigationBarView navigationBarView = new NavigationBar3DView();
            return navigationBarView;
        }
        INavigationBarView navigationBarView2 = new PlatformNavigationBar2DView();
        return navigationBarView2;
    }

    public static IInfoflowView getVerticalInfoflowView() {
        if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
            IInfoflowView infoflowView = new Infoflow3DView();
            return infoflowView;
        }
        IInfoflowView infoflowView2 = new VerticalInfoflow2DView();
        return infoflowView2;
    }

    public static IInfoflowView getLandscapeInfoflowView() {
        IInfoflowView iInfoflowView = mLandscapeInfoflowView;
        if (iInfoflowView != null) {
            return iInfoflowView;
        }
        if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
            mLandscapeInfoflowView = new Infoflow3DView();
        } else {
            mLandscapeInfoflowView = new LandscapeInfoflow2DView();
        }
        return mLandscapeInfoflowView;
    }

    public static IAsrView getAsrView() {
        if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
            IAsrView asrView = new Asr3DView();
            return asrView;
        }
        IAsrView asrView2 = new Asr2DView();
        return asrView2;
    }

    public static IOsdView getOsdView() {
        if (CarModelsManager.getFeature().isOsdReduceSelfUse()) {
            IOsdView osdView = new Osd2DView2();
            return osdView;
        }
        IOsdView osdView2 = new Osd2DView();
        return osdView2;
    }

    public static IQuickMenuViewHolder getNapaView() {
        IQuickMenuViewHolder iQuickMenuViewHolder = mNapaView;
        if (iQuickMenuViewHolder != null) {
            return iQuickMenuViewHolder;
        }
        mNapaView = new QuickMenu3DView();
        return new QuickMenu3DView();
    }

    public static IQuickMenuViewHolder getQuickMenuVerticalViewHolder() {
        IQuickMenuViewHolder quickMenuViewHolder = new QuickMenuVerticalViewHolder();
        return quickMenuViewHolder;
    }

    public static ISecondaryWindowView getSecondaryWindowView() {
        if (mSecondaryWindowView == null) {
            if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
                mSecondaryWindowView = new SecondaryWindow3DView();
            } else {
                mSecondaryWindowView = new SecondaryWindow2DView();
            }
        }
        return mSecondaryWindowView;
    }
}
