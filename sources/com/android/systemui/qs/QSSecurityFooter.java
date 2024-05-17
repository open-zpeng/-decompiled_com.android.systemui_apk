package com.android.systemui.qs;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyEventLogger;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserManager;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.Dependency;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.SecurityController;
/* loaded from: classes21.dex */
public class QSSecurityFooter implements View.OnClickListener, DialogInterface.OnClickListener {
    private final ActivityStarter mActivityStarter;
    private final Context mContext;
    private AlertDialog mDialog;
    private final View mDivider;
    private final ImageView mFooterIcon;
    private int mFooterIconId;
    private final TextView mFooterText;
    private int mFooterTextId;
    protected H mHandler;
    private QSTileHost mHost;
    private boolean mIsVisible;
    private final Handler mMainHandler;
    private final View mRootView;
    private final SecurityController mSecurityController;
    private final UserManager mUm;
    protected static final String TAG = "QSSecurityFooter";
    protected static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private final Callback mCallback = new Callback();
    private CharSequence mFooterTextContent = null;
    private final Runnable mUpdateIcon = new Runnable() { // from class: com.android.systemui.qs.QSSecurityFooter.1
        @Override // java.lang.Runnable
        public void run() {
            QSSecurityFooter.this.mFooterIcon.setImageResource(QSSecurityFooter.this.mFooterIconId);
        }
    };
    private final Runnable mUpdateDisplayState = new Runnable() { // from class: com.android.systemui.qs.QSSecurityFooter.2
        @Override // java.lang.Runnable
        public void run() {
            if (QSSecurityFooter.this.mFooterTextContent != null) {
                QSSecurityFooter.this.mFooterText.setText(QSSecurityFooter.this.mFooterTextContent);
            }
            QSSecurityFooter.this.mRootView.setVisibility(QSSecurityFooter.this.mIsVisible ? 0 : 8);
            if (QSSecurityFooter.this.mDivider != null) {
                QSSecurityFooter.this.mDivider.setVisibility(QSSecurityFooter.this.mIsVisible ? 8 : 0);
            }
        }
    };

    public QSSecurityFooter(QSPanel qsPanel, Context context) {
        this.mRootView = LayoutInflater.from(context).inflate(R.layout.quick_settings_footer, (ViewGroup) qsPanel, false);
        this.mRootView.setOnClickListener(this);
        this.mFooterText = (TextView) this.mRootView.findViewById(R.id.footer_text);
        this.mFooterIcon = (ImageView) this.mRootView.findViewById(R.id.footer_icon);
        this.mFooterIconId = R.drawable.ic_info_outline;
        this.mContext = context;
        this.mMainHandler = new Handler(Looper.myLooper());
        this.mActivityStarter = (ActivityStarter) Dependency.get(ActivityStarter.class);
        this.mSecurityController = (SecurityController) Dependency.get(SecurityController.class);
        this.mHandler = new H((Looper) Dependency.get(Dependency.BG_LOOPER));
        this.mDivider = qsPanel != null ? qsPanel.getDivider() : null;
        this.mUm = (UserManager) this.mContext.getSystemService("user");
    }

    public void setHostEnvironment(QSTileHost host) {
        this.mHost = host;
    }

    public void setListening(boolean listening) {
        if (listening) {
            this.mSecurityController.addCallback(this.mCallback);
            refreshState();
            return;
        }
        this.mSecurityController.removeCallback(this.mCallback);
    }

    public void onConfigurationChanged() {
        FontSizeUtils.updateFontSize(this.mFooterText, R.dimen.qs_tile_text_size);
    }

    public View getView() {
        return this.mRootView;
    }

