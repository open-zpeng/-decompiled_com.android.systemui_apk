package com.xiaopeng.speech.protocol.node.video;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.query.QueryVideoEvent;
/* loaded from: classes23.dex */
public class VideoNode extends SpeechNode<VideoListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_PREV)
    public void onVideoPrev(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoPrev(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_NEXT)
    public void onVideoNext(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoNext(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_SET)
    public void onVideoSet(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoSet(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_FORWARD)
    public void onVideoForward(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoForward(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_BACKWARD)
    public void onVideoBackward(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoBackward(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_SETTIME)
    public void onVideoSettime(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoSettime(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_PAUSE)
    public void onVideoPause(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoPause(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_RESUME)
    public void onVideoResume(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoResume(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_SPEED_UP)
    public void onVideoSpeedUp(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoSpeedUp(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_SPEED_DOWN)
    public void onVideoSpeedDown(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoSpeedDown(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_SPEED_SET)
    public void onVideoSpeedSet(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoSpeedSet(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_END)
    public void onVideoEnd(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoEnd(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_CLARITY_UP)
    public void onVideoClarityUP(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoClarityUP(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_CLARITY_DOWN)
    public void onVideoClarityDown(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoClarityDown(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_CLARITY_SET)
    public void onVideoClaritySet(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoClaritySet(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_COLLECT)
    public void onVideoCollect(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoCollect(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_PLAY_COLLECT_CANCEL)
    public void onVideoCollectCancel(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoCollectCancel(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_SKIP_BEGIN)
    public void onVideoSkipBegin(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoSkipBegin(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = QueryVideoEvent.VIDEO_SKIP_END)
    public void onVideoSkipEnd(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((VideoListener) obj).onVideoSkipEnd(data);
            }
        }
    }
}
