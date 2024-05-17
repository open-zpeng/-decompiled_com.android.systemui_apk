package com.android.keyguard;

import android.app.Presentation;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.media.MediaRouter;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.phone.NavigationBarView;
import com.android.systemui.util.InjectionInflationController;
import com.xiaopeng.systemui.controller.CarController;
/* loaded from: classes19.dex */
public class KeyguardDisplayManager {
    private static boolean DEBUG = false;
    protected static final String TAG = "KeyguardDisplayManager";
    private final Context mContext;
    private final DisplayManager mDisplayService;
    private final InjectionInflationController mInjectableInflater;
    private final MediaRouter mMediaRouter;
    private boolean mShowing;
    private final DisplayInfo mTmpDisplayInfo = new DisplayInfo();
    private final SparseArray<Presentation> mPresentations = new SparseArray<>();
    private final NavigationBarController mNavBarController = (NavigationBarController) Dependency.get(NavigationBarController.class);
    private final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener() { // from class: com.android.keyguard.KeyguardDisplayManager.1
        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int displayId) {
            Display display = KeyguardDisplayManager.this.mDisplayService.getDisplay(displayId);
            if (KeyguardDisplayManager.this.mShowing) {
                KeyguardDisplayManager.this.updateNavigationBarVisibility(displayId, false);
                KeyguardDisplayManager.this.showPresentation(display);
            }
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int displayId) {
            Display display;
            Presentation presentation;
            if (displayId != 0 && (display = KeyguardDisplayManager.this.mDisplayService.getDisplay(displayId)) != null && KeyguardDisplayManager.this.mShowing && (presentation = (Presentation) KeyguardDisplayManager.this.mPresentations.get(displayId)) != null && !presentation.getDisplay().equals(display)) {
                KeyguardDisplayManager.this.hidePresentation(displayId);
                KeyguardDisplayManager.this.showPresentation(display);
            }
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int displayId) {
            KeyguardDisplayManager.this.hidePresentation(displayId);
        }
    };
    private final MediaRouter.SimpleCallback mMediaRouterCallback = new MediaRouter.SimpleCallback() { // from class: com.android.keyguard.KeyguardDisplayManager.2
        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
            if (KeyguardDisplayManager.DEBUG) {
                Log.d(KeyguardDisplayManager.TAG, "onRouteSelected: type=" + type + ", info=" + info);
            }
            KeyguardDisplayManager keyguardDisplayManager = KeyguardDisplayManager.this;
            keyguardDisplayManager.updateDisplays(keyguardDisplayManager.mShowing);
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteUnselected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
            if (KeyguardDisplayManager.DEBUG) {
                Log.d(KeyguardDisplayManager.TAG, "onRouteUnselected: type=" + type + ", info=" + info);
            }
            KeyguardDisplayManager keyguardDisplayManager = KeyguardDisplayManager.this;
            keyguardDisplayManager.updateDisplays(keyguardDisplayManager.mShowing);
        }

        @Override // android.media.MediaRouter.Callback
        public void onRoutePresentationDisplayChanged(MediaRouter router, MediaRouter.RouteInfo info) {
            if (KeyguardDisplayManager.DEBUG) {
                Log.d(KeyguardDisplayManager.TAG, "onRoutePresentationDisplayChanged: info=" + info);
            }
            KeyguardDisplayManager keyguardDisplayManager = KeyguardDisplayManager.this;
            keyguardDisplayManager.updateDisplays(keyguardDisplayManager.mShowing);
        }
    };

    public KeyguardDisplayManager(Context context, InjectionInflationController injectableInflater) {
        this.mContext = context;
        this.mInjectableInflater = injectableInflater;
        this.mMediaRouter = (MediaRouter) this.mContext.getSystemService(MediaRouter.class);
        this.mDisplayService = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
        this.mDisplayService.registerDisplayListener(this.mDisplayListener, null);
    }

    private boolean isKeyguardShowable(Display display) {
        if (display == null) {
            if (DEBUG) {
                Log.i(TAG, "Cannot show Keyguard on null display");
            }
            return false;
        } else if (display.getDisplayId() == 0) {
            if (DEBUG) {
                Log.i(TAG, "Do not show KeyguardPresentation on the default display");
            }
            return false;
        } else {
            display.getDisplayInfo(this.mTmpDisplayInfo);
            if ((this.mTmpDisplayInfo.flags & 4) != 0) {
                if (DEBUG) {
                    Log.i(TAG, "Do not show KeyguardPresentation on a private display");
                }
                return false;
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean showPresentation(Display display) {
        if (isKeyguardShowable(display)) {
            if (DEBUG) {
                Log.i(TAG, "Keyguard enabled on display: " + display);
            }
            final int displayId = display.getDisplayId();
            if (this.mPresentations.get(displayId) == null) {
                Presentation presentation = new KeyguardPresentation(this.mContext, display, this.mInjectableInflater);
                presentation.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.keyguard.-$$Lambda$KeyguardDisplayManager$aDNbCRMbiN2e5OjkGdRkys94554
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        KeyguardDisplayManager.this.lambda$showPresentation$0$KeyguardDisplayManager(displayId, dialogInterface);
                    }
                });
                try {
                    presentation.show();
                } catch (WindowManager.InvalidDisplayException ex) {
                    Log.w(TAG, "Invalid display:", ex);
                    presentation = null;
                }
                if (presentation != null) {
                    this.mPresentations.append(displayId, presentation);
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public /* synthetic */ void lambda$showPresentation$0$KeyguardDisplayManager(int displayId, DialogInterface dialog) {
        if (this.mPresentations.get(displayId) != null) {
            this.mPresentations.remove(displayId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hidePresentation(int displayId) {
        Presentation presentation = this.mPresentations.get(displayId);
        if (presentation != null) {
            presentation.dismiss();
            this.mPresentations.remove(displayId);
        }
    }

    public void show() {
        if (!this.mShowing) {
            if (DEBUG) {
                Log.v(TAG, "show");
            }
            this.mMediaRouter.addCallback(4, this.mMediaRouterCallback, 8);
            updateDisplays(true);
        }
        this.mShowing = true;
    }

    public void hide() {
        if (this.mShowing) {
            if (DEBUG) {
                Log.v(TAG, "hide");
            }
            this.mMediaRouter.removeCallback(this.mMediaRouterCallback);
            updateDisplays(false);
        }
        this.mShowing = false;
    }

    protected boolean updateDisplays(boolean showing) {
        boolean changed;
        if (showing) {
            Display[] displays = this.mDisplayService.getDisplays();
            changed = false;
            for (Display display : displays) {
                int displayId = display.getDisplayId();
                updateNavigationBarVisibility(displayId, false);
                changed |= showPresentation(display);
            }
        } else {
            changed = this.mPresentations.size() > 0;
            for (int i = this.mPresentations.size() - 1; i >= 0; i--) {
                int displayId2 = this.mPresentations.keyAt(i);
                updateNavigationBarVisibility(displayId2, true);
                this.mPresentations.valueAt(i).dismiss();
            }
            this.mPresentations.clear();
        }
        return changed;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNavigationBarVisibility(int displayId, boolean navBarVisible) {
        NavigationBarView navBarView;
        if (displayId == 0 || (navBarView = this.mNavBarController.getNavigationBarView(displayId)) == null) {
            return;
        }
        if (navBarVisible) {
            navBarView.getRootView().setVisibility(0);
        } else {
            navBarView.getRootView().setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes19.dex */
    public static final class KeyguardPresentation extends Presentation {
        private static final int MOVE_CLOCK_TIMEOUT = 10000;
        private static final int VIDEO_SAFE_REGION = 80;
        private View mClock;
        private final InjectionInflationController mInjectableInflater;
        private int mMarginLeft;
        private int mMarginTop;
        Runnable mMoveTextRunnable;
        private int mUsableHeight;
        private int mUsableWidth;

        KeyguardPresentation(Context context, Display display, InjectionInflationController injectionInflater) {
            super(context, display, R.style.Theme_SystemUI_KeyguardPresentation);
            this.mMoveTextRunnable = new Runnable() { // from class: com.android.keyguard.KeyguardDisplayManager.KeyguardPresentation.1
                @Override // java.lang.Runnable
                public void run() {
                    int x = KeyguardPresentation.this.mMarginLeft + ((int) (Math.random() * (KeyguardPresentation.this.mUsableWidth - KeyguardPresentation.this.mClock.getWidth())));
                    int y = KeyguardPresentation.this.mMarginTop + ((int) (Math.random() * (KeyguardPresentation.this.mUsableHeight - KeyguardPresentation.this.mClock.getHeight())));
                    KeyguardPresentation.this.mClock.setTranslationX(x);
                    KeyguardPresentation.this.mClock.setTranslationY(y);
                    KeyguardPresentation.this.mClock.postDelayed(KeyguardPresentation.this.mMoveTextRunnable, 10000L);
                }
            };
            this.mInjectableInflater = injectionInflater;
            getWindow().setType(CarController.TYPE_CAR_CLTC_DRIVE_DISTANCE);
            setCancelable(false);
        }

        @Override // android.app.Dialog, android.view.Window.Callback
        public void onDetachedFromWindow() {
            this.mClock.removeCallbacks(this.mMoveTextRunnable);
        }

        @Override // android.app.Dialog
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Point p = new Point();
            getDisplay().getSize(p);
            this.mUsableWidth = (p.x * 80) / 100;
            this.mUsableHeight = (p.y * 80) / 100;
            this.mMarginLeft = (p.x * 20) / 200;
            this.mMarginTop = (p.y * 20) / 200;
            LayoutInflater inflater = this.mInjectableInflater.injectable(LayoutInflater.from(getContext()));
            setContentView(inflater.inflate(R.layout.keyguard_presentation, (ViewGroup) null));
            getWindow().getDecorView().setSystemUiVisibility(1792);
            getWindow().setNavigationBarContrastEnforced(false);
            getWindow().setNavigationBarColor(0);
            this.mClock = findViewById(R.id.clock);
            this.mClock.post(this.mMoveTextRunnable);
        }
    }
}
