package com.android.systemui.tuner;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.plugins.IntentButtonProvider;
import com.android.systemui.statusbar.ScalingDrawableWrapper;
import com.android.systemui.statusbar.phone.ExpandableIndicator;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.tuner.LockscreenFragment;
import com.android.systemui.tuner.ShortcutParser;
import com.android.systemui.tuner.TunerService;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class LockscreenFragment extends PreferenceFragment {
    private static final String KEY_CUSTOMIZE = "customize";
    private static final String KEY_LEFT = "left";
    private static final String KEY_RIGHT = "right";
    private static final String KEY_SHORTCUT = "shortcut";
    public static final String LOCKSCREEN_LEFT_BUTTON = "sysui_keyguard_left";
    public static final String LOCKSCREEN_LEFT_UNLOCK = "sysui_keyguard_left_unlock";
    public static final String LOCKSCREEN_RIGHT_BUTTON = "sysui_keyguard_right";
    public static final String LOCKSCREEN_RIGHT_UNLOCK = "sysui_keyguard_right_unlock";
    private Handler mHandler;
    private final ArrayList<TunerService.Tunable> mTunables = new ArrayList<>();
    private TunerService mTunerService;

    @Override // androidx.preference.PreferenceFragment
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.mTunerService = (TunerService) Dependency.get(TunerService.class);
        this.mHandler = new Handler();
        addPreferencesFromResource(R.xml.lockscreen_settings);
        setupGroup(LOCKSCREEN_LEFT_BUTTON, LOCKSCREEN_LEFT_UNLOCK);
        setupGroup(LOCKSCREEN_RIGHT_BUTTON, LOCKSCREEN_RIGHT_UNLOCK);
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mTunables.forEach(new Consumer() { // from class: com.android.systemui.tuner.-$$Lambda$LockscreenFragment$Lo7jOQgOiEZ4M1LxVUxyoD69g0s
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                LockscreenFragment.this.lambda$onDestroy$0$LockscreenFragment((TunerService.Tunable) obj);
            }
        });
    }

    public /* synthetic */ void lambda$onDestroy$0$LockscreenFragment(TunerService.Tunable t) {
        this.mTunerService.removeTunable(t);
    }

    private void setupGroup(String buttonSetting, String unlockKey) {
        final Preference shortcut = findPreference(buttonSetting);
        final SwitchPreference unlock = (SwitchPreference) findPreference(unlockKey);
        addTunable(new TunerService.Tunable() { // from class: com.android.systemui.tuner.-$$Lambda$LockscreenFragment$Obd464MAoJT5uRv3BJuc47igR_Y
            @Override // com.android.systemui.tuner.TunerService.Tunable
            public final void onTuningChanged(String str, String str2) {
                LockscreenFragment.this.lambda$setupGroup$1$LockscreenFragment(unlock, shortcut, str, str2);
            }
        }, buttonSetting);
    }

    public /* synthetic */ void lambda$setupGroup$1$LockscreenFragment(SwitchPreference unlock, Preference shortcut, String k, String v) {
        boolean visible = !TextUtils.isEmpty(v);
        unlock.setVisible(visible);
        setSummary(shortcut, v);
    }

    private void showSelectDialog(final String buttonSetting) {
        RecyclerView v = (RecyclerView) LayoutInflater.from(getContext()).inflate(R.layout.tuner_shortcut_list, (ViewGroup) null);
        v.setLayoutManager(new LinearLayoutManager(getContext()));
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(v).show();
        Adapter adapter = new Adapter(getContext(), new Consumer() { // from class: com.android.systemui.tuner.-$$Lambda$LockscreenFragment$rUf2LVAaTrvvxJhtMQbvLS9ZFbI
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                LockscreenFragment.this.lambda$showSelectDialog$2$LockscreenFragment(buttonSetting, dialog, (LockscreenFragment.Item) obj);
            }
        });
        v.setAdapter(adapter);
    }

    public /* synthetic */ void lambda$showSelectDialog$2$LockscreenFragment(String buttonSetting, AlertDialog dialog, Item item) {
        this.mTunerService.setValue(buttonSetting, item.getSettingValue());
        dialog.dismiss();
    }

    private void setSummary(Preference shortcut, String value) {
        if (value == null) {
            shortcut.setSummary(R.string.lockscreen_none);
            return;
        }
        if (value.contains("::")) {
            ShortcutParser.Shortcut info = getShortcutInfo(getContext(), value);
            shortcut.setSummary(info != null ? info.label : null);
        } else if (value.contains("/")) {
            ActivityInfo info2 = getActivityinfo(getContext(), value);
            shortcut.setSummary(info2 != null ? info2.loadLabel(getContext().getPackageManager()) : null);
        } else {
            shortcut.setSummary(R.string.lockscreen_none);
        }
    }

    private void addTunable(TunerService.Tunable t, String... keys) {
        this.mTunables.add(t);
        this.mTunerService.addTunable(t, keys);
    }

    public static ActivityInfo getActivityinfo(Context context, String value) {
        ComponentName component = ComponentName.unflattenFromString(value);
        try {
            return context.getPackageManager().getActivityInfo(component, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static ShortcutParser.Shortcut getShortcutInfo(Context context, String value) {
        return ShortcutParser.Shortcut.create(context, value);
    }

    /* loaded from: classes21.dex */
    public static class Holder extends RecyclerView.ViewHolder {
        public final ExpandableIndicator expand;
        public final ImageView icon;
        public final TextView title;

        public Holder(View itemView) {
            super(itemView);
            this.icon = (ImageView) itemView.findViewById(16908294);
            this.title = (TextView) itemView.findViewById(16908310);
            this.expand = (ExpandableIndicator) itemView.findViewById(R.id.expand);
        }
    }

    /* loaded from: classes21.dex */
    private static class StaticShortcut extends Item {
        private final Context mContext;
        private final ShortcutParser.Shortcut mShortcut;

        public StaticShortcut(Context context, ShortcutParser.Shortcut shortcut) {
            super();
            this.mContext = context;
            this.mShortcut = shortcut;
        }

        @Override // com.android.systemui.tuner.LockscreenFragment.Item
        public Drawable getDrawable() {
            return this.mShortcut.icon.loadDrawable(this.mContext);
        }

        @Override // com.android.systemui.tuner.LockscreenFragment.Item
        public String getLabel() {
            return this.mShortcut.label;
        }

        @Override // com.android.systemui.tuner.LockscreenFragment.Item
        public String getSettingValue() {
            return this.mShortcut.toString();
        }

        @Override // com.android.systemui.tuner.LockscreenFragment.Item
        public Boolean getExpando() {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class App extends Item {
        private final ArrayList<Item> mChildren;
        private final Context mContext;
        private boolean mExpanded;
        private final LauncherActivityInfo mInfo;

        public App(Context context, LauncherActivityInfo info) {
            super();
            this.mChildren = new ArrayList<>();
            this.mContext = context;
            this.mInfo = info;
            this.mExpanded = false;
        }

        public void addChild(Item child) {
            this.mChildren.add(child);
        }

        @Override // com.android.systemui.tuner.LockscreenFragment.Item
        public Drawable getDrawable() {
            return this.mInfo.getBadgedIcon(this.mContext.getResources().getConfiguration().densityDpi);
        }

        @Override // com.android.systemui.tuner.LockscreenFragment.Item
        public String getLabel() {
            return this.mInfo.getLabel().toString();
        }

        @Override // com.android.systemui.tuner.LockscreenFragment.Item
        public String getSettingValue() {
            return this.mInfo.getComponentName().flattenToString();
        }

        @Override // com.android.systemui.tuner.LockscreenFragment.Item
        public Boolean getExpando() {
            if (this.mChildren.size() != 0) {
                return Boolean.valueOf(this.mExpanded);
            }
            return null;
        }

        @Override // com.android.systemui.tuner.LockscreenFragment.Item
        public void toggleExpando(final Adapter adapter) {
            this.mExpanded = !this.mExpanded;
            if (this.mExpanded) {
                this.mChildren.forEach(new Consumer() { // from class: com.android.systemui.tuner.-$$Lambda$LockscreenFragment$App$ETExpSuIeTllbJ9AB_3DTGOAJgk
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        LockscreenFragment.App.this.lambda$toggleExpando$0$LockscreenFragment$App(adapter, (LockscreenFragment.Item) obj);
                    }
                });
            } else {
                this.mChildren.forEach(new Consumer() { // from class: com.android.systemui.tuner.-$$Lambda$LockscreenFragment$App$KymmDZF-Q8mj0Qr5uc4akrkgskU
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        LockscreenFragment.Adapter.this.remItem((LockscreenFragment.Item) obj);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$toggleExpando$0$LockscreenFragment$App(Adapter adapter, Item child) {
            adapter.addItem(this, child);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static abstract class Item {
        public abstract Drawable getDrawable();

        public abstract Boolean getExpando();

        public abstract String getLabel();

        public abstract String getSettingValue();

        private Item() {
        }

        public void toggleExpando(Adapter adapter) {
        }
    }

    /* loaded from: classes21.dex */
    public static class Adapter extends RecyclerView.Adapter<Holder> {
        private final Consumer<Item> mCallback;
        private final Context mContext;
        private ArrayList<Item> mItems = new ArrayList<>();

        public Adapter(Context context, Consumer<Item> callback) {
            this.mContext = context;
            this.mCallback = callback;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tuner_shortcut_item, parent, false));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(final Holder holder, int position) {
            Item item = this.mItems.get(position);
            holder.icon.setImageDrawable(item.getDrawable());
            holder.title.setText(item.getLabel());
            holder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.tuner.-$$Lambda$LockscreenFragment$Adapter$VuIE2eL9-LHOyBflZw_Px7xwF04
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LockscreenFragment.Adapter.this.lambda$onBindViewHolder$0$LockscreenFragment$Adapter(holder, view);
                }
            });
            Boolean expando = item.getExpando();
            if (expando != null) {
                holder.expand.setVisibility(0);
                holder.expand.setExpanded(expando.booleanValue());
                holder.expand.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.tuner.-$$Lambda$LockscreenFragment$Adapter$fS6IuUEavDgpMOkDZLNh46UcUNQ
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        LockscreenFragment.Adapter.this.lambda$onBindViewHolder$1$LockscreenFragment$Adapter(holder, view);
                    }
                });
                return;
            }
            holder.expand.setVisibility(8);
        }

        public /* synthetic */ void lambda$onBindViewHolder$0$LockscreenFragment$Adapter(Holder holder, View v) {
            this.mCallback.accept(this.mItems.get(holder.getAdapterPosition()));
        }

        public /* synthetic */ void lambda$onBindViewHolder$1$LockscreenFragment$Adapter(Holder holder, View v) {
            this.mItems.get(holder.getAdapterPosition()).toggleExpando(this);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.mItems.size();
        }

        public void addItem(Item item) {
            this.mItems.add(item);
            notifyDataSetChanged();
        }

        public void remItem(Item item) {
            int index = this.mItems.indexOf(item);
            this.mItems.remove(item);
            notifyItemRemoved(index);
        }

        public void addItem(Item parent, Item child) {
            int index = this.mItems.indexOf(parent);
            this.mItems.add(index + 1, child);
            notifyItemInserted(index + 1);
        }
    }

    /* loaded from: classes21.dex */
    public static class LockButtonFactory implements ExtensionController.TunerFactory<IntentButtonProvider.IntentButton> {
        private final Context mContext;
        private final String mKey;

        @Override // com.android.systemui.statusbar.policy.ExtensionController.TunerFactory
        public /* bridge */ /* synthetic */ IntentButtonProvider.IntentButton create(Map map) {
            return create((Map<String, String>) map);
        }

        public LockButtonFactory(Context context, String key) {
            this.mContext = context;
            this.mKey = key;
        }

        @Override // com.android.systemui.statusbar.policy.ExtensionController.TunerFactory
        public String[] keys() {
            return new String[]{this.mKey};
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.systemui.statusbar.policy.ExtensionController.TunerFactory
        public IntentButtonProvider.IntentButton create(Map<String, String> settings) {
            ActivityInfo info;
            String buttonStr = settings.get(this.mKey);
            if (!TextUtils.isEmpty(buttonStr)) {
                if (buttonStr.contains("::")) {
                    ShortcutParser.Shortcut shortcut = LockscreenFragment.getShortcutInfo(this.mContext, buttonStr);
                    if (shortcut != null) {
                        return new ShortcutButton(this.mContext, shortcut);
                    }
                    return null;
                } else if (buttonStr.contains("/") && (info = LockscreenFragment.getActivityinfo(this.mContext, buttonStr)) != null) {
                    return new ActivityButton(this.mContext, info);
                } else {
                    return null;
                }
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class ShortcutButton implements IntentButtonProvider.IntentButton {
        private final IntentButtonProvider.IntentButton.IconState mIconState = new IntentButtonProvider.IntentButton.IconState();
        private final ShortcutParser.Shortcut mShortcut;

        public ShortcutButton(Context context, ShortcutParser.Shortcut shortcut) {
            this.mShortcut = shortcut;
            IntentButtonProvider.IntentButton.IconState iconState = this.mIconState;
            iconState.isVisible = true;
            iconState.drawable = shortcut.icon.loadDrawable(context).mutate();
            this.mIconState.contentDescription = this.mShortcut.label;
            int size = (int) TypedValue.applyDimension(1, 32.0f, context.getResources().getDisplayMetrics());
            IntentButtonProvider.IntentButton.IconState iconState2 = this.mIconState;
            iconState2.drawable = new ScalingDrawableWrapper(iconState2.drawable, size / this.mIconState.drawable.getIntrinsicWidth());
            this.mIconState.tint = false;
        }

        @Override // com.android.systemui.plugins.IntentButtonProvider.IntentButton
        public IntentButtonProvider.IntentButton.IconState getIcon() {
            return this.mIconState;
        }

        @Override // com.android.systemui.plugins.IntentButtonProvider.IntentButton
        public Intent getIntent() {
            return this.mShortcut.intent;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class ActivityButton implements IntentButtonProvider.IntentButton {
        private final IntentButtonProvider.IntentButton.IconState mIconState = new IntentButtonProvider.IntentButton.IconState();
        private final Intent mIntent;

        public ActivityButton(Context context, ActivityInfo info) {
            this.mIntent = new Intent().setComponent(new ComponentName(info.packageName, info.name));
            IntentButtonProvider.IntentButton.IconState iconState = this.mIconState;
            iconState.isVisible = true;
            iconState.drawable = info.loadIcon(context.getPackageManager()).mutate();
            this.mIconState.contentDescription = info.loadLabel(context.getPackageManager());
            int size = (int) TypedValue.applyDimension(1, 32.0f, context.getResources().getDisplayMetrics());
            IntentButtonProvider.IntentButton.IconState iconState2 = this.mIconState;
            iconState2.drawable = new ScalingDrawableWrapper(iconState2.drawable, size / this.mIconState.drawable.getIntrinsicWidth());
            this.mIconState.tint = false;
        }

        @Override // com.android.systemui.plugins.IntentButtonProvider.IntentButton
        public IntentButtonProvider.IntentButton.IconState getIcon() {
            return this.mIconState;
        }

        @Override // com.android.systemui.plugins.IntentButtonProvider.IntentButton
        public Intent getIntent() {
            return this.mIntent;
        }
    }
}
