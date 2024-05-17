package com.xiaopeng.xui.app;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.xiaopeng.xpui.R;
/* loaded from: classes25.dex */
public class XPanelActivity extends AppCompatActivity {
    public static final int FEATURE_XUI_FULLSCREEN = 14;
    private static final long PANEL_DISMISS_DELAY = 30000;
    public static final int TYPE_DISMISS_BACK = 1;
    public static final int TYPE_DISMISS_STOP = 0;
    private Builder mLayoutBuilder;
    private static final String KEY_PANEL_SPEED = "key_panel_car_speed";
    private static final Uri URI_PANEL_SPEED = Settings.System.getUriFor(KEY_PANEL_SPEED);
    private float mLastSpeed = 0.0f;
    private final Runnable mAutoDismissRunnable = new Runnable() { // from class: com.xiaopeng.xui.app.XPanelActivity.1
        @Override // java.lang.Runnable
        public void run() {
            XPanelActivity.this.handleAutoDismissEvent();
        }
    };
    private final Handler mPanelHandler = new Handler();
    private boolean mCloseOnTouchOutside = false;
    private boolean mCloseOnPauseOneshot = true;
    private float mDimAlpha = 0.6f;
    private long mDismissDelay = 0;
    private boolean mAutoVisibility = false;
    private int mDismissType = 0;
    private ContentObserver mPanelObserver = new ContentObserver(this.mPanelHandler) { // from class: com.xiaopeng.xui.app.XPanelActivity.3
        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            XPanelActivity.this.onPanelSpeedChanged();
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(14);
        super.onCreate(savedInstanceState);
        this.mLayoutBuilder = new Builder(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        int orientation = getResources().getConfiguration().orientation;
        int x = dp2px(this, R.dimen.x_compat_app_panel_x);
        int y = dp2px(this, R.dimen.x_compat_app_panel_y);
        int width = dp2px(this, R.dimen.x_compat_app_panel_width);
        int height = dp2px(this, R.dimen.x_compat_app_panel_height);
        if (orientation == 1) {
            getLayoutBuilder().setX(x).setY(y).setWidth(width).setHeight(height).setGravity(80).apply();
        } else if (orientation == 2) {
            getLayoutBuilder().setX(x).setY(y).setWidth(width).setHeight(height).setGravity(49).apply();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        postAutoDismissRunnable(false);
        handleWindowVisibility(true);
        registerPanelObserver(getApplicationContext());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        handleWindowVisibility(false);
        unregisterPanelObserver(getApplicationContext());
        if (this.mCloseOnPauseOneshot) {
            handleDismissEvent();
        }
        this.mCloseOnPauseOneshot = true;
    }

    @Override // android.app.Activity
    public void recreate() {
        super.recreate();
        this.mCloseOnPauseOneshot = false;
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent event) {
        if (shouldCloseOnTouch(this, event)) {
            handleDismissEvent();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
        postAutoDismissRunnable(false);
        return super.dispatchTouchEvent(ev);
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
    }

    @Override // android.app.Activity
    public boolean moveTaskToBack(boolean nonRoot) {
        return super.moveTaskToBack(nonRoot);
    }

    public Builder getLayoutBuilder() {
        return this.mLayoutBuilder;
    }

    public void setDismissType(int type) {
        this.mDismissType = type;
    }

    public void setCloseOnPauseOneshot(boolean close) {
        this.mCloseOnPauseOneshot = close;
    }

    public void setCloseOnTouchOutside(boolean close) {
        this.mCloseOnTouchOutside = close;
    }

    public void setDimAlpha(float alpha) {
        this.mDimAlpha = alpha;
    }

    public void setDismissDelay(long delay) {
        this.mDismissDelay = delay;
    }

    public void setAutoVisibilityEnable(boolean enable) {
        this.mAutoVisibility = enable;
    }

    public void handleDismissEvent() {
        final int type = this.mDismissType;
        Runnable r = new Runnable() { // from class: com.xiaopeng.xui.app.XPanelActivity.2
            @Override // java.lang.Runnable
            public void run() {
                int i = type;
                if (i == 0) {
                    ActivityUtils.finish(XPanelActivity.this);
                } else if (i == 1) {
                    ActivityUtils.moveTaskToBack(XPanelActivity.this, true);
                }
            }
        };
        handleWindowVisibility(false);
        this.mPanelHandler.postDelayed(r, this.mDismissDelay);
    }

    public void dispatchUserEvent() {
        postAutoDismissRunnable(false);
    }

    public void handleWindowVisibility(boolean visible) {
        if (this.mAutoVisibility) {
            setWindowVisible(visible);
        }
    }

    private void setWindowVisible(boolean visible) {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            if (lp != null) {
                lp.alpha = visible ? 1.0f : 0.0f;
                lp.dimAmount = visible ? this.mDimAlpha : 0.0f;
            }
            window.setAttributes(lp);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAutoDismissEvent() {
        float speed = getSpeed();
        if (speed > 0.0f) {
            handleDismissEvent();
        }
    }

    private void postAutoDismissRunnable(boolean fromSpeed) {
        float speed = getSpeed();
        boolean changed = true;
        if (fromSpeed) {
            changed = this.mLastSpeed * speed <= 0.0f;
        }
        if (speed <= 0.0f) {
            this.mPanelHandler.removeCallbacks(this.mAutoDismissRunnable);
        } else if (changed) {
            this.mPanelHandler.removeCallbacks(this.mAutoDismissRunnable);
            this.mPanelHandler.postDelayed(this.mAutoDismissRunnable, PANEL_DISMISS_DELAY);
        }
        this.mLastSpeed = speed;
    }

    private float getSpeed() {
        try {
            return Settings.System.getFloat(getContentResolver(), KEY_PANEL_SPEED, 0.0f);
        } catch (Exception e) {
            return 0.0f;
        }
    }

    private boolean isOutOfBounds(Context context, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        View decorView = getWindow().getDecorView();
        return x < (-slop) || y < (-slop) || x > decorView.getWidth() + slop || y > decorView.getHeight() + slop;
    }

    private boolean shouldCloseOnTouch(Context context, MotionEvent event) {
        boolean isOutside = (event.getAction() == 0 && isOutOfBounds(context, event)) || event.getAction() == 4;
        View decorView = getWindow().getDecorView();
        return this.mCloseOnTouchOutside && decorView != null && isOutside;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPanelSpeedChanged() {
        postAutoDismissRunnable(true);
    }

    private void registerPanelObserver(Context context) {
        try {
            context.getContentResolver().registerContentObserver(URI_PANEL_SPEED, true, this.mPanelObserver);
        } catch (Exception e) {
        }
    }

    private void unregisterPanelObserver(Context context) {
        try {
            context.getContentResolver().unregisterContentObserver(this.mPanelObserver);
        } catch (Exception e) {
        }
    }

    /* loaded from: classes25.dex */
    public static final class Builder {
        private int flags;
        private int gravity;
        private int height;
        private final Activity mActivity;
        private int systemUiVisibility;
        private int width;
        private int x;
        private int y;

        public Builder(@NonNull Activity activity) {
            this.mActivity = activity;
            init();
        }

        private void init() {
            WindowManager.LayoutParams lp;
            Activity activity = this.mActivity;
            if (activity != null && activity.getWindow() != null && (lp = this.mActivity.getWindow().getAttributes()) != null) {
                this.x = lp.x;
                this.y = lp.y;
                this.flags = lp.flags;
                this.width = lp.width;
                this.height = lp.height;
                this.gravity = lp.gravity;
                this.systemUiVisibility = lp.systemUiVisibility;
            }
        }

        public void apply() {
            WindowManager.LayoutParams lp;
            Activity activity = this.mActivity;
            if (activity != null && activity.getWindow() != null && (lp = this.mActivity.getWindow().getAttributes()) != null) {
                lp.x = this.x;
                lp.y = this.y;
                lp.flags |= this.flags;
                lp.width = this.width;
                lp.height = this.height;
                lp.gravity = this.gravity;
                lp.systemUiVisibility |= this.systemUiVisibility;
                this.mActivity.getWindow().setAttributes(lp);
            }
        }

        public Builder setX(int x) {
            this.x = x;
            return this;
        }

        public Builder setY(int y) {
            this.y = y;
            return this;
        }

        public Builder setFlags(int flags) {
            this.flags = flags;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setSystemUiVisibility(int visibility) {
            this.systemUiVisibility = visibility;
            return this;
        }
    }

    public static int dp2px(Context context, @DimenRes int id) {
        float scale = context.getResources().getDisplayMetrics().density;
        float value = context.getResources().getDimension(id);
        return (int) ((value * scale) + 0.5f);
    }
}
