package com.xiaopeng.systemui.qs.tilemodels;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes24.dex */
public class BackBoxTileModel extends ContentProviderTileModel {
    private static final String KEY = "open_close_back_box";

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface State {
        public static final int CLOSED = 1;
        public static final int CLOSING = 4;
        public static final int CLOSING_PAUSE = 6;
        public static final int INIT = -1;
        public static final int MIN_OPEN_ANGLE = 8;
        public static final int OPENED = 2;
        public static final int OPENING = 3;
        public static final int OPENING_PAUSE = 5;
        public static final int PREPARE = 7;
    }

    public BackBoxTileModel(String tileSpec) {
        super(tileSpec);
    }
}
