package com.android.systemui;

import android.os.RemoteException;
import android.util.Log;
import android.view.IDockedStackListener;
import android.view.WindowManagerGlobal;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* loaded from: classes21.dex */
public class DockedStackExistsListener {
    private static final String TAG = "DockedStackExistsListener";
    private static boolean mLastExists;
    private static ArrayList<WeakReference<Consumer<Boolean>>> sCallbacks = new ArrayList<>();

    static {
        try {
            WindowManagerGlobal.getWindowManagerService().registerDockedStackListener(new IDockedStackListener.Stub() { // from class: com.android.systemui.DockedStackExistsListener.1
                public void onDividerVisibilityChanged(boolean b) throws RemoteException {
                }

                public void onDockedStackExistsChanged(boolean exists) throws RemoteException {
                    DockedStackExistsListener.onDockedStackExistsChanged(exists);
                }

                public void onDockedStackMinimizedChanged(boolean b, long l, boolean b1) throws RemoteException {
                }

                public void onAdjustedForImeChanged(boolean b, long l) throws RemoteException {
                }

                public void onDockSideChanged(int i) throws RemoteException {
                }
            });
        } catch (RemoteException e) {
            Log.e(TAG, "Failed registering docked stack exists listener", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void onDockedStackExistsChanged(final boolean exists) {
        mLastExists = exists;
        synchronized (sCallbacks) {
            sCallbacks.removeIf(new Predicate() { // from class: com.android.systemui.-$$Lambda$DockedStackExistsListener$fsI9l50cYy8em3Xlw9NfoEH95Z8
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return DockedStackExistsListener.lambda$onDockedStackExistsChanged$0(exists, (WeakReference) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$onDockedStackExistsChanged$0(boolean exists, WeakReference wf) {
        Consumer<Boolean> l = (Consumer) wf.get();
        if (l != null) {
            l.accept(Boolean.valueOf(exists));
        }
        return l == null;
    }

    public static void register(Consumer<Boolean> callback) {
        callback.accept(Boolean.valueOf(mLastExists));
        synchronized (sCallbacks) {
            sCallbacks.add(new WeakReference<>(callback));
        }
    }
}
