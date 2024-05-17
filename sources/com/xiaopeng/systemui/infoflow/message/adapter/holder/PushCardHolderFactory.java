package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
/* loaded from: classes24.dex */
public class PushCardHolderFactory {
    public static final int ID_GREETING_MESSAGE = 4;
    public static final int ID_GREETING_TRAFFIC = 3;
    public static final int ID_GREETING_WEATHER = 2;
    private static final String TAG = "PushCardHolderFactory";

    public BaseCardHolder createCardHolder(ViewGroup parent, CardEntry cardEntry) {
        Log.i(TAG, "createCardHolder with status " + cardEntry.status);
        if (cardEntry.status == 2) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_weather, parent, false);
            return new WeatherCardHolder(itemView);
        }
        View itemView2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_push, parent, false);
        return new PushCardHolder(itemView2);
    }
}
