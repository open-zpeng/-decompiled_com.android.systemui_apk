package com.xiaopeng.systemui.speech.component.asr;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.speech.component.AreaWidget;
import com.xiaopeng.systemui.speech.component.asr.AsrModel;
import com.xiaopeng.systemui.speech.data.SpeechDataInput;
/* loaded from: classes24.dex */
class AsrAreaWidget extends AreaWidget<IAsrListener> implements AsrModel.AsrAreaModelCallBack {
    private static final String TAG = "Sp-AsrArea";
    private TextView mAsrTextView;

    public AsrAreaWidget(Context context, int area) {
        super(context, area);
        AsrModel.get().addCallBack(area, this);
    }

    @Override // com.xiaopeng.systemui.speech.component.AreaWidget
    protected View initView(View view) {
        this.mAsrTextView = (TextView) view.findViewById(R.id.speech_tv_asr);
        return this.mAsrTextView;
    }

    @Override // com.xiaopeng.systemui.speech.component.AreaWidget
    protected String logTag() {
        return TAG;
    }

    @Override // com.xiaopeng.systemui.speech.component.asr.AsrModel.AsrAreaModelCallBack
    public void onAsrHide(int area) {
        Logger.i(TAG, "onAsrHide area=" + area);
        hide(true);
    }

    @Override // com.xiaopeng.systemui.speech.component.asr.AsrModel.AsrAreaModelCallBack
    public void onAsrShow(SpeechDataInput data) {
        Logger.i(TAG, "onAsrShow " + data);
        if (!createAndShow(data, true)) {
            Logger.w(TAG, "createAndShow fail !!!");
            return;
        }
        this.mAsrTextView.setText(data.getText());
        if (data.isInvalid()) {
            TextView textView = this.mAsrTextView;
            textView.setTextColor(textView.getContext().getColor(R.color.speech_tv_invalid));
            return;
        }
        TextView textView2 = this.mAsrTextView;
        textView2.setTextColor(textView2.getContext().getColor(R.color.speech_tv_normal));
    }
}
