package com.xiaopeng.vui.commons;

import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public enum VuiActType {
    SEARCH("Search"),
    SELECT("Select"),
    EDIT("Edit"),
    OPEN("Open"),
    DELETE("Delete"),
    DETAIL("Detail"),
    EXPANDFOLD("ExpandFold"),
    ROLL("Roll"),
    TAB("Tab"),
    SELECTTAB("SelectTab"),
    SLIDE("Slide"),
    UP("Up"),
    DOWN("Down"),
    LEFT("Left"),
    RIGHT("Right"),
    SET("Set"),
    SORT("Sort"),
    EXPAND("Expand"),
    ADD("Add"),
    PLAY("Play"),
    NULL("Null");
    
    private String type;

    VuiActType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public static List<String> getVuiActTypeList() {
        VuiActType[] values;
        List<String> actTypeList = new ArrayList<>();
        for (VuiActType actType : values()) {
            actTypeList.add(actType.getType());
        }
        return actTypeList;
    }
}
