package com.android.systemui.statusbar.notification.row;

import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import com.android.internal.widget.ImageResolver;
import com.android.internal.widget.LocalImageResolver;
import com.android.internal.widget.MessagingMessage;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class NotificationInlineImageResolver implements ImageResolver {
    private static final String TAG = NotificationInlineImageResolver.class.getSimpleName();
    private final Context mContext;
    private final ImageCache mImageCache;
    private Set<Uri> mWantedUriSet;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public interface ImageCache {
        Drawable get(Uri uri);

        boolean hasEntry(Uri uri);

        void preload(Uri uri);

        void purge();

        void setImageResolver(NotificationInlineImageResolver notificationInlineImageResolver);
    }

    public NotificationInlineImageResolver(Context context, ImageCache imageCache) {
        this.mContext = context.getApplicationContext();
        this.mImageCache = imageCache;
        ImageCache imageCache2 = this.mImageCache;
        if (imageCache2 != null) {
            imageCache2.setImageResolver(this);
        }
    }

    public boolean hasCache() {
        return (this.mImageCache == null || ActivityManager.isLowRamDeviceStatic()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Drawable resolveImage(Uri uri) throws IOException {
        return LocalImageResolver.resolveImage(uri, this.mContext);
    }

    public Drawable loadImage(Uri uri) {
        try {
            Drawable result = hasCache() ? this.mImageCache.get(uri) : resolveImage(uri);
            return result;
        } catch (IOException | SecurityException ex) {
            String str = TAG;
            Log.d(str, "loadImage: Can't load image from " + uri, ex);
            return null;
        }
    }

    public void preloadImages(Notification notification) {
        if (!hasCache()) {
            return;
        }
        retrieveWantedUriSet(notification);
        Set<Uri> wantedSet = getWantedUriSet();
        wantedSet.forEach(new Consumer() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationInlineImageResolver$9tt2CqLsWBYt2coRCrkS9VmF2EU
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NotificationInlineImageResolver.this.lambda$preloadImages$0$NotificationInlineImageResolver((Uri) obj);
            }
        });
    }

    public /* synthetic */ void lambda$preloadImages$0$NotificationInlineImageResolver(Uri uri) {
        if (!this.mImageCache.hasEntry(uri)) {
            this.mImageCache.preload(uri);
        }
    }

    public void purgeCache() {
        if (!hasCache()) {
            return;
        }
        this.mImageCache.purge();
    }

    private void retrieveWantedUriSet(Notification notification) {
        List<Notification.MessagingStyle.Message> messageList;
        Set<Uri> result = new HashSet<>();
        Bundle extras = notification.extras;
        if (extras == null) {
            return;
        }
        Parcelable[] messages = extras.getParcelableArray("android.messages");
        List<Notification.MessagingStyle.Message> historicList = null;
        if (messages == null) {
            messageList = null;
        } else {
            messageList = Notification.MessagingStyle.Message.getMessagesFromBundleArray(messages);
        }
        if (messageList != null) {
            for (Notification.MessagingStyle.Message message : messageList) {
                if (MessagingMessage.hasImage(message)) {
                    result.add(message.getDataUri());
                }
            }
        }
        Parcelable[] historicMessages = extras.getParcelableArray("android.messages.historic");
        if (historicMessages != null) {
            historicList = Notification.MessagingStyle.Message.getMessagesFromBundleArray(historicMessages);
        }
        if (historicList != null) {
            for (Notification.MessagingStyle.Message historic : historicList) {
                if (MessagingMessage.hasImage(historic)) {
                    result.add(historic.getDataUri());
                }
            }
        }
        this.mWantedUriSet = result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Set<Uri> getWantedUriSet() {
        return this.mWantedUriSet;
    }
}
