package com.android.systemui.settings;
/* loaded from: classes21.dex */
public interface ToggleSlider {

    /* loaded from: classes21.dex */
    public interface Listener {
        void onChanged(ToggleSlider toggleSlider, boolean z, boolean z2, int i, boolean z3);

        void onInit(ToggleSlider toggleSlider);
    }

    int getValue();

    void setMax(int i);

    void setOnChangedListener(Listener listener);

    void setValue(int i);

    default void setChecked(boolean checked) {
    }

    default boolean isChecked() {
        return false;
    }
}
