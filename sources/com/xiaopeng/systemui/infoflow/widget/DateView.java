package com.xiaopeng.systemui.infoflow.widget;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.SystemController;
import com.xiaopeng.systemui.infoflow.helper.TimePickHelper;
import com.xiaopeng.systemui.infoflow.util.TimeUtils;
import com.xiaopeng.systemui.utils.Utils;
import java.text.SimpleDateFormat;
import java.util.Date;
/* loaded from: classes24.dex */
public class DateView extends LinearLayout implements SystemController.OnTimeFormatChangedListener {
    private static final String TAG = "DataView";
    private TextView mDataTv;
    private TimePickHelper.OnTimeChangedListener mOnTimeChangedListener;
    private TextView mTimeTv;

    public DateView(Context context) {
        this(context, null);
    }

    public DateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mOnTimeChangedListener = new TimePickHelper.OnTimeChangedListener() { // from class: com.xiaopeng.systemui.infoflow.widget.DateView.1
            @Override // com.xiaopeng.systemui.infoflow.helper.TimePickHelper.OnTimeChangedListener
            public void onTimeChanged() {
                DateView.this.refreshTime();
            }
        };
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        refreshTime();
        TimePickHelper.instance().addListener(this.mOnTimeChangedListener);
        SystemController.getInstance(this.mContext).addOnTimeFormatChangeListener(this);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        TimePickHelper.instance().removeListenter(this.mOnTimeChangedListener);
        SystemController.getInstance(this.mContext).removeOnTimeFormatChangeListener(this);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mDataTv = (TextView) findViewById(R.id.tv_info_date);
        this.mTimeTv = (TextView) findViewById(R.id.tv_info_time);
        TextPaint tp = this.mTimeTv.getPaint();
        tp.setFakeBoldText(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshTime() {
        SimpleDateFormat formatDay;
        Date date = new Date();
        if (Utils.isChineseLanguage()) {
            formatDay = new SimpleDateFormat("M月d日");
        } else {
            formatDay = new SimpleDateFormat("MMM d, YYYY");
        }
        String dateString = formatDay.format(date);
        String timeString = TimeUtils.getFormatTimeString(SystemUIApplication.getContext(), date);
        this.mTimeTv.setText(timeString);
        this.mDataTv.setText(dateString);
        Logger.d(TAG, "refreshTime : " + ((Object) this.mTimeTv.getText()) + ((Object) this.mDataTv.getText()));
    }

    @Override // com.xiaopeng.systemui.controller.SystemController.OnTimeFormatChangedListener
    public void onTimeFormatChanged() {
        refreshTime();
    }
}
