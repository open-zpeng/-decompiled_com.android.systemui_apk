package com.android.systemui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.SliceBroadcastRelay;
import java.util.Iterator;
/* loaded from: classes21.dex */
public class SliceBroadcastRelayHandler extends SystemUI {
    private static final boolean DEBUG = false;
    private static final String TAG = "SliceBroadcastRelay";
    private final ArrayMap<Uri, BroadcastRelay> mRelays = new ArrayMap<>();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.systemui.SliceBroadcastRelayHandler.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            SliceBroadcastRelayHandler.this.handleIntent(intent);
        }
    };

    @Override // com.android.systemui.SystemUI
    public void start() {
        IntentFilter filter = new IntentFilter(SliceBroadcastRelay.ACTION_REGISTER);
        filter.addAction(SliceBroadcastRelay.ACTION_UNREGISTER);
        this.mContext.registerReceiver(this.mReceiver, filter);
    }

    @VisibleForTesting
    void handleIntent(Intent intent) {
        if (SliceBroadcastRelay.ACTION_REGISTER.equals(intent.getAction())) {
            Uri uri = (Uri) intent.getParcelableExtra(SliceBroadcastRelay.EXTRA_URI);
            ComponentName receiverClass = (ComponentName) intent.getParcelableExtra(SliceBroadcastRelay.EXTRA_RECEIVER);
            IntentFilter filter = (IntentFilter) intent.getParcelableExtra(SliceBroadcastRelay.EXTRA_FILTER);
            getOrCreateRelay(uri).register(this.mContext, receiverClass, filter);
        } else if (SliceBroadcastRelay.ACTION_UNREGISTER.equals(intent.getAction())) {
            Uri uri2 = (Uri) intent.getParcelableExtra(SliceBroadcastRelay.EXTRA_URI);
            BroadcastRelay relay = getAndRemoveRelay(uri2);
            if (relay != null) {
                relay.unregister(this.mContext);
            }
        }
    }

    private BroadcastRelay getOrCreateRelay(Uri uri) {
        BroadcastRelay ret = this.mRelays.get(uri);
        if (ret == null) {
            BroadcastRelay ret2 = new BroadcastRelay(uri);
            this.mRelays.put(uri, ret2);
            return ret2;
        }
        return ret;
    }

    private BroadcastRelay getAndRemoveRelay(Uri uri) {
        return this.mRelays.remove(uri);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class BroadcastRelay extends BroadcastReceiver {
        private final ArraySet<ComponentName> mReceivers = new ArraySet<>();
        private final Uri mUri;
        private final UserHandle mUserId;

        public BroadcastRelay(Uri uri) {
            this.mUserId = new UserHandle(ContentProvider.getUserIdFromUri(uri));
            this.mUri = uri;
        }

        public void register(Context context, ComponentName receiver, IntentFilter filter) {
            this.mReceivers.add(receiver);
            context.registerReceiver(this, filter);
        }

        public void unregister(Context context) {
            context.unregisterReceiver(this);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            intent.addFlags(268435456);
            Iterator<ComponentName> it = this.mReceivers.iterator();
            while (it.hasNext()) {
                ComponentName receiver = it.next();
                intent.setComponent(receiver);
                intent.putExtra(SliceBroadcastRelay.EXTRA_URI, this.mUri.toString());
                context.sendBroadcastAsUser(intent, this.mUserId);
            }
        }
    }
}
