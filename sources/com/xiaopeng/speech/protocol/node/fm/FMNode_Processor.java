package com.xiaopeng.speech.protocol.node.fm;

import com.xiaopeng.speech.annotation.ICommandProcessor;
/* loaded from: classes23.dex */
public class FMNode_Processor implements ICommandProcessor {
    private FMNode mTarget;

    public FMNode_Processor(FMNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1616140812:
                if (event.equals("command://fm.local.off")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -627784393:
                if (event.equals("command://fm.network.off")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -297345769:
                if (event.equals("command://fm.network.on")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -190680902:
                if (event.equals("command://fm.local.on")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 616790465:
                if (event.equals("command://fm.play.channel")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1124241696:
                if (event.equals("command://fm.play.collection")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 2103491480:
                if (event.equals("native://fm.play.channelname")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                this.mTarget.onFmLocalOn(event, data);
                return;
            case 1:
                this.mTarget.onFmLocalOff(event, data);
                return;
            case 2:
                this.mTarget.onFmNetworkOn(event, data);
                return;
            case 3:
                this.mTarget.onFmNetworkOff(event, data);
                return;
            case 4:
                this.mTarget.onFmPlayChannel(event, data);
                return;
            case 5:
                this.mTarget.onFmPlayChannelName(event, data);
                return;
            case 6:
                this.mTarget.onPlayCollectFM(event, data);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{"command://fm.local.on", "command://fm.local.off", "command://fm.network.on", "command://fm.network.off", "command://fm.play.channel", "native://fm.play.channelname", "command://fm.play.collection"};
    }
}
