package com.xiaopeng.systemui;

import android.util.SparseArray;
import com.xiaopeng.systemui.infoflow.AbstractInfoFlow;
import com.xiaopeng.systemui.infoflow.message.presenter.CallCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.CarCheckCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.CardsPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.CruiseSceneCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.ExplorerSceneCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.HomeCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.MusicCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.NaviSceneCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.NotificationCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.PhoneCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.RecommendCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.SmartCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.WeatherCardPresenter;
import com.xiaopeng.systemui.navigationbar.INavigationBarPresenter;
import com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter;
import com.xiaopeng.systemui.statusbar.IStatusbarPresenter;
/* loaded from: classes24.dex */
public class PresenterCenter {
    private CardsPresenter mCardsPresenter;
    private AbstractInfoFlow mInfoFlow;
    private SparseArray<INavigationBarPresenter> mNavigationBarPresenters;
    private IQuickMenuHolderPresenter mQuickMenuHolderPresenter;
    private IStatusbarPresenter mStatusbarPresenter;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final PresenterCenter sInstance = new PresenterCenter();

        private SingleHolder() {
        }
    }

    private PresenterCenter() {
        this.mNavigationBarPresenters = new SparseArray<>();
    }

    public static PresenterCenter getInstance() {
        return SingleHolder.sInstance;
    }

    public void setCardsPresenter(CardsPresenter cardsPresenter) {
        this.mCardsPresenter = cardsPresenter;
    }

    public CardsPresenter getCardsPresenter() {
        return this.mCardsPresenter;
    }

    public void setInfoflow(AbstractInfoFlow infoFlow) {
        this.mInfoFlow = infoFlow;
    }

    public AbstractInfoFlow getInfoFlow() {
        return this.mInfoFlow;
    }

    public void setStatusbarPresenter(IStatusbarPresenter presenter) {
        this.mStatusbarPresenter = presenter;
    }

    public IStatusbarPresenter getStatusbarPresenter() {
        return this.mStatusbarPresenter;
    }

    public void addNavigationBarPresenter(int index, INavigationBarPresenter presenter) {
        this.mNavigationBarPresenters.put(index, presenter);
    }

    public INavigationBarPresenter getNavigationBarPresenter(int index) {
        return this.mNavigationBarPresenters.get(index);
    }

    public IInfoflowCardPresenter getCardPresenter(int cardType) {
        if (cardType != 1) {
            if (cardType != 9) {
                if (cardType != 17) {
                    if (cardType != 19) {
                        if (cardType != 22) {
                            if (cardType != 1001) {
                                if (cardType != 4) {
                                    if (cardType == 5) {
                                        return PhoneCardPresenter.getInstance();
                                    }
                                    switch (cardType) {
                                        case 24:
                                            return NaviSceneCardPresenter.getInstance();
                                        case 25:
                                            return CruiseSceneCardPresenter.getInstance();
                                        case 26:
                                            return RecommendCardPresenter.getInstance();
                                        case 27:
                                            return ExplorerSceneCardPresenter.getInstance();
                                        case 28:
                                            return SmartCardPresenter.getInstance();
                                        default:
                                            return null;
                                    }
                                }
                                return CallCardPresenter.getInstance();
                            }
                            return WeatherCardPresenter.getInstance();
                        }
                        return HomeCardPresenter.getInstance();
                    }
                    return CarCheckCardPresenter.getInstance();
                }
                return PushCardPresenter.getInstance();
            }
            return MusicCardPresenter.getInstance();
        }
        return NotificationCardPresenter.getInstance();
    }
}
