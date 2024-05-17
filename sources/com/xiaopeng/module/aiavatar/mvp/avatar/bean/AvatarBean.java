package com.xiaopeng.module.aiavatar.mvp.avatar.bean;

import com.google.gson.annotations.SerializedName;
import com.xiaopeng.lib.apirouter.ClientConstants;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import java.util.List;
/* loaded from: classes23.dex */
public class AvatarBean {
    @SerializedName("actionList")
    public List<AvatarAction> actionList;
    @SerializedName("envBgTexturePath")
    public String envBgTexturePath;
    @SerializedName("g3dbModelPath")
    public String g3dbModelPath;
    @SerializedName("glassesTextureBean")
    public GlassesTexture glassesTextureBean;
    @SerializedName("isSpread")
    public boolean isSpread;
    @SerializedName("isZoom")
    public boolean isZoom;
    @SerializedName("left")
    public String left;
    @SerializedName("leftTop")
    public String leftTop;
    @SerializedName("lightColor")
    public LightColor lightColor;
    @SerializedName("modelTexturePath")
    public String modelTexturePath;
    @SerializedName(VuiConstants.SCENE_PACKAGE_NAME)
    public String packageName;
    @SerializedName("right")
    public String right;
    public Skin skin;
    @SerializedName("warnLevel")
    public int warnLevel;
    @SerializedName("eventId")
    public int eventId = -1;
    public int xPositon = -10;

    /* loaded from: classes23.dex */
    public static class GlassesTexture {
        @SerializedName("loopCount")
        public int loopCount = -1;
        @SerializedName(ClientConstants.ALIAS.PATH)
        public String path;
    }

    /* loaded from: classes23.dex */
    public static class LightColor {
        public float alpha = 1.0f;
        public float blue;
        public float green;
        public float red;
    }

    /* loaded from: classes23.dex */
    public static class Skin {
        public String FullDay;
        public String halfDay;
    }

    /* loaded from: classes23.dex */
    public class AvatarAction {
        @SerializedName("actionId")
        private String actionId;
        @SerializedName("loopTimes")
        private int loopTimes;

        public AvatarAction() {
        }

        public String getActionId() {
            return this.actionId;
        }

        public void setActionId(String actionId) {
            this.actionId = actionId;
        }

        public int getLoopTimes() {
            return this.loopTimes;
        }

        public void setLoopTimes(int loopTimes) {
            this.loopTimes = loopTimes;
        }
    }
}
