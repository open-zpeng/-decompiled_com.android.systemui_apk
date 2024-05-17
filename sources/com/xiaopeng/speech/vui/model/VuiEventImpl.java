package com.xiaopeng.speech.vui.model;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.vui.commons.model.VuiEvent;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class VuiEventImpl extends VuiEvent {
    private VuiElement data;
    private String userQuery = null;
    private String eventName = null;
    private String props = null;

    public VuiEventImpl(VuiElement data) {
        this.data = null;
        this.data = data;
    }

    @Override // com.xiaopeng.vui.commons.model.VuiEvent
    public <T> T getEventValue(VuiEvent vuiEvent) {
        VuiElement vuiElement;
        Map<String, Object> value;
        LogUtils.logDebug("getEventValue", new Gson().toJson(vuiEvent));
        if (vuiEvent == null || (vuiElement = vuiEvent.getHitVuiElement()) == null || vuiElement.getResultActions() == null || vuiElement.getResultActions().isEmpty()) {
            return null;
        }
        String key = vuiElement.getResultActions().get(0);
        if ((vuiElement.getValues() instanceof LinkedTreeMap) && (value = (Map) vuiElement.getValues()) != null) {
            if (value.get(key) instanceof LinkedTreeMap) {
                Map<String, Object> insideValue = (Map) value.get(key);
                if (insideValue != null) {
                    if (insideValue.containsKey(VuiConstants.ELEMENT_VALUE)) {
                        if (insideValue.get(VuiConstants.ELEMENT_VALUE) != null) {
                            return (T) insideValue.get(VuiConstants.ELEMENT_VALUE);
                        }
                    } else if (insideValue.containsKey("index") && insideValue.get("index") != null) {
                        return (T) insideValue.get("index");
                    }
                }
            } else if (value.get(value) != null) {
                return (T) value.get(VuiConstants.ELEMENT_VALUE);
            }
        }
        return null;
    }

    public String getUserQuery() {
        return this.userQuery;
    }

    public String getEventName() {
        return this.eventName;
    }

    @Override // com.xiaopeng.vui.commons.model.VuiEvent
    public VuiElement getHitVuiElement() {
        VuiElement vuiElement = this.data;
        if (vuiElement != null) {
            if (vuiElement.getResultActions() != null && !this.data.getResultActions().isEmpty()) {
                return this.data;
            }
            List<VuiElement> vuiElements = getHitVuiElements(this.data.getElements());
            if (vuiElements != null && !vuiElements.isEmpty()) {
                return vuiElements.get(0);
            }
            return null;
        }
        return null;
    }

    public VuiElement getMetaData() {
        return this.data;
    }

    public String getProps() {
        return this.props;
    }

    public void setUserQuery(String userQuery) {
        this.userQuery = userQuery;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setData(VuiElement data) {
        this.data = data;
    }

    public void setProps(String props) {
        this.props = props;
    }

    @Override // com.xiaopeng.vui.commons.model.VuiEvent
    public List<VuiElement> getHitVuiElements(List<VuiElement> vuiElements) {
        if (vuiElements == null || vuiElements.isEmpty()) {
            return vuiElements;
        }
        if (isLeafNode(vuiElements.get(0))) {
            return vuiElements;
        }
        VuiElement vui = vuiElements.get(0);
        if (vui == null) {
            return null;
        }
        return getHitVuiElements(vuiElements.get(0).getElements());
    }

    private boolean isLeafNode(VuiElement vuiElement) {
        if (vuiElement == null || vuiElement.getResultActions() == null || vuiElement.getResultActions().isEmpty()) {
            return false;
        }
        return true;
    }
}
