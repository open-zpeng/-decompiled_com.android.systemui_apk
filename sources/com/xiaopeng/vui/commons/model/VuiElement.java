package com.xiaopeng.vui.commons.model;

import android.text.TextUtils;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaopeng.vui.commons.VuiActType;
import com.xiaopeng.vui.commons.VuiAction;
import java.util.List;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class VuiElement {
    public JsonObject actions;
    public AnimationObj animation;
    private List<VuiElement> elements;
    private Boolean enabled;
    public String fatherElementId;
    public String fatherLabel;
    private String feedbackType;
    public String id;
    public String label;
    private Boolean layoutLoadable;
    private String mode;
    private int position;
    private int priority;
    private JsonObject props;
    private String resourceName;
    public List<String> resultActions;
    private long timestamp;
    public String type;
    public Object values;
    private Boolean visible;

    public void setValues(Object values) {
        this.values = values;
    }

    public void setLayoutLoadable(Boolean layoutLoadable) {
        this.layoutLoadable = layoutLoadable;
    }

    public Object getValues() {
        return this.values;
    }

    public boolean isLayoutLoadable() {
        Boolean bool = this.layoutLoadable;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public AnimationObj getAnimation() {
        return this.animation;
    }

    public void setAnimation(AnimationObj animation) {
        this.animation = animation;
    }

    public void setResultActions(List<String> resultActions) {
        this.resultActions = resultActions;
    }

    public List<String> getResultActions() {
        return this.resultActions;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setFatherElementId(String fatherElementId) {
        this.fatherElementId = fatherElementId;
    }

    public void setActions(String actions) {
        this.actions = getElementAction(actions);
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }

    public JsonObject getProps() {
        return this.props;
    }

    public String getId() {
        return this.id;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getLabel() {
        return this.label;
    }

    public String getFatherLabel() {
        return this.fatherLabel;
    }

    public String getType() {
        return this.type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setFatherLabel(String fatherLabel) {
        this.fatherLabel = fatherLabel;
    }

    public void setProps(JsonObject props) {
        this.props = props;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFatherElementId() {
        return this.fatherElementId;
    }

    public JsonObject getActions() {
        return this.actions;
    }

    public boolean isVisible() {
        return this.visible.booleanValue();
    }

    public boolean isEnabled() {
        return this.enabled.booleanValue();
    }

    public String getFeedbackType() {
        return this.feedbackType;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public List<VuiElement> getElements() {
        return this.elements;
    }

    public void setElements(List<VuiElement> elements) {
        this.elements = elements;
    }

    public VuiElement() {
        this.id = null;
        this.fatherElementId = null;
        this.label = "";
        this.fatherLabel = null;
        this.values = null;
        this.props = null;
        this.type = null;
        this.actions = null;
        this.layoutLoadable = null;
        this.resourceName = null;
        this.mode = null;
        this.resultActions = null;
        this.animation = null;
        this.position = -1;
        this.visible = null;
        this.enabled = null;
        this.feedbackType = null;
        this.timestamp = -1L;
        this.priority = -1;
        this.elements = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VuiElement{id='");
        sb.append(this.id);
        sb.append('\'');
        sb.append(", fatherElementId='");
        sb.append(this.fatherElementId);
        sb.append('\'');
        sb.append(", label='");
        sb.append(this.label);
        sb.append('\'');
        sb.append(", fatherLabel='");
        sb.append(this.fatherLabel);
        sb.append('\'');
        sb.append(", values=");
        sb.append(this.values);
        sb.append(", props=");
        sb.append(this.props);
        sb.append(", type='");
        sb.append(this.type);
        sb.append('\'');
        sb.append(", actions=");
        sb.append(this.actions);
        sb.append(", layoutLoadable=");
        sb.append(this.layoutLoadable);
        sb.append(", resourceName='");
        sb.append(this.resourceName);
        sb.append('\'');
        sb.append(", mode='");
        sb.append(this.mode);
        sb.append('\'');
        sb.append(", resultActions=");
        sb.append(this.resultActions);
        sb.append(", animation=");
        AnimationObj animationObj = this.animation;
        sb.append(animationObj == null ? "" : animationObj.toString());
        sb.append(", position=");
        sb.append(this.position);
        sb.append(", visible=");
        sb.append(this.visible);
        sb.append(", enabled=");
        sb.append(this.enabled);
        sb.append(", feedbackType='");
        sb.append(this.feedbackType);
        sb.append('\'');
        sb.append(", timestamp=");
        sb.append(this.timestamp);
        sb.append(", priority=");
        sb.append(this.priority);
        sb.append(", elements=");
        sb.append(this.elements);
        sb.append('}');
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof VuiElement) {
            VuiElement element = (VuiElement) obj;
            if (isEqualsOfString(this.type, element.getType()) && isEqualsOfString(this.label, element.getLabel()) && isEqualsOfString(this.fatherElementId, element.getFatherElementId()) && isEqualsOfString(this.fatherLabel, element.getFatherLabel()) && isEqualsOfString(this.mode, element.getMode()) && isEqualsOfString(this.resourceName, element.getResourceName()) && this.position == element.getPosition() && this.priority == element.getPriority() && isEqualsOfBoolean(this.layoutLoadable, element.layoutLoadable) && isEqualsOfBoolean(this.visible, element.visible) && isEqualsOfJson(this.actions, element.actions) && isEqualsOfJson(this.props, element.props) && isEqualsOfJson(this.values, element.values)) {
                if (this.elements == null && element.getElements() == null) {
                    return true;
                }
                if (this.elements == null || element.getElements() == null || this.elements.size() != element.getElements().size()) {
                    return false;
                }
                for (int i = 0; i < this.elements.size(); i++) {
                    if (!this.elements.get(i).equals(element.elements.get(i))) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean isEqualsOfString(String newOne, String oldOne) {
        if (TextUtils.isEmpty(newOne) && TextUtils.isEmpty(oldOne)) {
            return true;
        }
        return (TextUtils.isEmpty(newOne) || TextUtils.isEmpty(oldOne) || !newOne.equals(oldOne)) ? false : true;
    }

    private boolean isEqualsOfBoolean(Boolean newOne, Boolean oldOne) {
        if (newOne == null && oldOne == null) {
            return true;
        }
        if (newOne != null && oldOne != null && newOne.booleanValue() == oldOne.booleanValue()) {
            return true;
        }
        return false;
    }

    private boolean isEqualsOfJson(Object newOne, Object oldOne) {
        if (newOne == null && oldOne == null) {
            return true;
        }
        if (newOne != null && oldOne != null && newOne.equals(oldOne)) {
            return true;
        }
        return false;
    }

    public VuiElement(Builder builder) {
        this.id = null;
        this.fatherElementId = null;
        this.label = "";
        this.fatherLabel = null;
        this.values = null;
        this.props = null;
        this.type = null;
        this.actions = null;
        this.layoutLoadable = null;
        this.resourceName = null;
        this.mode = null;
        this.resultActions = null;
        this.animation = null;
        this.position = -1;
        this.visible = null;
        this.enabled = null;
        this.feedbackType = null;
        this.timestamp = -1L;
        this.priority = -1;
        this.elements = null;
        this.type = builder.type;
        this.label = builder.label;
        this.id = builder.id;
        this.timestamp = builder.timeStamp;
        this.actions = builder.actions;
        this.visible = builder.visible;
        this.enabled = builder.enable;
        this.props = builder.props;
        this.values = builder.values;
        this.resourceName = builder.resourceName;
    }

    public static JsonObject getElementAction(String actionStr) {
        if (actionStr == null) {
            return null;
        }
        String[] actionStrs = actionStr.split("\\|");
        JSONObject actionObj = new JSONObject();
        List<String> actionEnumList = VuiAction.getVuiActionList();
        List<String> actTypeEnumList = VuiActType.getVuiActTypeList();
        for (int i = 0; i < actionStrs.length; i++) {
            if (actionStrs[i].contains(NavigationBarInflaterView.KEY_IMAGE_DELIM)) {
                int index = actionStrs[i].indexOf(NavigationBarInflaterView.KEY_IMAGE_DELIM);
                String name = actionStrs[i].substring(0, index);
                if (!actionEnumList.contains(name)) {
                    return null;
                }
                String[] actExtends = actionStrs[i].substring(index + 1).split(",");
                if (actExtends.length < 2) {
                    return null;
                }
                try {
                    if (!actTypeEnumList.contains(actExtends[0])) {
                        return null;
                    }
                    JSONObject extActionObj = new JSONObject();
                    extActionObj.put("actType", actExtends[0]);
                    extActionObj.put("bizType", actExtends[1]);
                    actionObj.put(name, extActionObj);
                } catch (Exception e) {
                    return null;
                }
            } else {
                try {
                    if (!actionEnumList.contains(actionStrs[i])) {
                        return null;
                    }
                    actionObj.put(actionStrs[i], new JSONObject());
                } catch (Exception e2) {
                    return null;
                }
            }
        }
        return (JsonObject) new Gson().fromJson(actionObj.toString(), (Class<Object>) JsonObject.class);
    }

    /* loaded from: classes24.dex */
    public static class Builder {
        private String type = null;
        private String label = "";
        private String id = null;
        private long timeStamp = System.currentTimeMillis();
        private JsonObject actions = null;
        private Boolean visible = null;
        private Boolean enable = null;
        private Object values = null;
        private JsonObject props = null;
        private String resourceName = null;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder timestamp(long timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public Builder action(String action) {
            this.actions = VuiElement.getElementAction(action);
            return this;
        }

        public Builder visible(int visibility) {
            if (visibility != 0) {
                this.visible = false;
            }
            return this;
        }

        public Builder enable(boolean enable) {
            if (!enable) {
            }
            return this;
        }

        public Builder value(Object value) {
            this.values = value;
            return this;
        }

        public Builder props(JsonObject props) {
            this.props = props;
            return this;
        }

        public Builder resourceName(String name) {
            this.resourceName = name;
            return this;
        }

        public VuiElement build() {
            return new VuiElement(this);
        }
    }
}
