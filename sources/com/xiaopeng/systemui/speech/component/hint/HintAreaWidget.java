package com.xiaopeng.systemui.speech.component.hint;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.speech.component.AreaWidget;
import com.xiaopeng.systemui.speech.component.hint.HintModel;
import com.xiaopeng.systemui.speech.data.SpeechDataHint;
/* loaded from: classes24.dex */
class HintAreaWidget extends AreaWidget<IHintListener> implements HintModel.HintModelCallBack {
    private static final String TAG = "Sp-Hint";
    private int mColorInvalid;
    private int mColorNormal;
    private TextView mHintTextView;

    public HintAreaWidget(Context context, int area) {
        super(context, area);
        HintModel.get().addCallBack(area, this);
        this.mColorNormal = getContext().getColor(R.color.speech_tv_normal);
        this.mColorInvalid = getContext().getColor(R.color.speech_tv_invalid);
    }

    @Override // com.xiaopeng.systemui.speech.component.AreaWidget
    protected View initView(View view) {
        this.mHintTextView = (TextView) view.findViewById(R.id.speech_tv_hint);
        return this.mHintTextView;
    }

    @Override // com.xiaopeng.systemui.speech.component.AreaWidget
    protected String logTag() {
        return TAG;
    }

    @Override // com.xiaopeng.systemui.speech.component.hint.HintModel.HintModelCallBack
    public void onHintHide(int area) {
        Logger.d(TAG, "onHintHide area=" + area);
        hide(true);
    }

    @Override // com.xiaopeng.systemui.speech.component.hint.HintModel.HintModelCallBack
    public void onHintShow(SpeechDataHint data) {
        Logger.d(TAG, "onHintShow " + data);
        this.mColorNormal = getContext().getColor(R.color.speech_tv_normal);
        this.mColorInvalid = getContext().getColor(R.color.speech_tv_invalid);
        if (!createAndShow(data, true)) {
            return;
        }
        if (TextUtils.isEmpty(data.getRelateText())) {
            this.mHintTextView.setText(data.getText());
            return;
        }
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append((CharSequence) data.getRelateText());
        sb.append((CharSequence) " \"");
        sb.append((CharSequence) data.getText());
        sb.append((CharSequence) "\"");
        sb.setSpan(new ForegroundColorSpan(this.mColorInvalid), 0, data.getRelateText().length() + 1, 33);
        sb.setSpan(new ForegroundColorSpan(this.mColorNormal), data.getRelateText().length() + 1, data.getRelateText().length() + 1 + data.getText().length() + 2, 33);
        this.mHintTextView.setText(sb);
    }
}
