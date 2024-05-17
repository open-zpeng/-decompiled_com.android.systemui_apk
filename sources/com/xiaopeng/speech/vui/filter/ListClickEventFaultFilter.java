package com.xiaopeng.speech.vui.filter;

import android.text.TextUtils;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class ListClickEventFaultFilter extends BaseFaultFilter {
    @Override // com.xiaopeng.speech.vui.filter.IFilter
    public VuiElement doFilter(VuiElement vuiElement) {
        if (vuiElement != null && !TextUtils.isEmpty(vuiElement.getId())) {
            List<String> actions = vuiElement.getResultActions();
            if (actions.contains("Click")) {
                String id = vuiElement.getId();
                String[] id_position = id.split("_");
                if (id_position != null && id_position.length > 0) {
                    for (String position : id_position) {
                        if (position.length() <= 4) {
                            vuiElement.setResultActions(Arrays.asList("listItemClick"));
                        }
                    }
                }
            }
        }
        return vuiElement;
    }
}
