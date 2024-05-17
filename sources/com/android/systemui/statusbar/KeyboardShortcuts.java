package com.android.systemui.statusbar;

import android.app.AlertDialog;
import android.app.AppGlobals;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v4.media.subtitle.Cea708CCParser;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyboardShortcutGroup;
import android.view.KeyboardShortcutInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.asm.Opcodes;
import com.android.internal.app.AssistUtils;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.Utils;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.badlogic.gdx.Input;
import com.xiaopeng.lib.utils.info.BuildInfoUtils;
import com.xiaopeng.speech.protocol.node.navi.bean.NaviPreferenceBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes21.dex */
public final class KeyboardShortcuts {
    private static KeyboardShortcuts sInstance;
    private KeyCharacterMap mBackupKeyCharacterMap;
    private final Context mContext;
    private KeyCharacterMap mKeyCharacterMap;
    private Dialog mKeyboardShortcutsDialog;
    private static final String TAG = KeyboardShortcuts.class.getSimpleName();
    private static final Object sLock = new Object();
    private final SparseArray<String> mSpecialCharacterNames = new SparseArray<>();
    private final SparseArray<String> mModifierNames = new SparseArray<>();
    private final SparseArray<Drawable> mSpecialCharacterDrawables = new SparseArray<>();
    private final SparseArray<Drawable> mModifierDrawables = new SparseArray<>();
    private final int[] mModifierList = {65536, 4096, 2, 1, 4, 8};
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final DialogInterface.OnClickListener mDialogCloseListener = new DialogInterface.OnClickListener() { // from class: com.android.systemui.statusbar.KeyboardShortcuts.1
        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int id) {
            KeyboardShortcuts.this.dismissKeyboardShortcuts();
        }
    };
    private final Comparator<KeyboardShortcutInfo> mApplicationItemsComparator = new Comparator<KeyboardShortcutInfo>() { // from class: com.android.systemui.statusbar.KeyboardShortcuts.2
        @Override // java.util.Comparator
        public int compare(KeyboardShortcutInfo ksh1, KeyboardShortcutInfo ksh2) {
            boolean ksh1ShouldBeLast = ksh1.getLabel() == null || ksh1.getLabel().toString().isEmpty();
            boolean ksh2ShouldBeLast = ksh2.getLabel() == null || ksh2.getLabel().toString().isEmpty();
            if (ksh1ShouldBeLast && ksh2ShouldBeLast) {
                return 0;
            }
            if (ksh1ShouldBeLast) {
                return 1;
            }
            if (ksh2ShouldBeLast) {
                return -1;
            }
            return ksh1.getLabel().toString().compareToIgnoreCase(ksh2.getLabel().toString());
        }
    };
    private final IPackageManager mPackageManager = AppGlobals.getPackageManager();

    private KeyboardShortcuts(Context context) {
        this.mContext = new ContextThemeWrapper(context, 16974371);
        loadResources(context);
    }

    private static KeyboardShortcuts getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new KeyboardShortcuts(context);
        }
        return sInstance;
    }

    public static void show(Context context, int deviceId) {
        MetricsLogger.visible(context, 500);
        synchronized (sLock) {
            if (sInstance != null && !sInstance.mContext.equals(context)) {
                dismiss();
            }
            getInstance(context).showKeyboardShortcuts(deviceId);
        }
    }

    public static void toggle(Context context, int deviceId) {
        synchronized (sLock) {
            if (isShowing()) {
                dismiss();
            } else {
                show(context, deviceId);
            }
        }
    }

    public static void dismiss() {
        synchronized (sLock) {
            if (sInstance != null) {
                MetricsLogger.hidden(sInstance.mContext, 500);
                sInstance.dismissKeyboardShortcuts();
                sInstance = null;
            }
        }
    }

    private static boolean isShowing() {
        Dialog dialog;
        KeyboardShortcuts keyboardShortcuts = sInstance;
        return (keyboardShortcuts == null || (dialog = keyboardShortcuts.mKeyboardShortcutsDialog) == null || !dialog.isShowing()) ? false : true;
    }

    private void loadResources(Context context) {
        this.mSpecialCharacterNames.put(3, context.getString(R.string.keyboard_key_home));
        this.mSpecialCharacterNames.put(4, context.getString(R.string.keyboard_key_back));
        this.mSpecialCharacterNames.put(19, context.getString(R.string.keyboard_key_dpad_up));
        this.mSpecialCharacterNames.put(20, context.getString(R.string.keyboard_key_dpad_down));
        this.mSpecialCharacterNames.put(21, context.getString(R.string.keyboard_key_dpad_left));
        this.mSpecialCharacterNames.put(22, context.getString(R.string.keyboard_key_dpad_right));
        this.mSpecialCharacterNames.put(23, context.getString(R.string.keyboard_key_dpad_center));
        this.mSpecialCharacterNames.put(56, ".");
        this.mSpecialCharacterNames.put(61, context.getString(R.string.keyboard_key_tab));
        this.mSpecialCharacterNames.put(62, context.getString(R.string.keyboard_key_space));
        this.mSpecialCharacterNames.put(66, context.getString(R.string.keyboard_key_enter));
        this.mSpecialCharacterNames.put(67, context.getString(R.string.keyboard_key_backspace));
        this.mSpecialCharacterNames.put(85, context.getString(R.string.keyboard_key_media_play_pause));
        this.mSpecialCharacterNames.put(86, context.getString(R.string.keyboard_key_media_stop));
        this.mSpecialCharacterNames.put(87, context.getString(R.string.keyboard_key_media_next));
        this.mSpecialCharacterNames.put(88, context.getString(R.string.keyboard_key_media_previous));
        this.mSpecialCharacterNames.put(89, context.getString(R.string.keyboard_key_media_rewind));
        this.mSpecialCharacterNames.put(90, context.getString(R.string.keyboard_key_media_fast_forward));
        this.mSpecialCharacterNames.put(92, context.getString(R.string.keyboard_key_page_up));
        this.mSpecialCharacterNames.put(93, context.getString(R.string.keyboard_key_page_down));
        this.mSpecialCharacterNames.put(96, context.getString(R.string.keyboard_key_button_template, "A"));
        this.mSpecialCharacterNames.put(97, context.getString(R.string.keyboard_key_button_template, "B"));
        this.mSpecialCharacterNames.put(98, context.getString(R.string.keyboard_key_button_template, "C"));
        this.mSpecialCharacterNames.put(99, context.getString(R.string.keyboard_key_button_template, "X"));
        this.mSpecialCharacterNames.put(100, context.getString(R.string.keyboard_key_button_template, "Y"));
        this.mSpecialCharacterNames.put(101, context.getString(R.string.keyboard_key_button_template, "Z"));
        this.mSpecialCharacterNames.put(102, context.getString(R.string.keyboard_key_button_template, "L1"));
        this.mSpecialCharacterNames.put(103, context.getString(R.string.keyboard_key_button_template, "R1"));
        this.mSpecialCharacterNames.put(104, context.getString(R.string.keyboard_key_button_template, "L2"));
        this.mSpecialCharacterNames.put(105, context.getString(R.string.keyboard_key_button_template, "R2"));
        this.mSpecialCharacterNames.put(108, context.getString(R.string.keyboard_key_button_template, "Start"));
        this.mSpecialCharacterNames.put(109, context.getString(R.string.keyboard_key_button_template, "Select"));
        this.mSpecialCharacterNames.put(110, context.getString(R.string.keyboard_key_button_template, "Mode"));
        this.mSpecialCharacterNames.put(112, context.getString(R.string.keyboard_key_forward_del));
        this.mSpecialCharacterNames.put(111, "Esc");
        this.mSpecialCharacterNames.put(120, "SysRq");
        this.mSpecialCharacterNames.put(121, "Break");
        this.mSpecialCharacterNames.put(116, "Scroll Lock");
        this.mSpecialCharacterNames.put(122, context.getString(R.string.keyboard_key_move_home));
        this.mSpecialCharacterNames.put(123, context.getString(R.string.keyboard_key_move_end));
        this.mSpecialCharacterNames.put(124, context.getString(R.string.keyboard_key_insert));
        this.mSpecialCharacterNames.put(131, "F1");
        this.mSpecialCharacterNames.put(132, "F2");
        this.mSpecialCharacterNames.put(133, "F3");
        this.mSpecialCharacterNames.put(Cea708CCParser.Const.CODE_C1_CW6, "F4");
        this.mSpecialCharacterNames.put(135, "F5");
        this.mSpecialCharacterNames.put(136, "F6");
        this.mSpecialCharacterNames.put(Cea708CCParser.Const.CODE_C1_DSW, "F7");
        this.mSpecialCharacterNames.put(Cea708CCParser.Const.CODE_C1_HDW, "F8");
        this.mSpecialCharacterNames.put(Cea708CCParser.Const.CODE_C1_TGW, "F9");
        this.mSpecialCharacterNames.put(Cea708CCParser.Const.CODE_C1_DLW, "F10");
        this.mSpecialCharacterNames.put(Cea708CCParser.Const.CODE_C1_DLY, "F11");
        this.mSpecialCharacterNames.put(Cea708CCParser.Const.CODE_C1_DLC, "F12");
        this.mSpecialCharacterNames.put(143, context.getString(R.string.keyboard_key_num_lock));
        this.mSpecialCharacterNames.put(144, context.getString(R.string.keyboard_key_numpad_template, "0"));
        this.mSpecialCharacterNames.put(145, context.getString(R.string.keyboard_key_numpad_template, "1"));
        this.mSpecialCharacterNames.put(146, context.getString(R.string.keyboard_key_numpad_template, "2"));
        this.mSpecialCharacterNames.put(Input.Keys.NUMPAD_3, context.getString(R.string.keyboard_key_numpad_template, "3"));
        this.mSpecialCharacterNames.put(148, context.getString(R.string.keyboard_key_numpad_template, BuildInfoUtils.BID_LAN));
        this.mSpecialCharacterNames.put(149, context.getString(R.string.keyboard_key_numpad_template, BuildInfoUtils.BID_PT_SPECIAL_1));
        this.mSpecialCharacterNames.put(150, context.getString(R.string.keyboard_key_numpad_template, BuildInfoUtils.BID_PT_SPECIAL_2));
        this.mSpecialCharacterNames.put(151, context.getString(R.string.keyboard_key_numpad_template, "7"));
        this.mSpecialCharacterNames.put(152, context.getString(R.string.keyboard_key_numpad_template, "8"));
        this.mSpecialCharacterNames.put(153, context.getString(R.string.keyboard_key_numpad_template, "9"));
        this.mSpecialCharacterNames.put(154, context.getString(R.string.keyboard_key_numpad_template, "/"));
        this.mSpecialCharacterNames.put(Cea708CCParser.Const.CODE_C1_DF3, context.getString(R.string.keyboard_key_numpad_template, "*"));
        this.mSpecialCharacterNames.put(Cea708CCParser.Const.CODE_C1_DF4, context.getString(R.string.keyboard_key_numpad_template, "-"));
        this.mSpecialCharacterNames.put(Cea708CCParser.Const.CODE_C1_DF5, context.getString(R.string.keyboard_key_numpad_template, "+"));
        this.mSpecialCharacterNames.put(158, context.getString(R.string.keyboard_key_numpad_template, "."));
        this.mSpecialCharacterNames.put(159, context.getString(R.string.keyboard_key_numpad_template, ","));
        this.mSpecialCharacterNames.put(160, context.getString(R.string.keyboard_key_numpad_template, context.getString(R.string.keyboard_key_enter)));
        this.mSpecialCharacterNames.put(Opcodes.IF_ICMPLT, context.getString(R.string.keyboard_key_numpad_template, "="));
        this.mSpecialCharacterNames.put(Opcodes.IF_ICMPGE, context.getString(R.string.keyboard_key_numpad_template, NavigationBarInflaterView.KEY_CODE_START));
        this.mSpecialCharacterNames.put(Opcodes.IF_ICMPGT, context.getString(R.string.keyboard_key_numpad_template, NavigationBarInflaterView.KEY_CODE_END));
        this.mSpecialCharacterNames.put(NaviPreferenceBean.PATH_PREF_AVOID_UNPAVED, "半角/全角");
        this.mSpecialCharacterNames.put(NaviPreferenceBean.PATH_PREF_COUNTRY_BORDER, "英数");
        this.mSpecialCharacterNames.put(NaviPreferenceBean.PATH_PREF_AVOID_COUNTRY_BORDER, "無変換");
        this.mSpecialCharacterNames.put(214, "変換");
        this.mSpecialCharacterNames.put(215, "かな");
        this.mModifierNames.put(65536, "Meta");
        this.mModifierNames.put(4096, "Ctrl");
        this.mModifierNames.put(2, "Alt");
        this.mModifierNames.put(1, "Shift");
        this.mModifierNames.put(4, "Sym");
        this.mModifierNames.put(8, "Fn");
        this.mSpecialCharacterDrawables.put(67, context.getDrawable(R.drawable.ic_ksh_key_backspace));
        this.mSpecialCharacterDrawables.put(66, context.getDrawable(R.drawable.ic_ksh_key_enter));
        this.mSpecialCharacterDrawables.put(19, context.getDrawable(R.drawable.ic_ksh_key_up));
        this.mSpecialCharacterDrawables.put(22, context.getDrawable(R.drawable.ic_ksh_key_right));
        this.mSpecialCharacterDrawables.put(20, context.getDrawable(R.drawable.ic_ksh_key_down));
        this.mSpecialCharacterDrawables.put(21, context.getDrawable(R.drawable.ic_ksh_key_left));
        this.mModifierDrawables.put(65536, context.getDrawable(R.drawable.ic_ksh_key_meta));
    }

    private void retrieveKeyCharacterMap(int deviceId) {
        InputDevice inputDevice;
        InputManager inputManager = InputManager.getInstance();
        this.mBackupKeyCharacterMap = inputManager.getInputDevice(-1).getKeyCharacterMap();
        if (deviceId != -1 && (inputDevice = inputManager.getInputDevice(deviceId)) != null) {
            this.mKeyCharacterMap = inputDevice.getKeyCharacterMap();
            return;
        }
        int[] deviceIds = inputManager.getInputDeviceIds();
        for (int i : deviceIds) {
            InputDevice inputDevice2 = inputManager.getInputDevice(i);
            if (inputDevice2.getId() != -1 && inputDevice2.isFullKeyboard()) {
                this.mKeyCharacterMap = inputDevice2.getKeyCharacterMap();
                return;
            }
        }
        this.mKeyCharacterMap = this.mBackupKeyCharacterMap;
    }

    private void showKeyboardShortcuts(int deviceId) {
        retrieveKeyCharacterMap(deviceId);
        WindowManager wm = (WindowManager) this.mContext.getSystemService("window");
        wm.requestAppKeyboardShortcuts(new WindowManager.KeyboardShortcutsReceiver() { // from class: com.android.systemui.statusbar.KeyboardShortcuts.3
            public void onKeyboardShortcutsReceived(List<KeyboardShortcutGroup> result) {
                result.add(KeyboardShortcuts.this.getSystemShortcuts());
                KeyboardShortcutGroup appShortcuts = KeyboardShortcuts.this.getDefaultApplicationShortcuts();
                if (appShortcuts != null) {
                    result.add(appShortcuts);
                }
                KeyboardShortcuts.this.showKeyboardShortcutsDialog(result);
            }
        }, deviceId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissKeyboardShortcuts() {
        Dialog dialog = this.mKeyboardShortcutsDialog;
        if (dialog != null) {
            dialog.dismiss();
            this.mKeyboardShortcutsDialog = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public KeyboardShortcutGroup getSystemShortcuts() {
        KeyboardShortcutGroup systemGroup = new KeyboardShortcutGroup((CharSequence) this.mContext.getString(R.string.keyboard_shortcut_group_system), true);
        systemGroup.addItem(new KeyboardShortcutInfo(this.mContext.getString(R.string.keyboard_shortcut_group_system_home), 66, 65536));
        systemGroup.addItem(new KeyboardShortcutInfo(this.mContext.getString(R.string.keyboard_shortcut_group_system_back), 67, 65536));
        systemGroup.addItem(new KeyboardShortcutInfo(this.mContext.getString(R.string.keyboard_shortcut_group_system_recents), 61, 2));
        systemGroup.addItem(new KeyboardShortcutInfo(this.mContext.getString(R.string.keyboard_shortcut_group_system_notifications), 42, 65536));
        systemGroup.addItem(new KeyboardShortcutInfo(this.mContext.getString(R.string.keyboard_shortcut_group_system_shortcuts_helper), 76, 65536));
        systemGroup.addItem(new KeyboardShortcutInfo(this.mContext.getString(R.string.keyboard_shortcut_group_system_switch_input), 62, 65536));
        return systemGroup;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public KeyboardShortcutGroup getDefaultApplicationShortcuts() {
        int userId = this.mContext.getUserId();
        List<KeyboardShortcutInfo> keyboardShortcutInfoAppItems = new ArrayList<>();
        AssistUtils assistUtils = new AssistUtils(this.mContext);
        ComponentName assistComponent = assistUtils.getAssistComponentForUser(userId);
        if (assistComponent != null) {
            PackageInfo assistPackageInfo = null;
            try {
                assistPackageInfo = this.mPackageManager.getPackageInfo(assistComponent.getPackageName(), 0, userId);
            } catch (RemoteException e) {
                Log.e(TAG, "PackageManagerService is dead");
            }
            if (assistPackageInfo != null) {
                Icon assistIcon = Icon.createWithResource(assistPackageInfo.applicationInfo.packageName, assistPackageInfo.applicationInfo.icon);
                keyboardShortcutInfoAppItems.add(new KeyboardShortcutInfo(this.mContext.getString(R.string.keyboard_shortcut_group_applications_assist), assistIcon, 0, 65536));
            }
        }
        Icon browserIcon = getIconForIntentCategory("android.intent.category.APP_BROWSER", userId);
        if (browserIcon != null) {
            keyboardShortcutInfoAppItems.add(new KeyboardShortcutInfo(this.mContext.getString(R.string.keyboard_shortcut_group_applications_browser), browserIcon, 30, 65536));
        }
        Icon contactsIcon = getIconForIntentCategory("android.intent.category.APP_CONTACTS", userId);
        if (contactsIcon != null) {
            keyboardShortcutInfoAppItems.add(new KeyboardShortcutInfo(this.mContext.getString(R.string.keyboard_shortcut_group_applications_contacts), contactsIcon, 31, 65536));
        }
        Icon emailIcon = getIconForIntentCategory("android.intent.category.APP_EMAIL", userId);
        if (emailIcon != null) {
            keyboardShortcutInfoAppItems.add(new KeyboardShortcutInfo(this.mContext.getString(R.string.keyboard_shortcut_group_applications_email), emailIcon, 33, 65536));
        }
        Icon messagingIcon = getIconForIntentCategory("android.intent.category.APP_MESSAGING", userId);
        if (messagingIcon != null) {
            keyboardShortcutInfoAppItems.add(new KeyboardShortcutInfo(this.mContext.getString(R.string.keyboard_shortcut_group_applications_sms), messagingIcon, 47, 65536));
        }
        Icon musicIcon = getIconForIntentCategory("android.intent.category.APP_MUSIC", userId);
        if (musicIcon != null) {
            keyboardShortcutInfoAppItems.add(new KeyboardShortcutInfo(this.mContext.getString(R.string.keyboard_shortcut_group_applications_music), musicIcon, 44, 65536));
        }
        Icon calendarIcon = getIconForIntentCategory("android.intent.category.APP_CALENDAR", userId);
        if (calendarIcon != null) {
            keyboardShortcutInfoAppItems.add(new KeyboardShortcutInfo(this.mContext.getString(R.string.keyboard_shortcut_group_applications_calendar), calendarIcon, 40, 65536));
        }
        int itemsSize = keyboardShortcutInfoAppItems.size();
        if (itemsSize == 0) {
            return null;
        }
        Collections.sort(keyboardShortcutInfoAppItems, this.mApplicationItemsComparator);
        return new KeyboardShortcutGroup(this.mContext.getString(R.string.keyboard_shortcut_group_applications), keyboardShortcutInfoAppItems, true);
    }

    private Icon getIconForIntentCategory(String intentCategory, int userId) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory(intentCategory);
        PackageInfo packageInfo = getPackageInfoForIntent(intent, userId);
        if (packageInfo != null && packageInfo.applicationInfo.icon != 0) {
            return Icon.createWithResource(packageInfo.applicationInfo.packageName, packageInfo.applicationInfo.icon);
        }
        return null;
    }

    private PackageInfo getPackageInfoForIntent(Intent intent, int userId) {
        try {
            ResolveInfo handler = this.mPackageManager.resolveIntent(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 0, userId);
            if (handler != null && handler.activityInfo != null) {
                return this.mPackageManager.getPackageInfo(handler.activityInfo.packageName, 0, userId);
            }
            return null;
        } catch (RemoteException e) {
            Log.e(TAG, "PackageManagerService is dead", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showKeyboardShortcutsDialog(final List<KeyboardShortcutGroup> keyboardShortcutGroups) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.KeyboardShortcuts.4
            @Override // java.lang.Runnable
            public void run() {
                KeyboardShortcuts.this.handleShowKeyboardShortcuts(keyboardShortcutGroups);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleShowKeyboardShortcuts(List<KeyboardShortcutGroup> keyboardShortcutGroups) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.mContext);
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
        View keyboardShortcutsView = inflater.inflate(R.layout.keyboard_shortcuts_view, (ViewGroup) null);
        populateKeyboardShortcuts((LinearLayout) keyboardShortcutsView.findViewById(R.id.keyboard_shortcuts_container), keyboardShortcutGroups);
        dialogBuilder.setView(keyboardShortcutsView);
        dialogBuilder.setPositiveButton(R.string.quick_settings_done, this.mDialogCloseListener);
        this.mKeyboardShortcutsDialog = dialogBuilder.create();
        this.mKeyboardShortcutsDialog.setCanceledOnTouchOutside(true);
        Window keyboardShortcutsWindow = this.mKeyboardShortcutsDialog.getWindow();
        keyboardShortcutsWindow.setType(2008);
        synchronized (sLock) {
            if (sInstance != null) {
                this.mKeyboardShortcutsDialog.show();
            }
        }
    }

    private void populateKeyboardShortcuts(LinearLayout keyboardShortcutsLayout, List<KeyboardShortcutGroup> keyboardShortcutGroups) {
        ColorStateList valueOf;
        boolean z;
        TextView shortcutsKeyView;
        KeyboardShortcutGroup group;
        TextView categoryTitle;
        int keyboardShortcutGroupsSize;
        int shortcutKeyIconItemHeightWidth;
        int i;
        int itemsSize;
        int keyboardShortcutGroupsSize2;
        int shortcutKeyIconItemHeightWidth2;
        int i2;
        int itemsSize2;
        KeyboardShortcutInfo info;
        LinearLayout linearLayout = keyboardShortcutsLayout;
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        int keyboardShortcutGroupsSize3 = keyboardShortcutGroups.size();
        boolean z2 = false;
        TextView shortcutsKeyView2 = (TextView) inflater.inflate(R.layout.keyboard_shortcuts_key_view, (ViewGroup) null, false);
        shortcutsKeyView2.measure(0, 0);
        int shortcutKeyTextItemMinWidth = shortcutsKeyView2.getMeasuredHeight();
        int shortcutKeyIconItemHeightWidth3 = (shortcutsKeyView2.getMeasuredHeight() - shortcutsKeyView2.getPaddingTop()) - shortcutsKeyView2.getPaddingBottom();
        int i3 = 0;
        while (i3 < keyboardShortcutGroupsSize3) {
            KeyboardShortcutGroup group2 = keyboardShortcutGroups.get(i3);
            TextView categoryTitle2 = (TextView) inflater.inflate(R.layout.keyboard_shortcuts_category_title, linearLayout, z2);
            categoryTitle2.setText(group2.getLabel());
            if (group2.isSystemGroup()) {
                valueOf = Utils.getColorAccent(this.mContext);
            } else {
                valueOf = ColorStateList.valueOf(this.mContext.getColor(R.color.ksh_application_group_color));
            }
            categoryTitle2.setTextColor(valueOf);
            linearLayout.addView(categoryTitle2);
            LinearLayout shortcutContainer = (LinearLayout) inflater.inflate(R.layout.keyboard_shortcuts_container, linearLayout, z2);
            int itemsSize3 = group2.getItems().size();
            int j = 0;
            while (j < itemsSize3) {
                KeyboardShortcutInfo info2 = group2.getItems().get(j);
                List<StringDrawableContainer> shortcutKeys = getHumanReadableShortcutKeys(info2);
                if (shortcutKeys == null) {
                    shortcutsKeyView = shortcutsKeyView2;
                    Log.w(TAG, "Keyboard Shortcut contains unsupported keys, skipping.");
                    keyboardShortcutGroupsSize = keyboardShortcutGroupsSize3;
                    shortcutKeyIconItemHeightWidth = shortcutKeyIconItemHeightWidth3;
                    i = i3;
                    group = group2;
                    categoryTitle = categoryTitle2;
                    itemsSize = itemsSize3;
                } else {
                    shortcutsKeyView = shortcutsKeyView2;
                    View shortcutView = inflater.inflate(R.layout.keyboard_shortcut_app_item, (ViewGroup) shortcutContainer, false);
                    if (info2.getIcon() == null) {
                        group = group2;
                    } else {
                        ImageView shortcutIcon = (ImageView) shortcutView.findViewById(R.id.keyboard_shortcuts_icon);
                        group = group2;
                        shortcutIcon.setImageIcon(info2.getIcon());
                        shortcutIcon.setVisibility(0);
                    }
                    TextView shortcutKeyword = (TextView) shortcutView.findViewById(R.id.keyboard_shortcuts_keyword);
                    shortcutKeyword.setText(info2.getLabel());
                    if (info2.getIcon() == null) {
                        categoryTitle = categoryTitle2;
                    } else {
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) shortcutKeyword.getLayoutParams();
                        categoryTitle = categoryTitle2;
                        lp.removeRule(20);
                        shortcutKeyword.setLayoutParams(lp);
                    }
                    ViewGroup shortcutItemsContainer = (ViewGroup) shortcutView.findViewById(R.id.keyboard_shortcuts_item_container);
                    int shortcutKeysSize = shortcutKeys.size();
                    int k = 0;
                    while (k < shortcutKeysSize) {
                        List<StringDrawableContainer> shortcutKeys2 = shortcutKeys;
                        StringDrawableContainer shortcutRepresentation = shortcutKeys.get(k);
                        int shortcutKeysSize2 = shortcutKeysSize;
                        if (shortcutRepresentation.mDrawable != null) {
                            itemsSize2 = itemsSize3;
                            ImageView shortcutKeyIconView = (ImageView) inflater.inflate(R.layout.keyboard_shortcuts_key_icon_view, shortcutItemsContainer, false);
                            Bitmap bitmap = Bitmap.createBitmap(shortcutKeyIconItemHeightWidth3, shortcutKeyIconItemHeightWidth3, Bitmap.Config.ARGB_8888);
                            shortcutKeyIconItemHeightWidth2 = shortcutKeyIconItemHeightWidth3;
                            Canvas canvas = new Canvas(bitmap);
                            info = info2;
                            Drawable drawable = shortcutRepresentation.mDrawable;
                            i2 = i3;
                            int i4 = canvas.getWidth();
                            keyboardShortcutGroupsSize2 = keyboardShortcutGroupsSize3;
                            int keyboardShortcutGroupsSize4 = canvas.getHeight();
                            drawable.setBounds(0, 0, i4, keyboardShortcutGroupsSize4);
                            shortcutRepresentation.mDrawable.draw(canvas);
                            shortcutKeyIconView.setImageBitmap(bitmap);
                            shortcutKeyIconView.setImportantForAccessibility(1);
                            shortcutKeyIconView.setAccessibilityDelegate(new ShortcutKeyAccessibilityDelegate(shortcutRepresentation.mString));
                            shortcutItemsContainer.addView(shortcutKeyIconView);
                        } else {
                            keyboardShortcutGroupsSize2 = keyboardShortcutGroupsSize3;
                            shortcutKeyIconItemHeightWidth2 = shortcutKeyIconItemHeightWidth3;
                            i2 = i3;
                            itemsSize2 = itemsSize3;
                            info = info2;
                            if (shortcutRepresentation.mString != null) {
                                TextView shortcutKeyTextView = (TextView) inflater.inflate(R.layout.keyboard_shortcuts_key_view, shortcutItemsContainer, false);
                                shortcutKeyTextView.setMinimumWidth(shortcutKeyTextItemMinWidth);
                                shortcutKeyTextView.setText(shortcutRepresentation.mString);
                                shortcutKeyTextView.setAccessibilityDelegate(new ShortcutKeyAccessibilityDelegate(shortcutRepresentation.mString));
                                shortcutItemsContainer.addView(shortcutKeyTextView);
                            }
                        }
                        k++;
                        shortcutKeysSize = shortcutKeysSize2;
                        shortcutKeys = shortcutKeys2;
                        itemsSize3 = itemsSize2;
                        shortcutKeyIconItemHeightWidth3 = shortcutKeyIconItemHeightWidth2;
                        info2 = info;
                        i3 = i2;
                        keyboardShortcutGroupsSize3 = keyboardShortcutGroupsSize2;
                    }
                    keyboardShortcutGroupsSize = keyboardShortcutGroupsSize3;
                    shortcutKeyIconItemHeightWidth = shortcutKeyIconItemHeightWidth3;
                    i = i3;
                    itemsSize = itemsSize3;
                    shortcutContainer.addView(shortcutView);
                }
                j++;
                shortcutsKeyView2 = shortcutsKeyView;
                group2 = group;
                categoryTitle2 = categoryTitle;
                itemsSize3 = itemsSize;
                shortcutKeyIconItemHeightWidth3 = shortcutKeyIconItemHeightWidth;
                i3 = i;
                keyboardShortcutGroupsSize3 = keyboardShortcutGroupsSize;
            }
            int keyboardShortcutGroupsSize5 = keyboardShortcutGroupsSize3;
            TextView shortcutsKeyView3 = shortcutsKeyView2;
            int shortcutKeyIconItemHeightWidth4 = shortcutKeyIconItemHeightWidth3;
            linearLayout = keyboardShortcutsLayout;
            linearLayout.addView(shortcutContainer);
            if (i3 >= keyboardShortcutGroupsSize5 - 1) {
                z = false;
            } else {
                z = false;
                View separator = inflater.inflate(R.layout.keyboard_shortcuts_category_separator, (ViewGroup) linearLayout, false);
                linearLayout.addView(separator);
            }
            i3++;
            z2 = z;
            shortcutsKeyView2 = shortcutsKeyView3;
            shortcutKeyIconItemHeightWidth3 = shortcutKeyIconItemHeightWidth4;
            keyboardShortcutGroupsSize3 = keyboardShortcutGroupsSize5;
        }
    }

    private List<StringDrawableContainer> getHumanReadableShortcutKeys(KeyboardShortcutInfo info) {
        String shortcutKeyString;
        List<StringDrawableContainer> shortcutKeys = getHumanReadableModifiers(info);
        if (shortcutKeys == null) {
            return null;
        }
        Drawable shortcutKeyDrawable = null;
        if (info.getBaseCharacter() > 0) {
            shortcutKeyString = String.valueOf(info.getBaseCharacter());
        } else if (this.mSpecialCharacterDrawables.get(info.getKeycode()) != null) {
            shortcutKeyDrawable = this.mSpecialCharacterDrawables.get(info.getKeycode());
            shortcutKeyString = this.mSpecialCharacterNames.get(info.getKeycode());
        } else if (this.mSpecialCharacterNames.get(info.getKeycode()) != null) {
            shortcutKeyString = this.mSpecialCharacterNames.get(info.getKeycode());
        } else if (info.getKeycode() == 0) {
            return shortcutKeys;
        } else {
            char displayLabel = this.mKeyCharacterMap.getDisplayLabel(info.getKeycode());
            if (displayLabel != 0) {
                shortcutKeyString = String.valueOf(displayLabel);
            } else {
                char displayLabel2 = this.mBackupKeyCharacterMap.getDisplayLabel(info.getKeycode());
                if (displayLabel2 == 0) {
                    return null;
                }
                shortcutKeyString = String.valueOf(displayLabel2);
            }
        }
        if (shortcutKeyString != null) {
            shortcutKeys.add(new StringDrawableContainer(shortcutKeyString, shortcutKeyDrawable));
        } else {
            Log.w(TAG, "Keyboard Shortcut does not have a text representation, skipping.");
        }
        return shortcutKeys;
    }

    private List<StringDrawableContainer> getHumanReadableModifiers(KeyboardShortcutInfo info) {
        List<StringDrawableContainer> shortcutKeys = new ArrayList<>();
        int modifiers = info.getModifiers();
        if (modifiers == 0) {
            return shortcutKeys;
        }
        int i = 0;
        while (true) {
            int[] iArr = this.mModifierList;
            if (i >= iArr.length) {
                break;
            }
            int supportedModifier = iArr[i];
            if ((modifiers & supportedModifier) != 0) {
                shortcutKeys.add(new StringDrawableContainer(this.mModifierNames.get(supportedModifier), this.mModifierDrawables.get(supportedModifier)));
                modifiers &= ~supportedModifier;
            }
            i++;
        }
        if (modifiers != 0) {
            return null;
        }
        return shortcutKeys;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class ShortcutKeyAccessibilityDelegate extends View.AccessibilityDelegate {
        private String mContentDescription;

        ShortcutKeyAccessibilityDelegate(String contentDescription) {
            this.mContentDescription = contentDescription;
        }

        @Override // android.view.View.AccessibilityDelegate
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            String str = this.mContentDescription;
            if (str != null) {
                info.setContentDescription(str.toLowerCase());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static final class StringDrawableContainer {
        public Drawable mDrawable;
        public String mString;

        StringDrawableContainer(String string, Drawable drawable) {
            this.mString = string;
            this.mDrawable = drawable;
        }
    }
}