    public boolean hasFooter() {
        return this.mRootView.getVisibility() != 8;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        this.mHandler.sendEmptyMessage(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleClick() {
        showDeviceMonitoringDialog();
        DevicePolicyEventLogger.createEvent(57).write();
    }

    public void showDeviceMonitoringDialog() {
        this.mHost.collapsePanels();
        createDialog();
    }

    public void refreshState() {
        this.mHandler.sendEmptyMessage(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRefreshState() {
        boolean isDeviceManaged = this.mSecurityController.isDeviceManaged();
        UserInfo currentUser = this.mUm.getUserInfo(ActivityManager.getCurrentUser());
        boolean z = true;
        boolean isDemoDevice = UserManager.isDeviceInDemoMode(this.mContext) && currentUser != null && currentUser.isDemo();
        boolean hasWorkProfile = this.mSecurityController.hasWorkProfile();
        boolean hasCACerts = this.mSecurityController.hasCACertInCurrentUser();
        boolean hasCACertsInWorkProfile = this.mSecurityController.hasCACertInWorkProfile();
        boolean isNetworkLoggingEnabled = this.mSecurityController.isNetworkLoggingEnabled();
        String vpnName = this.mSecurityController.getPrimaryVpnName();
        String vpnNameWorkProfile = this.mSecurityController.getWorkProfileVpnName();
        CharSequence organizationName = this.mSecurityController.getDeviceOwnerOrganizationName();
        CharSequence workProfileName = this.mSecurityController.getWorkProfileOrganizationName();
        if ((!isDeviceManaged || isDemoDevice) && !hasCACerts && !hasCACertsInWorkProfile && vpnName == null && vpnNameWorkProfile == null) {
            z = false;
        }
        this.mIsVisible = z;
        this.mFooterTextContent = getFooterText(isDeviceManaged, hasWorkProfile, hasCACerts, hasCACertsInWorkProfile, isNetworkLoggingEnabled, vpnName, vpnNameWorkProfile, organizationName, workProfileName);
        int footerIconId = R.drawable.ic_info_outline;
        if (vpnName != null || vpnNameWorkProfile != null) {
            if (this.mSecurityController.isVpnBranded()) {
                footerIconId = R.drawable.stat_sys_branded_vpn;
            } else {
                footerIconId = R.drawable.stat_sys_vpn_ic;
            }
        }
        if (this.mFooterIconId != footerIconId) {
            this.mFooterIconId = footerIconId;
            this.mMainHandler.post(this.mUpdateIcon);
        }
        this.mMainHandler.post(this.mUpdateDisplayState);
    }

    protected CharSequence getFooterText(boolean isDeviceManaged, boolean hasWorkProfile, boolean hasCACerts, boolean hasCACertsInWorkProfile, boolean isNetworkLoggingEnabled, String vpnName, String vpnNameWorkProfile, CharSequence organizationName, CharSequence workProfileName) {
        if (isDeviceManaged) {
            if (hasCACerts || hasCACertsInWorkProfile || isNetworkLoggingEnabled) {
                return organizationName == null ? this.mContext.getString(R.string.quick_settings_disclosure_management_monitoring) : this.mContext.getString(R.string.quick_settings_disclosure_named_management_monitoring, organizationName);
            } else if (vpnName != null && vpnNameWorkProfile != null) {
                return organizationName == null ? this.mContext.getString(R.string.quick_settings_disclosure_management_vpns) : this.mContext.getString(R.string.quick_settings_disclosure_named_management_vpns, organizationName);
            } else if (vpnName == null && vpnNameWorkProfile == null) {
                return organizationName == null ? this.mContext.getString(R.string.quick_settings_disclosure_management) : this.mContext.getString(R.string.quick_settings_disclosure_named_management, organizationName);
            } else if (organizationName == null) {
                Context context = this.mContext;
                int i = R.string.quick_settings_disclosure_management_named_vpn;
                Object[] objArr = new Object[1];
                objArr[0] = vpnName != null ? vpnName : vpnNameWorkProfile;
                return context.getString(i, objArr);
            } else {
                Context context2 = this.mContext;
                int i2 = R.string.quick_settings_disclosure_named_management_named_vpn;
                Object[] objArr2 = new Object[2];
                objArr2[0] = organizationName;
                objArr2[1] = vpnName != null ? vpnName : vpnNameWorkProfile;
                return context2.getString(i2, objArr2);
            }
        } else if (hasCACertsInWorkProfile) {
            return workProfileName == null ? this.mContext.getString(R.string.quick_settings_disclosure_managed_profile_monitoring) : this.mContext.getString(R.string.quick_settings_disclosure_named_managed_profile_monitoring, workProfileName);
        } else if (hasCACerts) {
            return this.mContext.getString(R.string.quick_settings_disclosure_monitoring);
        } else {
            if (vpnName != null && vpnNameWorkProfile != null) {
                return this.mContext.getString(R.string.quick_settings_disclosure_vpns);
            }
            if (vpnNameWorkProfile != null) {
                return this.mContext.getString(R.string.quick_settings_disclosure_managed_profile_named_vpn, vpnNameWorkProfile);
            }
            if (vpnName != null) {
                return hasWorkProfile ? this.mContext.getString(R.string.quick_settings_disclosure_personal_profile_named_vpn, vpnName) : this.mContext.getString(R.string.quick_settings_disclosure_named_vpn, vpnName);
            }
            return null;
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        if (which == -2) {
            Intent intent = new Intent("android.settings.ENTERPRISE_PRIVACY_SETTINGS");
            this.mDialog.dismiss();
            this.mActivityStarter.postStartActivityDismissingKeyguard(intent, 0);
        }
    }

    private void createDialog() {
        boolean isDeviceManaged = this.mSecurityController.isDeviceManaged();
        boolean hasWorkProfile = this.mSecurityController.hasWorkProfile();
        CharSequence deviceOwnerOrganization = this.mSecurityController.getDeviceOwnerOrganizationName();
        boolean hasCACerts = this.mSecurityController.hasCACertInCurrentUser();
        boolean hasCACertsInWorkProfile = this.mSecurityController.hasCACertInWorkProfile();
        boolean isNetworkLoggingEnabled = this.mSecurityController.isNetworkLoggingEnabled();
        String vpnName = this.mSecurityController.getPrimaryVpnName();
        String vpnNameWorkProfile = this.mSecurityController.getWorkProfileVpnName();
        this.mDialog = new SystemUIDialog(this.mContext);
        this.mDialog.requestWindowFeature(1);
        View dialogView = LayoutInflater.from(new ContextThemeWrapper(this.mContext, R.style.Theme_SystemUI_Dialog)).inflate(R.layout.quick_settings_footer_dialog, (ViewGroup) null, false);
        this.mDialog.setView(dialogView);
        this.mDialog.setButton(-1, getPositiveButton(), this);
        CharSequence managementMessage = getManagementMessage(isDeviceManaged, deviceOwnerOrganization);
        if (managementMessage != null) {
            dialogView.findViewById(R.id.device_management_disclosures).setVisibility(0);
            TextView deviceManagementWarning = (TextView) dialogView.findViewById(R.id.device_management_warning);
            deviceManagementWarning.setText(managementMessage);
            this.mDialog.setButton(-2, getSettingsButton(), this);
        } else {
            dialogView.findViewById(R.id.device_management_disclosures).setVisibility(8);
        }
        CharSequence caCertsMessage = getCaCertsMessage(isDeviceManaged, hasCACerts, hasCACertsInWorkProfile);
        if (caCertsMessage == null) {
            dialogView.findViewById(R.id.ca_certs_disclosures).setVisibility(8);
        } else {
            dialogView.findViewById(R.id.ca_certs_disclosures).setVisibility(0);
            TextView caCertsWarning = (TextView) dialogView.findViewById(R.id.ca_certs_warning);
            caCertsWarning.setText(caCertsMessage);
            caCertsWarning.setMovementMethod(new LinkMovementMethod());
        }
        CharSequence networkLoggingMessage = getNetworkLoggingMessage(isNetworkLoggingEnabled);
        if (networkLoggingMessage == null) {
            dialogView.findViewById(R.id.network_logging_disclosures).setVisibility(8);
        } else {
            dialogView.findViewById(R.id.network_logging_disclosures).setVisibility(0);
            TextView networkLoggingWarning = (TextView) dialogView.findViewById(R.id.network_logging_warning);
            networkLoggingWarning.setText(networkLoggingMessage);
        }
        CharSequence vpnMessage = getVpnMessage(isDeviceManaged, hasWorkProfile, vpnName, vpnNameWorkProfile);
        if (vpnMessage == null) {
            dialogView.findViewById(R.id.vpn_disclosures).setVisibility(8);
        } else {
            dialogView.findViewById(R.id.vpn_disclosures).setVisibility(0);
            TextView vpnWarning = (TextView) dialogView.findViewById(R.id.vpn_warning);
            vpnWarning.setText(vpnMessage);
            vpnWarning.setMovementMethod(new LinkMovementMethod());
        }
        configSubtitleVisibility(managementMessage != null, caCertsMessage != null, networkLoggingMessage != null, vpnMessage != null, dialogView);
        this.mDialog.show();
        this.mDialog.getWindow().setLayout(-1, -2);
    }

    protected void configSubtitleVisibility(boolean showDeviceManagement, boolean showCaCerts, boolean showNetworkLogging, boolean showVpn, View dialogView) {
        if (showDeviceManagement) {
            return;
        }
        int mSectionCountExcludingDeviceMgt = showCaCerts ? 0 + 1 : 0;
        if (showNetworkLogging) {
            mSectionCountExcludingDeviceMgt++;
        }
        if (showVpn) {
            mSectionCountExcludingDeviceMgt++;
        }
        if (mSectionCountExcludingDeviceMgt != 1) {
            return;
        }
        if (showCaCerts) {
            dialogView.findViewById(R.id.ca_certs_subtitle).setVisibility(8);
        }
        if (showNetworkLogging) {
            dialogView.findViewById(R.id.network_logging_subtitle).setVisibility(8);
        }
        if (showVpn) {
            dialogView.findViewById(R.id.vpn_subtitle).setVisibility(8);
        }
    }

    private String getSettingsButton() {
        return this.mContext.getString(R.string.monitoring_button_view_policies);
    }

    private String getPositiveButton() {
        return this.mContext.getString(R.string.ok);
    }

    protected CharSequence getManagementMessage(boolean isDeviceManaged, CharSequence organizationName) {
        if (isDeviceManaged) {
            return organizationName != null ? this.mContext.getString(R.string.monitoring_description_named_management, organizationName) : this.mContext.getString(R.string.monitoring_description_management);
        }
        return null;
    }

    protected CharSequence getCaCertsMessage(boolean isDeviceManaged, boolean hasCACerts, boolean hasCACertsInWorkProfile) {
        if (hasCACerts || hasCACertsInWorkProfile) {
            if (isDeviceManaged) {
                return this.mContext.getString(R.string.monitoring_description_management_ca_certificate);
            }
            if (hasCACertsInWorkProfile) {
                return this.mContext.getString(R.string.monitoring_description_managed_profile_ca_certificate);
            }
            return this.mContext.getString(R.string.monitoring_description_ca_certificate);
        }
        return null;
    }

    protected CharSequence getNetworkLoggingMessage(boolean isNetworkLoggingEnabled) {
        if (isNetworkLoggingEnabled) {
            return this.mContext.getString(R.string.monitoring_description_management_network_logging);
        }
        return null;
    }

    protected CharSequence getVpnMessage(boolean isDeviceManaged, boolean hasWorkProfile, String vpnName, String vpnNameWorkProfile) {
        if (vpnName == null && vpnNameWorkProfile == null) {
            return null;
        }
        SpannableStringBuilder message = new SpannableStringBuilder();
        if (isDeviceManaged) {
            if (vpnName == null || vpnNameWorkProfile == null) {
                Context context = this.mContext;
                int i = R.string.monitoring_description_named_vpn;
                Object[] objArr = new Object[1];
                objArr[0] = vpnName != null ? vpnName : vpnNameWorkProfile;
                message.append((CharSequence) context.getString(i, objArr));
            } else {
                message.append((CharSequence) this.mContext.getString(R.string.monitoring_description_two_named_vpns, vpnName, vpnNameWorkProfile));
            }
        } else if (vpnName != null && vpnNameWorkProfile != null) {
            message.append((CharSequence) this.mContext.getString(R.string.monitoring_description_two_named_vpns, vpnName, vpnNameWorkProfile));
        } else if (vpnNameWorkProfile != null) {
            message.append((CharSequence) this.mContext.getString(R.string.monitoring_description_managed_profile_named_vpn, vpnNameWorkProfile));
        } else if (hasWorkProfile) {
            message.append((CharSequence) this.mContext.getString(R.string.monitoring_description_personal_profile_named_vpn, vpnName));
        } else {
            message.append((CharSequence) this.mContext.getString(R.string.monitoring_description_named_vpn, vpnName));
        }
        message.append((CharSequence) this.mContext.getString(R.string.monitoring_description_vpn_settings_separator));
        message.append(this.mContext.getString(R.string.monitoring_description_vpn_settings), new VpnSpan(), 0);
        return message;
    }

    private int getTitle(String deviceOwner) {
        if (deviceOwner != null) {
            return R.string.monitoring_title_device_owned;
        }
        return R.string.monitoring_title;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class Callback implements SecurityController.SecurityControllerCallback {
        private Callback() {
        }

        @Override // com.android.systemui.statusbar.policy.SecurityController.SecurityControllerCallback
        public void onStateChanged() {
            QSSecurityFooter.this.refreshState();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class H extends Handler {
        private static final int CLICK = 0;
        private static final int REFRESH_STATE = 1;

        private H(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            String name = null;
            try {
                if (msg.what == 1) {
                    name = "handleRefreshState";
                    QSSecurityFooter.this.handleRefreshState();
                } else if (msg.what == 0) {
                    name = "handleClick";
                    QSSecurityFooter.this.handleClick();
                }
            } catch (Throwable t) {
                String error = "Error in " + name;
                Log.w(QSSecurityFooter.TAG, error, t);
                QSSecurityFooter.this.mHost.warn(error, t);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public class VpnSpan extends ClickableSpan {
        protected VpnSpan() {
        }

        @Override // android.text.style.ClickableSpan
        public void onClick(View widget) {
            Intent intent = new Intent("android.settings.VPN_SETTINGS");
            QSSecurityFooter.this.mDialog.dismiss();
            QSSecurityFooter.this.mActivityStarter.postStartActivityDismissingKeyguard(intent, 0);
        }

        public boolean equals(Object object) {
            return object instanceof VpnSpan;
        }

        public int hashCode() {
            return 314159257;
        }
    }
}
