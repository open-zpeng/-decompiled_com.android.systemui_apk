package com.xiaopeng.systemui.infoflow.message.presenter;

import android.content.ComponentName;
import android.os.Handler;
import android.os.Message;
import com.android.systemui.R;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.aissistant.push.TextToSpeechHelper;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class AutoParkingCardPresenter extends BaseCardPresenter implements IInfoflowCardPresenter {
    private static final int MSG_PLAY_TTS = 1;
    private Handler mHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.AutoParkingCardPresenter.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                AutoParkingCardPresenter.this.playTts((String) msg.obj);
            }
        }
    };

    /* loaded from: classes24.dex */
    private static class SingleHolder {
        private static final AutoParkingCardPresenter sInstance = new AutoParkingCardPresenter();

        private SingleHolder() {
        }
    }

    public static AutoParkingCardPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected int getCardType() {
        return 29;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public String getCardPackageName() {
        ComponentName componentName = ComponentName.unflattenFromString(this.mContext.getString(R.string.component_autopilot));
        return componentName.getPackageName();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardClicked() {
        PackageHelper.startSuperPark(this.mContext);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithInfoflowView() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithCardHolder() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
        String extraData = this.mCardData.extraData;
        this.mHandler.removeMessages(1);
        Message msg = this.mHandler.obtainMessage(1);
        msg.obj = extraData;
        this.mHandler.sendMessageDelayed(msg, 200L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playTts(String extraData) {
        try {
            JSONObject jsonObject = new JSONObject(extraData);
            if (jsonObject.has("action")) {
                int action = jsonObject.getInt("action");
                if (action == 2) {
                    TextToSpeechHelper.instance().speak(this.mContext.getString(R.string.study_auto_parking), (TextToSpeechHelper.ISpeakCallback) null);
                } else {
                    TextToSpeechHelper.instance().speak(this.mContext.getString(R.string.auto_parking_is_available), (TextToSpeechHelper.ISpeakCallback) null);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
