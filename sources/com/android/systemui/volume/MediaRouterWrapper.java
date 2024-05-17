package com.android.systemui.volume;

import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;
import java.util.List;
/* loaded from: classes21.dex */
public class MediaRouterWrapper {
    private final MediaRouter mRouter;

    public MediaRouterWrapper(MediaRouter router) {
        this.mRouter = router;
    }

    public void addCallback(MediaRouteSelector selector, MediaRouter.Callback callback, int flags) {
        this.mRouter.addCallback(selector, callback, flags);
    }

    public void removeCallback(MediaRouter.Callback callback) {
        this.mRouter.removeCallback(callback);
    }

    public void unselect(int reason) {
        this.mRouter.unselect(reason);
    }

    public List<MediaRouter.RouteInfo> getRoutes() {
        return this.mRouter.getRoutes();
    }
}
