package com.android.systemui.statusbar.phone;

import android.view.View;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes21.dex */
public class ContextualButtonGroup extends ButtonDispatcher {
    private static final int INVALID_INDEX = -1;
    private final List<ButtonData> mButtonData;

    public ContextualButtonGroup(int containerId) {
        super(containerId);
        this.mButtonData = new ArrayList();
    }

    public void addButton(ContextualButton button) {
        button.attachToGroup(this);
        this.mButtonData.add(new ButtonData(button));
    }

    public ContextualButton getContextButton(int buttonResId) {
        int index = getContextButtonIndex(buttonResId);
        if (index != -1) {
            return this.mButtonData.get(index).button;
        }
        return null;
    }

    public ContextualButton getVisibleContextButton() {
        for (int i = this.mButtonData.size() - 1; i >= 0; i--) {
            if (this.mButtonData.get(i).markedVisible) {
                return this.mButtonData.get(i).button;
            }
        }
        return null;
    }

    public int setButtonVisibility(int buttonResId, boolean visible) {
        int index = getContextButtonIndex(buttonResId);
        if (index == -1) {
            throw new RuntimeException("Cannot find the button id of " + buttonResId + " in context group");
        }
        setVisibility(4);
        this.mButtonData.get(index).markedVisible = visible;
        boolean alreadyFoundVisibleButton = false;
        for (int i = this.mButtonData.size() - 1; i >= 0; i--) {
            ButtonData buttonData = this.mButtonData.get(i);
            if (!alreadyFoundVisibleButton && buttonData.markedVisible) {
                buttonData.setVisibility(0);
                setVisibility(0);
                alreadyFoundVisibleButton = true;
            } else {
                buttonData.setVisibility(4);
            }
        }
        return this.mButtonData.get(index).button.getVisibility();
    }

    public boolean isButtonVisibleWithinGroup(int buttonResId) {
        int index = getContextButtonIndex(buttonResId);
        return index != -1 && this.mButtonData.get(index).markedVisible;
    }

    public void updateIcons() {
        for (ButtonData data : this.mButtonData) {
            data.button.updateIcon();
        }
    }

    public void dump(PrintWriter pw) {
        View view = getCurrentView();
        pw.println("ContextualButtonGroup {");
        pw.println("      getVisibleContextButton(): " + getVisibleContextButton());
        pw.println("      isVisible(): " + isVisible());
        StringBuilder sb = new StringBuilder();
        sb.append("      attached(): ");
        sb.append(view != null && view.isAttachedToWindow());
        pw.println(sb.toString());
        pw.println("      mButtonData [ ");
        for (int i = this.mButtonData.size() - 1; i >= 0; i--) {
            ButtonData data = this.mButtonData.get(i);
            View view2 = data.button.getCurrentView();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("            ");
            sb2.append(i);
            sb2.append(": markedVisible=");
            sb2.append(data.markedVisible);
            sb2.append(" visible=");
            sb2.append(data.button.getVisibility());
            sb2.append(" attached=");
            sb2.append(view2 != null && view2.isAttachedToWindow());
            sb2.append(" alpha=");
            sb2.append(data.button.getAlpha());
            pw.println(sb2.toString());
        }
        pw.println("      ]");
        pw.println("    }");
    }

    private int getContextButtonIndex(int buttonResId) {
        for (int i = 0; i < this.mButtonData.size(); i++) {
            if (this.mButtonData.get(i).button.getId() == buttonResId) {
                return i;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static final class ButtonData {
        ContextualButton button;
        boolean markedVisible = false;

        ButtonData(ContextualButton button) {
            this.button = button;
        }

        void setVisibility(int visiblity) {
            this.button.setVisibility(visiblity);
        }
    }
}
