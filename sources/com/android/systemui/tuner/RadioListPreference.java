package com.android.systemui.tuner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.Utils;
import com.android.systemui.R;
import com.android.systemui.fragments.FragmentHostManager;
import java.util.Objects;
/* loaded from: classes21.dex */
public class RadioListPreference extends CustomListPreference {
    private DialogInterface.OnClickListener mOnClickListener;
    private CharSequence mSummary;

    public RadioListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.android.systemui.tuner.CustomListPreference
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    @Override // androidx.preference.ListPreference, androidx.preference.Preference
    public void setSummary(CharSequence summary) {
        super.setSummary(summary);
        this.mSummary = summary;
    }

    @Override // androidx.preference.ListPreference, androidx.preference.Preference
    public CharSequence getSummary() {
        CharSequence charSequence = this.mSummary;
        if (charSequence == null || charSequence.toString().contains("%s")) {
            return super.getSummary();
        }
        return this.mSummary;
    }

    @Override // com.android.systemui.tuner.CustomListPreference
    protected Dialog onDialogCreated(DialogFragment fragment, Dialog dialog) {
        final Dialog d = new Dialog(getContext(), 16974371);
        Toolbar t = (Toolbar) d.findViewById(16908781);
        View v = new View(getContext());
        v.setId(R.id.content);
        d.setContentView(v);
        t.setTitle(getTitle());
        t.setNavigationIcon(Utils.getDrawable(d.getContext(), 16843531));
        t.setNavigationOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.tuner.-$$Lambda$RadioListPreference$4DEUOALD3KxT1NUXowELf-5ZJ2M
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                d.dismiss();
            }
        });
        RadioFragment f = new RadioFragment();
        f.setPreference(this);
        FragmentHostManager.get(v).getFragmentManager().beginTransaction().add(16908290, f).commit();
        return d;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.tuner.CustomListPreference
    public void onDialogStateRestored(DialogFragment fragment, Dialog dialog, Bundle savedInstanceState) {
        super.onDialogStateRestored(fragment, dialog, savedInstanceState);
        View view = dialog.findViewById(R.id.content);
        RadioFragment radioFragment = (RadioFragment) FragmentHostManager.get(view).getFragmentManager().findFragmentById(R.id.content);
        if (radioFragment != null) {
            radioFragment.setPreference(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.tuner.CustomListPreference
    public void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
    }

    /* loaded from: classes21.dex */
    public static class RadioFragment extends TunerPreferenceFragment {
        private RadioListPreference mListPref;

        @Override // androidx.preference.PreferenceFragment
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            Context context = getPreferenceManager().getContext();
            PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);
            setPreferenceScreen(screen);
            if (this.mListPref != null) {
                update();
            }
        }

        private void update() {
            Context context = getPreferenceManager().getContext();
            CharSequence[] entries = this.mListPref.getEntries();
            CharSequence[] values = this.mListPref.getEntryValues();
            CharSequence current = this.mListPref.getValue();
            for (int i = 0; i < entries.length; i++) {
                CharSequence entry = entries[i];
                SelectablePreference pref = new SelectablePreference(context);
                getPreferenceScreen().addPreference(pref);
                pref.setTitle(entry);
                pref.setChecked(Objects.equals(current, values[i]));
                pref.setKey(String.valueOf(i));
            }
        }

        @Override // androidx.preference.PreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
        public boolean onPreferenceTreeClick(Preference preference) {
            this.mListPref.mOnClickListener.onClick(null, Integer.parseInt(preference.getKey()));
            return true;
        }

        public void setPreference(RadioListPreference radioListPreference) {
            this.mListPref = radioListPreference;
            if (getPreferenceManager() != null) {
                update();
            }
        }
    }
}
