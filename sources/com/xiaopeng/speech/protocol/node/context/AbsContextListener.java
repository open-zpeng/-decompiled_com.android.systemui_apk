package com.xiaopeng.speech.protocol.node.context;
/* loaded from: classes23.dex */
public abstract class AbsContextListener implements ContextListener {
    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onInputText(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onOutputText(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onTipsText(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onWidgetCustom(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onWidgetText(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onWidgetList(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onWidgetMedia(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onWidgetListFocus(String source, int index) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onWidgetCard(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onWidgetRecommend(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onWidgetSearch(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onExpression(String mode) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onSayWelcome(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onWidgetScroll(String source, int index) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onWidgetListSelect(String source, int index) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onWidgetCancel(String widgetId, String cancelWay) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onPageNext() {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onPagePrev() {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onPageSetLow() {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onPageTopping() {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onWidgetListCancelFocus(String source, int index) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onScript(String event, String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onScriptStatus(String event, String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onWidgetListSelect(String source, int index, String type) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onExitRecommendCard() {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onWidgetListExpend(String source, String type) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onWidgetListFold(String source, String type) {
    }

    @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
    public void onWidgetListStopCountdown() {
    }
}
