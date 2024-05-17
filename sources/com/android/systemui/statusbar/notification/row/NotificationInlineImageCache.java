package com.android.systemui.statusbar.notification.row;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.android.systemui.statusbar.notification.row.NotificationInlineImageResolver;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
/* loaded from: classes21.dex */
public class NotificationInlineImageCache implements NotificationInlineImageResolver.ImageCache {
    private static final String TAG = NotificationInlineImageCache.class.getSimpleName();
    private final ConcurrentHashMap<Uri, PreloadImageTask> mCache = new ConcurrentHashMap<>();
    private NotificationInlineImageResolver mResolver;

    @Override // com.android.systemui.statusbar.notification.row.NotificationInlineImageResolver.ImageCache
    public void setImageResolver(NotificationInlineImageResolver resolver) {
        this.mResolver = resolver;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationInlineImageResolver.ImageCache
    public boolean hasEntry(Uri uri) {
        return this.mCache.containsKey(uri);
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationInlineImageResolver.ImageCache
    public void preload(Uri uri) {
        PreloadImageTask newTask = new PreloadImageTask(this.mResolver);
        newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uri);
        this.mCache.put(uri, newTask);
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationInlineImageResolver.ImageCache
    public Drawable get(Uri uri) {
        try {
            Drawable result = this.mCache.get(uri).get();
            return result;
        } catch (InterruptedException | ExecutionException e) {
            String str = TAG;
            Log.d(str, "get: Failed get image from " + uri);
            return null;
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationInlineImageResolver.ImageCache
    public void purge() {
        final Set<Uri> wantedSet = this.mResolver.getWantedUriSet();
        this.mCache.entrySet().removeIf(new Predicate() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationInlineImageCache$W1d4bA0jU1G2gSKuFNWjVLFgYyA
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return NotificationInlineImageCache.lambda$purge$0(wantedSet, (Map.Entry) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$purge$0(Set wantedSet, Map.Entry entry) {
        return !wantedSet.contains(entry.getKey());
    }

    /* loaded from: classes21.dex */
    private static class PreloadImageTask extends AsyncTask<Uri, Void, Drawable> {
        private final NotificationInlineImageResolver mResolver;

        PreloadImageTask(NotificationInlineImageResolver resolver) {
            this.mResolver = resolver;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Drawable doInBackground(Uri... uris) {
            Uri target = uris[0];
            try {
                Drawable drawable = this.mResolver.resolveImage(target);
                return drawable;
            } catch (IOException | SecurityException ex) {
                String str = NotificationInlineImageCache.TAG;
                Log.d(str, "PreloadImageTask: Resolve failed from " + target, ex);
                return null;
            }
        }
    }
}
