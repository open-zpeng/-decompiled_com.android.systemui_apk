package com.badlogic.gdx.backends.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.android.systemui.DemoMode;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SnapshotArray;
import java.lang.reflect.Method;
/* loaded from: classes21.dex */
public abstract class AndroidViewApplication<T extends ApplicationListener> extends FrameLayout implements AndroidApplicationBase {
    private static final String TAG = "AndroidViewApplication";
    private final Array<AndroidEventListener> androidEventListeners;
    protected ApplicationLogger applicationLogger;
    protected AndroidAudio audio;
    protected Callbacks callbacks;
    protected AndroidClipboard clipboard;
    protected final Array<Runnable> executedRunnables;
    protected AndroidFiles files;
    protected boolean finishing;
    protected boolean firstResume;
    protected AndroidGraphics graphics;
    public Handler handler;
    protected AndroidInput input;
    protected final SnapshotArray<LifecycleListener> lifecycleListeners;
    protected T listener;
    protected int logLevel;
    private Handler mMainHandler;
    protected AndroidNet net;
    protected final Array<Runnable> runnables;

    /* loaded from: classes21.dex */
    public interface Callbacks {
        void exit();
    }

    static {
        GdxNativesLoader.load();
    }

    public AndroidViewApplication(Context context) {
        this(context, null);
    }

