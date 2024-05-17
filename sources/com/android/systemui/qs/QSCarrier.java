package com.android.systemui.qs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settingslib.Utils;
import com.android.settingslib.graph.SignalDrawable;
import com.android.systemui.DualToneHandler;
import com.android.systemui.R;
import com.android.systemui.qs.QSCarrierGroup;
/* loaded from: classes21.dex */
public class QSCarrier extends LinearLayout {
    private TextView mCarrierText;
    private float mColorForegroundIntensity;
    private ColorStateList mColorForegroundStateList;
    private DualToneHandler mDualToneHandler;
    private View mMobileGroup;
    private ImageView mMobileRoaming;
    private ImageView mMobileSignal;

    public QSCarrier(Context context) {
        super(context);
    }

    public QSCarrier(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QSCarrier(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public QSCarrier(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mDualToneHandler = new DualToneHandler(getContext());
        this.mMobileGroup = findViewById(R.id.mobile_combo);
        this.mMobileSignal = (ImageView) findViewById(R.id.mobile_signal);
        this.mMobileRoaming = (ImageView) findViewById(R.id.mobile_roaming);
        this.mCarrierText = (TextView) findViewById(R.id.qs_carrier_text);
        int colorForeground = Utils.getColorAttrDefaultColor(this.mContext, 16842800);
        this.mColorForegroundStateList = ColorStateList.valueOf(colorForeground);
        this.mColorForegroundIntensity = QuickStatusBarHeader.getColorIntensity(colorForeground);
    }

    public void updateState(QSCarrierGroup.CellSignalState state) {
        this.mMobileGroup.setVisibility(state.visible ? 0 : 8);
        if (state.visible) {
            this.mMobileRoaming.setVisibility(state.roaming ? 0 : 8);
            ColorStateList colorStateList = ColorStateList.valueOf(this.mDualToneHandler.getSingleColor(this.mColorForegroundIntensity));
            this.mMobileRoaming.setImageTintList(colorStateList);
            this.mMobileSignal.setImageDrawable(new SignalDrawable(this.mContext));
            this.mMobileSignal.setImageTintList(colorStateList);
            this.mMobileSignal.setImageLevel(state.mobileSignalIconId);
            StringBuilder contentDescription = new StringBuilder();
            if (state.contentDescription != null) {
                contentDescription.append(state.contentDescription);
                contentDescription.append(", ");
            }
            if (state.roaming) {
                contentDescription.append(this.mContext.getString(R.string.data_connection_roaming));
                contentDescription.append(", ");
            }
            if (hasValidTypeContentDescription(state.typeContentDescription)) {
                contentDescription.append(state.typeContentDescription);
            }
            this.mMobileSignal.setContentDescription(contentDescription);
        }
    }

    private boolean hasValidTypeContentDescription(String typeContentDescription) {
        return TextUtils.equals(typeContentDescription, this.mContext.getString(R.string.data_connection_no_internet)) || TextUtils.equals(typeContentDescription, this.mContext.getString(R.string.cell_data_off_content_description)) || TextUtils.equals(typeContentDescription, this.mContext.getString(R.string.not_default_data_content_description));
    }

    public void setCarrierText(CharSequence text) {
        this.mCarrierText.setText(text);
    }
}
