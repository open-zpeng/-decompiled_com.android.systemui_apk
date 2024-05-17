package com.xiaopeng.systemui.infoflow;

import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import com.xiaopeng.systemui.infoflow.message.contract.CardsContract;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.speech.ISpeechView;
/* loaded from: classes24.dex */
public interface IInfoflowView extends ISpeechView, CardsContract.View, ICruiseSceneCardView, IExplorerSceneCardView, IMusicCardView, INaviSceneCardView, INotificationCardView, IPushCardView, IWeatherCardView, ICallCardView, IRecommendCardView {
    void addSpeechCardBackground();

    void collapseCardStack();

    void enterEasterMode();

    void enterNormalMode();

    void enterSpeechMode(int i);

    void exitCarCheckMode();

    void exitEasterMode();

    void exitSpeechMode();

    void expandCardStack();

    void initView();

    void onAvatarSkinUpdate(Drawable drawable);

    void onAvatarStateChanged(int i);

    void onNavigationItemChanged(String str, String str2, boolean z);

    void onWheelKeyEvent(KeyEvent keyEvent);

    void setAsrLoc(int i);

    void setCallCardContent(String str);

    void setPhoneCardStatus(int i);

    void setPhoneCardTime(String str);

    void setSceneType(int i);

    void setWakeupStatus(int i, String str);

    void showCarCheckView(CardEntry cardEntry);

    void showDateTimeView(boolean z);

    void showMessageViewGroup(boolean z);

    void showMiniAsrContainer(boolean z);

    void showPanelAsr(boolean z);

    void showVisualizerWindow(boolean z);

    void showVoiceLoc(int i);

    void startNgpWarningAnim();

    void stopNgpWarningAnim();
}