    public AndroidViewApplication(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AndroidViewApplication(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.firstResume = true;
        this.runnables = new Array<>();
        this.executedRunnables = new Array<>();
        this.lifecycleListeners = new SnapshotArray<>(LifecycleListener.class);
        this.androidEventListeners = new Array<>();
        this.logLevel = 2;
    }

    public Callbacks getCallbacks() {
        return this.callbacks;
    }

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    protected void createWakeLock(boolean use) {
    }

    public View initializeForView(T listener) {
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        return initializeForView(listener, config);
    }

    public View initializeForView(T listener, AndroidApplicationConfiguration config) {
        log(TAG, "initializeForView");
        if (getVersion() < 8) {
            throw new GdxRuntimeException("LibGDX requires Android API Level 8 or later.");
        }
        setApplicationLogger(new AndroidApplicationLogger());
        this.graphics = new AndroidGraphics(this, config, config.resolutionStrategy == null ? new FillResolutionStrategy() : config.resolutionStrategy);
        this.input = AndroidInputFactory.newAndroidInput(this, getContext(), this.graphics.view, config);
        this.audio = new AndroidAudio(getContext(), config);
        Context remoteContext = RemoteContext.getRemoteContext(getContext());
        this.files = new AndroidFiles(remoteContext.getResources().getAssets(), remoteContext.getFilesDir().getAbsolutePath());
        this.net = new AndroidNet(this);
        this.listener = listener;
        this.handler = new Handler();
        this.clipboard = new AndroidClipboard(getContext());
        addLifecycleListener(new LifecycleListener() { // from class: com.badlogic.gdx.backends.android.AndroidViewApplication.1
            @Override // com.badlogic.gdx.LifecycleListener
            public void resume() {
                AndroidViewApplication.this.audio.resume();
            }

            @Override // com.badlogic.gdx.LifecycleListener
            public void pause() {
                AndroidViewApplication.this.audio.pause();
            }

            @Override // com.badlogic.gdx.LifecycleListener
            public void dispose() {
                AndroidViewApplication.this.audio.dispose();
            }
        });
        Gdx.app = this;
        Gdx.input = getInput();
        Gdx.audio = getAudio();
        Gdx.files = getFiles();
        Gdx.graphics = getGraphics();
        Gdx.net = getNet();
        createWakeLock(config.useWakelock);
        useImmersiveMode(config.useImmersiveMode);
        if (config.useImmersiveMode && getVersion() >= 19) {
            try {
                Class<?> vlistener = Class.forName("com.badlogic.gdx.backends.android.AndroidVisibilityListener");
                Object o = vlistener.newInstance();
                Method method = vlistener.getDeclaredMethod("createListener", AndroidApplicationBase.class);
                method.invoke(o, this);
            } catch (Exception e) {
                log(TAG, "Failed to create AndroidVisibilityListener", e);
            }
        }
        return this.graphics.getView();
    }

    @Override // com.badlogic.gdx.backends.android.AndroidApplicationBase
    public Array<Runnable> getRunnables() {
        return this.runnables;
    }

    @Override // com.badlogic.gdx.backends.android.AndroidApplicationBase
    public Array<Runnable> getExecutedRunnables() {
        return this.executedRunnables;
    }

    @Override // com.badlogic.gdx.backends.android.AndroidApplicationBase
    public void runOnUiThread(Runnable runnable) {
        this.mMainHandler.post(runnable);
    }

    @Override // com.badlogic.gdx.backends.android.AndroidApplicationBase
    public void startActivity(Intent intent) {
        getContext().startActivity(intent);
    }

    @Override // com.badlogic.gdx.Application
    public AndroidInput getInput() {
        return this.input;
    }

    @Override // com.badlogic.gdx.backends.android.AndroidApplicationBase
    public SnapshotArray<LifecycleListener> getLifecycleListeners() {
        return this.lifecycleListeners;
    }

    @Override // com.badlogic.gdx.backends.android.AndroidApplicationBase
    public Window getApplicationWindow() {
        throw new UnsupportedOperationException();
    }

    @Override // com.badlogic.gdx.backends.android.AndroidApplicationBase
    public WindowManager getWindowManager() {
        return (WindowManager) getContext().getSystemService("window");
    }

    @Override // com.badlogic.gdx.backends.android.AndroidApplicationBase
    public void useImmersiveMode(boolean use) {
        if (!use || getVersion() < 19) {
            return;
        }
        try {
            View view = this.graphics.getView();
            Method m = View.class.getMethod("setSystemUiVisibility", Integer.TYPE);
            m.invoke(view, 5894);
        } catch (Exception e) {
            log(TAG, "Failed to setup immersive mode, a throwable has occurred.", e);
        }
    }

    @Override // android.view.View, com.badlogic.gdx.backends.android.AndroidApplicationBase
    public Handler getHandler() {
        return this.handler;
    }

    @Override // com.badlogic.gdx.Application
    public ApplicationListener getApplicationListener() {
        return this.listener;
    }

    @Override // com.badlogic.gdx.Application
    public Graphics getGraphics() {
        return this.graphics;
    }

    @Override // com.badlogic.gdx.Application
    public Audio getAudio() {
        return this.audio;
    }

    @Override // com.badlogic.gdx.Application
    public Files getFiles() {
        return this.files;
    }

    @Override // com.badlogic.gdx.Application
    public Net getNet() {
        return this.net;
    }

    @Override // com.badlogic.gdx.Application
    public void log(String tag, String message) {
        if (this.logLevel >= 2) {
            Log.i(tag, message);
        }
    }

    @Override // com.badlogic.gdx.Application
    public void log(String tag, String message, Throwable exception) {
        if (this.logLevel >= 2) {
            Log.i(tag, message, exception);
        }
    }

    @Override // com.badlogic.gdx.Application
    public void error(String tag, String message) {
        if (this.logLevel >= 1) {
            Log.e(tag, message);
        }
    }

    @Override // com.badlogic.gdx.Application
    public void error(String tag, String message, Throwable exception) {
        if (this.logLevel >= 1) {
            Log.e(tag, message, exception);
        }
    }

    @Override // com.badlogic.gdx.Application
    public void debug(String tag, String message) {
        if (this.logLevel >= 3) {
            Log.d(tag, message);
        }
    }

    @Override // com.badlogic.gdx.Application
    public void debug(String tag, String message, Throwable exception) {
        if (this.logLevel >= 3) {
            Log.d(tag, message, exception);
        }
    }

    @Override // com.badlogic.gdx.Application
    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    @Override // com.badlogic.gdx.Application
    public int getLogLevel() {
        return this.logLevel;
    }

    @Override // com.badlogic.gdx.Application
    public void setApplicationLogger(ApplicationLogger applicationLogger) {
        this.applicationLogger = applicationLogger;
    }

    @Override // com.badlogic.gdx.Application
    public ApplicationLogger getApplicationLogger() {
        return this.applicationLogger;
    }

    @Override // com.badlogic.gdx.Application
    public Application.ApplicationType getType() {
        return Application.ApplicationType.Android;
    }

    @Override // com.badlogic.gdx.Application
    public int getVersion() {
        return Build.VERSION.SDK_INT;
    }

    @Override // com.badlogic.gdx.Application
    public long getJavaHeap() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    @Override // com.badlogic.gdx.Application
    public long getNativeHeap() {
        return Debug.getNativeHeapAllocatedSize();
    }

    @Override // com.badlogic.gdx.Application
    public Preferences getPreferences(String name) {
        return new AndroidPreferences(getContext().getSharedPreferences(name, 0));
    }

    @Override // com.badlogic.gdx.Application
    public Clipboard getClipboard() {
        return this.clipboard;
    }

    @Override // com.badlogic.gdx.Application
    public void postRunnable(Runnable runnable) {
        synchronized (this.runnables) {
            this.runnables.add(runnable);
            Gdx.graphics.requestRendering();
        }
    }

    @Override // com.badlogic.gdx.Application
    public void exit() {
        log(TAG, DemoMode.COMMAND_EXIT);
        this.handler.post(new Runnable() { // from class: com.badlogic.gdx.backends.android.AndroidViewApplication.2
            @Override // java.lang.Runnable
            public void run() {
                AndroidViewApplication.this.callbacks.exit();
            }
        });
    }

    @Override // com.badlogic.gdx.Application
    public void addLifecycleListener(LifecycleListener listener) {
        synchronized (this.lifecycleListeners) {
            this.lifecycleListeners.add(listener);
        }
    }

    @Override // com.badlogic.gdx.Application
    public void removeLifecycleListener(LifecycleListener listener) {
        synchronized (this.lifecycleListeners) {
            this.lifecycleListeners.removeValue(listener, true);
        }
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        boolean keyboardAvailable = false;
        if (config.hardKeyboardHidden == 1) {
            keyboardAvailable = true;
        }
        this.input.keyboardAvailable = keyboardAvailable;
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View changedView, int visibility) {
        log(TAG, "onVisibilityChanged:" + visibility);
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == 0) {
            onResume();
        } else {
            onPause();
        }
    }

