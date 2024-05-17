package com.android.keyguard.clock;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes19.dex */
public final class ClockOptionsProvider extends ContentProvider {
    private static final String AUTHORITY = "com.android.keyguard.clock";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PREVIEW = "preview";
    private static final String COLUMN_THUMBNAIL = "thumbnail";
    private static final String COLUMN_TITLE = "title";
    private static final String CONTENT_SCHEME = "content";
    private static final String KEY_LIST_OPTIONS = "/list_options";
    private static final String KEY_PREVIEW = "preview";
    private static final String KEY_THUMBNAIL = "thumbnail";
    private static final String MIME_TYPE_PNG = "image/png";
    private static final String TAG = "ClockOptionsProvider";
    private final Supplier<List<ClockInfo>> mClocksSupplier;

    public ClockOptionsProvider() {
        this(new Supplier() { // from class: com.android.keyguard.clock.-$$Lambda$ClockOptionsProvider$VCF-r6VBqrtOSuPKYuOzo6kUuyg
            @Override // java.util.function.Supplier
            public final Object get() {
                List clockInfos;
                clockInfos = ((ClockManager) Dependency.get(ClockManager.class)).getClockInfos();
                return clockInfos;
            }
        });
    }

    @VisibleForTesting
    ClockOptionsProvider(Supplier<List<ClockInfo>> clocksSupplier) {
        this.mClocksSupplier = clocksSupplier;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments.size() > 0) {
            if ("preview".equals(segments.get(0)) || "thumbnail".equals(segments.get(0))) {
                return MIME_TYPE_PNG;
            }
            return "vnd.android.cursor.dir/clock_faces";
        }
        return "vnd.android.cursor.dir/clock_faces";
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (!KEY_LIST_OPTIONS.equals(uri.getPath())) {
            return null;
        }
        MatrixCursor cursor = new MatrixCursor(new String[]{"name", "title", "id", "thumbnail", "preview"});
        List<ClockInfo> clocks = this.mClocksSupplier.get();
        for (int i = 0; i < clocks.size(); i++) {
            ClockInfo clock = clocks.get(i);
            cursor.newRow().add("name", clock.getName()).add("title", clock.getTitle()).add("id", clock.getId()).add("thumbnail", createThumbnailUri(clock)).add("preview", createPreviewUri(clock));
        }
        return cursor;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues initialValues) {
        return null;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        List<String> segments = uri.getPathSegments();
        if (segments.size() != 2 || (!"preview".equals(segments.get(0)) && !"thumbnail".equals(segments.get(0)))) {
            throw new FileNotFoundException("Invalid preview url");
        }
        String id = segments.get(1);
        if (TextUtils.isEmpty(id)) {
            throw new FileNotFoundException("Invalid preview url, missing id");
        }
        ClockInfo clock = null;
        List<ClockInfo> clocks = this.mClocksSupplier.get();
        int i = 0;
        while (true) {
            if (i >= clocks.size()) {
                break;
            } else if (!id.equals(clocks.get(i).getId())) {
                i++;
            } else {
                ClockInfo clock2 = clocks.get(i);
                clock = clock2;
                break;
            }
        }
        if (clock == null) {
            throw new FileNotFoundException("Invalid preview url, id not found");
        }
        return openPipeHelper(uri, MIME_TYPE_PNG, null, "preview".equals(segments.get(0)) ? clock.getPreview() : clock.getThumbnail(), new MyWriter());
    }

    private Uri createThumbnailUri(ClockInfo clock) {
        return new Uri.Builder().scheme("content").authority(AUTHORITY).appendPath("thumbnail").appendPath(clock.getId()).build();
    }

    private Uri createPreviewUri(ClockInfo clock) {
        return new Uri.Builder().scheme("content").authority(AUTHORITY).appendPath("preview").appendPath(clock.getId()).build();
    }

    /* loaded from: classes19.dex */
    private static class MyWriter implements ContentProvider.PipeDataWriter<Bitmap> {
        private MyWriter() {
        }

        @Override // android.content.ContentProvider.PipeDataWriter
        public void writeDataToPipe(ParcelFileDescriptor output, Uri uri, String mimeType, Bundle opts, Bitmap bitmap) {
            try {
                ParcelFileDescriptor.AutoCloseOutputStream os = new ParcelFileDescriptor.AutoCloseOutputStream(output);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.close();
            } catch (Exception e) {
                Log.w(ClockOptionsProvider.TAG, "fail to write to pipe", e);
            }
        }
    }
}
