package com.xiaopeng.systemui.qs;
/* loaded from: classes24.dex */
public class TileState {
    public int clickable;
    public int configurable;
    public int groupId;
    public int height;
    public boolean ifAnimatedIcon;
    public boolean ifTxtUpdate;
    public String interactType;
    public int jumpable;
    public String key;
    public int quickClickSafeTime;
    public String resBg;
    public String resImg;
    public String resTxt;
    public int width;
    public int x;
    public int y;

    public TileState(String key, int groupId, String interactType, int clickable, int jumpable, int width, int height, int x, int y, int quickClickSafeTime, int configurable, String resImg, String resTxt, String resBg, boolean ifTxtUpdate, boolean ifAnimatedIcon) {
        this.key = key;
        this.groupId = groupId;
        this.interactType = interactType;
        this.clickable = clickable;
        this.jumpable = jumpable;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.quickClickSafeTime = quickClickSafeTime;
        this.configurable = configurable;
        this.resImg = resImg;
        this.resTxt = resTxt;
        this.resBg = resBg;
        this.ifTxtUpdate = ifTxtUpdate;
        this.ifAnimatedIcon = ifAnimatedIcon;
    }
}
