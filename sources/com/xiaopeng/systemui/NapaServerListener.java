package com.xiaopeng.systemui;

import android.content.pm.IPackageDeleteObserver;
import android.graphics.Bitmap;
import android.text.TextUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.JSONLexer;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.android.systemui.SystemUIApplication;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.aar.server.ServerListener;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.jarvisproto.SoundAreaStatus;
import com.xiaopeng.speech.protocol.node.context.ContextNode;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.AudioController;
import com.xiaopeng.systemui.controller.BluetoothController;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.systemui.helper.BitmapHelper;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.IInfoflowPresenter;
import com.xiaopeng.systemui.infoflow.LandscapeInfoFlow;
import com.xiaopeng.systemui.infoflow.checking.CarCheckHelper;
import com.xiaopeng.systemui.infoflow.manager.MediaManager;
import com.xiaopeng.systemui.infoflow.message.helper.MusicResourcesHelper;
import com.xiaopeng.systemui.infoflow.message.presenter.CallCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.MusicCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.SecondaryMusicCardPresenter;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.speech.SpeechPresenter;
import com.xiaopeng.systemui.infoflow.speech.core.speech.model.ContextModel;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.ImageUtil;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.navigationbar.INavigationBarPresenter;
import com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView;
import com.xiaopeng.systemui.statusbar.IStatusbarPresenter;
import com.xiaopeng.systemui.utils.BIHelper;
import com.xiaopeng.systemui.utils.SystemUIMediatorUtil;
import com.xiaopeng.systemui.utils.Utils;
import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
import com.xiaopeng.xuimanager.mediacenter.lyric.LyricInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kotlin.text.Typography;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class NapaServerListener implements ServerListener {
    private static final String TAG = "NapaServerListener";
    private AppDownloadPresenter mAppDownloadPresenter;
    private AudioController mAudioController;
    private CallCardPresenter mCallCardPresenter;
    private IInfoflowPresenter mInfoflowPresenter;
    private MusicCardPresenter mMusicCardPresenter;
    private INavigationBarPresenter mNavigationBarPresenter;
    private IOsdPresenter mOsdPresenter;
    private PackageDeleteObserver mPackageDeleteObserver = new PackageDeleteObserver();
    private PushCardPresenter mPushCardPresenter;
    private SecondaryMusicCardPresenter mSecondaryMusicCardPresenter;
    private INavigationBarPresenter mSecondaryNavigationBarPresenter;
    private ISecondaryWindowView mSecondaryWindowView;
    private SpeechPresenter mSpeechPresenter;
    private IStatusbarPresenter mStatusbarPresenter;

    /* loaded from: classes24.dex */
    public class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
        public PackageDeleteObserver() {
        }

        public void packageDeleted(String packageName, int returnCode) {
            Logger.d(NapaServerListener.TAG, "packageDeleted : pkg = " + packageName + " returnCode = " + returnCode);
            ViewFactory.getSecondaryWindowView().notifyUninstallResult(packageName, returnCode);
        }
    }

    public NapaServerListener() {
        BluetoothController.getInstance();
        this.mNavigationBarPresenter = PresenterCenter.getInstance().getNavigationBarPresenter(0);
        this.mSecondaryNavigationBarPresenter = PresenterCenter.getInstance().getNavigationBarPresenter(1);
        this.mStatusbarPresenter = PresenterCenter.getInstance().getStatusbarPresenter();
        this.mInfoflowPresenter = PresenterCenter.getInstance().getInfoFlow();
        this.mAudioController = AudioController.getInstance(SystemUIApplication.getContext());
        this.mAppDownloadPresenter = AppDownloadPresenter.getInstance();
        this.mOsdPresenter = OsdPresenter.getInstance();
        this.mSpeechPresenter = SpeechPresenter.getInstance();
        this.mCallCardPresenter = CallCardPresenter.getInstance();
        this.mMusicCardPresenter = MusicCardPresenter.getInstance();
        this.mSecondaryMusicCardPresenter = SecondaryMusicCardPresenter.getInstance();
        this.mPushCardPresenter = PushCardPresenter.getInstance();
        if (CarModelsManager.getFeature().isSecondaryWindowSupport()) {
            this.mSecondaryWindowView = ViewFactory.getSecondaryWindowView();
        }
    }

    private boolean ignoreSplit(String method) {
        if ("sendBIData".equals(method)) {
            return true;
        }
        return false;
    }

    @Override // com.xiaopeng.aar.server.ServerListener
    public String onCall(String module, String method, String param, byte[] blob) {
        String[] args = null;
        if (ignoreSplit(method)) {
            try {
                return execute(method, new String[]{param});
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        if (!TextUtils.isEmpty(param)) {
            args = param.split(",");
        }
        try {
            return execute(method, args);
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private String execute(String method, String[] args) {
        char c;
        switch (method.hashCode()) {
            case -2122902463:
                if (method.equals("exitOOBE")) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case -2090303566:
                if (method.equals("setTemperature")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -2085674434:
                if (method.equals("onDiagnosticModeClicked")) {
                    c = 'W';
                    break;
                }
                c = 65535;
                break;
            case -2023135159:
                if (method.equals("getAvailableDrivingDistance")) {
                    c = 'S';
                    break;
                }
                c = 65535;
                break;
            case -2021979317:
                if (method.equals("startXpMusic")) {
                    c = 'Q';
                    break;
                }
                c = 65535;
                break;
            case -1958842583:
                if (method.equals("getSrsIconStatus")) {
                    c = 'k';
                    break;
                }
                c = 65535;
                break;
            case -1953122728:
                if (method.equals("startPsnApp")) {
                    c = 'N';
                    break;
                }
                c = 65535;
                break;
            case -1934106161:
                if (method.equals("isUpgrading")) {
                    c = '*';
                    break;
                }
                c = 65535;
                break;
            case -1899971856:
                if (method.equals("setInfoflowStatus")) {
                    c = 'h';
                    break;
                }
                c = 65535;
                break;
            case -1858710861:
                if (method.equals("isDiagnosticModeOn")) {
                    c = 'V';
                    break;
                }
                c = 65535;
                break;
            case -1838883771:
                if (method.equals("onDockHvacClicked")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1789171734:
                if (method.equals("stopDialog")) {
                    c = '5';
                    break;
                }
                c = 65535;
                break;
            case -1765222889:
                if (method.equals("sendSelectedEvent")) {
                    c = 'U';
                    break;
                }
                c = 65535;
                break;
            case -1755225444:
                if (method.equals("onDefrostBackClicked")) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case -1679670739:
                if (method.equals("isMicrophoneMute")) {
                    c = '#';
                    break;
                }
                c = 65535;
                break;
            case -1660546096:
                if (method.equals("onTemperatureDownClicked")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1630461190:
                if (method.equals("getWirelessChargeStatus")) {
                    c = Typography.dollar;
                    break;
                }
                c = 65535;
                break;
            case -1618177826:
                if (method.equals("onMusicCardPrevClicked")) {
                    c = 'B';
                    break;
                }
                c = 65535;
                break;
            case -1559166048:
                if (method.equals("getIfSrsOn")) {
                    c = '3';
                    break;
                }
                c = 65535;
                break;
            case -1552589327:
                if (method.equals("isBackDefrostOn")) {
                    c = 29;
                    break;
                }
                c = 65535;
                break;
            case -1525761806:
                if (method.equals("onTemperatureProgressChanged")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1525384283:
                if (method.equals("onMicrophoneMuteClicked")) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case -1461434594:
                if (method.equals("onNotTipDownClicked")) {
                    c = 'H';
                    break;
                }
                c = 65535;
                break;
            case -1454000567:
                if (method.equals("onDashCamClicked")) {
                    c = 'f';
                    break;
                }
                c = 65535;
                break;
            case -1384737288:
                if (method.equals("getSignalType")) {
                    c = '+';
                    break;
                }
                c = 65535;
                break;
            case -1369011948:
                if (method.equals("isBatteryCharging")) {
                    c = Typography.quote;
                    break;
                }
                c = 65535;
                break;
            case -1189591694:
                if (method.equals("onActionClicked")) {
                    c = Typography.less;
                    break;
                }
                c = 65535;
                break;
            case -1166233594:
                if (method.equals("notifyUIReady")) {
                    c = '_';
                    break;
                }
                c = 65535;
                break;
            case -1131426828:
                if (method.equals("getHvacInfo")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -1084322851:
                if (method.equals("setMusicVolume")) {
                    c = '9';
                    break;
                }
                c = 65535;
                break;
            case -1080637671:
                if (method.equals("isRepairModeOn")) {
                    c = '/';
                    break;
                }
                c = 65535;
                break;
            case -1015707127:
                if (method.equals("isCenterLocked")) {
                    c = 27;
                    break;
                }
                c = 65535;
                break;
            case -974000065:
                if (method.equals("getInnerQuality")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -910387557:
                if (method.equals("onNotTip7DayClicked")) {
                    c = 'J';
                    break;
                }
                c = 65535;
                break;
            case -858196741:
                if (method.equals("enterOOBE")) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case -825657536:
                if (method.equals("onDriverClicked")) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case -803378408:
                if (method.equals("onRepairModeClicked")) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case -760354511:
                if (method.equals("getBackDefrostIconStatus")) {
                    c = 'i';
                    break;
                }
                c = 65535;
                break;
            case -723835099:
                if (method.equals("sendScrollEvent")) {
                    c = 'T';
                    break;
                }
                c = 65535;
                break;
            case -669642395:
                if (method.equals("hasPackage1")) {
                    c = 'X';
                    break;
                }
                c = 65535;
                break;
            case -649062486:
                if (method.equals("getCurrentLyricInfo")) {
                    c = 'G';
                    break;
                }
                c = 65535;
                break;
            case -625596190:
                if (method.equals("uninstall")) {
                    c = 'P';
                    break;
                }
                c = 65535;
                break;
            case -425494656:
                if (method.equals("hasUsbDevice")) {
                    c = '\'';
                    break;
                }
                c = 65535;
                break;
            case -403438378:
                if (method.equals("getSecondScreenAppsInfo")) {
                    c = 'm';
                    break;
                }
                c = 65535;
                break;
            case -382023045:
                if (method.equals("onMusicCardPlayPauseClicked")) {
                    c = 'A';
                    break;
                }
                c = 65535;
                break;
            case -371626519:
                if (method.equals("getCurrentPlayStatus")) {
                    c = '\\';
                    break;
                }
                c = 65535;
                break;
            case -318567362:
                if (method.equals("getTemperature")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -305770636:
                if (method.equals("isAuthModeOn")) {
                    c = '0';
                    break;
                }
                c = 65535;
                break;
            case -173431838:
                if (method.equals("onDefrostFrontClicked")) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case -138576115:
                if (method.equals("onStatusBarIconClick")) {
                    c = 'l';
                    break;
                }
                c = 65535;
                break;
            case -82096147:
                if (method.equals("getBatteryLevel")) {
                    c = ' ';
                    break;
                }
                c = 65535;
                break;
            case -81221118:
                if (method.equals("onNavigationButtonClicked")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -78029959:
                if (method.equals("getBluetoothState")) {
                    c = 30;
                    break;
                }
                c = 65535;
                break;
            case -75204358:
                if (method.equals("getBatteryState")) {
                    c = '!';
                    break;
                }
                c = 65535;
                break;
            case -60270141:
                if (method.equals("onECallClicked")) {
                    c = '`';
                    break;
                }
                c = 65535;
                break;
            case -42796519:
                if (method.equals("sendBIData")) {
                    c = 'c';
                    break;
                }
                c = 65535;
                break;
            case -8699951:
                if (method.equals("getNewInstalledApp")) {
                    c = 'b';
                    break;
                }
                c = 65535;
                break;
            case 7308610:
                if (method.equals("hasDownload")) {
                    c = '(';
                    break;
                }
                c = 65535;
                break;
            case 14835319:
                if (method.equals("onTemperatureUpClicked")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 14838918:
                if (method.equals("getSignalLevel")) {
                    c = ',';
                    break;
                }
                c = 65535;
                break;
            case 105607468:
                if (method.equals("cancelSpeechCard")) {
                    c = '[';
                    break;
                }
                c = 65535;
                break;
            case 118743322:
                if (method.equals("getTimeFormat")) {
                    c = Typography.amp;
                    break;
                }
                c = 65535;
                break;
            case 148901958:
                if (method.equals("getFRWirelessChargeStatus")) {
                    c = '%';
                    break;
                }
                c = 65535;
                break;
            case 185382595:
                if (method.equals("onIHBClicked")) {
                    c = 'g';
                    break;
                }
                c = 65535;
                break;
            case 316577183:
                if (method.equals("cancelDownloadApp")) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case 327209019:
                if (method.equals("getMusicVolumeMax")) {
                    c = '8';
                    break;
                }
                c = 65535;
                break;
            case 352242576:
                if (method.equals("getDownloadStatus")) {
                    c = ')';
                    break;
                }
                c = 65535;
                break;
            case 395313091:
                if (method.equals("isFrontDefrostOn")) {
                    c = 28;
                    break;
                }
                c = 65535;
                break;
            case 521902323:
                if (method.equals("onMusicCardCollectClicked")) {
                    c = 'D';
                    break;
                }
                c = 65535;
                break;
            case 538192692:
                if (method.equals("onCallActionClicked")) {
                    c = '@';
                    break;
                }
                c = 65535;
                break;
            case 661638204:
                if (method.equals("isECallEnable")) {
                    c = '^';
                    break;
                }
                c = 65535;
                break;
            case 670514716:
                if (method.equals("setVolume")) {
                    c = '6';
                    break;
                }
                c = 65535;
                break;
            case 687413353:
                if (method.equals("getMusicVolume")) {
                    c = '7';
                    break;
                }
                c = 65535;
                break;
            case 712860533:
                if (method.equals("getDisableMode")) {
                    c = '1';
                    break;
                }
                c = 65535;
                break;
            case 821679389:
                if (method.equals("onLockClicked")) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 837550333:
                if (method.equals("isDriverSeatActive")) {
                    c = 25;
                    break;
                }
                c = 65535;
                break;
            case 906197110:
                if (method.equals("onCallSwitchClicked")) {
                    c = '?';
                    break;
                }
                c = 65535;
                break;
            case 922769618:
                if (method.equals("exitCarCheck")) {
                    c = 'M';
                    break;
                }
                c = 65535;
                break;
            case 937768313:
                if (method.equals("getDashCamStatus")) {
                    c = 'e';
                    break;
                }
                c = 65535;
                break;
            case 969808407:
                if (method.equals("getDriverAvatar")) {
                    c = JSONLexer.EOI;
                    break;
                }
                c = 65535;
                break;
            case 972860433:
                if (method.equals("getModeEntriesType")) {
                    c = ']';
                    break;
                }
                c = 65535;
                break;
            case 973500070:
                if (method.equals("getSeatIconStatus")) {
                    c = 'j';
                    break;
                }
                c = 65535;
                break;
            case 978035875:
                if (method.equals("isAppInstalled")) {
                    c = 'R';
                    break;
                }
                c = 65535;
                break;
            case 1046583871:
                if (method.equals("getIHBStatus")) {
                    c = 'd';
                    break;
                }
                c = 65535;
                break;
            case 1062883358:
                if (method.equals("onMusicCardNextClicked")) {
                    c = 'C';
                    break;
                }
                c = 65535;
                break;
            case 1065113763:
                if (method.equals("pauseDownloadApp")) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case 1111941151:
                if (method.equals("getAppDownloadInfoList")) {
                    c = 24;
                    break;
                }
                c = 65535;
                break;
            case 1115183914:
                if (method.equals("closeSpeechCard")) {
                    c = 'Z';
                    break;
                }
                c = 65535;
                break;
            case 1135091652:
                if (method.equals("onHvacSynchronizedClicked")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1150044334:
                if (method.equals("onVolumeProgressChanged")) {
                    c = 'E';
                    break;
                }
                c = 65535;
                break;
            case 1221035062:
                if (method.equals("onNotTipCloseClicked")) {
                    c = 'I';
                    break;
                }
                c = 65535;
                break;
            case 1316768351:
                if (method.equals("startApp")) {
                    c = 'O';
                    break;
                }
                c = 65535;
                break;
            case 1373606585:
                if (method.equals("getWifiLevel")) {
                    c = '.';
                    break;
                }
                c = 65535;
                break;
            case 1393483544:
                if (method.equals("getSoundAreaStatus")) {
                    c = 'a';
                    break;
                }
                c = 65535;
                break;
            case 1424727956:
                if (method.equals("isChildModeOn")) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case 1453908573:
                if (method.equals("onAuthModeClicked")) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 1482731150:
                if (method.equals("onCloseClicked")) {
                    c = ';';
                    break;
                }
                c = 65535;
                break;
            case 1545248582:
                if (method.equals("getIfSupportSeatHeatVent")) {
                    c = '2';
                    break;
                }
                c = 65535;
                break;
            case 1599886712:
                if (method.equals("getPsnBluetoothState")) {
                    c = 31;
                    break;
                }
                c = 65535;
                break;
            case 1647239786:
                if (method.equals("isWifiConnected")) {
                    c = '-';
                    break;
                }
                c = 65535;
                break;
            case 1799985327:
                if (method.equals("getCurrentMediaInfo")) {
                    c = 'F';
                    break;
                }
                c = 65535;
                break;
            case 1862454786:
                if (method.equals("getCfcVehicleLevel")) {
                    c = 'Y';
                    break;
                }
                c = 65535;
                break;
            case 1863106231:
                if (method.equals("startDownloadApp")) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case 1867121698:
                if (method.equals("onCallAcceptClicked")) {
                    c = '=';
                    break;
                }
                c = 65535;
                break;
            case 1908777880:
                if (method.equals("onCardClicked")) {
                    c = AccessibilityUtils.ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR;
                    break;
                }
                c = 65535;
                break;
            case 2038329568:
                if (method.equals("onEnergyClicked")) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 2041835261:
                if (method.equals("onCallHangupClicked")) {
                    c = Typography.greater;
                    break;
                }
                c = 65535;
                break;
            case 2069419332:
                if (method.equals("getCardList")) {
                    c = '4';
                    break;
                }
                c = 65535;
                break;
            case 2117391008:
                if (method.equals("onNotTipClicked")) {
                    c = 'L';
                    break;
                }
                c = 65535;
                break;
            case 2146885925:
                if (method.equals("onNotTipAllDayClicked")) {
                    c = 'K';
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                onDockHvacClicked();
                break;
            case 1:
                setTemperature(Integer.parseInt(args[0]), Float.parseFloat(args[1]));
                break;
            case 2:
                onTemperatureDownClicked(Integer.parseInt(args[0]));
                break;
            case 3:
                onTemperatureUpClicked(Integer.parseInt(args[0]));
                break;
            case 4:
                onTemperatureProgressChanged(Integer.parseInt(args[0]), Float.parseFloat(args[1]), Boolean.parseBoolean(args[2]));
                break;
            case 5:
                onHvacSynchronizedClicked();
                break;
            case 6:
                onNavigationButtonClicked(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
                break;
            case 7:
                return getHvacInfo();
            case '\b':
                return String.valueOf(getTemperature(Integer.parseInt(args[0])));
            case '\t':
                return String.valueOf(getInnerQuality());
            case '\n':
                onLockClicked();
                break;
            case 11:
                onEnergyClicked();
                break;
            case '\f':
                onDriverClicked();
                break;
            case '\r':
                onDefrostFrontClicked();
                break;
            case 14:
                onDefrostBackClicked();
                break;
            case 15:
                onRepairModeClicked();
                break;
            case 16:
                onAuthModeClicked();
                break;
            case 17:
                return String.valueOf(isChildModeOn());
            case 18:
                onMicrophoneMuteClicked();
                break;
            case 19:
                enterOOBE();
                break;
            case 20:
                exitOOBE();
                break;
            case 21:
                return String.valueOf(startDownloadApp(args[0], args[1]));
            case 22:
                pauseDownloadApp(args[0]);
                break;
            case 23:
                cancelDownloadApp(args[0]);
                break;
            case 24:
                return getAppDownloadInfoList();
            case 25:
                return String.valueOf(isDriverSeatActive());
            case 26:
                return getDriverAvatar();
            case 27:
                return String.valueOf(isCenterLocked());
            case 28:
                return String.valueOf(isFrontDefrostOn());
            case 29:
                return String.valueOf(isBackDefrostOn());
            case 30:
                return String.valueOf(getBluetoothState());
            case 31:
                return String.valueOf(getPsnBluetoothState());
            case ' ':
                return String.valueOf(getBatteryLevel());
            case '!':
                return String.valueOf(getBatteryState());
            case '\"':
                return String.valueOf(isBatteryCharging());
            case '#':
                return String.valueOf(isMicrophoneMute());
            case '$':
                return String.valueOf(getWirelessChargeStatus());
            case '%':
                return String.valueOf(getFRWirelessChargeStatus());
            case '&':
                return String.valueOf(getTimeFormat());
            case '\'':
                return String.valueOf(hasUsbDevice());
            case '(':
                return String.valueOf(hasDownload());
            case ')':
                return String.valueOf(getDownloadStatus());
            case '*':
                return String.valueOf(isUpgrading());
            case '+':
                return String.valueOf(getSignalType());
            case ',':
                return String.valueOf(getSignalLevel());
            case '-':
                return String.valueOf(isWifiConnected());
            case '.':
                return String.valueOf(getWifiLevel());
            case '/':
                return String.valueOf(isRepairModeOn());
            case '0':
                return String.valueOf(isAuthModeOn());
            case '1':
                return String.valueOf(getDisableMode());
            case '2':
                return String.valueOf(getIfSupportSeatHeatVent());
            case '3':
                return String.valueOf(getIfSrsOn());
            case '4':
                return getCardList();
            case '5':
                stopDialog();
                break;
            case '6':
                setVolume(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
                break;
            case '7':
                return String.valueOf(getMusicVolume(Integer.parseInt(args[0])));
            case '8':
                return String.valueOf(getMusicVolumeMax(Integer.parseInt(args[0])));
            case '9':
                setMusicVolume(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
                break;
            case ':':
                onCardClicked(Integer.parseInt(args[0]));
                break;
            case ';':
                onCloseClicked(Integer.parseInt(args[0]));
                break;
            case '<':
                onActionClicked(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
                break;
            case '=':
                onCallAcceptClicked();
                break;
            case '>':
                onCallHangupClicked();
                break;
            case '?':
                onCallSwitchClicked();
                break;
            case '@':
                onCallActionClicked();
                break;
            case 'A':
                onMusicCardPlayPauseClicked(Integer.parseInt(args[0]));
                break;
            case 'B':
                onMusicCardPrevClicked(Integer.parseInt(args[0]));
                break;
            case 'C':
                onMusicCardNextClicked(Integer.parseInt(args[0]));
                break;
            case 'D':
                onMusicCardCollectClicked(Integer.parseInt(args[0]));
                break;
            case 'E':
                onVolumeProgressChanged(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
                break;
            case 'F':
                return getCurrentMediaInfo(Integer.parseInt(args[0]));
            case 'G':
                return getCurrentLyricInfo(Integer.parseInt(args[0]));
            case 'H':
                onNotTipDownClicked();
                break;
            case 'I':
                onNotTipCloseClicked();
                break;
            case 'J':
                onNotTip7DayClicked();
                break;
            case 'K':
                onNotTipAllDayClicked();
                break;
            case 'L':
                onNotTipClicked();
                break;
            case 'M':
                exitCarCheck();
                break;
            case 'N':
                startPsnApp(args[0]);
                break;
            case 'O':
                startApp(Integer.parseInt(args[0]), args[1]);
                break;
            case 'P':
                uninstall(args[0]);
                break;
            case 'Q':
                startXpMusic(Integer.parseInt(args[0]));
                break;
            case 'R':
                return String.valueOf(isAppInstalled(args[0]));
            case 'S':
                return String.valueOf(getAvailableDrivingDistance());
            case 'T':
                sendScrollEvent(Integer.parseInt(args[0]));
                break;
            case 'U':
                sendSelectedEvent(Integer.parseInt(args[0]));
                break;
            case 'V':
                return String.valueOf(isDiagnosticModeOn());
            case 'W':
                onDiagnosticModeClicked();
                break;
            case 'X':
                return String.valueOf(hasPackage1());
            case 'Y':
                return String.valueOf(getCfcVehicleLevel());
            case 'Z':
                closeSpeechCard();
                break;
            case '[':
                cancelSpeechCard();
                break;
            case '\\':
                return String.valueOf(getCurrentPlayStatus(Integer.parseInt(args[0])));
            case ']':
                return String.valueOf(getModeEntriesType());
            case '^':
                return String.valueOf(isECallEnable());
            case '_':
                CarCheckHelper.notifyUIReady();
                break;
            case '`':
                onECallClicked();
                break;
            case 'a':
                return getSoundAreaStatus();
            case 'b':
                return getNewInstalledApp();
            case 'c':
                sendBIData(args[0]);
                break;
            case 'd':
                return getIHBStatus();
            case 'e':
                return getDashCamStatus();
            case 'f':
                onDashCamClicked();
                break;
            case 'g':
                onIHBClicked();
                break;
            case 'h':
                setInfoflowStatus(Integer.parseInt(args[0]));
                break;
            case 'i':
                return String.valueOf(getBackDefrostIconStatus());
            case 'j':
                return String.valueOf(getSeatIconStatus());
            case 'k':
                return String.valueOf(getSrsIconStatus());
            case 'l':
                onStatusBarIconClick(args[0]);
                break;
            case 'm':
                return this.mSecondaryWindowView.getSecondScreenAppsInfo();
            default:
                return null;
        }
        return null;
    }

    private void onStatusBarIconClick(String key) {
        IStatusbarPresenter iStatusbarPresenter = this.mStatusbarPresenter;
        if (iStatusbarPresenter != null) {
            iStatusbarPresenter.onStatusBarIconClick(key);
        }
    }

    private int getModeEntriesType() {
        if (CarModelsManager.getConfig().isMakeupMirrorSupport() && CarModelsManager.getConfig().isDolbySupport() && CarModelsManager.getConfig().isAmpSupport()) {
            return 1;
        }
        return 0;
    }

    @Override // com.xiaopeng.aar.server.ServerListener
    public void onSubscribe(String module) {
    }

    @Override // com.xiaopeng.aar.server.ServerListener
    public void onUnSubscribe(String module) {
    }

    public void onDockHvacClicked() {
        this.mNavigationBarPresenter.onDockHvacClicked();
    }

    public void setTemperature(int type, float temperature) {
        this.mNavigationBarPresenter.setTemperature(type, temperature);
    }

    public void onTemperatureDownClicked(int type) {
        this.mNavigationBarPresenter.onTemperatureDownClicked(type);
    }

    public void onTemperatureUpClicked(int type) {
        this.mNavigationBarPresenter.onTemperatureUpClicked(type);
    }

    public void onTemperatureProgressChanged(int type, float temperature, boolean fromUser) {
        this.mNavigationBarPresenter.onTemperatureProgressChanged(type, temperature, fromUser);
    }

    public void onHvacSynchronizedClicked() {
        this.mNavigationBarPresenter.onHvacSynchronizedClicked();
    }

    public void onNavigationButtonClicked(int displayId, int btnIndex) {
        if (displayId == 0) {
            this.mNavigationBarPresenter.onNavigationButtonClicked(btnIndex);
        } else if (displayId == 1) {
            this.mSecondaryNavigationBarPresenter.onNavigationButtonClicked(btnIndex);
        }
        BIHelper.sendBIData(BIHelper.ID.applist, BIHelper.Type.dock, BIHelper.Action.click, displayId == 0 ? BIHelper.Screen.main : BIHelper.Screen.second);
    }

    public String getHvacInfo() {
        return this.mNavigationBarPresenter.getHvacInfo();
    }

    public float getTemperature(int type) {
        Logger.d(TAG, "getTemperature ：" + type + this.mNavigationBarPresenter.getTemperature(type));
        return this.mNavigationBarPresenter.getTemperature(type);
    }

    public int getInnerQuality() {
        return this.mNavigationBarPresenter.getInnerQuality();
    }

    public void onLockClicked() {
        this.mStatusbarPresenter.onLockClicked();
    }

    public void onEnergyClicked() {
        this.mStatusbarPresenter.onEnergyClicked();
    }

    public void onDriverClicked() {
        this.mStatusbarPresenter.onDriverClicked();
    }

    public void onDefrostFrontClicked() {
        this.mStatusbarPresenter.onDefrostFrontClicked();
    }

    public void onDefrostBackClicked() {
        this.mStatusbarPresenter.onDefrostBackClicked();
    }

    public void onRepairModeClicked() {
        this.mStatusbarPresenter.onRepairModeClicked();
    }

    public void onMicrophoneMuteClicked() {
        this.mStatusbarPresenter.onMicrophoneMuteClicked();
    }

    public void onAuthModeClicked() {
        this.mStatusbarPresenter.onAuthModeClicked();
    }

    public boolean isChildModeOn() {
        return this.mStatusbarPresenter.isChildModeOn();
    }

    public void enterOOBE() {
        CarCheckHelper.enterOOBE();
    }

    public void exitOOBE() {
        CarCheckHelper.exitOOBE();
    }

    public boolean startDownloadApp(String pkgName, String label) {
        this.mAppDownloadPresenter.startDownloadApp(pkgName, label);
        return true;
    }

    public void pauseDownloadApp(String pkgName) {
        this.mAppDownloadPresenter.pauseDownloadApp(pkgName);
    }

    public void cancelDownloadApp(String pkgName) {
        this.mAppDownloadPresenter.cancelDownloadApp(pkgName);
    }

    public String getAppDownloadInfoList() {
        return JSONArray.toJSONString(this.mAppDownloadPresenter.getAppDownloadInfoList());
    }

    public boolean isDriverSeatActive() {
        if (CarModelsManager.getFeature().isSimpleAccountIcon()) {
            return false;
        }
        return this.mStatusbarPresenter.isDriverSeatActive();
    }

    public String getDriverAvatar() {
        return this.mStatusbarPresenter.getDriverAvatar();
    }

    public boolean isCenterLocked() {
        return this.mStatusbarPresenter.isCenterLocked();
    }

    public boolean isFrontDefrostOn() {
        return this.mStatusbarPresenter.isFrontDefrostOn();
    }

    public boolean isBackDefrostOn() {
        return this.mStatusbarPresenter.isBackDefrostOn();
    }

    public int getBluetoothState() {
        return this.mStatusbarPresenter.getBluetoothState();
    }

    public int getPsnBluetoothState() {
        return this.mStatusbarPresenter.getPsnBluetoothState();
    }

    public int getBatteryLevel() {
        return this.mStatusbarPresenter.getBatteryLevel();
    }

    public int getBatteryState() {
        return this.mStatusbarPresenter.getBatteryState();
    }

    public boolean isBatteryCharging() {
        return this.mStatusbarPresenter.isBatteryCharging();
    }

    public boolean isMicrophoneMute() {
        return this.mStatusbarPresenter.isMicrophoneMute();
    }

    public int getWirelessChargeStatus() {
        return this.mStatusbarPresenter.getWirelessChargeStatus();
    }

    public int getFRWirelessChargeStatus() {
        return this.mStatusbarPresenter.getFRWirelessChargeStatus();
    }

    public String getTimeFormat() {
        return this.mStatusbarPresenter.getTimeFormat();
    }

    public boolean hasUsbDevice() {
        return this.mStatusbarPresenter.hasUsbDevice();
    }

    public boolean hasDownload() {
        return this.mStatusbarPresenter.hasDownload();
    }

    public int getDownloadStatus() {
        return this.mStatusbarPresenter.getDownloadStatus();
    }

    public boolean isUpgrading() {
        return this.mStatusbarPresenter.isUpgrading();
    }

    public int getSignalType() {
        return this.mStatusbarPresenter.getSignalType();
    }

    public int getSignalLevel() {
        return this.mStatusbarPresenter.getSignalLevel();
    }

    public boolean isWifiConnected() {
        return this.mStatusbarPresenter.isWifiConnected();
    }

    public int getWifiLevel() {
        return this.mStatusbarPresenter.getWifiLevel();
    }

    public boolean isRepairModeOn() {
        return this.mStatusbarPresenter.isRepairModeOn();
    }

    public boolean isAuthModeOn() {
        return this.mStatusbarPresenter.isAuthModeOn();
    }

    public int getDisableMode() {
        return this.mStatusbarPresenter.getDisableMode();
    }

    public int getIfSupportSeatHeatVent() {
        return this.mStatusbarPresenter.getIfSupportSeatHeatVent();
    }

    public boolean getIfSrsOn() {
        return this.mStatusbarPresenter.getIfSrsOn();
    }

    public String getCardList() {
        LandscapeInfoFlow landscapeSpeechPresenter = (LandscapeInfoFlow) this.mInfoflowPresenter;
        return landscapeSpeechPresenter.getCardList();
    }

    public void stopDialog() {
        LandscapeInfoFlow landscapeSpeechPresenter = (LandscapeInfoFlow) this.mInfoflowPresenter;
        landscapeSpeechPresenter.stopDialog();
    }

    public void setVolume(int streamType, int volume) {
        this.mOsdPresenter.setVolume(streamType, volume);
    }

    public int getMusicVolume(int displayId) {
        return this.mAudioController.getMusicVolume(displayId);
    }

    public int getMusicVolumeMax(int displayId) {
        return this.mAudioController.getMusicVolumeMax(displayId);
    }

    public void setMusicVolume(int displayId, int volume) {
        this.mAudioController.setMusicVolume(displayId, volume);
    }

    public void onCardClicked(final int cardType) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.NapaServerListener.1
            @Override // java.lang.Runnable
            public void run() {
                IInfoflowCardPresenter infoflowCardPresenter = PresenterCenter.getInstance().getCardPresenter(cardType);
                if (infoflowCardPresenter != null) {
                    infoflowCardPresenter.onCardClicked();
                }
            }
        });
    }

    public void onCloseClicked(final int cardType) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.NapaServerListener.2
            @Override // java.lang.Runnable
            public void run() {
                IInfoflowCardPresenter infoflowCardPresenter = PresenterCenter.getInstance().getCardPresenter(cardType);
                if (infoflowCardPresenter != null) {
                    infoflowCardPresenter.onCardCloseClicked();
                }
            }
        });
    }

    public void onActionClicked(final int cardType, final int actionIndex) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.NapaServerListener.3
            @Override // java.lang.Runnable
            public void run() {
                try {
                    IInfoflowCardPresenter infoflowCardPresenter = PresenterCenter.getInstance().getCardPresenter(cardType);
                    if (infoflowCardPresenter != null) {
                        infoflowCardPresenter.onActionClicked(actionIndex);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onCallAcceptClicked() {
        this.mCallCardPresenter.onCallAcceptClicked();
    }

    public void onCallHangupClicked() {
        this.mCallCardPresenter.onCallHangupClicked();
    }

    public void onCallSwitchClicked() {
        this.mCallCardPresenter.onCallSwitchClicked();
    }

    public void onCallActionClicked() {
        this.mCallCardPresenter.onCallActionClicked();
    }

    public void onMusicCardPlayPauseClicked(int displayId) {
        if (displayId == 0) {
            this.mMusicCardPresenter.onMusicCardPlayPauseClicked();
        } else if (displayId == 1) {
            this.mSecondaryMusicCardPresenter.onMusicCardPlayPauseClicked();
        }
    }

    public void onMusicCardPrevClicked(int displayId) {
        if (displayId == 0) {
            this.mMusicCardPresenter.onMusicCardPrevClicked();
        } else if (displayId == 1) {
            this.mSecondaryMusicCardPresenter.onMusicCardPrevClicked();
        }
    }

    public void onMusicCardNextClicked(int displayId) {
        if (displayId == 0) {
            this.mMusicCardPresenter.onMusicCardNextClicked();
        } else if (displayId == 1) {
            this.mSecondaryMusicCardPresenter.onMusicCardNextClicked();
        }
    }

    public void onMusicCardCollectClicked(int displayId) {
        if (displayId == 0) {
            this.mMusicCardPresenter.onMusicCardCollectClicked();
        } else if (displayId == 1) {
            this.mSecondaryMusicCardPresenter.onMusicCardCollectClicked();
        }
    }

    public void onVolumeProgressChanged(int displayId, int progress) {
        this.mAudioController.setMusicVolume(displayId, progress);
    }

    public String getCurrentMediaInfo(final int displayId) {
        ThreadUtils.executeSingleThread(new Runnable() { // from class: com.xiaopeng.systemui.NapaServerListener.4
            @Override // java.lang.Runnable
            public void run() {
                MediaInfo mediaInfo = NapaServerListener.this.mMusicCardPresenter.getCurrentMediaInfo(displayId);
                if (mediaInfo != null) {
                    Logger.d(NapaServerListener.TAG, "setMusicCardMediaInfo : " + displayId + " title = " + mediaInfo.getTitle() + " packageName = " + mediaInfo.getPackageName());
                    Map<String, Object> map = new HashMap<>();
                    map.put("displayId", Integer.valueOf(displayId));
                    Bitmap album = mediaInfo.getAlbumBitmap();
                    try {
                        JSONObject jsonObject = new JSONObject(GsonUtil.toJson(mediaInfo));
                        String isDefaultString = mediaInfo.getString("isDefault");
                        if (!TextUtils.isEmpty(isDefaultString)) {
                            Logger.d(NapaServerListener.TAG, "isDefaultString:" + isDefaultString);
                            jsonObject.put("isDefault", Boolean.parseBoolean(isDefaultString));
                        }
                        String startPosition = mediaInfo.getString("startPosition");
                        if (!TextUtils.isEmpty(startPosition)) {
                            Logger.d(NapaServerListener.TAG, "startPosition:" + startPosition);
                            int startTimeInMs = Integer.valueOf(startPosition).intValue();
                            int startTimeInS = (startTimeInMs + 999) / 1000;
                            jsonObject.put("startPosition", startTimeInS);
                        }
                        String pkgName = mediaInfo.getPackageName();
                        if (!TextUtils.isEmpty(pkgName)) {
                            jsonObject.put("appIcon", BitmapHelper.getAppIconByPackage(mediaInfo.getPackageName()));
                        } else {
                            Bitmap bitmap = MusicResourcesHelper.getDefaultMusicLogo();
                            jsonObject.put("appIcon", ImageUtil.getBase64String(bitmap));
                        }
                        map.put("mediaInfo", jsonObject);
                        byte[] blob = null;
                        if (album != null) {
                            jsonObject.put("albumWidth", album.getWidth());
                            jsonObject.put("albumHeight", album.getHeight());
                            blob = ImageUtil.getByteArray(album);
                        }
                        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setMusicCardMediaInfo", map, blob);
                    } catch (JSONException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
        return null;
    }

    public String getCurrentLyricInfo(int displayId) {
        LyricInfo lyricInfo = this.mMusicCardPresenter.getCurrentLyricInfo(displayId);
        if (lyricInfo == null) {
            return null;
        }
        String strLyricInfo = GsonUtil.toJson(lyricInfo);
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(strLyricInfo);
        return jsonObject.toJSONString();
    }

    public void onNotTipDownClicked() {
        this.mPushCardPresenter.onNotTipDownClicked();
    }

    public void onNotTipCloseClicked() {
        this.mPushCardPresenter.onNotTipCloseClicked();
    }

    public void onNotTip7DayClicked() {
        this.mPushCardPresenter.onNotTip7DayClicked();
    }

    public void onNotTipAllDayClicked() {
        this.mPushCardPresenter.onNotTipAllDayClicked();
    }

    public void onNotTipClicked() {
        this.mPushCardPresenter.onNotTipClicked();
    }

    public void exitCarCheck() {
        LandscapeInfoFlow landscapeSpeechPresenter = (LandscapeInfoFlow) PresenterCenter.getInstance().getInfoFlow();
        landscapeSpeechPresenter.exitCarCheck();
    }

    public void startPsnApp(String pkgName) {
        Logger.d(TAG, "startPsnApp : " + pkgName);
        PackageHelper.startActivityInSecondaryWindow(ContextUtils.getContext(), pkgName);
    }

    public void startApp(int displayId, String pkgName) {
        Logger.d(TAG, "startApp : displayId = " + displayId + " pkgName = " + pkgName);
        if (displayId == 1) {
            PackageHelper.startActivityInSecondaryWindow(ContextUtils.getContext(), pkgName);
        } else {
            PackageHelper.startApplicationWithPackageName(ContextUtils.getContext(), pkgName);
        }
    }

    public void uninstall(String pkgName) {
        PackageHelper.getInstance().uninstall(pkgName, this.mPackageDeleteObserver);
    }

    public void startXpMusic(int screenId) {
        PackageHelper.startCarMusic(ContextUtils.getContext(), screenId);
    }

    public boolean isAppInstalled(String pkgName) {
        return PackageHelper.isAppInstalled(ContextUtils.getContext(), pkgName);
    }

    public float getAvailableDrivingDistance() {
        float carAvailableMileage = CarController.getInstance(ContextUtils.getContext()).getCarServiceAdapter().getDriveDistance();
        return carAvailableMileage;
    }

    public void sendScrollEvent(int firstVisibleItemPosition) {
        SpeechListView.sendScrollEvent(firstVisibleItemPosition);
    }

    public void sendSelectedEvent(int position) {
        SpeechListView.sendSelectedEvent(position);
    }

    public boolean isDiagnosticModeOn() {
        return this.mStatusbarPresenter.isDiagnosticModeOn();
    }

    public boolean isECallEnable() {
        return this.mStatusbarPresenter.isECallEnable();
    }

    private void onECallClicked() {
        this.mStatusbarPresenter.onECallClicked();
    }

    public void onDiagnosticModeClicked() {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.NapaServerListener.5
            @Override // java.lang.Runnable
            public void run() {
                NapaServerListener.this.mStatusbarPresenter.onDiagnosticModeClicked();
            }
        });
    }

    public boolean hasPackage1() {
        return CarModelsManager.getConfig().hasPackage1();
    }

    public int getCfcVehicleLevel() {
        int level = CarModelsManager.getConfig().getCfcVehicleLevel();
        Logger.d(TAG, "getCfcVehicleLevel : " + level);
        return level;
    }

    public void closeSpeechCard() {
        String widgetId = this.mSpeechPresenter.getWidgetId();
        Logger.d(TAG, "closeSpeechCard : " + widgetId);
        this.mSpeechPresenter.onWidgetCancel(widgetId, ContextModel.CTRL_CARD_CANCEL_WAY_FORCE);
        ((ContextNode) SpeechClient.instance().getNodeManager().getNode(ContextNode.class)).onWidgetCancelByUser(widgetId);
    }

    public void cancelSpeechCard() {
        String widgetId = this.mSpeechPresenter.getWidgetId();
        Logger.d(TAG, "cancelSpeechCard : " + widgetId);
        this.mSpeechPresenter.onWidgetCancel(widgetId, ContextModel.CTRL_CARD_CANCEL_WAY_FORCE);
    }

    public int getCurrentPlayStatus(int displayId) {
        return MediaManager.getInstance().getCurrentPlayStatus(displayId);
    }

    public String getSoundAreaStatus() {
        ArrayList<SoundAreaStatus> statues = this.mSpeechPresenter.getSoundAreaStatus();
        String json = GsonUtil.toJson(statues);
        JSONObject object = new JSONObject();
        try {
            object.put("status", new org.json.JSONArray(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.d(TAG, "getSoundAreaStatus : " + object.toString());
        return object.toString();
    }

    private String getNewInstalledApp() {
        ISecondaryWindowView iSecondaryWindowView = this.mSecondaryWindowView;
        if (iSecondaryWindowView != null) {
            List<String> list = iSecondaryWindowView.getNewInstalledApp();
            String result = Utils.listToString(list, NavigationBarInflaterView.GRAVITY_SEPARATOR);
            Logger.d(TAG, "getNewInstalledApp : " + result);
            return result;
        }
        return null;
    }

    private void sendBIData(String arg) {
        BIHelper.sendBIData(arg);
    }

    private void onIHBClicked() {
        this.mStatusbarPresenter.onIHBClicked();
    }

    private String getIHBStatus() {
        int status = this.mStatusbarPresenter.getIHBStatus();
        return String.valueOf(status);
    }

    private String getDashCamStatus() {
        int status = this.mStatusbarPresenter.getDashCamStatus();
        return String.valueOf(status);
    }

    private void onDashCamClicked() {
        this.mStatusbarPresenter.onDashCamClicked();
    }

    private void setInfoflowStatus(int status) {
        IInfoflowPresenter iInfoflowPresenter = this.mInfoflowPresenter;
        if (iInfoflowPresenter != null) {
            iInfoflowPresenter.setInfoflowStatus(status);
        }
    }

    private int getBackDefrostIconStatus() {
        if (CarModelsManager.getFeature().isShowDefrostBackOnStatusBar()) {
            return 1;
        }
        return 0;
    }

    private int getSeatIconStatus() {
        if (CarModelsManager.getFeature().isSeatVentSupportOnStatusBar() && CarModelsManager.getFeature().isSeatHeatSupportOnStatusBar()) {
            return 2;
        }
        if (CarModelsManager.getFeature().isSeatHeatSupportOnStatusBar()) {
            return 1;
        }
        return 0;
    }

    private int getSrsIconStatus() {
        if (CarModelsManager.getConfig().isSrsSupport()) {
            return 1;
        }
        return 0;
    }
}
