package com.android.systemui;

import android.app.ActivityTaskManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.hardware.display.DisplayManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.SizeCompatModeActivityController;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.statusbar.CommandQueue;
import java.lang.ref.WeakReference;
/* loaded from: classes21.dex */
public class SizeCompatModeActivityController extends SystemUI implements CommandQueue.Callbacks {
    private static final String TAG = "SizeCompatMode";
    private final SparseArray<RestartActivityButton> mActiveButtons;
    private final SparseArray<WeakReference<Context>> mDisplayContextCache;
    private boolean mHasShownHint;

    public SizeCompatModeActivityController() {
        this(ActivityManagerWrapper.getInstance());
    }

    @VisibleForTesting
    SizeCompatModeActivityController(ActivityManagerWrapper am) {
        this.mActiveButtons = new SparseArray<>(1);
        this.mDisplayContextCache = new SparseArray<>(0);
        am.registerTaskStackListener(new TaskStackChangeListener() { // from class: com.android.systemui.SizeCompatModeActivityController.1
            @Override // com.android.systemui.shared.system.TaskStackChangeListener
            public void onSizeCompatModeActivityChanged(int displayId, IBinder activityToken) {
                SizeCompatModeActivityController.this.updateRestartButton(displayId, activityToken);
            }
        });
    }

