package com.android.systemui.qs.tiles;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes21.dex */
public class IntentTile extends QSTileImpl<QSTile.State> {
    public static final String PREFIX = "intent(";
    private int mCurrentUserId;
    private String mIntentPackage;
    private Intent mLastIntent;
    private PendingIntent mOnClick;
    private String mOnClickUri;
    private PendingIntent mOnLongClick;
    private String mOnLongClickUri;
    private final BroadcastReceiver mReceiver;

    private IntentTile(QSHost host, String action) {
        super(host);
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.systemui.qs.tiles.IntentTile.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                IntentTile.this.refreshState(intent);
            }
        };
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter(action));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleDestroy() {
        super.handleDestroy();
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    public static IntentTile create(QSHost host, String spec) {
        if (spec == null || !spec.startsWith(PREFIX) || !spec.endsWith(NavigationBarInflaterView.KEY_CODE_END)) {
            throw new IllegalArgumentException("Bad intent tile spec: " + spec);
        }
        String action = spec.substring(PREFIX.length(), spec.length() - 1);
        if (action.isEmpty()) {
            throw new IllegalArgumentException("Empty intent tile spec action");
        }
        return new IntentTile(host, action);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleSetListening(boolean listening) {
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public QSTile.State newTileState() {
        return new QSTile.State();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUserSwitch(int newUserId) {
        super.handleUserSwitch(newUserId);
        this.mCurrentUserId = newUserId;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleClick() {
        sendIntent("click", this.mOnClick, this.mOnClickUri);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public Intent getLongClickIntent() {
        return null;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleLongClick() {
        sendIntent("long-click", this.mOnLongClick, this.mOnLongClickUri);
    }

    private void sendIntent(String type, PendingIntent pi, String uri) {
        try {
            if (pi != null) {
                if (pi.isActivity()) {
                    ((ActivityStarter) Dependency.get(ActivityStarter.class)).postStartActivityDismissingKeyguard(pi);
                } else {
                    pi.send();
                }
            } else if (uri != null) {
                Intent intent = Intent.parseUri(uri, 1);
                this.mContext.sendBroadcastAsUser(intent, new UserHandle(this.mCurrentUserId));
            }
        } catch (Throwable t) {
            String str = this.TAG;
            Log.w(str, "Error sending " + type + " intent", t);
        }
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return getState().label;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleUpdateState(QSTile.State state, Object arg) {
        Intent intent = (Intent) arg;
        if (intent == null) {
            if (this.mLastIntent == null) {
                return;
            }
            intent = this.mLastIntent;
        }
        this.mLastIntent = intent;
        state.contentDescription = intent.getStringExtra("contentDescription");
        state.label = intent.getStringExtra("label");
        state.icon = null;
        byte[] iconBitmap = intent.getByteArrayExtra("iconBitmap");
        if (iconBitmap != null) {
            try {
                state.icon = new BytesIcon(iconBitmap);
            } catch (Throwable t) {
                String str = this.TAG;
                Log.w(str, "Error loading icon bitmap, length " + iconBitmap.length, t);
            }
        } else {
            int iconId = intent.getIntExtra("iconId", 0);
            if (iconId != 0) {
                String iconPackage = intent.getStringExtra("iconPackage");
                if (!TextUtils.isEmpty(iconPackage)) {
                    state.icon = new PackageDrawableIcon(iconPackage, iconId);
                } else {
                    state.icon = QSTileImpl.ResourceIcon.get(iconId);
                }
            }
        }
        this.mOnClick = (PendingIntent) intent.getParcelableExtra("onClick");
        this.mOnClickUri = intent.getStringExtra("onClickUri");
        this.mOnLongClick = (PendingIntent) intent.getParcelableExtra("onLongClick");
        this.mOnLongClickUri = intent.getStringExtra("onLongClickUri");
        this.mIntentPackage = intent.getStringExtra("package");
        String str2 = this.mIntentPackage;
        if (str2 == null) {
            str2 = "";
        }
        this.mIntentPackage = str2;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return 121;
    }

    /* loaded from: classes21.dex */
    private static class BytesIcon extends QSTile.Icon {
        private final byte[] mBytes;

        public BytesIcon(byte[] bytes) {
            this.mBytes = bytes;
        }

        @Override // com.android.systemui.plugins.qs.QSTile.Icon
        public Drawable getDrawable(Context context) {
            byte[] bArr = this.mBytes;
            Bitmap b = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
            return new BitmapDrawable(context.getResources(), b);
        }

        public boolean equals(Object o) {
            return (o instanceof BytesIcon) && Arrays.equals(((BytesIcon) o).mBytes, this.mBytes);
        }

        public String toString() {
            return String.format("BytesIcon[len=%s]", Integer.valueOf(this.mBytes.length));
        }
    }

    /* loaded from: classes21.dex */
    private class PackageDrawableIcon extends QSTile.Icon {
        private final String mPackage;
        private final int mResId;

        public PackageDrawableIcon(String pkg, int resId) {
            this.mPackage = pkg;
            this.mResId = resId;
        }

        public boolean equals(Object o) {
            if (o instanceof PackageDrawableIcon) {
                PackageDrawableIcon other = (PackageDrawableIcon) o;
                return Objects.equals(other.mPackage, this.mPackage) && other.mResId == this.mResId;
            }
            return false;
        }

        @Override // com.android.systemui.plugins.qs.QSTile.Icon
        public Drawable getDrawable(Context context) {
            try {
                return context.createPackageContext(this.mPackage, 0).getDrawable(this.mResId);
            } catch (Throwable t) {
                String str = IntentTile.this.TAG;
                Log.w(str, "Error loading package drawable pkg=" + this.mPackage + " id=" + this.mResId, t);
                return null;
            }
        }

        public String toString() {
            return String.format("PackageDrawableIcon[pkg=%s,id=0x%08x]", this.mPackage, Integer.valueOf(this.mResId));
        }
    }
}
