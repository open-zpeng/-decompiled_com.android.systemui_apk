package com.android.systemui.qs.tileimpl;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settingslib.Utils;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSIconView;
import com.android.systemui.plugins.qs.QSTile;
import java.util.Objects;
/* loaded from: classes21.dex */
public class QSTileView extends QSTileBaseView {
    private static final boolean DUAL_TARGET_ALLOWED = false;
    private static final int MAX_LABEL_LINES = 2;
    private ColorStateList mColorLabelDefault;
    private ColorStateList mColorLabelUnavailable;
    private View mDivider;
    private View mExpandIndicator;
    private View mExpandSpace;
    protected TextView mLabel;
    private ViewGroup mLabelContainer;
    private ImageView mPadLock;
    protected TextView mSecondLine;
    private int mState;

    public QSTileView(Context context, QSIconView icon) {
        this(context, icon, false);
    }

    public QSTileView(Context context, QSIconView icon, boolean collapsedView) {
        super(context, icon, collapsedView);
        setClipChildren(false);
        setClipToPadding(false);
        setClickable(true);
        setId(View.generateViewId());
        createLabel();
        setOrientation(1);
        setGravity(49);
        this.mColorLabelDefault = Utils.getColorAttr(getContext(), 16842806);
        this.mColorLabelUnavailable = Utils.getColorAttr(getContext(), 16842808);
    }

    TextView getLabel() {
        return this.mLabel;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FontSizeUtils.updateFontSize(this.mLabel, R.dimen.qs_tile_text_size);
        FontSizeUtils.updateFontSize(this.mSecondLine, R.dimen.qs_tile_text_size);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileBaseView, com.android.systemui.plugins.qs.QSTileView
    public int getDetailY() {
        return getTop() + this.mLabelContainer.getTop() + (this.mLabelContainer.getHeight() / 2);
    }

    protected void createLabel() {
        this.mLabelContainer = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.qs_tile_label, (ViewGroup) this, false);
        this.mLabelContainer.setClipChildren(false);
        this.mLabelContainer.setClipToPadding(false);
        this.mLabel = (TextView) this.mLabelContainer.findViewById(R.id.tile_label);
        this.mPadLock = (ImageView) this.mLabelContainer.findViewById(R.id.restricted_padlock);
        this.mDivider = this.mLabelContainer.findViewById(R.id.underline);
        this.mExpandIndicator = this.mLabelContainer.findViewById(R.id.expand_indicator);
        this.mExpandSpace = this.mLabelContainer.findViewById(R.id.expand_space);
        this.mSecondLine = (TextView) this.mLabelContainer.findViewById(R.id.app_label);
        addView(this.mLabelContainer);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mLabel.getLineCount() > 2 || (!TextUtils.isEmpty(this.mSecondLine.getText()) && this.mSecondLine.getLineHeight() > this.mSecondLine.getHeight())) {
            this.mLabel.setSingleLine();
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileBaseView
    public void handleStateChanged(QSTile.State state) {
        super.handleStateChanged(state);
        if (!Objects.equals(this.mLabel.getText(), state.label) || this.mState != state.state) {
            this.mLabel.setTextColor(state.state == 0 ? this.mColorLabelUnavailable : this.mColorLabelDefault);
            this.mState = state.state;
            this.mLabel.setText(state.label);
        }
        if (!Objects.equals(this.mSecondLine.getText(), state.secondaryLabel)) {
            this.mSecondLine.setText(state.secondaryLabel);
            this.mSecondLine.setVisibility(TextUtils.isEmpty(state.secondaryLabel) ? 8 : 0);
        }
        this.mExpandIndicator.setVisibility(0 != 0 ? 0 : 8);
        this.mExpandSpace.setVisibility(0 != 0 ? 0 : 8);
        this.mLabelContainer.setContentDescription(0 != 0 ? state.dualLabelContentDescription : null);
        if (false != this.mLabelContainer.isClickable()) {
            this.mLabelContainer.setClickable(false);
            this.mLabelContainer.setLongClickable(false);
            this.mLabelContainer.setBackground(0 != 0 ? newTileBackground() : null);
        }
        this.mLabel.setEnabled(!state.disabledByPolicy);
        this.mPadLock.setVisibility(state.disabledByPolicy ? 0 : 8);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileBaseView
    public void init(View.OnClickListener click, View.OnClickListener secondaryClick, View.OnLongClickListener longClick) {
        super.init(click, secondaryClick, longClick);
        this.mLabelContainer.setOnClickListener(secondaryClick);
        this.mLabelContainer.setOnLongClickListener(longClick);
        this.mLabelContainer.setClickable(false);
        this.mLabelContainer.setLongClickable(false);
    }
}
