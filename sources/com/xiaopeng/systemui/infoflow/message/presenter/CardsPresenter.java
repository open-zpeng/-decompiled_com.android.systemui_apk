package com.xiaopeng.systemui.infoflow.message.presenter;

import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.infoflow.message.contract.CardsContract;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.CardsData;
import com.xiaopeng.systemui.infoflow.util.Logger;
import java.util.List;
/* loaded from: classes24.dex */
public class CardsPresenter implements CardsContract.Presenter {
    private static final String TAG = CardsPresenter.class.getSimpleName();
    private CardsData mCardsData;
    private boolean mInCarCheckMode = false;
    private int mSceneType;
    private CardsContract.View mView;

    public CardsPresenter(CardsData cards, CardsContract.View view) {
        this.mView = view;
        this.mCardsData = cards;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.contract.CardsContract.Presenter
    public CardEntry getCardEntry(int type) {
        return this.mCardsData.getCard(type);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.contract.CardsContract.Presenter
    public void addCardEntry(CardEntry cardEntry) {
        String str = TAG;
        Logger.d(str, "addCardEntry : displayType = " + CarModelsManager.getFeature().getSysUIDisplayType());
        this.mCardsData.addCard(cardEntry, new CardsData.LoadEntriesCallback() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.CardsPresenter.1
            @Override // com.xiaopeng.systemui.infoflow.message.data.CardsData.LoadEntriesCallback
            public void onEntriesLoaded(List<CardEntry> items) {
                Logger.d(CardsPresenter.TAG, "Added onEntriesLoaded");
                CardsPresenter.this.mView.refreshList(items);
            }

            @Override // com.xiaopeng.systemui.infoflow.message.data.CardsData.LoadEntriesCallback
            public void onDataNotAvailable() {
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.message.contract.CardsContract.Presenter
    public void enterCarCheckMode() {
        this.mInCarCheckMode = true;
        this.mView.enterCarCheckMode();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.contract.CardsContract.Presenter
    public void enterCarCheckMode(CardEntry cardEntry) {
        this.mInCarCheckMode = true;
        this.mView.showCarCheckView(cardEntry);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.contract.CardsContract.Presenter
    public void exitCarCheckMode() {
        this.mInCarCheckMode = false;
        PresenterCenter.getInstance().getInfoFlow().checkToShowMessageViewGroup();
        this.mView.exitCarCheckMode();
    }

    public boolean isInCarCheckMode() {
        return this.mInCarCheckMode;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.contract.CardsContract.Presenter
    public void removeCardEntry(CardEntry cardEntry) {
        this.mCardsData.removeCard(cardEntry, new CardsData.LoadEntriesCallback() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.CardsPresenter.2
            @Override // com.xiaopeng.systemui.infoflow.message.data.CardsData.LoadEntriesCallback
            public void onEntriesLoaded(List<CardEntry> items) {
                Logger.d(CardsPresenter.TAG, "removed onEntriesLoaded");
                CardsPresenter.this.mView.refreshList(items);
            }

            @Override // com.xiaopeng.systemui.infoflow.message.data.CardsData.LoadEntriesCallback
            public void onDataNotAvailable() {
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.message.contract.CardsContract.Presenter
    public void removeNotification(String key) {
        this.mCardsData.removeNotification(key, new CardsData.LoadEntriesCallback() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.CardsPresenter.3
            @Override // com.xiaopeng.systemui.infoflow.message.data.CardsData.LoadEntriesCallback
            public void onEntriesLoaded(List<CardEntry> items) {
                Logger.d(CardsPresenter.TAG, "removed notification with key");
                CardsPresenter.this.mView.refreshList(items);
            }

            @Override // com.xiaopeng.systemui.infoflow.message.data.CardsData.LoadEntriesCallback
            public void onDataNotAvailable() {
            }
        });
    }
}
