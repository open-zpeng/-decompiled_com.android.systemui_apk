package com.android.systemui.tuner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import androidx.preference.ListPreference;
import androidx.preference.ListPreferenceDialogFragment;
/* loaded from: classes21.dex */
public class CustomListPreference extends ListPreference {
    public CustomListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener listener) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDialogClosed(boolean positiveResult) {
    }

    protected Dialog onDialogCreated(DialogFragment fragment, Dialog dialog) {
        return dialog;
    }

    protected boolean isAutoClosePreference() {
        return true;
    }

    protected CharSequence getConfirmationMessage(String value) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDialogStateRestored(DialogFragment fragment, Dialog dialog, Bundle savedInstanceState) {
    }

    /* loaded from: classes21.dex */
    public static class CustomListPreferenceDialogFragment extends ListPreferenceDialogFragment {
        private static final String KEY_CLICKED_ENTRY_INDEX = "settings.CustomListPrefDialog.KEY_CLICKED_ENTRY_INDEX";
        private int mClickedDialogEntryIndex;

        public static ListPreferenceDialogFragment newInstance(String key) {
            ListPreferenceDialogFragment fragment = new CustomListPreferenceDialogFragment();
            Bundle b = new Bundle(1);
            b.putString("key", key);
            fragment.setArguments(b);
            return fragment;
        }

        public CustomListPreference getCustomizablePreference() {
            return (CustomListPreference) getPreference();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.preference.ListPreferenceDialogFragment, androidx.preference.PreferenceDialogFragment
        public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
            super.onPrepareDialogBuilder(builder);
            this.mClickedDialogEntryIndex = getCustomizablePreference().findIndexOfValue(getCustomizablePreference().getValue());
            getCustomizablePreference().onPrepareDialogBuilder(builder, getOnItemClickListener());
            if (!getCustomizablePreference().isAutoClosePreference()) {
                builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.systemui.tuner.CustomListPreference.CustomListPreferenceDialogFragment.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        CustomListPreferenceDialogFragment.this.onItemConfirmed();
                    }
                });
            }
        }

        @Override // androidx.preference.PreferenceDialogFragment, android.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            if (savedInstanceState != null) {
                this.mClickedDialogEntryIndex = savedInstanceState.getInt(KEY_CLICKED_ENTRY_INDEX, this.mClickedDialogEntryIndex);
            }
            return getCustomizablePreference().onDialogCreated(this, dialog);
        }

        @Override // androidx.preference.ListPreferenceDialogFragment, androidx.preference.PreferenceDialogFragment, android.app.DialogFragment, android.app.Fragment
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(KEY_CLICKED_ENTRY_INDEX, this.mClickedDialogEntryIndex);
        }

        @Override // android.app.DialogFragment, android.app.Fragment
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getCustomizablePreference().onDialogStateRestored(this, getDialog(), savedInstanceState);
        }

        protected DialogInterface.OnClickListener getOnItemClickListener() {
            return new DialogInterface.OnClickListener() { // from class: com.android.systemui.tuner.CustomListPreference.CustomListPreferenceDialogFragment.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    CustomListPreferenceDialogFragment.this.setClickedDialogEntryIndex(which);
                    if (CustomListPreferenceDialogFragment.this.getCustomizablePreference().isAutoClosePreference()) {
                        CustomListPreferenceDialogFragment.this.onItemConfirmed();
                    }
                }
            };
        }

        protected void setClickedDialogEntryIndex(int which) {
            this.mClickedDialogEntryIndex = which;
        }

        private String getValue() {
            ListPreference preference = getCustomizablePreference();
            if (this.mClickedDialogEntryIndex >= 0 && preference.getEntryValues() != null) {
                return preference.getEntryValues()[this.mClickedDialogEntryIndex].toString();
            }
            return null;
        }

        protected void onItemConfirmed() {
            onClick(getDialog(), -1);
            getDialog().dismiss();
        }

        @Override // androidx.preference.ListPreferenceDialogFragment, androidx.preference.PreferenceDialogFragment
        public void onDialogClosed(boolean positiveResult) {
            getCustomizablePreference().onDialogClosed(positiveResult);
            ListPreference preference = getCustomizablePreference();
            String value = getValue();
            if (positiveResult && value != null && preference.callChangeListener(value)) {
                preference.setValue(value);
            }
        }
    }
}
