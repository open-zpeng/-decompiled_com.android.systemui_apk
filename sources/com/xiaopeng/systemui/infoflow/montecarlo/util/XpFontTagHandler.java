package com.xiaopeng.systemui.infoflow.montecarlo.util;

import android.text.Editable;
import android.text.Html;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Stack;
import org.xml.sax.XMLReader;
/* loaded from: classes24.dex */
public class XpFontTagHandler implements Html.TagHandler {
    private DisplayMetrics mDisplayMetrics;
    private Stack<String> mPropertyValue;
    private Stack<Integer> mStartIndex;

    public XpFontTagHandler(DisplayMetrics displayMetrics) {
        this.mDisplayMetrics = displayMetrics;
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    @Override // android.text.Html.TagHandler
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (opening) {
            handlerStartTAG(tag, output, xmlReader);
        } else {
            handlerEndTAG(tag, output);
        }
    }

    private void handlerStartTAG(String tag, Editable output, XMLReader xmlReader) {
        if (tag.equalsIgnoreCase("xpsize")) {
            handlerStartSIZE(output, xmlReader);
        } else if (tag.equalsIgnoreCase("xpface")) {
            handlerStartFAMILY(output, xmlReader);
        }
    }

    private void handlerEndTAG(String tag, Editable output) {
        if (tag.equalsIgnoreCase("xpsize")) {
            handlerEndSIZE(output);
        } else if (tag.equalsIgnoreCase("xpface")) {
            handlerEndFAMILY(output);
        }
    }

    private void handlerStartSIZE(Editable output, XMLReader xmlReader) {
        if (this.mStartIndex == null) {
            this.mStartIndex = new Stack<>();
        }
        this.mStartIndex.push(Integer.valueOf(output.length()));
        if (this.mPropertyValue == null) {
            this.mPropertyValue = new Stack<>();
        }
        this.mPropertyValue.push(getProperty(xmlReader, VuiConstants.ELEMENT_VALUE));
    }

    private void handlerEndSIZE(Editable output) {
        if (!isEmpty(this.mPropertyValue)) {
            try {
                int value = Integer.parseInt(this.mPropertyValue.pop());
                output.setSpan(new AbsoluteSizeSpan(sp2px(value)), this.mStartIndex.pop().intValue(), output.length(), 33);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handlerStartFAMILY(Editable output, XMLReader xmlReader) {
        if (this.mStartIndex == null) {
            this.mStartIndex = new Stack<>();
        }
        this.mStartIndex.push(Integer.valueOf(output.length()));
        if (this.mPropertyValue == null) {
            this.mPropertyValue = new Stack<>();
        }
        this.mPropertyValue.push(getProperty(xmlReader, VuiConstants.ELEMENT_VALUE));
    }

    private void handlerEndFAMILY(Editable output) {
        if (!isEmpty(this.mPropertyValue)) {
            try {
                String value = this.mPropertyValue.pop();
                output.setSpan(new TypefaceSpan(value), this.mStartIndex.pop().intValue(), output.length(), 33);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getProperty(XMLReader xmlReader, String property) {
        try {
            Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            Object element = elementField.get(xmlReader);
            Field attsField = element.getClass().getDeclaredField("theAtts");
            attsField.setAccessible(true);
            Object atts = attsField.get(element);
            Field dataField = atts.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            String[] data = (String[]) dataField.get(atts);
            Field lengthField = atts.getClass().getDeclaredField("length");
            lengthField.setAccessible(true);
            int len = ((Integer) lengthField.get(atts)).intValue();
            for (int i = 0; i < len; i++) {
                if (property.equals(data[(i * 5) + 1])) {
                    return data[(i * 5) + 4];
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int sp2px(float spValue) {
        return (int) (TypedValue.applyDimension(2, spValue, this.mDisplayMetrics) + 0.5f);
    }
}
