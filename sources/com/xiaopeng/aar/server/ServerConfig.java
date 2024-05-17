package com.xiaopeng.aar.server;
/* loaded from: classes22.dex */
public class ServerConfig {
    private final boolean autoUnSubscribeWhenNaPaDied;
    private final boolean interceptSendWhenNotSubscribed;
    private final boolean keepAlive;
    private final int logLength;
    private final int logLevel;
    private final boolean sendAsync;
    private final String targetPackage;
    private final boolean useMock;
    private final boolean waitInit;
    private final int waitTimeout;

    private ServerConfig(Builder builder) {
        this.waitInit = builder.waitInit;
        this.waitTimeout = builder.waitTimeout;
        this.keepAlive = builder.keepAlive;
        this.useMock = builder.useMock;
        this.autoUnSubscribeWhenNaPaDied = builder.autoUnSubscribeWhenNaPaDied;
        this.interceptSendWhenNotSubscribed = builder.interceptSendWhenNotSubscribed;
        this.logLevel = builder.logLevel;
        this.targetPackage = builder.targetPackage;
        this.sendAsync = builder.sendAsync;
        this.logLength = builder.logLength;
    }

    public boolean isWaitInit() {
        return this.waitInit;
    }

    public int getWaitTimeout() {
        return this.waitTimeout;
    }

    public boolean isKeepAlive() {
        return this.keepAlive;
    }

    public boolean isUseMock() {
        return this.useMock;
    }

    public boolean isAutoUnSubscribeWhenNaPaDied() {
        return this.autoUnSubscribeWhenNaPaDied;
    }

    public int getLogLevel() {
        return this.logLevel;
    }

    public boolean isInterceptSendWhenNotSubscribed() {
        return this.interceptSendWhenNotSubscribed;
    }

    public String getTargetPackage() {
        return this.targetPackage;
    }

    public boolean isSendAsync() {
        return this.sendAsync;
    }

    public int getLogLength() {
        return this.logLength;
    }

    public String toString() {
        return "ServerConfig{waitInit=" + this.waitInit + ", waitTimeout=" + this.waitTimeout + ", keepAlive=" + this.keepAlive + ", useMock=" + this.useMock + ", autoUnSubscribeWhenNaPaDied=" + this.autoUnSubscribeWhenNaPaDied + ", interceptSendWhenNotSubscribed=" + this.interceptSendWhenNotSubscribed + ", logLevel=" + this.logLevel + ", targetPackage='" + this.targetPackage + "', sendAsync=" + this.sendAsync + ", logLength=" + this.logLength + '}';
    }

    /* loaded from: classes22.dex */
    public static class Builder {
        public static final int LOG_D = 3;
        public static final int LOG_E = 6;
        public static final int LOG_I = 4;
        public static final int LOG_W = 5;
        private boolean autoUnSubscribeWhenNaPaDied;
        private boolean interceptSendWhenNotSubscribed;
        private boolean keepAlive;
        private boolean sendAsync;
        private String targetPackage;
        private boolean waitInit;
        private int waitTimeout;
        private boolean useMock = true;
        private int logLevel = 4;
        private int logLength = 256;

        public Builder setWaitInit(boolean waitInit) {
            this.waitInit = waitInit;
            return this;
        }

        public Builder setWaitTimeout(int waitTimeout) {
            this.waitTimeout = waitTimeout;
            return this;
        }

        public Builder useKeepAlive(boolean keepAlive) {
            this.keepAlive = keepAlive;
            return this;
        }

        public Builder useMock(boolean useMock) {
            this.useMock = useMock;
            return this;
        }

        public Builder setAutoUnSubscribeWhenNaPaDied(boolean autoUnSubscribeWhenNaPaDied) {
            this.autoUnSubscribeWhenNaPaDied = autoUnSubscribeWhenNaPaDied;
            return this;
        }

        public Builder setLogLevel(int logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public Builder setInterceptSendWhenNotSubscribed(boolean interceptSendWhenNotSubscribed) {
            this.interceptSendWhenNotSubscribed = interceptSendWhenNotSubscribed;
            return this;
        }

        public Builder setTargetPackage(String targetPackage) {
            this.targetPackage = targetPackage;
            return this;
        }

        public Builder setSendAsync(boolean sendAsync) {
            this.sendAsync = sendAsync;
            return this;
        }

        public Builder setLogLength(int logLength) {
            this.logLength = logLength;
            return this;
        }

        public ServerConfig build() {
            return new ServerConfig(this);
        }
    }
}
