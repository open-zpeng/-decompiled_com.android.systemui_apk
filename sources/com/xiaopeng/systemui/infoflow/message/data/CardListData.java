package com.xiaopeng.systemui.infoflow.message.data;

import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.SparseArray;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.carmanager.CarClientWrapper;
import com.xiaopeng.systemui.carmanager.controller.IIcmController;
import com.xiaopeng.systemui.infoflow.message.Global;
import com.xiaopeng.systemui.infoflow.message.data.CardsData;
import com.xiaopeng.systemui.infoflow.message.define.CardKey;
import com.xiaopeng.systemui.infoflow.message.helper.SoundHelper;
import com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.NotificationCardPresenter;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes24.dex */
public class CardListData implements CardsData {
    private static final String TAG = CardListData.class.getSimpleName();
    private static volatile CardListData mInstance;
    private final ArrayMap<String, CardEntry> mEntries = new ArrayMap<>();
    private final ArrayList<CardEntry> mSortedAndFilteredEntry = new ArrayList<>();
    private SparseArray<CardEntry> mTypeMapEntries = new SparseArray<>();
    private final Comparator<CardEntry> mSortComparator = new Comparator<CardEntry>() { // from class: com.xiaopeng.systemui.infoflow.message.data.CardListData.1
        @Override // java.util.Comparator
        public int compare(CardEntry a, CardEntry b) {
            int aImportance = a.importance;
            int bImportance = b.importance;
            if (a.position != b.position) {
                return a.position - b.position;
            }
            if (aImportance != bImportance) {
                return bImportance - aImportance;
            }
            return Long.compare(b.when, a.when);
        }
    };
    private NotificationList mNotificationList = NotificationList.getInstance();

    public static CardListData getInstance() {
        if (mInstance == null) {
            synchronized (CardListData.class) {
                if (mInstance == null) {
                    mInstance = new CardListData();
                }
            }
        }
        return mInstance;
    }

    private CardListData() {
        initFixAppCard();
    }

    private void initFixAppCard() {
        initMusicCardData();
        initPhoneCardData();
    }

    private void initMusicCardData() {
        CardEntry musicCardEntry = new CardEntry();
        musicCardEntry.key = CardKey.MUSIC_ENTRY_KEY;
        musicCardEntry.type = 9;
        musicCardEntry.position = 2;
        musicCardEntry.title = SystemUIApplication.getContext().getResources().getString(R.string.music_default_title);
        musicCardEntry.content = SystemUIApplication.getContext().getResources().getString(R.string.music_default_ablum);
        musicCardEntry.status = 1;
        addCardInternal(musicCardEntry);
    }

    private void initPhoneCardData() {
        CardEntry phoneCardEntry = new CardEntry();
        phoneCardEntry.key = CardKey.PHONE_ENTRY_KEY;
        phoneCardEntry.position = 5;
        phoneCardEntry.type = 5;
        phoneCardEntry.action = "android.intent.action.DIAL";
        phoneCardEntry.title = SystemUIApplication.getContext().getResources().getString(R.string.phone_default_title);
        phoneCardEntry.content = SystemUIApplication.getContext().getResources().getString(R.string.phone_default_content);
        addCardInternal(phoneCardEntry);
    }

    private void initNotificationCardData() {
        CardEntry notificationCardEntry = new CardEntry();
        notificationCardEntry.key = CardKey.MESSAGE_ENTRY_KEY;
        notificationCardEntry.position = 6;
        notificationCardEntry.type = 1;
        notificationCardEntry.title = SystemUIApplication.getContext().getResources().getString(R.string.no_notification);
        notificationCardEntry.content = NotificationCardPresenter.getCurrentTime();
        notificationCardEntry.importance = 0;
        addCardInternal(notificationCardEntry);
    }

    private void initSettingsCardData() {
        CardEntry settingsCardEntry = new CardEntry();
        settingsCardEntry.key = CardKey.SETTINGS_ENTRY_KEY;
        settingsCardEntry.position = 7;
        settingsCardEntry.type = 20;
        addCardInternal(settingsCardEntry);
    }

    private void initCruiseCardData() {
        CardEntry mapCardEntry = new CardEntry();
        mapCardEntry.key = CardKey.MAP_ENTRY_KEY;
        mapCardEntry.position = 1;
        mapCardEntry.type = 25;
        addCardInternal(mapCardEntry);
    }

    private void initCameraCardData() {
        CardEntry cameraCardEntry = new CardEntry();
        cameraCardEntry.key = CardKey.CAMERA_ENTRY_KEY;
        cameraCardEntry.position = 4;
        cameraCardEntry.type = 21;
        addCardInternal(cameraCardEntry);
    }