    @Override // com.android.systemui.SystemUI
    public void start() {
        ((CommandQueue) SysUiServiceProvider.getComponent(this.mContext, CommandQueue.class)).addCallback((CommandQueue.Callbacks) this);
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void setImeWindowStatus(int displayId, IBinder token, int vis, int backDisposition, boolean showImeSwitcher) {
        RestartActivityButton button = this.mActiveButtons.get(displayId);
        if (button == null) {
            return;
        }
        boolean imeShown = (vis & 2) != 0;
        int newVisibility = imeShown ? 8 : 0;
        if (button.getVisibility() != newVisibility) {
            button.setVisibility(newVisibility);
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void onDisplayRemoved(int displayId) {
        this.mDisplayContextCache.remove(displayId);
        removeRestartButton(displayId);
    }

    private void removeRestartButton(int displayId) {
        RestartActivityButton button = this.mActiveButtons.get(displayId);
        if (button != null) {
            button.remove();
            this.mActiveButtons.remove(displayId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRestartButton(int displayId, IBinder activityToken) {
        if (activityToken == null) {
            removeRestartButton(displayId);
            return;
        }
        RestartActivityButton restartButton = this.mActiveButtons.get(displayId);
        if (restartButton != null) {
            restartButton.updateLastTargetActivity(activityToken);
            return;
        }
        Context context = getOrCreateDisplayContext(displayId);
        if (context == null) {
            Log.i(TAG, "Cannot get context for display " + displayId);
            return;
        }
        RestartActivityButton restartButton2 = createRestartButton(context);
        restartButton2.updateLastTargetActivity(activityToken);
        if (restartButton2.show()) {
            this.mActiveButtons.append(displayId, restartButton2);
        } else {
            onDisplayRemoved(displayId);
        }
    }

    @VisibleForTesting
    RestartActivityButton createRestartButton(Context context) {
        RestartActivityButton button = new RestartActivityButton(context, this.mHasShownHint);
        this.mHasShownHint = true;
        return button;
    }

    private Context getOrCreateDisplayContext(int displayId) {
        Display display;
        if (displayId == 0) {
            return this.mContext;
        }
        Context context = null;
        WeakReference<Context> ref = this.mDisplayContextCache.get(displayId);
        if (ref != null) {
            Context context2 = ref.get();
            context = context2;
        }
        if (context == null && (display = ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).getDisplay(displayId)) != null) {
            Context context3 = this.mContext.createDisplayContext(display);
            this.mDisplayContextCache.put(displayId, new WeakReference<>(context3));
            return context3;
        }
        return context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes21.dex */
    public static class RestartActivityButton extends ImageButton implements View.OnClickListener, View.OnLongClickListener {
        IBinder mLastActivityToken;
        final int mPopupOffsetX;
        final int mPopupOffsetY;
        final boolean mShouldShowHint;
        PopupWindow mShowingHint;
        final WindowManager.LayoutParams mWinParams;

        RestartActivityButton(Context context, boolean hasShownHint) {
            super(context);
            this.mShouldShowHint = !hasShownHint;
            Drawable drawable = context.getDrawable(R.drawable.btn_restart);
            setImageDrawable(drawable);
            setContentDescription(context.getString(R.string.restart_button_description));
            int drawableW = drawable.getIntrinsicWidth();
            int drawableH = drawable.getIntrinsicHeight();
            this.mPopupOffsetX = drawableW / 2;
            this.mPopupOffsetY = drawableH * 2;
            ColorStateList color = ColorStateList.valueOf(-3355444);
            GradientDrawable mask = new GradientDrawable();
            mask.setShape(1);
            mask.setColor(color);
            setBackground(new RippleDrawable(color, null, mask));
            setOnClickListener(this);
            setOnLongClickListener(this);
            this.mWinParams = new WindowManager.LayoutParams();
            this.mWinParams.gravity = getGravity(getResources().getConfiguration().getLayoutDirection());
            WindowManager.LayoutParams layoutParams = this.mWinParams;
            layoutParams.width = drawableW * 2;
            layoutParams.height = drawableH * 2;
            layoutParams.type = 2038;
            layoutParams.flags = 40;
            layoutParams.format = -3;
            layoutParams.privateFlags |= 16;
            this.mWinParams.setTitle(SizeCompatModeActivityController.class.getSimpleName() + context.getDisplayId());
        }

        void updateLastTargetActivity(IBinder activityToken) {
            this.mLastActivityToken = activityToken;
        }

        boolean show() {
            try {
                ((WindowManager) getContext().getSystemService(WindowManager.class)).addView(this, this.mWinParams);
                return true;
            } catch (WindowManager.InvalidDisplayException e) {
                Log.w(SizeCompatModeActivityController.TAG, "Cannot show on display " + getContext().getDisplayId(), e);
                return false;
            }
        }

        void remove() {
            ((WindowManager) getContext().getSystemService(WindowManager.class)).removeViewImmediate(this);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            try {
                ActivityTaskManager.getService().restartActivityProcessIfVisible(this.mLastActivityToken);
            } catch (RemoteException e) {
                Log.w(SizeCompatModeActivityController.TAG, "Unable to restart activity", e);
            }
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View v) {
            showHint();
            return true;
        }

        @Override // android.widget.ImageView, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (this.mShouldShowHint) {
                showHint();
            }
        }

        @Override // android.view.View
        public void setLayoutDirection(int layoutDirection) {
            int gravity = getGravity(layoutDirection);
            if (this.mWinParams.gravity != gravity) {
                this.mWinParams.gravity = gravity;
                PopupWindow popupWindow = this.mShowingHint;
                if (popupWindow != null) {
                    popupWindow.dismiss();
                    showHint();
                }
                ((WindowManager) getContext().getSystemService(WindowManager.class)).updateViewLayout(this, this.mWinParams);
            }
            super.setLayoutDirection(layoutDirection);
        }

        void showHint() {
            if (this.mShowingHint != null) {
                return;
            }
            View popupView = LayoutInflater.from(getContext()).inflate(R.layout.size_compat_mode_hint, (ViewGroup) null);
            final PopupWindow popupWindow = new PopupWindow(popupView, -2, -2);
            popupWindow.setElevation(getResources().getDimension(R.dimen.bubble_elevation));
            popupWindow.setAnimationStyle(16973910);
            popupWindow.setClippingEnabled(false);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: com.android.systemui.-$$Lambda$SizeCompatModeActivityController$RestartActivityButton$rxc8GUe9hnz5kAfzl4xmCIiwi3Y
                @Override // android.widget.PopupWindow.OnDismissListener
                public final void onDismiss() {
                    SizeCompatModeActivityController.RestartActivityButton.this.lambda$showHint$0$SizeCompatModeActivityController$RestartActivityButton();
                }
            });
            this.mShowingHint = popupWindow;
            Button gotItButton = (Button) popupView.findViewById(R.id.got_it);
            gotItButton.setBackground(new RippleDrawable(ColorStateList.valueOf(-3355444), null, null));
            gotItButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.-$$Lambda$SizeCompatModeActivityController$RestartActivityButton$tZJkvUnAETgfbkQvNUGL2mQWd9s
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    popupWindow.dismiss();
                }
            });
            popupWindow.showAtLocation(this, this.mWinParams.gravity, this.mPopupOffsetX, this.mPopupOffsetY);
        }

        public /* synthetic */ void lambda$showHint$0$SizeCompatModeActivityController$RestartActivityButton() {
            this.mShowingHint = null;
        }

        private static int getGravity(int layoutDirection) {
            return (layoutDirection == 1 ? 8388611 : 8388613) | 80;
        }
    }
}
