package com.android.keyguard.clock;

import android.graphics.Bitmap;
import java.util.function.Supplier;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes19.dex */
public final class ClockInfo {
    private final String mId;
    private final String mName;
    private final Supplier<Bitmap> mPreview;
    private final Supplier<Bitmap> mThumbnail;
    private final String mTitle;

    private ClockInfo(String name, String title, String id, Supplier<Bitmap> thumbnail, Supplier<Bitmap> preview) {
        this.mName = name;
        this.mTitle = title;
        this.mId = id;
        this.mThumbnail = thumbnail;
        this.mPreview = preview;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getName() {
        return this.mName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getTitle() {
        return this.mTitle;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getId() {
        return this.mId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bitmap getThumbnail() {
        return this.mThumbnail.get();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bitmap getPreview() {
        return this.mPreview.get();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Builder builder() {
        return new Builder();
    }

    /* loaded from: classes19.dex */
    static class Builder {
        private String mId;
        private String mName;
        private Supplier<Bitmap> mPreview;
        private Supplier<Bitmap> mThumbnail;
        private String mTitle;

        Builder() {
        }

        public ClockInfo build() {
            return new ClockInfo(this.mName, this.mTitle, this.mId, this.mThumbnail, this.mPreview);
        }

        public Builder setName(String name) {
            this.mName = name;
            return this;
        }

        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder setId(String id) {
            this.mId = id;
            return this;
        }

        public Builder setThumbnail(Supplier<Bitmap> thumbnail) {
            this.mThumbnail = thumbnail;
            return this;
        }

        public Builder setPreview(Supplier<Bitmap> preview) {
            this.mPreview = preview;
            return this;
        }
    }
}
