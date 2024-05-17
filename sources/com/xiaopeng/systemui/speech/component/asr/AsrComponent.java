package com.xiaopeng.systemui.speech.component.asr;

import android.content.Context;
import com.xiaopeng.systemui.speech.component.Component;
import com.xiaopeng.systemui.speech.model.SpeechConfig;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes24.dex */
public class AsrComponent extends Component<IAsrListener> {
    private static final String TAG = "Sp-AsrComponent";
    private final ArrayList<AsrAreaWidget> mAsrList;

    public AsrComponent(Context context, IAsrListener iAsrListener) {
        super(context, iAsrListener);
        this.mAsrList = new ArrayList<>();
    }

    @Override // com.xiaopeng.systemui.speech.component.Component
    public void start() {
        ArrayList<Integer> list = SpeechConfig.get().getSoundAreasForAsr();
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            Integer area = it.next();
            this.mAsrList.add(new AsrAreaWidget(this.mContext, area.intValue()));
        }
        Iterator<AsrAreaWidget> it2 = this.mAsrList.iterator();
        while (it2.hasNext()) {
            AsrAreaWidget widget = it2.next();
            widget.setListener(getComponentListener());
        }
    }
}
