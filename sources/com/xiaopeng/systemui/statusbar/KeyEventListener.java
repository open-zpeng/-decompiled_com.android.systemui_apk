package com.xiaopeng.systemui.statusbar;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.media.MediaPlayer2;
import android.view.KeyEvent;
import com.xiaopeng.IXPKeyListener;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.AudioController;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager;
/* loaded from: classes24.dex */
public class KeyEventListener extends IXPKeyListener.Stub {
    private static final String TAG = "StatusKeyEventListener";
    private Context mContext;
    private Handler mHandler;
    private InputManager mInputManager = InputManager.getInstance();

    public KeyEventListener(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public void register() {
        InputManager inputManager = this.mInputManager;
        if (inputManager != null) {
            inputManager.registerListener(this, TAG, true);
        }
    }

    public int notify(final KeyEvent event, String extra) throws RemoteException {
        int keycode = event.getKeyCode();
        int action = event.getAction();
        Logger.d(TAG, "notify keycode=" + keycode + " action=" + action);
        if (keycode == 164) {
            this.mHandler.post(new Runnable() { // from class: com.xiaopeng.systemui.statusbar.KeyEventListener.1
                @Override // java.lang.Runnable
                public void run() {
                    AudioController.getInstance(KeyEventListener.this.mContext).handleVolumeKeyEvent(event);
                }
            });
            return 0;
        } else if (keycode != 1016) {
            switch (keycode) {
                case MediaPlayer2.MEDIA_INFO_BAD_INTERLEAVING /* 800 */:
                case MediaPlayer2.MEDIA_INFO_NOT_SEEKABLE /* 801 */:
                case MediaPlayer2.MEDIA_INFO_METADATA_UPDATE /* 802 */:
                    this.mHandler.post(new Runnable() { // from class: com.xiaopeng.systemui.statusbar.KeyEventListener.2
                        @Override // java.lang.Runnable
                        public void run() {
                            CarController.getInstance(KeyEventListener.this.mContext).handleHvacKeyEvent(event);
                        }
                    });
                    return 0;
                default:
                    return 0;
            }
        } else {
            this.mHandler.post(new Runnable() { // from class: com.xiaopeng.systemui.statusbar.KeyEventListener.3
                @Override // java.lang.Runnable
                public void run() {
                    QuickMenuPresenterManager.getInstance().autoHideQuickMenu(0);
                }
            });
            return 0;
        }
    }
}
