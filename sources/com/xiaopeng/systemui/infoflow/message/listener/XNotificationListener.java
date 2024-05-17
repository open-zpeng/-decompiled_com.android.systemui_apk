package com.xiaopeng.systemui.infoflow.message.listener;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.infoflow.message.contract.CardsContract;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.NotificationList;
import com.xiaopeng.systemui.infoflow.message.define.CardKey;
import com.xiaopeng.systemui.infoflow.message.helper.CardHelper;
import com.xiaopeng.systemui.infoflow.message.presenter.RecommendCardPresenter;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.Logger;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class XNotificationListener extends NotificationListenerService {
    private static volatile XNotificationListener mInstance;
    private Context mContext;
    private CardsContract.Presenter mPresenter;
    private static final String TAG = XNotificationListener.class.getSimpleName();
    private static boolean mShowCommonNotification = SystemProperties.getBoolean("persist.sys.show.common.notify", false);
    private final int AI_PUSH_ADD_EVENT = 1;
    private final String AI_PUSH_KEY_PREFIX = "com.xiaopeng.aiassistant";
    private boolean mRecommendCardAdded = false;
    private boolean mNaviCardAdded = false;
    private boolean mExploreCardAdded = false;

    public static XNotificationListener getInstance(Context context) {
        if (mInstance == null) {
            synchronized (XNotificationListener.class) {
                if (mInstance == null) {
                    mInstance = new XNotificationListener(context);
                }
            }
        }
        return mInstance;
    }

    private XNotificationListener(Context context) {
        this.mContext = context;
    }

    public void setupWithPresenter(CardsContract.Presenter presenter) {
        this.mPresenter = presenter;
        try {
            registerAsSystemService(this.mContext, new ComponentName(this.mContext.getPackageName(), getClass().getCanonicalName()), -1);
        } catch (RemoteException e) {
            Logger.e(TAG, "Unable to register notification listener");
        }
    }

    @Override // android.service.notification.NotificationListenerService
    public StatusBarNotification[] getActiveNotifications() {
        return super.getActiveNotifications();
    }

    @Override // android.service.notification.NotificationListenerService
    public NotificationListenerService.RankingMap getCurrentRanking() {
        return super.getCurrentRanking();
    }

    @Override // android.service.notification.NotificationListenerService
    public void onListenerConnected() {
        super.onListenerConnected();
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationPosted(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap) {
        String str = TAG;
        Logger.d(str, "onNotificationPosted key" + sbn.getKey());
        Notification notification = sbn.getNotification();
        if (mShowCommonNotification || (notification.displayFlag & 32) == 32 || (notification.displayFlag & 4) == 4) {
            CardEntry cardEntry = convertSbnToEntry(sbn);
            String str2 = TAG;
            Logger.d(str2, "addCardEntry entry--" + cardEntry.toString());
            if (cardEntry.type == 18) {
                this.mPresenter.enterCarCheckMode(cardEntry);
            } else if (CardHelper.shouldAddCardEntry(cardEntry, notification)) {
                if (cardEntry.type == 29) {
                    try {
                        JSONObject jsonObject = new JSONObject(cardEntry.extraData);
                        if (jsonObject.has("action")) {
                            int action = jsonObject.getInt("action");
                            if (action == 1 || action == 2) {
                                this.mPresenter.addCardEntry(cardEntry);
                            } else {
                                this.mPresenter.removeCardEntry(cardEntry);
                            }
                        }
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                this.mPresenter.addCardEntry(cardEntry);
            }
        }
    }

    private CardEntry convertSbnToEntry(StatusBarNotification sbn) {
        int i;
        CardEntry entry = new CardEntry(sbn);
        if (entry.type == 25) {
            entry.key = CardKey.MAP_ENTRY_KEY;
            entry.position = 1;
        } else if (entry.type == 19) {
            if (CardHelper.isCarHasException(entry)) {
                i = 16;
            } else {
                i = 10;
            }
            entry.importance = i;
        } else if (entry.type == 29) {
            entry.key = CardKey.AUTO_PARKING_ENTRY_KEY;
            entry.position = 0;
        }
        return entry;
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationRemoved(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap) {
        if (sbn == null) {
            return;
        }
        String str = TAG;
        Logger.d(str, "onNotificationRemoved key" + sbn.getKey());
        CardEntry removeEntry = convertSbnToEntry(sbn);
        String str2 = TAG;
        Logger.d(str2, "onNotificationRemoved type = " + removeEntry.type);
        if (removeEntry.type != 18 && CardHelper.shouldRemoveCardEntry(removeEntry)) {
            Logger.d(TAG, "mPresenter removeCardEntry");
            this.mPresenter.removeCardEntry(removeEntry);
        }
    }

    public void exitCarCheckMode() {
        CardsContract.Presenter presenter = this.mPresenter;
        if (presenter != null) {
            presenter.exitCarCheckMode();
        }
    }

    public void onAIPushMessage(String event) {
        if (this.mPresenter == null) {
            Logger.d(TAG, "onAIPushMessage presenter is null");
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(event);
            String notifyId = jsonObject.optString("notifyId");
            String notifyKey = "com.xiaopeng.aiassistant|" + notifyId;
            int eventId = jsonObject.optInt("eventId");
            boolean priority = jsonObject.optBoolean(VuiConstants.ELEMENT_PRIORITY);
            int level = jsonObject.optInt("level", 0);
            String cardEntry = jsonObject.optString("cardEntry");
            if (eventId == 1) {
                CardEntry data = (CardEntry) GsonUtil.fromJson(cardEntry, (Class<Object>) CardEntry.class);
                data.type = 17;
                data.key = notifyKey;
                data.when = System.currentTimeMillis();
                data.importance = level + 11;
                data.priority = priority;
                this.mPresenter.addCardEntry(data);
            } else {
                CardEntry removeData = new CardEntry();
                removeData.type = 17;
                removeData.key = notifyKey;
                this.mPresenter.removeCardEntry(removeData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onExitNaviMode() {
        if (this.mPresenter != null) {
            this.mNaviCardAdded = false;
            CardEntry cardEntry = createNaviCardEntry();
            this.mPresenter.removeCardEntry(cardEntry);
        }
    }

    public void onEnterNaviMode() {
        if (this.mPresenter != null) {
            this.mNaviCardAdded = true;
            CardEntry cardEntry = createNaviCardEntry();
            this.mPresenter.addCardEntry(cardEntry);
        }
    }

    public void onEnterExlporeMode() {
        if (this.mPresenter != null) {
            this.mExploreCardAdded = true;
            CardEntry cardEntry = createExploreCardEntry();
            this.mPresenter.addCardEntry(cardEntry);
        }
    }

    public void onExitExlporeMode() {
        if (this.mPresenter != null) {
            this.mExploreCardAdded = false;
            CardEntry cardEntry = createExploreCardEntry();
            this.mPresenter.removeCardEntry(cardEntry);
        }
    }

    public void addCarControlCardEntry() {
        CardEntry cardEntry = createCarControlCardEntry();
        this.mPresenter.addCardEntry(cardEntry);
    }

    private CardEntry createExploreCardEntry() {
        CardEntry cardEntry = new CardEntry();
        cardEntry.key = CardKey.EXPLORE_SCENE_ENTRY_KEY;
        cardEntry.type = 27;
        cardEntry.position = 1;
        return cardEntry;
    }

    private CardEntry createNaviCardEntry() {
        CardEntry cardEntry = new CardEntry();
        cardEntry.key = CardKey.NAVI_SCENE_ENTRY_KEY;
        cardEntry.type = 24;
        cardEntry.position = 1;
        return cardEntry;
    }

    private CardEntry createCarControlCardEntry() {
        CardEntry cardEntry = new CardEntry();
        cardEntry.key = CardKey.CAR_CONTROL_ENTRY_KEY;
        cardEntry.type = 7;
        cardEntry.position = 3;
        return cardEntry;
    }

    public void onEnterRecommendMode(String content) {
        if (!this.mRecommendCardAdded) {
            CardEntry cardEntry = new CardEntry();
            cardEntry.key = CardKey.RECOMMEND_ENTRY_KEY;
            cardEntry.type = 26;
            cardEntry.content = content;
            this.mPresenter.addCardEntry(cardEntry);
            this.mRecommendCardAdded = true;
            return;
        }
        RecommendCardPresenter.getInstance().refreshCard(content);
    }

    public void onExitRecommendMode() {
        RecommendCardPresenter.getInstance().removeCard();
        this.mRecommendCardAdded = false;
    }

    public void clearNotificationList() {
        NotificationList.getInstance().clear();
        this.mPresenter.addCardEntry(NotificationList.getInstance().getDefaultNotification());
    }

    public void removeCardEntry(int type) {
        CardEntry cardEntry;
        CardsContract.Presenter presenter = this.mPresenter;
        if (presenter != null && (cardEntry = presenter.getCardEntry(type)) != null) {
            this.mPresenter.removeCardEntry(cardEntry);
        }
    }
}
