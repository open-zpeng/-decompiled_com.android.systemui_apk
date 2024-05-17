package com.android.systemui.assist;

import android.content.Context;
import com.android.systemui.assist.AssistHandleBehaviorController;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
final class AssistHandleOffBehavior implements AssistHandleBehaviorController.BehaviorController {
    @Override // com.android.systemui.assist.AssistHandleBehaviorController.BehaviorController
    public void onModeActivated(Context context, AssistHandleCallbacks callbacks) {
        callbacks.hide();
    }
}
