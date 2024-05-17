package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.KeyValueListParser;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R;
import com.xiaopeng.speech.protocol.event.OOBEEvent;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public final class SmartReplyConstants {
    private static final String TAG = "SmartReplyConstants";
    private final Context mContext;
    private final boolean mDefaultEditChoicesBeforeSending;
    private final boolean mDefaultEnabled;
    private final int mDefaultMaxNumActions;
    private final int mDefaultMaxSqueezeRemeasureAttempts;
    private final int mDefaultMinNumSystemGeneratedReplies;
    private final int mDefaultOnClickInitDelay;
    private final boolean mDefaultRequiresP;
    private final boolean mDefaultShowInHeadsUp;
    private volatile boolean mEditChoicesBeforeSending;
    private volatile boolean mEnabled;
    private final Handler mHandler;
    private volatile int mMaxNumActions;
    private volatile int mMaxSqueezeRemeasureAttempts;
    private volatile int mMinNumSystemGeneratedReplies;
    private volatile long mOnClickInitDelay;
    private final KeyValueListParser mParser = new KeyValueListParser(',');
    private volatile boolean mRequiresTargetingP;
    private volatile boolean mShowInHeadsUp;

    @Inject
    public SmartReplyConstants(@Named("main_handler") Handler handler, Context context) {
        this.mHandler = handler;
        this.mContext = context;
        Resources resources = this.mContext.getResources();
        this.mDefaultEnabled = resources.getBoolean(R.bool.config_smart_replies_in_notifications_enabled);
        this.mDefaultRequiresP = resources.getBoolean(R.bool.config_smart_replies_in_notifications_requires_targeting_p);
        this.mDefaultMaxSqueezeRemeasureAttempts = resources.getInteger(R.integer.config_smart_replies_in_notifications_max_squeeze_remeasure_attempts);
        this.mDefaultEditChoicesBeforeSending = resources.getBoolean(R.bool.config_smart_replies_in_notifications_edit_choices_before_sending);
        this.mDefaultShowInHeadsUp = resources.getBoolean(R.bool.config_smart_replies_in_notifications_show_in_heads_up);
        this.mDefaultMinNumSystemGeneratedReplies = resources.getInteger(R.integer.config_smart_replies_in_notifications_min_num_system_generated_replies);
        this.mDefaultMaxNumActions = resources.getInteger(R.integer.config_smart_replies_in_notifications_max_num_actions);
        this.mDefaultOnClickInitDelay = resources.getInteger(R.integer.config_smart_replies_in_notifications_onclick_init_delay);
        registerDeviceConfigListener();
        updateConstants();
    }

    private void registerDeviceConfigListener() {
        DeviceConfig.addOnPropertiesChangedListener("systemui", new Executor() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$SmartReplyConstants$6OXW9pAAXeePuUfPuGxYU98bifc
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                SmartReplyConstants.this.postToHandler(runnable);
            }
        }, new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$SmartReplyConstants$4opg-Q5IrqXO7Mn9_Fp2x2nMZNY
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                SmartReplyConstants.this.lambda$registerDeviceConfigListener$0$SmartReplyConstants(properties);
            }
        });
    }

    public /* synthetic */ void lambda$registerDeviceConfigListener$0$SmartReplyConstants(DeviceConfig.Properties properties) {
        onDeviceConfigPropertiesChanged(properties.getNamespace());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postToHandler(Runnable r) {
        this.mHandler.post(r);
    }

    @VisibleForTesting
    void onDeviceConfigPropertiesChanged(String namespace) {
        if (!"systemui".equals(namespace)) {
            Log.e(TAG, "Received update from DeviceConfig for unrelated namespace: " + namespace);
            return;
        }
        updateConstants();
    }

    private void updateConstants() {
        synchronized (this) {
            this.mEnabled = readDeviceConfigBooleanOrDefaultIfEmpty("ssin_enabled", this.mDefaultEnabled);
            this.mRequiresTargetingP = readDeviceConfigBooleanOrDefaultIfEmpty("ssin_requires_targeting_p", this.mDefaultRequiresP);
            this.mMaxSqueezeRemeasureAttempts = DeviceConfig.getInt("systemui", "ssin_max_squeeze_remeasure_attempts", this.mDefaultMaxSqueezeRemeasureAttempts);
            this.mEditChoicesBeforeSending = readDeviceConfigBooleanOrDefaultIfEmpty("ssin_edit_choices_before_sending", this.mDefaultEditChoicesBeforeSending);
            this.mShowInHeadsUp = readDeviceConfigBooleanOrDefaultIfEmpty("ssin_show_in_heads_up", this.mDefaultShowInHeadsUp);
            this.mMinNumSystemGeneratedReplies = DeviceConfig.getInt("systemui", "ssin_min_num_system_generated_replies", this.mDefaultMinNumSystemGeneratedReplies);
            this.mMaxNumActions = DeviceConfig.getInt("systemui", "ssin_max_num_actions", this.mDefaultMaxNumActions);
            this.mOnClickInitDelay = DeviceConfig.getInt("systemui", "ssin_onclick_init_delay", this.mDefaultOnClickInitDelay);
        }
    }

    private static boolean readDeviceConfigBooleanOrDefaultIfEmpty(String propertyName, boolean defaultValue) {
        String value = DeviceConfig.getProperty("systemui", propertyName);
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        if (OOBEEvent.STRING_TRUE.equals(value)) {
            return true;
        }
        if (OOBEEvent.STRING_FALSE.equals(value)) {
            return false;
        }
        return defaultValue;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public boolean requiresTargetingP() {
        return this.mRequiresTargetingP;
    }

    public int getMaxSqueezeRemeasureAttempts() {
        return this.mMaxSqueezeRemeasureAttempts;
    }

    public boolean getEffectiveEditChoicesBeforeSending(int remoteInputEditChoicesBeforeSending) {
        if (remoteInputEditChoicesBeforeSending != 1) {
            if (remoteInputEditChoicesBeforeSending == 2) {
                return true;
            }
            return this.mEditChoicesBeforeSending;
        }
        return false;
    }

    public boolean getShowInHeadsUp() {
        return this.mShowInHeadsUp;
    }

    public int getMinNumSystemGeneratedReplies() {
        return this.mMinNumSystemGeneratedReplies;
    }

    public int getMaxNumActions() {
        return this.mMaxNumActions;
    }

    public long getOnClickInitDelay() {
        return this.mOnClickInitDelay;
    }
}
