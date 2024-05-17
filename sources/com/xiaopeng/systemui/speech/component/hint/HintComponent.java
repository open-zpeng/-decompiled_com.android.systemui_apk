package com.xiaopeng.systemui.speech.component.hint;

import android.content.Context;
import com.xiaopeng.systemui.speech.component.Component;
import com.xiaopeng.systemui.speech.model.SpeechConfig;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes24.dex */
public class HintComponent extends Component<IHintListener> {
    private final ArrayList<HintAreaWidget> mHintList;

    public HintComponent(Context context, IHintListener hintListener) {
        super(context, hintListener);
        this.mHintList = new ArrayList<>();
    }

    @Override // com.xiaopeng.systemui.speech.component.Component
    public void start() {
        ArrayList<Integer> list = SpeechConfig.get().getSoundAreasForHint();
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            Integer area = it.next();
            this.mHintList.add(new HintAreaWidget(this.mContext, area.intValue()));
        }
        Iterator<HintAreaWidget> it2 = this.mHintList.iterator();
        while (it2.hasNext()) {
            HintAreaWidget widget = it2.next();
            widget.setListener(getComponentListener());
        }
    }
}
