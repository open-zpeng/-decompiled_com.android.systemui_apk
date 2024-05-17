package com.android.systemui.shared.system;

import android.graphics.Rect;
import com.android.systemui.shared.recents.model.ThumbnailData;
/* loaded from: classes21.dex */
public interface RecentsAnimationListener {
    void onAnimationCanceled(ThumbnailData thumbnailData);

    void onAnimationStart(RecentsAnimationControllerCompat recentsAnimationControllerCompat, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, Rect rect, Rect rect2);
}
