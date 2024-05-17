package com.xiaopeng.systemui.speech.component.echo;

import android.content.Context;
import com.xiaopeng.systemui.speech.component.Component;
import com.xiaopeng.systemui.speech.model.SpeechConfig;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes24.dex */
public class EchoComponent extends Component<IEchoListener> {
    private final ArrayList<EchoAreaWidget> mEchoList;

    public EchoComponent(Context context, IEchoListener iComponentListener) {
        super(context, iComponentListener);
        this.mEchoList = new ArrayList<>();
    }

    @Override // com.xiaopeng.systemui.speech.component.Component
    public void start() {
        ArrayList<Integer> list = SpeechConfig.get().getSoundAreasForEchoList();
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            Integer area = it.next();
            this.mEchoList.add(new EchoAreaWidget(this.mContext, area.intValue()));
        }
        Iterator<EchoAreaWidget> it2 = this.mEchoList.iterator();
        while (it2.hasNext()) {
            EchoAreaWidget widget = it2.next();
            widget.setListener(getComponentListener());
        }
    }
}
