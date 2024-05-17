package com.android.systemui.qs.tiles;

import android.content.Intent;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.NightDisplayListener;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Switch;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import java.text.DateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.TimeZone;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class NightDisplayTile extends QSTileImpl<QSTile.BooleanState> implements NightDisplayListener.Callback {
    private static final String PATTERN_HOUR = "h a";
    private static final String PATTERN_HOUR_MINUTE = "h:mm a";
    private static final String PATTERN_HOUR_NINUTE_24 = "HH:mm";
    private boolean mIsListening;
    private NightDisplayListener mListener;
    private final ColorDisplayManager mManager;

    @Inject
    public NightDisplayTile(QSHost host) {
        super(host);
        this.mManager = (ColorDisplayManager) this.mContext.getSystemService(ColorDisplayManager.class);
        this.mListener = new NightDisplayListener(this.mContext, new Handler(Looper.myLooper()));
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public boolean isAvailable() {
        return ColorDisplayManager.isNightDisplayAvailable(this.mContext);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleClick() {
        if ("1".equals(Settings.Global.getString(this.mContext.getContentResolver(), "night_display_forced_auto_mode_available")) && this.mManager.getNightDisplayAutoModeRaw() == -1) {
            this.mManager.setNightDisplayAutoMode(1);
            Log.i("NightDisplayTile", "Enrolled in forced night display auto mode");
        }
        boolean activated = !((QSTile.BooleanState) this.mState).value;
        this.mManager.setNightDisplayActivated(activated);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUserSwitch(int newUserId) {
        if (this.mIsListening) {
            this.mListener.setCallback((NightDisplayListener.Callback) null);
        }
        this.mListener = new NightDisplayListener(this.mContext, newUserId, new Handler(Looper.myLooper()));
        if (this.mIsListening) {
            this.mListener.setCallback(this);
        }
        super.handleUserSwitch(newUserId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUpdateState(QSTile.BooleanState state, Object arg) {
        state.value = this.mManager.isNightDisplayActivated();
        state.label = this.mContext.getString(R.string.quick_settings_night_display_label);
        state.icon = QSTileImpl.ResourceIcon.get(17302789);
        state.expandedAccessibilityClassName = Switch.class.getName();
        state.state = state.value ? 2 : 1;
        state.secondaryLabel = getSecondaryLabel(state.value);
        state.contentDescription = TextUtils.isEmpty(state.secondaryLabel) ? state.label : TextUtils.concat(state.label, ", ", state.secondaryLabel);
    }

    private String getSecondaryLabel(boolean isNightLightActivated) {
        LocalTime toggleTime;
        int toggleTimeStringRes;
        int nightDisplayAutoMode = this.mManager.getNightDisplayAutoMode();
        if (nightDisplayAutoMode != 1) {
            if (nightDisplayAutoMode == 2) {
                if (isNightLightActivated) {
                    return this.mContext.getString(R.string.quick_settings_night_secondary_label_until_sunrise);
                }
                return this.mContext.getString(R.string.quick_settings_night_secondary_label_on_at_sunset);
            }
            return null;
        }
        if (isNightLightActivated) {
            toggleTime = this.mManager.getNightDisplayCustomEndTime();
            toggleTimeStringRes = R.string.quick_settings_secondary_label_until;
        } else {
            toggleTime = this.mManager.getNightDisplayCustomStartTime();
            toggleTimeStringRes = R.string.quick_settings_night_secondary_label_on_at;
        }
        Calendar c = Calendar.getInstance();
        DateFormat nightTileFormat = android.text.format.DateFormat.getTimeFormat(this.mContext);
        nightTileFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        c.setTimeZone(nightTileFormat.getTimeZone());
        c.set(11, toggleTime.getHour());
        c.set(12, toggleTime.getMinute());
        c.set(13, 0);
        c.set(14, 0);
        return this.mContext.getString(toggleTimeStringRes, nightTileFormat.format(c.getTime()));
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return 491;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public LogMaker populate(LogMaker logMaker) {
        return super.populate(logMaker).addTaggedData(1311, Integer.valueOf(this.mManager.getNightDisplayAutoModeRaw()));
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public Intent getLongClickIntent() {
        return new Intent("android.settings.NIGHT_DISPLAY_SETTINGS");
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleSetListening(boolean listening) {
        this.mIsListening = listening;
        if (listening) {
            this.mListener.setCallback(this);
            refreshState();
            return;
        }
        this.mListener.setCallback((NightDisplayListener.Callback) null);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return this.mContext.getString(R.string.quick_settings_night_display_label);
    }

    public void onActivated(boolean activated) {
        refreshState();
    }
}
