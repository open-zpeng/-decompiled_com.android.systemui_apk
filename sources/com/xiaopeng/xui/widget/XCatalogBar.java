package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import com.xiaopeng.xpui.R;
/* loaded from: classes25.dex */
public class XCatalogBar extends XRadioGroup {
    private int[] mIcons;
    private String[] mNames;
    private int mOrientation;

    public XCatalogBar(Context context) {
        this(context, null);
    }

    public XCatalogBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XCatalogBar);
        int position = a.getInteger(R.styleable.XCatalogBar_catalog_check_position, 0);
        int names = a.getResourceId(R.styleable.XCatalogBar_catalog_text_array, 0);
        if (names != 0) {
            this.mNames = getResources().getStringArray(names);
        }
        int icons = a.getResourceId(R.styleable.XCatalogBar_catalog_icon_array, 0);
        if (icons != 0) {
            String[] iconsArray = getResources().getStringArray(icons);
            if (iconsArray.length > 0) {
                this.mIcons = new int[iconsArray.length];
                for (int i = 0; i < iconsArray.length; i++) {
                    this.mIcons[i] = getResources().getIdentifier(iconsArray[i], "drawable", context.getPackageName());
                }
            }
        }
        a.recycle();
        this.mOrientation = getResources().getConfiguration().orientation;
        init(position);
    }

    private void init(int position) {
        LayoutInflater inflate = LayoutInflater.from(getContext());
        for (int i = 0; i < this.mNames.length; i++) {
            RadioButton radioButton = (RadioButton) inflate.inflate(R.layout.x_catalogbar_item, (ViewGroup) this, false);
            radioButton.setText(this.mNames[i]);
            int[] iArr = this.mIcons;
            if (i < iArr.length) {
                if (this.mOrientation == 1) {
                    radioButton.setCompoundDrawablesWithIntrinsicBounds(0, iArr[i], 0, 0);
                } else {
                    radioButton.setCompoundDrawablesWithIntrinsicBounds(iArr[i], 0, 0, 0);
                }
            }
            radioButton.setId(i);
            addView(radioButton);
            if (position == i) {
                radioButton.setChecked(true);
            }
        }
    }

    public void addTabItem(@NonNull String text, @DrawableRes int icon) {
        addTabItem(text, icon, -1);
    }

    public void addTabItem(@NonNull CharSequence text, @DrawableRes int icon, int index) {
        LayoutInflater inflate = LayoutInflater.from(getContext());
        RadioButton radioButton = (RadioButton) inflate.inflate(R.layout.x_catalogbar_item, (ViewGroup) this, false);
        radioButton.setText(text);
        if (this.mOrientation == 1) {
            radioButton.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0);
        } else {
            radioButton.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        }
        radioButton.setId(getChildCount());
        addView(radioButton, index);
    }
}
