package com.xiaopeng.vui.commons;

import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public enum VuiAction {
    CLICK("Click"),
    SETVALUE("SetValue"),
    SCROLLBYX("ScrollByX"),
    SCROLLBYY("ScrollByY"),
    SETCHECK("SetCheck"),
    ITEMCLICK("ItemClick"),
    SELECTTAB("SelectTab"),
    SCROLLTO("ScrollTo"),
    SETSELECTED("SetSelected");
    
    private String name;

    VuiAction(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static List<String> getVuiActionList() {
        VuiAction[] values;
        List<String> actionList = new ArrayList<>();
        for (VuiAction action : values()) {
            actionList.add(action.getName());
        }
        return actionList;
    }
}
