package com.xiaopeng.speech.vui.event;

import android.view.View;
import androidx.viewpager.widget.ViewPager;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.VuiAction;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.model.VuiElement;
/* loaded from: classes.dex */
public class ScrollByXEvent extends BaseEvent {
    @Override // com.xiaopeng.speech.vui.event.IVuiEvent
    public <T extends View> T run(T view, VuiElement vuiElement) {
        ViewPager viewPager;
        int extraPage;
        int extraPage2;
        if (view != null && vuiElement != null && vuiElement.getResultActions() != null && !vuiElement.getResultActions().isEmpty() && VuiAction.SCROLLBYX.getName().equals(vuiElement.getResultActions().get(0))) {
            String direction = (String) VuiUtils.getValueByName(vuiElement, VuiConstants.EVENT_VALUE_DIRECTION);
            if (direction == null) {
                return view;
            }
            if (view instanceof IVuiElement) {
                ((IVuiElement) view).setPerformVuiAction(true);
            }
            LogUtils.i("ScrollByXEvent run direction:" + direction);
            if (vuiElement.type.equals(VuiElementType.VIEWPAGER.getType())) {
                if (!(view instanceof ViewPager)) {
                    viewPager = VuiUtils.findViewPager(view);
                } else {
                    viewPager = (ViewPager) view;
                }
                if (viewPager != null) {
                    boolean canLeft = viewPager.canScrollHorizontally(-1);
                    boolean canRight = viewPager.canScrollHorizontally(1);
                    int nextItem = viewPager.getCurrentItem();
                    if ("left".equals(direction)) {
                        int nextItem2 = nextItem - 1;
                        if (canLeft) {
                            viewPager.setCurrentItem(nextItem2, false);
                        } else if (canRight && (extraPage2 = VuiUtils.getExtraPage(vuiElement)) != -1) {
                            viewPager.setCurrentItem((viewPager.getAdapter().getCount() - 1) - extraPage2, false);
                        }
                    } else {
                        int nextItem3 = nextItem + 1;
                        if (canRight) {
                            viewPager.setCurrentItem(nextItem3, false);
                        } else if (canLeft && (extraPage = VuiUtils.getExtraPage(vuiElement)) != -1) {
                            viewPager.setCurrentItem(extraPage, false);
                        }
                    }
                }
            } else {
                vuiElement.type.equals(VuiElementType.SCROLLVIEW.getType());
            }
            if (view instanceof IVuiElement) {
                ((IVuiElement) view).setPerformVuiAction(false);
            }
        }
        return view;
    }
}
