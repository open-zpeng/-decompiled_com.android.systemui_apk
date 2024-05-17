package com.xiaopeng.speech.speechwidget;
/* loaded from: classes23.dex */
public class ContentWidget extends SpeechWidget {
    public ContentWidget() {
        super("content");
    }

    public ContentWidget setTitle(String val) {
        return (ContentWidget) super.addContent(SpeechWidget.WIDGET_TITLE, val);
    }

    public ContentWidget setSubTitle(String val) {
        return (ContentWidget) super.addContent(SpeechWidget.WIDGET_SUBTITLE, val);
    }

    public ContentWidget setLabel(String val) {
        return (ContentWidget) super.addContent("label", val);
    }

    public ContentWidget setImageUrl(String val) {
        return (ContentWidget) super.addContent(SpeechWidget.WIDGET_IMAGEURL, val);
    }

    public String getLinkUrl() {
        return getContent(SpeechWidget.WIDGET_LINKURL);
    }

    public ContentWidget setLinkUrl(String val) {
        return (ContentWidget) super.addContent(SpeechWidget.WIDGET_LINKURL, val);
    }

    @Override // com.xiaopeng.speech.speechwidget.SpeechWidget
    public ContentWidget addExtra(String val, String var2) {
        return (ContentWidget) super.addExtra(val, var2);
    }

    public ContentWidget setOnClickEvent(String event) {
        return addExtra("extraEvent", event);
    }
}
