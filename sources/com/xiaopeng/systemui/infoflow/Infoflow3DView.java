package com.xiaopeng.systemui.infoflow;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import com.xiaopeng.speech.jarvisproto.SoundAreaStatus;
import com.xiaopeng.speech.protocol.bean.recommend.RecommendBean;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.helper.BitmapHelper;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.bean.CallInfo;
import com.xiaopeng.systemui.infoflow.message.define.CardExtra;
import com.xiaopeng.systemui.infoflow.message.define.CardKey;
import com.xiaopeng.systemui.infoflow.message.helper.MusicResourcesHelper;
import com.xiaopeng.systemui.infoflow.message.presenter.PushBean;
import com.xiaopeng.systemui.infoflow.message.presenter.WeatherBean;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.ImageUtil;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.infoflow.widget.RoundedDrawable;
import com.xiaopeng.systemui.utils.SystemUIMediatorUtil;
import com.xiaopeng.xuimanager.contextinfo.HomeCompanyRouteInfo;
import com.xiaopeng.xuimanager.contextinfo.Lane;
import com.xiaopeng.xuimanager.contextinfo.Maneuver;
import com.xiaopeng.xuimanager.contextinfo.Navi;
import com.xiaopeng.xuimanager.contextinfo.RemainInfo;
import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
import com.xiaopeng.xuimanager.mediacenter.lyric.LyricInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class Infoflow3DView extends LandscapeInfoflow2DView implements IInfoflowView {
    private static final String TAG = "Infoflow3DView";
    private boolean mIsPanelVisible = false;

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void onNavigationItemChanged(String packageName, String className, boolean isCarControlReady) {
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onWidgetListFocus(int index) {
        Map<String, Object> map = new HashMap<>();
        map.put("index", Integer.valueOf(index));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("onWidgetListFocus", map);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onWidgetListSelect(int index) {
        Map<String, Object> map = new HashMap<>();
        map.put("index", Integer.valueOf(index));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("onWidgetListSelect", map);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onBugReportBegin() {
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("onBugReportBegin");
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onBugReportEnd() {
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("onBugReportEnd");
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onWidgetListCancelFocus(int index) {
        Map<String, Object> map = new HashMap<>();
        map.put("index", Integer.valueOf(index));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("onWidgetListCancelFocus", map);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onWidgetListExpand() {
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("onWidgetListExpand");
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onWidgetListFold() {
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("onWidgetListFold");
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onSoundAreaStatus(SoundAreaStatus status) {
        if (status == null) {
            return;
        }
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("onSoundAreaStatus", status.getJsonData());
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onPanelVisibilityChanged(boolean isPanelVisible) {
        this.mIsPanelVisible = isPanelVisible;
        if (!this.mIsPanelVisible) {
            super.removeVoiceWaveLayer();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onWidgetListStopCountdown() {
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("onWidgetListStopCountdown");
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void showListWidget(SpeechWidget widget) {
        CardEntry cardEntry = new CardEntry();
        cardEntry.key = CardKey.SPEECH_CARD_ENTRY_KEY;
        cardEntry.type = 2000;
        cardEntry.importance = 100;
        cardEntry.when = System.currentTimeMillis();
        JSONObject jsonObject = widget.getExtra();
        if (jsonObject != null) {
            cardEntry.speechCardType = getSpeechCardType(jsonObject);
            cardEntry.extraData = jsonObject.toString();
            if (cardEntry.speechCardType != -1) {
                PresenterCenter.getInstance().getCardsPresenter().addCardEntry(cardEntry);
            }
        }
    }

    private int getSpeechCardType(JSONObject jsonObject) {
        boolean z;
        String cardName = jsonObject.optString("name", "");
        int hashCode = cardName.hashCode();
        if (hashCode != 1223440372) {
            if (hashCode == 1549887614 && cardName.equals("knowledge")) {
                z = true;
            }
            z = true;
        } else {
            if (cardName.equals("weather")) {
                z = false;
            }
            z = true;
        }
        if (z) {
            return !z ? -1 : 2;
        }
        return 1;
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void enterSpeechMode(int type) {
        Map<String, Object> map = new HashMap<>();
        map.put(VuiConstants.ELEMENT_TYPE, Integer.valueOf(type));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("enterSpeechMode", map);
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void exitSpeechMode() {
        CardEntry cardEntry = new CardEntry();
        cardEntry.key = CardKey.SPEECH_CARD_ENTRY_KEY;
        PresenterCenter.getInstance().getCardsPresenter().removeCardEntry(cardEntry);
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("exitSpeechMode");
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showMessageViewGroup(boolean show) {
        Map<String, Object> map = new HashMap<>();
        map.put("show", Boolean.valueOf(show));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("showMessageViewGroup", map);
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.message.contract.CardsContract.View
    public void refreshList(List<CardEntry> cardEntries) {
        Logger.d(TAG, "refreshList start");
        for (CardEntry cardEntry : cardEntries) {
            Logger.d(TAG, "refreshList : " + cardEntry.type + "," + cardEntry);
        }
        Logger.d(TAG, "refreshList end");
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("cardEntries", new JSONArray(GsonUtil.toJson(cardEntries)));
            SystemUIMediatorUtil.systemUIMediatorApiRouterCallImportant("refreshCardList", map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.message.contract.CardsContract.View
    public void enterCarCheckMode() {
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("enterCarCheckMode");
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView, com.xiaopeng.systemui.infoflow.message.contract.CardsContract.View
    public void showCarCheckView(CardEntry cardEntry) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("cardEntry", new JSONObject(GsonUtil.toJson(cardEntry)));
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("showCarCheckView", map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView, com.xiaopeng.systemui.infoflow.message.contract.CardsContract.View
    public void exitCarCheckMode() {
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("exitCarCheckMode");
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void onAvatarSkinUpdate(Drawable skin) {
        Bitmap bmp = RoundedDrawable.drawableToBitmap(skin);
        Map<String, Object> map = new HashMap<>();
        map.put("avatarSkin", ImageUtil.getBase64String(bmp));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("onAvatarSkinUpdate", map);
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void onAvatarStateChanged(int state) {
        Map<String, Object> map = new HashMap<>();
        map.put("state", Integer.valueOf(state));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("onAvatarStateChanged", map);
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void enterEasterMode() {
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("enterEasterMode");
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void exitEasterMode() {
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("exitEasterMode");
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showVisualizerWindow(boolean show) {
        Map<String, Object> map = new HashMap<>();
        map.put("show", Boolean.valueOf(show));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("showVisualizerWindow", map);
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showDateTimeView(boolean show) {
        Map<String, Object> map = new HashMap<>();
        map.put("show", Boolean.valueOf(show));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("showDateTimeView", map);
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void collapseCardStack() {
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("collapseCardStack");
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void expandCardStack() {
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("expandCardStack");
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void setWakeupStatus(int status, String info) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", Integer.valueOf(status));
        map.put("info", info);
        SystemUIMediatorUtil.systemUIMediatorApiRouterCallImportant("setWakeupStatus", map);
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void onWheelKeyEvent(KeyEvent keyEvent) {
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onDialogStart() {
        super.onDialogStart();
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("onDialogStart");
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onDialogEnd(DialogEndReason endReason) {
        super.onDialogEnd(endReason);
        try {
            Map<String, Object> map = new HashMap<>();
            if (endReason != null) {
                map.put("endReason", new JSONObject(GsonUtil.toJson(endReason)));
            }
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("onDialogEnd", map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IPushCardView
    public void setCardFocused(int cardType, boolean focused) {
        Map<String, Object> map = new HashMap<>();
        map.put(CardExtra.KEY_CARD_TYPE, Integer.valueOf(cardType));
        map.put("focused", Boolean.valueOf(focused));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setCardFocused", map);
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void setCallCardContent(String content) {
        Map<String, Object> map = new HashMap<>();
        map.put("content", content);
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setCallCardContent", map);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void setPhoneCardStatus(int status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", Integer.valueOf(status));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setPhoneCardStatus", map);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void setPhoneCardTime(String time) {
        Map<String, Object> map = new HashMap<>();
        map.put("time", time);
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setPhoneCardTime", map);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.ICruiseSceneCardView
    public void setCruiseSceneCardActionNum(int actionNum) {
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.ICruiseSceneCardView
    public void setCruiseSceneCardBtnImages(List<String> images) {
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.ICruiseSceneCardView
    public void setCruiseSceneCardRouteInfo(HomeCompanyRouteInfo routeInfo) {
        Logger.d(TAG, "routeInfo : " + routeInfo);
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("routeInfo", new JSONObject(GsonUtil.toJson(routeInfo)));
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setCruiseSceneCardRouteInfo", map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IExplorerSceneCardView
    public void setExploreSceneCardManeuverData(Maneuver maneuver) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("maneuver", new JSONObject(GsonUtil.toJson(maneuver)));
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setNaviSceneCardManeuverData", map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IExplorerSceneCardView
    public void setExploreSceneCardNaviData(Navi navi) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("navi", new JSONObject(GsonUtil.toJson(navi)));
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setNaviSceneCardNaviData", map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IExplorerSceneCardView
    public void setExploreSceneCardLaneData(Lane lane) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("lane", new JSONObject(GsonUtil.toJson(lane)));
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setNaviSceneCardLaneData", map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.INaviSceneCardView
    public void setNaviSceneCardManeuverData(Maneuver maneuver) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("maneuver", new JSONObject(GsonUtil.toJson(maneuver)));
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setNaviSceneCardManeuverData", map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.INaviSceneCardView
    public void setNaviSceneCardLaneData(Lane lane) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("lane", new JSONObject(GsonUtil.toJson(lane)));
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setNaviSceneCardLaneData", map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.INaviSceneCardView
    public void setNaviSceneCardNaviData(Navi navi) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("navi", new JSONObject(GsonUtil.toJson(navi)));
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setNaviSceneCardNaviData", map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.INaviSceneCardView
    public void setNaviSceneCardRemainInfoData(RemainInfo remainInfo) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("remainInfo", new JSONObject(GsonUtil.toJson(remainInfo)));
            SystemUIMediatorUtil.systemUIMediatorApiRouterCallImportant("setNaviSceneCardRemainInfoData", map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardMediaInfo(final int displayId, final MediaInfo mediaInfo) {
        if (mediaInfo == null) {
            Logger.d(TAG, "setMusicCardMediaInfo : mediainfo is null");
        } else {
            ThreadUtils.executeSingleThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.Infoflow3DView.1
                @Override // java.lang.Runnable
                public void run() {
                    Logger.i(Infoflow3DView.TAG, "setMusicCardMediaInfo : " + displayId + " title = " + mediaInfo.getTitle() + " packageName = " + mediaInfo.getPackageName());
                    Map<String, Object> map = new HashMap<>();
                    map.put("displayId", Integer.valueOf(displayId));
                    Bitmap album = mediaInfo.getAlbumBitmap();
                    try {
                        JSONObject jsonObject = new JSONObject(GsonUtil.toJson(mediaInfo));
                        String isDefaultString = mediaInfo.getString("isDefault");
                        if (!TextUtils.isEmpty(isDefaultString)) {
                            Logger.d(Infoflow3DView.TAG, "isDefaultString:" + isDefaultString);
                            jsonObject.put("isDefault", Boolean.parseBoolean(isDefaultString));
                        }
                        String playMode = mediaInfo.getString("playMode");
                        if (!TextUtils.isEmpty(playMode)) {
                            Logger.d(Infoflow3DView.TAG, "playMode:" + playMode);
                            jsonObject.put("playMode", playMode);
                        }
                        String startPosition = mediaInfo.getString("startPosition");
                        if (!TextUtils.isEmpty(startPosition)) {
                            Logger.d(Infoflow3DView.TAG, "startPosition:" + startPosition);
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
                            Log.d(Infoflow3DView.TAG, "album   getWidth : " + album.getWidth() + "getHeight : " + album.getHeight());
                            jsonObject.put("albumWidth", album.getWidth());
                            jsonObject.put("albumHeight", album.getHeight());
                            blob = ImageUtil.getByteArray(album);
                        }
                        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setMusicCardMediaInfo", map, blob);
                    } catch (JSONException exception) {
                        exception.printStackTrace();
                    }
                }
            });
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardPlayStatus(int displayId, int playStatus) {
        Map<String, Object> map = new HashMap<>();
        map.put("displayId", Integer.valueOf(displayId));
        map.put("playStatus", Integer.valueOf(playStatus));
        if (displayId == 1) {
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setSecondaryMusicCardPlayStatus", map);
        } else {
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setMusicCardPlayStatus", map);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardProgress(int displayId, int progress) {
        Map<String, Object> map = new HashMap<>();
        map.put("displayId", Integer.valueOf(displayId));
        map.put("progress", Integer.valueOf(progress));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setMusicCardProgress", map, false);
    }

    @Override // com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardLyricInfo(int displayId, LyricInfo info) {
        if (info != null) {
            Logger.d(TAG, "setMusicCardLyricInfo : " + info.toString());
            Map<String, Object> map = new HashMap<>();
            try {
                map.put("displayId", Integer.valueOf(displayId));
                map.put("lyricInfo", new JSONObject(GsonUtil.toJson(info)));
                SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setMusicCardLyricInfo", map);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IMusicCardView
    public void showMusicCardProgress(int displayId, boolean show) {
        Map<String, Object> map = new HashMap<>();
        map.put("displayId", Integer.valueOf(displayId));
        map.put("show", Boolean.valueOf(show));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("showMusicCardProgress", map, false);
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardPosition(int displayId, String position, String duration) {
        Map<String, Object> map = new HashMap<>();
        map.put("displayId", Integer.valueOf(displayId));
        map.put(VuiConstants.ELEMENT_POSITION, position);
        map.put("duration", duration);
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setMusicCardPosition", map, false);
        Logger.i("setMusicCardPosition" + displayId, 10000, TAG, "setMusicCardPosition : " + map);
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.INotificationCardView
    public void setNotificationCardStatus(boolean hasNotification, String currentTime) {
        Logger.d(TAG, "setNotificationCardStatus : hasNotification = " + hasNotification + ",currentTime = " + currentTime);
        Map<String, Object> map = new HashMap<>();
        map.put("hasNotification", Boolean.valueOf(hasNotification));
        map.put("currentTime", currentTime);
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setNotificationCardStatus", map);
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.INotificationCardView
    public void setNotificationCardSubDesc(String desc) {
        Logger.d(TAG, "setNotificationCardSubDesc : " + desc);
        Map<String, Object> map = new HashMap<>();
        map.put("desc", desc);
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setNotificationCardSubDesc", map);
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IPushCardView
    public void hidePushCardNotTip() {
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("hidePushCardNotTip");
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IPushCardView
    public void setPushCardContent(PushBean pushBean) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("pushBean", new JSONObject(GsonUtil.toJson(pushBean)));
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setPushCardContent", map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IWeatherCardView
    public void hideWeatherCardNotTip() {
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("hideWeatherCardNotTip");
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IWeatherCardView
    public void setWeatherCardContent(WeatherBean weatherBean) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("weatherBean", new JSONObject(GsonUtil.toJson(weatherBean)));
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setWeatherCardContent", map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IRecommendCardView
    public void setRecommendCardContent(RecommendBean recommendBean) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("recommendBean", new JSONObject(GsonUtil.toJson(recommendBean)));
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setRecommendCardContent", map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void setAsrLoc(int asrLoc) {
        Log.d(TAG, "setAsrLoc : " + asrLoc);
        super.setAsrLoc(asrLoc);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showMiniAsrContainer(boolean listeningStatus) {
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void addSpeechCardBackground() {
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showPanelAsr(boolean visible) {
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void enterNormalMode() {
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showVoiceLoc(int voiceLoc) {
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void setSceneType(int sceneType) {
    }

    @Override // com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView, com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void initView() {
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.ICallCardView
    public void updateActionImg(CallInfo callInfo) {
    }
}