    private void initHomeCardData() {
        CardEntry homeCardEntry = new CardEntry();
        homeCardEntry.key = CardKey.HOME_ENTRY_KEY;
        homeCardEntry.position = 8;
        homeCardEntry.type = 22;
        addCardInternal(homeCardEntry);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.data.CardsData
    public void removeNotification(String key, CardsData.LoadEntriesCallback callback) {
        this.mNotificationList.remove(key);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.data.CardsData
    public void addCard(CardEntry cardEntry, CardsData.LoadEntriesCallback callback) {
        String str = TAG;
        Logger.d(str, "addCard cardEntry" + cardEntry.toString());
        if (cardEntry.type == 1) {
            this.mNotificationList.add(cardEntry);
        } else if (cardEntry.type == 5) {
            cardEntry.key = CardKey.PHONE_ENTRY_KEY;
            cardEntry.position = 5;
            addCardInternal(cardEntry);
        } else {
            addCardInternal(cardEntry);
        }
        callback.onEntriesLoaded(this.mSortedAndFilteredEntry);
    }

    private void addCardInternal(final CardEntry cardEntry) {
        IIcmController icmController;
        IIcmController icmController2;
        if (cardEntry == null) {
            return;
        }
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.data.CardListData.2
            @Override // java.lang.Runnable
            public void run() {
                if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
                    String str = CardListData.TAG;
                    Logger.d(str, "addCardEntry : " + cardEntry.type + ", content = " + cardEntry.content);
                    IInfoflowCardPresenter infoflowCardPresenter = PresenterCenter.getInstance().getCardPresenter(cardEntry.type);
                    if (infoflowCardPresenter != null) {
                        infoflowCardPresenter.onViewAttachedToWindow();
                        infoflowCardPresenter.bindData(cardEntry);
                    }
                }
            }
        });
        if (this.mEntries.containsKey(cardEntry.key)) {
            if (Global.enjoyMode && (icmController2 = (IIcmController) CarClientWrapper.getInstance().getController(CarClientWrapper.XP_ICM_SERVICE)) != null) {
                icmController2.setIcmInfoCardUpdate(cardEntry);
            }
        } else {
            ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.data.CardListData.3
                @Override // java.lang.Runnable
                public void run() {
                    if (cardEntry.soundEffect != 0) {
                        SoundHelper.play(SoundHelper.PATH_INFOFLOW_NEWCARD);
                    }
                }
            });
            if (Global.enjoyMode && (icmController = (IIcmController) CarClientWrapper.getInstance().getController(CarClientWrapper.XP_ICM_SERVICE)) != null) {
                icmController.setIcmInfoCardAdd(cardEntry);
            }
        }
        synchronized (this.mEntries) {
            this.mEntries.put(cardEntry.key, cardEntry);
            this.mTypeMapEntries.put(cardEntry.type, cardEntry);
        }
        filterAndSort();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.data.CardsData
    public void removeCard(final CardEntry cardEntry, CardsData.LoadEntriesCallback callback) {
        if (cardEntry == null) {
            return;
        }
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.data.CardListData.4
            @Override // java.lang.Runnable
            public void run() {
                IInfoflowCardPresenter infoflowCardPresenter = PresenterCenter.getInstance().getCardPresenter(cardEntry.type);
                if (infoflowCardPresenter != null) {
                    infoflowCardPresenter.destroyCard();
                }
                if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
                    String str = CardListData.TAG;
                    Logger.d(str, "removeCardEntry : " + cardEntry.type);
                    if (infoflowCardPresenter != null) {
                        infoflowCardPresenter.onViewDetachedFromWindow();
                    }
                }
            }
        });
        if (cardEntry.type == 1) {
            this.mNotificationList.remove(cardEntry);
        } else {
            removeCardInternal(cardEntry);
        }
        callback.onEntriesLoaded(this.mSortedAndFilteredEntry);
    }

    private void removeCardInternal(CardEntry cardEntry) {
        synchronized (this.mEntries) {
            this.mEntries.remove(cardEntry.key);
        }
        if (Global.enjoyMode) {
            IIcmController icmController = (IIcmController) CarClientWrapper.getInstance().getController(CarClientWrapper.XP_ICM_SERVICE);
            icmController.setIcmInfoCardRemoved(cardEntry);
        }
        filterAndSort();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.data.CardsData
    public List<CardEntry> getCards() {
        return this.mSortedAndFilteredEntry;
    }

    private void filterAndSort() {
        this.mSortedAndFilteredEntry.clear();
        synchronized (this.mEntries) {
            int N = this.mEntries.size();
            for (int i = 0; i < N; i++) {
                CardEntry entry = this.mEntries.valueAt(i);
                this.mSortedAndFilteredEntry.add(entry);
            }
            Collections.sort(this.mSortedAndFilteredEntry, this.mSortComparator);
            for (int j = 0; j < this.mSortedAndFilteredEntry.size(); j++) {
                this.mSortedAndFilteredEntry.get(j).showIndex = j;
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.data.CardsData
    public CardEntry getCard(String key) {
        if (!TextUtils.isEmpty(key)) {
            return this.mEntries.get(key);
        }
        return null;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.data.CardsData
    public CardEntry getCard(int type) {
        return this.mTypeMapEntries.get(type);
    }
}
