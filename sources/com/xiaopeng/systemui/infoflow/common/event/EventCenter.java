package com.xiaopeng.systemui.infoflow.common.event;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/* loaded from: classes24.dex */
public class EventCenter {
    private static final String TAG = "EventCenter";
    private static volatile EventCenter mInstance;
    private Map<Object, Set<WeakReference<IEventListener>>> mListeners = new HashMap();

    private EventCenter() {
    }

    public static EventCenter instance() {
        if (mInstance == null) {
            synchronized (EventCenter.class) {
                if (mInstance == null) {
                    mInstance = new EventCenter();
                }
            }
        }
        return mInstance;
    }

    public void bindListener(Object event, IEventListener listener) {
        if (event == null || listener == null) {
            return;
        }
        Set<WeakReference<IEventListener>> listenerlink = this.mListeners.get(event);
        if (listenerlink == null) {
            Set<WeakReference<IEventListener>> listenerlink2 = new HashSet<>();
            listenerlink2.add(new WeakReference<>(listener));
            this.mListeners.put(event, listenerlink2);
            return;
        }
        boolean exist = false;
        Iterator<WeakReference<IEventListener>> it = listenerlink.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            WeakReference<IEventListener> obj = it.next();
            IEventListener temp = obj.get();
            if (listener.equals(temp)) {
                exist = true;
                break;
            }
        }
        if (!exist) {
            listenerlink.add(new WeakReference<>(listener));
        }
    }

    public void unbindListener(Object event, IEventListener listener) {
        Set<WeakReference<IEventListener>> listenerlink;
        if (event != null && listener != null && (listenerlink = this.mListeners.get(event)) != null) {
            WeakReference<IEventListener> exist = null;
            Iterator<WeakReference<IEventListener>> it = listenerlink.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                WeakReference<IEventListener> obj = it.next();
                IEventListener temp = obj.get();
                if (listener.equals(temp)) {
                    exist = obj;
                    break;
                }
            }
            if (exist != null) {
                listenerlink.remove(exist);
            }
        }
    }

    public void raiseEvent(EventPackage eventPackage) {
        Set<WeakReference<IEventListener>> listenerLink;
        if (eventPackage != null && eventPackage.event != null && (listenerLink = this.mListeners.get(eventPackage.event)) != null) {
            WeakReference<IEventListener>[] list = new WeakReference[listenerLink.size()];
            listenerLink.toArray(list);
            for (WeakReference<IEventListener> li : list) {
                li.get().onEvent(eventPackage);
            }
        }
    }
}