    @TargetApi(19)
    public void onPause() {
        log(TAG, "onPause");
        boolean isContinuous = this.graphics.isContinuousRendering();
        boolean isContinuousEnforced = AndroidGraphics.enforceContinuousRendering;
        AndroidGraphics.enforceContinuousRendering = true;
        this.graphics.setContinuousRendering(true);
        this.graphics.pause();
        this.audio.pause();
        this.input.onPause();
        if (isFinishing() || isActivityFinishing()) {
            this.graphics.clearManagedCaches();
            this.graphics.destroy();
        }
        AndroidGraphics.enforceContinuousRendering = isContinuousEnforced;
        this.graphics.setContinuousRendering(isContinuous);
        this.graphics.onPauseGLSurfaceView();
    }

    public void onResume() {
        log(TAG, "onResume");
        Gdx.app = this;
        Gdx.input = getInput();
        Gdx.audio = getAudio();
        Gdx.files = getFiles();
        Gdx.graphics = getGraphics();
        Gdx.net = getNet();
        this.input.onResume();
        AndroidGraphics androidGraphics = this.graphics;
        if (androidGraphics != null) {
            androidGraphics.onResumeGLSurfaceView();
        }
        if (!this.firstResume) {
            this.audio.resume();
            this.graphics.resume();
            return;
        }
        this.firstResume = false;
    }

    public void finish() {
        this.finishing = true;
        onPause();
    }

    public boolean isFinishing() {
        return this.finishing;
    }

    public Activity getActivity() {
        Context context = getContext();
        while (context != null && !(context instanceof android.app.Application)) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            if (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                context = context.getApplicationContext();
            }
        }
        return null;
    }

    public boolean isActivityFinishing() {
        Activity activity = getActivity();
        return activity != null && activity.isFinishing();
    }
}
