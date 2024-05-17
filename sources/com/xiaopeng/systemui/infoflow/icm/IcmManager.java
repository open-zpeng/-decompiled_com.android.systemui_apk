package com.xiaopeng.systemui.infoflow.icm;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.icm.CarIcmManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import com.xiaopeng.systemui.carmanager.CarClientWrapper;
import com.xiaopeng.systemui.infoflow.icm.bean.IcmCardEntry;
import com.xiaopeng.systemui.infoflow.icm.bean.IcmCardList;
import com.xiaopeng.systemui.infoflow.manager.MediaManager;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.bean.MediaExtraInfo;
import com.xiaopeng.systemui.infoflow.message.helper.SoundHelper;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class IcmManager {
    private static final String CMD_ACTION = "Action";
    private static final String CMD_CLOSE = "close";
    private static final String CMD_OPEN = "open";
    private static final String ERROR_NOT_INIT = "IcmManager must be init before using";
    private static final String TAG = "IcmManager";
    private static volatile IcmManager mInstance;
    private Car mCar;
    private CarIcmManager mCarIcmManager;
    private Context mContext;
    private IcmCardClickListener mIcmCardClickListener;
    private final int MEDIA_PAUSED = 0;
    private final int MEDIA_PALYED = 1;
    private final ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.xiaopeng.systemui.infoflow.icm.IcmManager.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                IcmManager.this.mCarIcmManager = (CarIcmManager) IcmManager.this.mCar.getCarManager(CarClientWrapper.XP_ICM_SERVICE);
                IcmManager.this.mCarIcmManager.registerPropCallback(IcmManager.this.mPropIds, IcmManager.this.mCarIcmEventCallback);
            } catch (CarNotConnectedException e) {
                Logger.e(IcmManager.TAG, "Car not connected");
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    CarIcmManager.CarIcmEventCallback mCarIcmEventCallback = new CarIcmManager.CarIcmEventCallback() { // from class: com.xiaopeng.systemui.infoflow.icm.IcmManager.8
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            Logger.d(IcmManager.TAG, "onChangeEvent id --" + carPropertyValue.getPropertyId());
            if (carPropertyValue.getPropertyId() == 554702359) {
                Object object = carPropertyValue.getValue();
                IcmManager.this.handleIcmMsg((String) object);
            }
        }

        public void onErrorEvent(int i, int i1) {
        }
    };
    private List<Integer> mPropIds = new ArrayList();

    /* loaded from: classes24.dex */
    public interface IcmCardClickListener {
        void onClick(String str);
    }

    public static IcmManager getInstance() {
        if (mInstance == null) {
            synchronized (IcmManager.class) {
                if (mInstance == null) {
                    mInstance = new IcmManager();
                }
            }
        }
        return mInstance;
    }

    private IcmManager() {
        this.mPropIds.add(new Integer(554702359));
    }

    public synchronized void init(Context context) {
        if (this.mCar != null) {
            Logger.w(TAG, "the icm have been init");
            return;
        }
        this.mContext = context;
        this.mCar = Car.createCar(this.mContext, this.mServiceConnection);
        this.mCar.connect();
    }

    public void setWheelKey(final KeyEvent keyEvent) {
        Logger.d(TAG, "setWheelKey event");
        if (keyEvent.getAction() == 1) {
            return;
        }
        playKeyEventSound(keyEvent);
        checkInit();
        if (this.mCarIcmManager != null) {
            ThreadUtils.executeIcmControl(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.icm.IcmManager.2
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        IcmManager.this.mCarIcmManager.setIcmWheelkey(keyEvent.getKeyCode());
                    } catch (Exception e) {
                        Logger.e(IcmManager.TAG, "setWheelKey", e);
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

    public void setIcmAllCard(final List<CardEntry> entries) {
        Logger.d(TAG, "setIcmAllCard");
        checkInit();
        if (this.mCarIcmManager != null) {
            SoundHelper.play(SoundHelper.PATH_INFOFLOW_TRANSFER);
            ThreadUtils.executeIcmControl(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.icm.IcmManager.3
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        IcmManager.this.mCarIcmManager.setIcmAllCardsRefresh(GsonUtil.toJson(IcmManager.this.convertCardList(entries)));
                    } catch (Exception e) {
                        Logger.e(IcmManager.TAG, "setIcmAllCard", e);
                    }
                }
            });
        }
    }

    public void exitEnjoyMode() {
        Logger.d(TAG, "exitEnjoyMode");
        checkInit();
        if (this.mCarIcmManager != null) {
            SoundHelper.play(SoundHelper.PATH_INFOFLOW_TRANSFER);
            ThreadUtils.executeIcmControl(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.icm.IcmManager.4
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        IcmCardList icmCardList = new IcmCardList();
                        icmCardList.cardTotalNum = 0;
                        icmCardList.cardListCount = 0;
                        List<IcmCardEntry> cardEntries = new ArrayList<>();
                        icmCardList.cardList = cardEntries;
                        IcmManager.this.mCarIcmManager.setIcmAllCardsRefresh(GsonUtil.toJson(icmCardList));
                    } catch (Exception e) {
                        Logger.e(IcmManager.TAG, "exitEnjoyMode", e);
                    }
                }
            });
        }
    }

    public void setIcmInfoCardAdd(final CardEntry cardEntry) {
        Logger.d(TAG, "setIcmInfoCardAdd");
        if (cardEntry.type == 8) {
            return;
        }
        checkInit();
        if (this.mCarIcmManager != null) {
            ThreadUtils.executeIcmControl(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.icm.IcmManager.5
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        IcmManager.this.mCarIcmManager.setIcmInfoCardAdd(GsonUtil.toJson(IcmManager.this.convertCardEntry(cardEntry)));
                    } catch (Exception e) {
                        Logger.e(IcmManager.TAG, "setIcmInfoCardAdd", e);
                    }
                }
            });
        }
    }

    public void setIcmInfoCardRemoved(final CardEntry cardEntry) {
        Logger.d(TAG, "setIcmInfoCardRemoved");
        if (cardEntry.type == 8) {
            return;
        }
        checkInit();
        if (this.mCarIcmManager != null) {
            ThreadUtils.executeIcmControl(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.icm.IcmManager.6
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        IcmManager.this.mCarIcmManager.setIcmInfoCardRemove(GsonUtil.toJson(IcmManager.this.convertCardEntry(cardEntry)));
                    } catch (Exception e) {
                        Logger.e(IcmManager.TAG, "setIcmInfoCarRemove", e);
                    }
                }
            });
        }
    }

    public void setIcmInfoCardUpdate(final CardEntry cardEntry) {
        Logger.d(TAG, "setIcmInfoCardUpdate");
        if (cardEntry.type == 8) {
            return;
        }
        checkInit();
        if (this.mCarIcmManager != null) {
            ThreadUtils.executeIcmControl(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.icm.IcmManager.7
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        IcmManager.this.mCarIcmManager.setIcmInfoCardUpdate(GsonUtil.toJson(IcmManager.this.convertCardEntry(cardEntry)));
                    } catch (Exception e) {
                        Logger.e(IcmManager.TAG, "setIcmInfoCardUpdate", e);
                    }
                }
            });
        }
    }

    private void checkInit() {
        if (this.mCar == null) {
            throw new IllegalStateException(ERROR_NOT_INIT);
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

    /* JADX INFO: Access modifiers changed from: private */
    public void handleIcmMsg(String json) {
        Logger.d(TAG, "receiver the icmMsg -" + json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject msgData = jsonObject.optJSONObject("msgData");
            String cardId = msgData.optString("cardId");
            String msgContent = msgData.optString("msgContent");
            if ("open".equals(msgContent) && this.mIcmCardClickListener != null) {
                Logger.d(TAG, "open card key-" + cardId);
                this.mIcmCardClickListener.onClick(cardId);
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

    public void setIcmCardClickListener(IcmCardClickListener icmCardClickListener) {
        this.mIcmCardClickListener = icmCardClickListener;
    }
}
