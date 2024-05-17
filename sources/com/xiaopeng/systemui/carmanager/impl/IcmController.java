package com.xiaopeng.systemui.carmanager.impl;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.icm.CarIcmManager;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.KeyEvent;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carmanager.BaseCarController;
import com.xiaopeng.systemui.carmanager.CarClientWrapper;
import com.xiaopeng.systemui.carmanager.controller.IIcmController;
import com.xiaopeng.systemui.infoflow.icm.bean.IcmCardEntry;
import com.xiaopeng.systemui.infoflow.icm.bean.IcmCardList;
import com.xiaopeng.systemui.infoflow.manager.MediaManager;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.bean.MediaExtraInfo;
import com.xiaopeng.systemui.infoflow.message.helper.SoundHelper;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class IcmController extends BaseCarController<CarIcmManager, IIcmController.Callback> implements IIcmController {
    protected static final String TAG = "IcmController";
    private final CarIcmManager.CarIcmEventCallback mCarIcmEventCallback = new CarIcmManager.CarIcmEventCallback() { // from class: com.xiaopeng.systemui.carmanager.impl.IcmController.1
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            Logger.d(IcmController.TAG, "onChangeEvent: " + carPropertyValue);
            IcmController.this.handleCarEventsUpdate(carPropertyValue);
        }

        public void onErrorEvent(int propertyId, int zone) {
            Logger.d(IcmController.TAG, "onErrorEvent: " + propertyId);
        }
    };

    /* JADX WARN: Type inference failed for: r0v4, types: [C, android.car.hardware.icm.CarIcmManager] */
    public IcmController(Car carClient) {
        try {
            this.mCarManager = (CarIcmManager) carClient.getCarManager(CarClientWrapper.XP_ICM_SERVICE);
        } catch (CarNotConnectedException e) {
        }
    }

    public static BaseCarController createCarController(Car carClient) {
        return new IcmController(carClient);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.carmanager.BaseCarController
    public void initCarManager(Car carClient) {
        Logger.d(TAG, "Init start");
        try {
            if (this.mCarManager != 0) {
                ((CarIcmManager) this.mCarManager).registerPropCallback(this.mPropertyIds, this.mCarIcmEventCallback);
            }
        } catch (CarNotConnectedException e) {
        }
        Logger.d(TAG, "Init end");
    }

    @Override // com.xiaopeng.systemui.carmanager.BaseCarController
    protected List<Integer> getRegisterPropertyIds() {
        List<Integer> propertyIds = new ArrayList<>();
        propertyIds.add(557847045);
        return propertyIds;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.carmanager.BaseCarController
    public void disconnect() {
        if (this.mCarManager != 0) {
            try {
                ((CarIcmManager) this.mCarManager).unregisterPropCallback(this.mPropertyIds, this.mCarIcmEventCallback);
            } catch (CarNotConnectedException e) {
            }
        }
    }

    @Override // com.xiaopeng.systemui.carmanager.BaseCarController
    protected void handleEventsUpdate(CarPropertyValue<?> value) {
        if (value.getPropertyId() == 554702359) {
            handleIcmInfoflowMsg((String) getValue(value));
            return;
        }
        Logger.d(TAG, "handle unknown event: " + value);
    }

    private void handleIcmInfoflowMsg(String value) {
        synchronized (this.mCallbackLock) {
            Iterator it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                IIcmController.Callback callback = (IIcmController.Callback) it.next();
                callback.onInfoflowMsg(value);
            }
        }
    }

    @Override // com.xiaopeng.systemui.carmanager.controller.IIcmController
    public void setWheelKey(final KeyEvent keyEvent) {
        com.xiaopeng.systemui.infoflow.util.Logger.d(TAG, "setWheelKey event");
        if (keyEvent.getAction() == 1) {
            return;
        }
        playKeyEventSound(keyEvent);
        if (this.mCarManager != 0) {
            ThreadUtils.executeIcmControl(new Runnable() { // from class: com.xiaopeng.systemui.carmanager.impl.IcmController.2
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        ((CarIcmManager) IcmController.this.mCarManager).setIcmWheelkey(keyEvent.getKeyCode());
                    } catch (Exception e) {
                        com.xiaopeng.systemui.infoflow.util.Logger.e(IcmController.TAG, "setWheelKey", e);
                    }
                }
            });
        }
    }

    @Override // com.xiaopeng.systemui.carmanager.controller.IIcmController
    public void setIcmAllCard(final List<CardEntry> entries) {
        com.xiaopeng.systemui.infoflow.util.Logger.d(TAG, "setIcmAllCard");
        if (this.mCarManager != 0) {
            SoundHelper.play(SoundHelper.PATH_INFOFLOW_TRANSFER);
            ThreadUtils.executeIcmControl(new Runnable() { // from class: com.xiaopeng.systemui.carmanager.impl.IcmController.3
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        ((CarIcmManager) IcmController.this.mCarManager).setIcmAllCardsRefresh(GsonUtil.toJson(IcmController.this.convertCardList(entries)));
                    } catch (Exception e) {
                        com.xiaopeng.systemui.infoflow.util.Logger.e(IcmController.TAG, "setIcmAllCard", e);
                    }
                }
            });
        }
    }

    @Override // com.xiaopeng.systemui.carmanager.controller.IIcmController
    public void setIcmInfoCardAdd(final CardEntry cardEntry) {
        Logger.d(TAG, "setIcmInfoCardAdd");
        if (cardEntry.type != 8 && this.mCarManager != 0) {
            ThreadUtils.executeIcmControl(new Runnable() { // from class: com.xiaopeng.systemui.carmanager.impl.IcmController.4
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        ((CarIcmManager) IcmController.this.mCarManager).setIcmInfoCardAdd(GsonUtil.toJson(IcmController.this.convertCardEntry(cardEntry)));
                    } catch (Exception e) {
                        com.xiaopeng.systemui.infoflow.util.Logger.e(IcmController.TAG, "setIcmInfoCardAdd", e);
                    }
                }
            });
        }
    }

    @Override // com.xiaopeng.systemui.carmanager.controller.IIcmController
    public void exitEnjoyMode() {
        Logger.d(TAG, "exitEnjoyMode");
        if (this.mCarManager != 0) {
            SoundHelper.play(SoundHelper.PATH_INFOFLOW_TRANSFER);
            ThreadUtils.executeIcmControl(new Runnable() { // from class: com.xiaopeng.systemui.carmanager.impl.IcmController.5
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        IcmCardList icmCardList = new IcmCardList();
                        icmCardList.cardTotalNum = 0;
                        icmCardList.cardListCount = 0;
                        List<IcmCardEntry> cardEntries = new ArrayList<>();
                        icmCardList.cardList = cardEntries;
                        ((CarIcmManager) IcmController.this.mCarManager).setIcmAllCardsRefresh(GsonUtil.toJson(icmCardList));
                    } catch (Exception e) {
                        com.xiaopeng.systemui.infoflow.util.Logger.e(IcmController.TAG, "exitEnjoyMode", e);
                    }
                }
            });
        }
    }

    @Override // com.xiaopeng.systemui.carmanager.controller.IIcmController
    public void setIcmInfoCardRemoved(final CardEntry cardEntry) {
        Logger.d(TAG, "setIcmInfoCardRemoved");
        if (cardEntry.type != 8 && this.mCarManager != 0) {
            ThreadUtils.executeIcmControl(new Runnable() { // from class: com.xiaopeng.systemui.carmanager.impl.IcmController.6
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        ((CarIcmManager) IcmController.this.mCarManager).setIcmInfoCardRemove(GsonUtil.toJson(IcmController.this.convertCardEntry(cardEntry)));
                    } catch (Exception e) {
                        com.xiaopeng.systemui.infoflow.util.Logger.e(IcmController.TAG, "setIcmInfoCarRemove", e);
                    }
                }
            });
        }
    }

    @Override // com.xiaopeng.systemui.carmanager.controller.IIcmController
    public void setIcmInfoCardUpdate(final CardEntry cardEntry) {
        Logger.d(TAG, "setIcmInfoCardUpdate");
        if (cardEntry.type != 8 && this.mCarManager != 0) {
            ThreadUtils.executeIcmControl(new Runnable() { // from class: com.xiaopeng.systemui.carmanager.impl.IcmController.7
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        ((CarIcmManager) IcmController.this.mCarManager).setIcmInfoCardUpdate(GsonUtil.toJson(IcmController.this.convertCardEntry(cardEntry)));
                    } catch (Exception e) {
                        com.xiaopeng.systemui.infoflow.util.Logger.e(IcmController.TAG, "setIcmInfoCardUpdate", e);
                    }
                }
            });
        }
    }

    private void playKeyEventSound(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0) {
            if (keyEvent.getKeyCode() == 1015) {
                SoundHelper.play(SoundHelper.PATH_WHEEL_TIP_1);
            } else if (keyEvent.getKeyCode() == 1084) {
                SoundHelper.play(SoundHelper.PATH_WHEEL_SCROLL_RIGHT);
            } else if (keyEvent.getKeyCode() == 1083) {
                SoundHelper.play(SoundHelper.PATH_WHEEL_SCROLL_RIGHT);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public IcmCardList convertCardList(List<CardEntry> cardEntries) {
        IcmCardList icmCardList = new IcmCardList();
        icmCardList.cardTotalNum = cardEntries.size();
        List<IcmCardEntry> cardList = new ArrayList<>();
        for (int i = 0; i < cardEntries.size(); i++) {
            CardEntry cardEntry = cardEntries.get(i);
            if (cardEntry.type != 8) {
                cardList.add(convertCardEntry(cardEntries.get(i)));
            }
        }
        icmCardList.cardList = cardList;
        return icmCardList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public IcmCardEntry convertCardEntry(CardEntry cardEntry) {
        if (cardEntry.type == 9) {
            return getMusicCardEntry(cardEntry);
        }
        IcmCardEntry icmCardEntry = new IcmCardEntry();
        icmCardEntry.type = cardEntry.type;
        icmCardEntry.key = cardEntry.key;
        icmCardEntry.title = cardEntry.title;
        icmCardEntry.mainContent = cardEntry.content;
        icmCardEntry.when = cardEntry.when;
        if (!TextUtils.isEmpty(cardEntry.extraData)) {
            icmCardEntry.extraData = GsonUtil.fromJson(cardEntry.extraData, (Class<Object>) Object.class);
        }
        if (cardEntry.bigIcon != null) {
            icmCardEntry.picture = bitmapToBytes(cardEntry.bigIcon);
        }
        return icmCardEntry;
    }

    private IcmCardEntry getMusicCardEntry(CardEntry cardEntry) {
        IcmCardEntry icmCardEntry = new IcmCardEntry();
        icmCardEntry.type = cardEntry.type;
        icmCardEntry.key = cardEntry.key;
        MediaInfo mediaInfo = MediaManager.getInstance().getCurrentMediaInfo();
        MediaExtraInfo mediaExtraInfo = new MediaExtraInfo();
        int mediaStatus = MediaManager.getInstance().getCurrentPlayStatus();
        if (mediaStatus == 0) {
            mediaExtraInfo.playStatus = 1;
        } else {
            mediaExtraInfo.playStatus = 0;
        }
        icmCardEntry.extraData = mediaExtraInfo;
        if (mediaInfo != null) {
            icmCardEntry.title = mediaInfo.getTitle();
            icmCardEntry.mainContent = mediaInfo.getArtist();
        }
        icmCardEntry.when = cardEntry.when;
        return icmCardEntry;
    }

    private void handleIcmMsg(String json) {
        com.xiaopeng.systemui.infoflow.util.Logger.d(TAG, "receiver the icmMsg -" + json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject msgData = jsonObject.optJSONObject("msgData");
            String cardId = msgData.optString("cardId");
            String msgContent = msgData.optString("msgContent");
            if (IIcmController.CMD_OPEN.equals(msgContent)) {
                Logger.d(TAG, "open card key-" + cardId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }
}
