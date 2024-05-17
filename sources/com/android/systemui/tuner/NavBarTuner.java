package com.android.systemui.tuner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.tuner.TunerService;
import java.util.ArrayList;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class NavBarTuner extends TunerPreferenceFragment {
    private static final String ICON = "icon";
    private static final int[][] ICONS = {new int[]{R.drawable.ic_qs_circle, R.string.tuner_circle}, new int[]{R.drawable.ic_add, R.string.tuner_plus}, new int[]{R.drawable.ic_remove, R.string.tuner_minus}, new int[]{R.drawable.ic_left, R.string.tuner_left}, new int[]{R.drawable.ic_right, R.string.tuner_right}, new int[]{R.drawable.ic_menu, R.string.tuner_menu}};
    private static final String KEYCODE = "keycode";
    private static final String LAYOUT = "layout";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String TYPE = "type";
    private Handler mHandler;
    private final ArrayList<TunerService.Tunable> mTunables = new ArrayList<>();

    @Override // androidx.preference.PreferenceFragment, android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        this.mHandler = new Handler();
        super.onCreate(savedInstanceState);
    }

    @Override // android.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override // androidx.preference.PreferenceFragment
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.nav_bar_tuner);
        bindLayout((ListPreference) findPreference(LAYOUT));
        bindButton(NavigationBarInflaterView.NAV_BAR_LEFT, NavigationBarInflaterView.NAVSPACE, "left");
        bindButton(NavigationBarInflaterView.NAV_BAR_RIGHT, NavigationBarInflaterView.MENU_IME_ROTATE, "right");
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mTunables.forEach(new Consumer() { // from class: com.android.systemui.tuner.-$$Lambda$NavBarTuner$tsKQ8HfwaDSvc3iDCsgHsW954hc
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((TunerService) Dependency.get(TunerService.class)).removeTunable((TunerService.Tunable) obj);
            }
        });
    }

    private void addTunable(TunerService.Tunable tunable, String... keys) {
        this.mTunables.add(tunable);
        ((TunerService) Dependency.get(TunerService.class)).addTunable(tunable, keys);
    }

    private void bindLayout(final ListPreference preference) {
        addTunable(new TunerService.Tunable() { // from class: com.android.systemui.tuner.-$$Lambda$NavBarTuner$nx5Q7aHowvZ9Bevy96_zeYYIxAY
            @Override // com.android.systemui.tuner.TunerService.Tunable
            public final void onTuningChanged(String str, String str2) {
                NavBarTuner.this.lambda$bindLayout$2$NavBarTuner(preference, str, str2);
            }
        }, NavigationBarInflaterView.NAV_BAR_VIEWS);
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.systemui.tuner.-$$Lambda$NavBarTuner$xJajVHN9uODpq3muoNpXW6-uxwc
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference2, Object obj) {
                return NavBarTuner.lambda$bindLayout$3(preference2, obj);
            }
        });
    }

    public /* synthetic */ void lambda$bindLayout$2$NavBarTuner(final ListPreference preference, String key, final String newValue) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.tuner.-$$Lambda$NavBarTuner$RQUqCpCXtFwKbIxFJ1AuU4K69W4
            @Override // java.lang.Runnable
            public final void run() {
                NavBarTuner.lambda$bindLayout$1(newValue, preference);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$bindLayout$1(String newValue, ListPreference preference) {
        String val = newValue;
        if (val == null) {
            val = "default";
        }
        preference.setValue(val);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$bindLayout$3(Preference preference1, Object newValue) {
        String val = (String) newValue;
        if ("default".equals(val)) {
            val = null;
        }
        ((TunerService) Dependency.get(TunerService.class)).setValue(NavigationBarInflaterView.NAV_BAR_VIEWS, val);
        return true;
    }

    private void bindButton(final String setting, final String def, String k) {
        final ListPreference type = (ListPreference) findPreference("type_" + k);
        final Preference keycode = findPreference("keycode_" + k);
        final ListPreference icon = (ListPreference) findPreference("icon_" + k);
        setupIcons(icon);
        addTunable(new TunerService.Tunable() { // from class: com.android.systemui.tuner.-$$Lambda$NavBarTuner$AtqwC3eDMLXM8PvQu0SrBbBcxZQ
            @Override // com.android.systemui.tuner.TunerService.Tunable
            public final void onTuningChanged(String str, String str2) {
                NavBarTuner.this.lambda$bindButton$5$NavBarTuner(def, type, icon, keycode, str, str2);
            }
        }, setting);
        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() { // from class: com.android.systemui.tuner.-$$Lambda$NavBarTuner$5vkJoJwaFUhdGZ7Fp4qtkLVqooo
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                return NavBarTuner.this.lambda$bindButton$7$NavBarTuner(setting, type, keycode, icon, preference, obj);
            }
        };
        type.setOnPreferenceChangeListener(listener);
        icon.setOnPreferenceChangeListener(listener);
        keycode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.systemui.tuner.-$$Lambda$NavBarTuner$VEefG8gxDDp8OSjE4w47bWNl4eQ
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return NavBarTuner.this.lambda$bindButton$9$NavBarTuner(keycode, setting, type, icon, preference);
            }
        });
    }

    public /* synthetic */ void lambda$bindButton$5$NavBarTuner(final String def, final ListPreference type, final ListPreference icon, final Preference keycode, String key, final String newValue) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.tuner.-$$Lambda$NavBarTuner$sQQgaEvmFdhni6jwm3oIAJf94Eo
            @Override // java.lang.Runnable
            public final void run() {
                NavBarTuner.this.lambda$bindButton$4$NavBarTuner(newValue, def, type, icon, keycode);
            }
        });
    }

    public /* synthetic */ void lambda$bindButton$4$NavBarTuner(String newValue, String def, ListPreference type, ListPreference icon, Preference keycode) {
        String val = newValue;
        if (val == null) {
            val = def;
        }
        String button = NavigationBarInflaterView.extractButton(val);
        if (button.startsWith("key")) {
            type.setValue("key");
            String uri = NavigationBarInflaterView.extractImage(button);
            int code = NavigationBarInflaterView.extractKeycode(button);
            icon.setValue(uri);
            updateSummary(icon);
            keycode.setSummary(code + "");
            keycode.setVisible(true);
            icon.setVisible(true);
            return;
        }
        type.setValue(button);
        keycode.setVisible(false);
        icon.setVisible(false);
    }

    public /* synthetic */ boolean lambda$bindButton$7$NavBarTuner(final String setting, final ListPreference type, final Preference keycode, final ListPreference icon, Preference preference, Object newValue) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.tuner.-$$Lambda$NavBarTuner$Q4QuzL1NB7uGZ3GCFspFwSEMA8g
            @Override // java.lang.Runnable
            public final void run() {
                NavBarTuner.this.lambda$bindButton$6$NavBarTuner(setting, type, keycode, icon);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$bindButton$6$NavBarTuner(String setting, ListPreference type, Preference keycode, ListPreference icon) {
        setValue(setting, type, keycode, icon);
        updateSummary(icon);
    }

    public /* synthetic */ boolean lambda$bindButton$9$NavBarTuner(final Preference keycode, final String setting, final ListPreference type, final ListPreference icon, Preference preference) {
        final EditText editText = new EditText(getContext());
        new AlertDialog.Builder(getContext()).setTitle(preference.getTitle()).setView(editText).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.systemui.tuner.-$$Lambda$NavBarTuner$oFwpdLrZA2BGC8nFWvjJ8NeCnQE
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                NavBarTuner.this.lambda$bindButton$8$NavBarTuner(editText, keycode, setting, type, icon, dialogInterface, i);
            }
        }).show();
        return true;
    }

    public /* synthetic */ void lambda$bindButton$8$NavBarTuner(EditText editText, Preference keycode, String setting, ListPreference type, ListPreference icon, DialogInterface dialog, int which) {
        int code = 66;
        try {
            code = Integer.parseInt(editText.getText().toString());
        } catch (Exception e) {
        }
        keycode.setSummary(code + "");
        setValue(setting, type, keycode, icon);
    }

    private void updateSummary(ListPreference icon) {
        try {
            int size = (int) TypedValue.applyDimension(1, 14.0f, getContext().getResources().getDisplayMetrics());
            String pkg = icon.getValue().split("/")[0];
            int id = Integer.parseInt(icon.getValue().split("/")[1]);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            Drawable d = Icon.createWithResource(pkg, id).loadDrawable(getContext());
            d.setTint(-16777216);
            d.setBounds(0, 0, size, size);
            ImageSpan span = new ImageSpan(d, 1);
            builder.append("  ", span, 0);
            builder.append((CharSequence) " ");
            for (int i = 0; i < ICONS.length; i++) {
                if (ICONS[i][0] == id) {
                    builder.append((CharSequence) getString(ICONS[i][1]));
                }
            }
            icon.setSummary(builder);
        } catch (Exception e) {
            Log.d("NavButton", "Problem with summary", e);
            icon.setSummary((CharSequence) null);
        }
    }

    private void setValue(String setting, ListPreference type, Preference keycode, ListPreference icon) {
        String button = type.getValue();
        if ("key".equals(button)) {
            String uri = icon.getValue();
            int code = 66;
            try {
                code = Integer.parseInt(keycode.getSummary().toString());
            } catch (Exception e) {
            }
            button = button + NavigationBarInflaterView.KEY_CODE_START + code + NavigationBarInflaterView.KEY_IMAGE_DELIM + uri + NavigationBarInflaterView.KEY_CODE_END;
        }
        ((TunerService) Dependency.get(TunerService.class)).setValue(setting, button);
    }

    private void setupIcons(ListPreference icon) {
        int[][] iArr = ICONS;
        CharSequence[] labels = new CharSequence[iArr.length];
        CharSequence[] values = new CharSequence[iArr.length];
        int size = (int) TypedValue.applyDimension(1, 14.0f, getContext().getResources().getDisplayMetrics());
        for (int i = 0; i < ICONS.length; i++) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            Drawable d = Icon.createWithResource(getContext().getPackageName(), ICONS[i][0]).loadDrawable(getContext());
            d.setTint(-16777216);
            d.setBounds(0, 0, size, size);
            ImageSpan span = new ImageSpan(d, 1);
            builder.append("  ", span, 0);
            builder.append((CharSequence) " ");
            builder.append((CharSequence) getString(ICONS[i][1]));
            labels[i] = builder;
            values[i] = getContext().getPackageName() + "/" + ICONS[i][0];
        }
        icon.setEntries(labels);
        icon.setEntryValues(values);
    }
}
