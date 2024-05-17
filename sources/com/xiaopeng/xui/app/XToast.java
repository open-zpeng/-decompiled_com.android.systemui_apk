package com.xiaopeng.xui.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.app.XToast;
import com.xiaopeng.xui.utils.XCharacterUtils;
import com.xiaopeng.xui.widget.XTextView;
import java.lang.reflect.Method;
/* loaded from: classes25.dex */
public class XToast {
    public static final int ID_SHARED_PRIMARY = 0;
    public static final int ID_SHARED_SECONDARY = 1;
    public static final int LENGTH_LONGER = 2;

    private XToast() {
    }

    private static Context getApplicationContext() {
        return Xui.getContext();
    }

    public static void show(@StringRes int msgRes) {
        show(Xui.getContext().getText(msgRes));
    }

    public static void showAt(@StringRes int msgRes, int shareId) {
        showAt(Xui.getContext().getText(msgRes), shareId);
    }

    public static void show(CharSequence msg) {
        int size = charactersSize(msg);
        if (size > 8) {
            showLong(msg);
        } else {
            showShort(msg);
        }
    }

    public static void showAt(CharSequence msg, int shareId) {
        int size = charactersSize(msg);
        if (size > 8) {
            showLong(msg, shareId);
        } else {
            showShort(msg, shareId);
        }
    }

    public static void showShort(@StringRes int msgRes) {
        showShort(Xui.getContext().getText(msgRes));
    }

    public static void showShort(@StringRes int msgRes, int shareId) {
        showShort(Xui.getContext().getText(msgRes), shareId);
    }

    public static void showShort(CharSequence msg) {
        show(msg, 0);
    }

    public static void showShort(CharSequence msg, int shareId) {
        show(msg, 0, shareId);
    }

    public static void showLong(@StringRes int msgRes) {
        showLong(Xui.getContext().getText(msgRes));
    }

    public static void showLong(@StringRes int msgRes, int shareId) {
        showLong(Xui.getContext().getText(msgRes), shareId);
    }

    public static void showLong(CharSequence msg) {
        show(msg, 1);
    }

    public static void showLong(CharSequence msg, int shareId) {
        show(msg, 1, shareId);
    }

    public static void showLonger(@StringRes int msgRes) {
        showLonger(Xui.getContext().getText(msgRes));
    }

    public static void showLonger(@StringRes int msgRes, int shareId) {
        showLonger(Xui.getContext().getText(msgRes), shareId);
    }

    public static void showLonger(CharSequence msg) {
        show(msg, 2);
    }

    public static void showLonger(CharSequence msg, int shareId) {
        show(msg, 2, shareId);
    }

    public static void show(CharSequence msg, int duration) {
        show(msg, duration, -1);
    }

    public static void show(CharSequence msg, int duration, int sharedId) {
        show(msg, duration, sharedId, 0);
    }

    public static void show(CharSequence msg, int duration, int sharedId, @DrawableRes int iconId) {
        Toast toast = makeToast(R.layout.x_toast);
        View view = toast.getView();
        XToastTextView textView = (XToastTextView) view.findViewById(R.id.textView);
        textView.setIcon(iconId);
        toast.setDuration(duration);
        if (Xui.isSharedDisplay()) {
            textView.setElevation(0.0f);
        } else {
            textView.setElevation(12.0f);
        }
        textView.setText(msg);
        if (sharedId != -1) {
            invokeShared(toast, sharedId);
        }
        toast.show();
    }

    private static void invokeShared(Toast toast, int sharedId) {
        try {
            Method method = Toast.class.getMethod("setSharedId", Integer.TYPE);
            method.invoke(toast, Integer.valueOf(sharedId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Toast makeToast(@LayoutRes int layoutId) {
        Context context = getApplicationContext();
        View view = LayoutInflater.from(context).inflate(layoutId, (ViewGroup) null);
        Toast toast = new Toast(context);
        toast.setGravity(8388661, 0, 0);
        toast.setView(view);
        return toast;
    }

    private static int charactersSize(CharSequence msg) {
        if (msg == null) {
            return 0;
        }
        String[] parts = msg.toString().trim().split(" ");
        int count = 0;
        for (String part : parts) {
            if (part.trim().length() != 0) {
                boolean hasFull = false;
                int lastCharacterType = -1;
                for (int i = 0; i < part.length(); i++) {
                    char c = part.charAt(i);
                    if (XCharacterUtils.isFullAngle(c)) {
                        if (lastCharacterType == 0) {
                            count++;
                        }
                        lastCharacterType = 1;
                        count++;
                        hasFull = true;
                    } else {
                        lastCharacterType = 0;
                    }
                }
                if (hasFull) {
                    if (lastCharacterType == 0) {
                        count++;
                    }
                } else {
                    count++;
                }
            }
        }
        return count;
    }

    /* loaded from: classes25.dex */
    public static class XToastTextView extends XTextView {
        private int mIcon;

        public XToastTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            if (this.mXViewDelegate != null && this.mXViewDelegate.getThemeViewModel() != null) {
                this.mXViewDelegate.getThemeViewModel().addCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.app.-$$Lambda$XToast$XToastTextView$LCJ2VUUQghoMdjN9EoMvxonSZGM
                    @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
                    public final void onThemeChanged() {
                        XToast.XToastTextView.this.lambda$new$0$XToast$XToastTextView();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$new$0$XToast$XToastTextView() {
            if (this.mIcon != 0) {
                logD("XToast onThemeChanged !!! ");
                setCompoundDrawablesWithIntrinsicBounds(this.mIcon, 0, 0, 0);
            }
        }

        public void setIcon(@DrawableRes int icon) {
            this.mIcon = icon;
            setCompoundDrawablesWithIntrinsicBounds(this.mIcon, 0, 0, 0);
        }
    }
}
