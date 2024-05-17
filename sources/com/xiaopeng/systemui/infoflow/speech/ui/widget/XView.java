package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.util.CommonUtils;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.VuiFeedbackType;
import com.xiaopeng.vui.commons.VuiMode;
import com.xiaopeng.vui.commons.VuiPriority;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class XView extends View implements IVuiElement {
    private Context context;
    private boolean performVuiAction;
    private String vuiAction;
    private boolean vuiDynamic;
    private String vuiElementId;
    private VuiElementType vuiElementType;
    private boolean vuiEnabled;
    private String vuiFatherElementId;
    private String vuiFatherLabel;
    private VuiFeedbackType vuiFeedbackType;
    private String vuiLabel;
    private Integer vuiPosition;
    private VuiPriority vuiPriority;
    private JSONObject vuiProps;

    public XView(Context context) {
        this(context, null);
    }

    public XView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public XView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    @TargetApi(21)
    public XView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        TypedArray ta = this.context.obtainStyledAttributes(attrs, R.styleable.xui);
        this.vuiAction = ta.getString(1);
        this.vuiElementType = CommonUtils.getElementType(ta.getInteger(4, -1));
        this.vuiPosition = Integer.valueOf(ta.getInteger(10, -1));
        this.vuiFatherElementId = ta.getString(6);
        this.vuiLabel = ta.getString(9);
        this.vuiFatherLabel = ta.getString(7);
        this.vuiElementId = ta.getString(3);
        this.vuiDynamic = ta.getBoolean(2, false);
        this.vuiEnabled = ta.getBoolean(5, true);
        int priority = ta.getInt(11, 2);
        this.vuiPriority = CommonUtils.getViewLeveByPriority(priority);
        this.vuiFeedbackType = CommonUtils.getFeedbackType(ta.getInteger(8, 1));
        ta.recycle();
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public String getVuiFatherLabel() {
        return this.vuiFatherLabel;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiFatherLabel(String vuiFatherLabel) {
        this.vuiFatherLabel = vuiFatherLabel;
    }

    public boolean isVuiDynamic() {
        return this.vuiDynamic;
    }

    public void setVuiDynamic(boolean vuiDynamic) {
        this.vuiDynamic = vuiDynamic;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public VuiMode getVuiMode() {
        return VuiMode.NORMAL;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiMode(VuiMode vuiMode) {
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public boolean isVuiLayoutLoadable() {
        return false;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiLayoutLoadable(boolean b) {
    }

    public boolean isVuiEnabled() {
        return this.vuiEnabled;
    }

    public void setVuiEnabled(boolean vuiEnabled) {
        this.vuiEnabled = vuiEnabled;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public VuiPriority getVuiPriority() {
        return this.vuiPriority;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiPriority(VuiPriority vuiPriority) {
        this.vuiPriority = vuiPriority;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public String getVuiAction() {
        return this.vuiAction;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiAction(String vuiAction) {
        this.vuiAction = vuiAction;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public VuiElementType getVuiElementType() {
        return this.vuiElementType;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiElementType(VuiElementType vuiElementType) {
        this.vuiElementType = vuiElementType;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public String getVuiFatherElementId() {
        return this.vuiFatherElementId;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiFatherElementId(String vuiFatherElementId) {
        this.vuiFatherElementId = vuiFatherElementId;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public String getVuiLabel() {
        return this.vuiLabel;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiLabel(String vuiLabel) {
        this.vuiLabel = vuiLabel;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public String getVuiElementId() {
        return this.vuiElementId;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiElementId(String vuiElementId) {
        this.vuiElementId = vuiElementId;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiPosition(int vuiPosition) {
        this.vuiPosition = Integer.valueOf(vuiPosition);
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public int getVuiPosition() {
        return this.vuiPosition.intValue();
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public VuiFeedbackType getVuiFeedbackType() {
        return this.vuiFeedbackType;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiFeedbackType(VuiFeedbackType vuiFeedbackType) {
        this.vuiFeedbackType = vuiFeedbackType;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public boolean isPerformVuiAction() {
        return this.performVuiAction;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setPerformVuiAction(boolean performVuiAction) {
        this.performVuiAction = performVuiAction;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiProps(JSONObject vuiProps) {
        this.vuiProps = vuiProps;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public JSONObject getVuiProps() {
        return this.vuiProps;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiBizId(String s) {
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public String getVuiBizId() {
        return null;
    }
}
