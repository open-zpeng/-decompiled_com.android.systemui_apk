package com.android.systemui.doze;

import com.android.systemui.doze.DozeMachine;
import com.android.systemui.plugins.FalsingManager;
/* loaded from: classes21.dex */
public class DozeFalsingManagerAdapter implements DozeMachine.Part {
    private final FalsingManager mFalsingManager;

    public DozeFalsingManagerAdapter(FalsingManager falsingManager) {
        this.mFalsingManager = falsingManager;
    }

    @Override // com.android.systemui.doze.DozeMachine.Part
    public void transitionTo(DozeMachine.State oldState, DozeMachine.State newState) {
        this.mFalsingManager.setShowingAod(isAodMode(newState));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.doze.DozeFalsingManagerAdapter$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$systemui$doze$DozeMachine$State = new int[DozeMachine.State.values().length];

        static {
            try {
                $SwitchMap$com$android$systemui$doze$DozeMachine$State[DozeMachine.State.DOZE_AOD.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$android$systemui$doze$DozeMachine$State[DozeMachine.State.DOZE_AOD_PAUSING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$android$systemui$doze$DozeMachine$State[DozeMachine.State.DOZE_AOD_PAUSED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private boolean isAodMode(DozeMachine.State state) {
        int i = AnonymousClass1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state.ordinal()];
        return i == 1 || i == 2 || i == 3;
    }
}
