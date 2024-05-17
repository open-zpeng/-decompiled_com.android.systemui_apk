package com.xiaopeng.module.aiavatar.event;

import android.text.TextUtils;
import com.xiaopeng.module.aiavatar.mvp.avatar.bean.AvatarBean;
import java.util.ArrayList;
import java.util.Random;
/* loaded from: classes23.dex */
public class AvatarEvents {

    /* loaded from: classes23.dex */
    public static class Default1Event extends DefaultEvent {
        public Default1Event(AvatarBean avatarBean) {
            super(avatarBean);
            setActionId("Anima_17_sikao");
            this.tts = new String[]{"嗨，试着戳一下我，打个招呼吧", "试试说：你好小P，打开冥想模式", "试试说：你好小P，我有点热", "试试说：你好小P，附近的充电站", "试试说：你好小P，播放热门歌曲"};
        }
    }

    /* loaded from: classes23.dex */
    public static class Default2Event extends DefaultEvent {
        public Default2Event(AvatarBean avatarBean) {
            super(avatarBean);
            setActionId("Anima_17_sikao");
        }
    }

    /* loaded from: classes23.dex */
    public static class Default3Event extends DefaultEvent {
        public Default3Event(AvatarBean avatarBean) {
            super(avatarBean);
            setActionId("Anima_18_tiaowu");
        }
    }

    /* loaded from: classes23.dex */
    public static class Default4Event extends DefaultEvent {
        public Default4Event(AvatarBean avatarBean) {
            super(avatarBean);
            setActionId("Anima_20_shuaitou");
        }
    }

    /* loaded from: classes23.dex */
    public static class Default5Event extends DefaultEvent {
        public Default5Event(AvatarBean avatarBean) {
            super(avatarBean);
            setActionId("Anima_22_jiancha");
        }
    }

    /* loaded from: classes23.dex */
    public static class Default9Event extends DefaultEvent {
        public Default9Event(AvatarBean avatarBean) {
            super(avatarBean);
            setActionId("Anima_23_4_erji");
        }
    }

    /* loaded from: classes23.dex */
    public static class HeadEvent extends AvatarEvent {
        public HeadEvent(AvatarBean avatarBean) {
            super(avatarBean);
            setActionId("Anima_25_beimotou");
            this.tts = new String[]{"哎呀，再敲头会变笨的。", "试试说“你好，小P”，让我陪你聊聊天。", "我看到最美的风景，是小鹏汽车前窗的落日。", "给我说“你好小P”，我就可以为你服务哦。", "哎呀，我好喜欢你哦~"};
        }
    }

    /* loaded from: classes23.dex */
    public static class EarsEvent extends AvatarEvent {
        public EarsEvent(AvatarBean avatarBean) {
            super(avatarBean);
            setActionId("Anima_27_beimoerduo");
            this.tts = new String[]{"跟我说“你好，小P”就可以唤醒我啦~", "让我猜猜你想跟我说点什么呢？", "试试说“你好小P”，我的听力可不错哦。", "给我说“你好小P”，我就可以为你服务哦。"};
        }
    }

    /* loaded from: classes23.dex */
    public static class BodyEvent extends AvatarEvent {
        public BodyEvent(AvatarBean avatarBean) {
            super(avatarBean);
            setActionId("Anima_24_beichuoduzi");
            this.tts = new String[]{"哎呀，我好喜欢你哦~", "好痒呀，试试说“你好小P，所有技能”就能知道我的技能啦。", "开车带我去兜兜风吧~", "哎呀，你可真是个小淘气鬼。", "下一次出发，沿途的风景会是什么样呢？", "你好呀，看来今天心情不错哦。", "跟我说“你好，小P”就可以唤醒我啦~", "我有很多小技能，可以到右边页面看看哦。"};
        }
    }

    /* loaded from: classes23.dex */
    public static class HandEvent extends AvatarEvent {
        public HandEvent(AvatarBean avatarBean) {
            super(avatarBean);
            setActionId("Anima_29_beichuoshou");
            this.tts = new String[]{"开车带我去兜兜风吧~", "握个手，我们就是好鹏友。", "无论向左走向右走，都有我的陪伴。", "哎呀，你可真是个小淘气鬼。"};
        }
    }

    /* loaded from: classes23.dex */
    public static class LegEvent extends AvatarEvent {
        public LegEvent(AvatarBean avatarBean) {
            super(avatarBean);
            setActionId("Anima_28_beichuoxigai");
            this.tts = new String[]{"你在找我吗？说“你好，小P”就可以唤醒我哦。", "你是想带我出去玩吗？现在就出发吧", "开车带我去兜兜风吧~", "下一次出发，沿途的风景会是什么样呢？"};
        }
    }

    /* loaded from: classes23.dex */
    public static class DefaultEvent extends AvatarEvent {
        public DefaultEvent(AvatarBean avatarBean) {
            super(avatarBean);
        }
    }

    /* loaded from: classes23.dex */
    public static class AvatarEvent extends AvatarBean {
        private String actionId;
        public String[] tts;

        public AvatarEvent(AvatarBean avatarBean) {
            if (avatarBean != null) {
                this.g3dbModelPath = avatarBean.g3dbModelPath;
                this.modelTexturePath = avatarBean.modelTexturePath;
                this.glassesTextureBean = avatarBean.glassesTextureBean;
                this.envBgTexturePath = avatarBean.envBgTexturePath;
                this.actionList = avatarBean.actionList;
                this.eventId = avatarBean.eventId;
                this.packageName = avatarBean.packageName;
                this.lightColor = avatarBean.lightColor;
                this.isZoom = avatarBean.isZoom;
                this.xPositon = avatarBean.xPositon;
                if (this.glassesTextureBean == null) {
                    this.glassesTextureBean = new AvatarBean.GlassesTexture();
                }
                this.glassesTextureBean.path = "avatar/idle/default.webp";
                this.glassesTextureBean.loopCount = 1;
            }
        }

        public void setActionId(String actionId) {
            this.actionId = actionId;
            if (!TextUtils.isEmpty(this.actionId)) {
                this.actionList = new ArrayList();
                AvatarBean.AvatarAction avatarAction = new AvatarBean.AvatarAction();
                avatarAction.setActionId(this.actionId);
                avatarAction.setLoopTimes(1);
                this.actionList.add(avatarAction);
            }
        }

        public String getTts() {
            String[] strArr = this.tts;
            if (strArr != null && strArr.length > 0) {
                int index = new Random().nextInt(this.tts.length);
                return this.tts[index];
            }
            return null;
        }
    }
}
