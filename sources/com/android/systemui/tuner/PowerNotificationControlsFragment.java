package com.android.systemui.tuner;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class PowerNotificationControlsFragment extends Fragment {
    private static final String KEY_SHOW_PNC = "show_importance_slider";

    @Override // android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.power_notification_controls_settings, container, false);
    }

    @Override // android.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        String string;
        super.onViewCreated(view, savedInstanceState);
        View switchBar = view.findViewById(R.id.switch_bar);
        final Switch switchWidget = (Switch) switchBar.findViewById(16908352);
        final TextView switchText = (TextView) switchBar.findViewById(R.id.switch_text);
        switchWidget.setChecked(isEnabled());
        if (isEnabled()) {
            string = getString(R.string.switch_bar_on);
        } else {
            string = getString(R.string.switch_bar_off);
        }
        switchText.setText(string);
        switchWidget.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.tuner.PowerNotificationControlsFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                String string2;
                boolean newState = !PowerNotificationControlsFragment.this.isEnabled();
                MetricsLogger.action(PowerNotificationControlsFragment.this.getContext(), 393, newState);
                Settings.Secure.putInt(PowerNotificationControlsFragment.this.getContext().getContentResolver(), PowerNotificationControlsFragment.KEY_SHOW_PNC, newState ? 1 : 0);
                switchWidget.setChecked(newState);
                TextView textView = switchText;
                if (newState) {
                    string2 = PowerNotificationControlsFragment.this.getString(R.string.switch_bar_on);
                } else {
                    string2 = PowerNotificationControlsFragment.this.getString(R.string.switch_bar_off);
                }
                textView.setText(string2);
            }
        });
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        MetricsLogger.visibility(getContext(), 392, true);
    }

    @Override // android.app.Fragment
    public void onPause() {
        super.onPause();
        MetricsLogger.visibility(getContext(), 392, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isEnabled() {
        int setting = Settings.Secure.getInt(getContext().getContentResolver(), KEY_SHOW_PNC, 0);
        return setting == 1;
    }
}
