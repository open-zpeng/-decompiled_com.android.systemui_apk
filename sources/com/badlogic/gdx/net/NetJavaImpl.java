package com.badlogic.gdx.net;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/* loaded from: classes21.dex */
public class NetJavaImpl {
    final ObjectMap<Net.HttpRequest, HttpURLConnection> connections;
    private final ExecutorService executorService;
    final ObjectMap<Net.HttpRequest, Net.HttpResponseListener> listeners;

    /* loaded from: classes21.dex */
    static class HttpClientResponse implements Net.HttpResponse {
        private final HttpURLConnection connection;
        private HttpStatus status;

        public HttpClientResponse(HttpURLConnection connection) throws IOException {
            this.connection = connection;
            try {
                this.status = new HttpStatus(connection.getResponseCode());
            } catch (IOException e) {
                this.status = new HttpStatus(-1);
            }
        }

        @Override // com.badlogic.gdx.Net.HttpResponse
        public byte[] getResult() {
            InputStream input = getInputStream();
            if (input == null) {
                return StreamUtils.EMPTY_BYTES;
            }
            try {
                return StreamUtils.copyStreamToByteArray(input, this.connection.getContentLength());
            } catch (IOException e) {
                return StreamUtils.EMPTY_BYTES;
            } finally {
                StreamUtils.closeQuietly(input);
            }
        }

        @Override // com.badlogic.gdx.Net.HttpResponse
        public String getResultAsString() {
            InputStream input = getInputStream();
            if (input == null) {
                return "";
            }
            try {
                return StreamUtils.copyStreamToString(input, this.connection.getContentLength(), "UTF8");
            } catch (IOException e) {
                return "";
            } finally {
                StreamUtils.closeQuietly(input);
            }
        }

        @Override // com.badlogic.gdx.Net.HttpResponse
        public InputStream getResultAsStream() {
            return getInputStream();
        }

        @Override // com.badlogic.gdx.Net.HttpResponse
        public HttpStatus getStatus() {
            return this.status;
        }

        @Override // com.badlogic.gdx.Net.HttpResponse
        public String getHeader(String name) {
            return this.connection.getHeaderField(name);
        }

        @Override // com.badlogic.gdx.Net.HttpResponse
        public Map<String, List<String>> getHeaders() {
            return this.connection.getHeaderFields();
        }

        private InputStream getInputStream() {
            try {
                return this.connection.getInputStream();
            } catch (IOException e) {
                return this.connection.getErrorStream();
            }
        }
    }

    public NetJavaImpl() {
        this(Integer.MAX_VALUE);
    }

    public NetJavaImpl(int maxThreads) {
        this.executorService = new ThreadPoolExecutor(0, maxThreads, 60L, TimeUnit.SECONDS, new SynchronousQueue(), new ThreadFactory() { // from class: com.badlogic.gdx.net.NetJavaImpl.1
            @Override // java.util.concurrent.ThreadFactory
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "NetThread");
                thread.setDaemon(true);
                return thread;
            }
        });
        this.connections = new ObjectMap<>();
        this.listeners = new ObjectMap<>();
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x00a9 A[Catch: Exception -> 0x00df, LOOP:0: B:25:0x00a3->B:27:0x00a9, LOOP_END, TryCatch #0 {Exception -> 0x00df, blocks: (B:6:0x0013, B:8:0x001f, B:10:0x0026, B:12:0x002c, B:13:0x003e, B:15:0x0060, B:17:0x006f, B:19:0x0077, B:24:0x0083, B:25:0x00a3, B:27:0x00a9, B:28:0x00bf, B:14:0x0057), top: B:37:0x0013 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void sendHttpRequest(final com.badlogic.gdx.Net.HttpRequest r13, final com.badlogic.gdx.Net.HttpResponseListener r14) {
        /*
            r12 = this;
            java.lang.String r0 = ""
            java.lang.String r1 = r13.getUrl()
            if (r1 != 0) goto L13
            com.badlogic.gdx.utils.GdxRuntimeException r0 = new com.badlogic.gdx.utils.GdxRuntimeException
            java.lang.String r1 = "can't process a HTTP request without URL set"
            r0.<init>(r1)
            r14.failed(r0)
            return
        L13:
            java.lang.String r1 = r13.getMethod()     // Catch: java.lang.Exception -> Ldf
            java.lang.String r2 = "GET"
            boolean r2 = r1.equalsIgnoreCase(r2)     // Catch: java.lang.Exception -> Ldf
            if (r2 == 0) goto L57
            r2 = r0
            java.lang.String r3 = r13.getContent()     // Catch: java.lang.Exception -> Ldf
            if (r3 == 0) goto L3e
            boolean r0 = r0.equals(r3)     // Catch: java.lang.Exception -> Ldf
            if (r0 != 0) goto L3e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> Ldf
            r0.<init>()     // Catch: java.lang.Exception -> Ldf
            java.lang.String r4 = "?"
            r0.append(r4)     // Catch: java.lang.Exception -> Ldf
            r0.append(r3)     // Catch: java.lang.Exception -> Ldf
            java.lang.String r0 = r0.toString()     // Catch: java.lang.Exception -> Ldf
            r2 = r0
        L3e:
            java.net.URL r0 = new java.net.URL     // Catch: java.lang.Exception -> Ldf
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> Ldf
            r4.<init>()     // Catch: java.lang.Exception -> Ldf
            java.lang.String r5 = r13.getUrl()     // Catch: java.lang.Exception -> Ldf
            r4.append(r5)     // Catch: java.lang.Exception -> Ldf
            r4.append(r2)     // Catch: java.lang.Exception -> Ldf
            java.lang.String r4 = r4.toString()     // Catch: java.lang.Exception -> Ldf
            r0.<init>(r4)     // Catch: java.lang.Exception -> Ldf
            goto L60
        L57:
            java.net.URL r0 = new java.net.URL     // Catch: java.lang.Exception -> Ldf
            java.lang.String r2 = r13.getUrl()     // Catch: java.lang.Exception -> Ldf
            r0.<init>(r2)     // Catch: java.lang.Exception -> Ldf
        L60:
            java.net.URLConnection r2 = r0.openConnection()     // Catch: java.lang.Exception -> Ldf
            java.net.HttpURLConnection r2 = (java.net.HttpURLConnection) r2     // Catch: java.lang.Exception -> Ldf
            java.lang.String r3 = "POST"
            boolean r3 = r1.equalsIgnoreCase(r3)     // Catch: java.lang.Exception -> Ldf
            r4 = 1
            if (r3 != 0) goto L82
            java.lang.String r3 = "PUT"
            boolean r3 = r1.equalsIgnoreCase(r3)     // Catch: java.lang.Exception -> Ldf
            if (r3 != 0) goto L82
            java.lang.String r3 = "PATCH"
            boolean r3 = r1.equalsIgnoreCase(r3)     // Catch: java.lang.Exception -> Ldf
            if (r3 == 0) goto L80
            goto L82
        L80:
            r3 = 0
            goto L83
        L82:
            r3 = r4
        L83:
            r9 = r3
            r2.setDoOutput(r9)     // Catch: java.lang.Exception -> Ldf
            r2.setDoInput(r4)     // Catch: java.lang.Exception -> Ldf
            r2.setRequestMethod(r1)     // Catch: java.lang.Exception -> Ldf
            boolean r3 = r13.getFollowRedirects()     // Catch: java.lang.Exception -> Ldf
            java.net.HttpURLConnection.setFollowRedirects(r3)     // Catch: java.lang.Exception -> Ldf
            r12.putIntoConnectionsAndListeners(r13, r14, r2)     // Catch: java.lang.Exception -> Ldf
            java.util.Map r3 = r13.getHeaders()     // Catch: java.lang.Exception -> Ldf
            java.util.Set r3 = r3.entrySet()     // Catch: java.lang.Exception -> Ldf
            java.util.Iterator r3 = r3.iterator()     // Catch: java.lang.Exception -> Ldf
        La3:
            boolean r4 = r3.hasNext()     // Catch: java.lang.Exception -> Ldf
            if (r4 == 0) goto Lbf
            java.lang.Object r4 = r3.next()     // Catch: java.lang.Exception -> Ldf
            java.util.Map$Entry r4 = (java.util.Map.Entry) r4     // Catch: java.lang.Exception -> Ldf
            java.lang.Object r5 = r4.getKey()     // Catch: java.lang.Exception -> Ldf
            java.lang.String r5 = (java.lang.String) r5     // Catch: java.lang.Exception -> Ldf
            java.lang.Object r6 = r4.getValue()     // Catch: java.lang.Exception -> Ldf
            java.lang.String r6 = (java.lang.String) r6     // Catch: java.lang.Exception -> Ldf
            r2.addRequestProperty(r5, r6)     // Catch: java.lang.Exception -> Ldf
            goto La3
        Lbf:
            int r3 = r13.getTimeOut()     // Catch: java.lang.Exception -> Ldf
            r2.setConnectTimeout(r3)     // Catch: java.lang.Exception -> Ldf
            int r3 = r13.getTimeOut()     // Catch: java.lang.Exception -> Ldf
            r2.setReadTimeout(r3)     // Catch: java.lang.Exception -> Ldf
            java.util.concurrent.ExecutorService r10 = r12.executorService     // Catch: java.lang.Exception -> Ldf
            com.badlogic.gdx.net.NetJavaImpl$2 r11 = new com.badlogic.gdx.net.NetJavaImpl$2     // Catch: java.lang.Exception -> Ldf
            r3 = r11
            r4 = r12
            r5 = r9
            r6 = r13
            r7 = r2
            r8 = r14
            r3.<init>()     // Catch: java.lang.Exception -> Ldf
            r10.submit(r11)     // Catch: java.lang.Exception -> Ldf
            return
        Ldf:
            r0 = move-exception
            r14.failed(r0)     // Catch: java.lang.Throwable -> Le8
            r12.removeFromConnectionsAndListeners(r13)
            return
        Le8:
            r1 = move-exception
            r12.removeFromConnectionsAndListeners(r13)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.net.NetJavaImpl.sendHttpRequest(com.badlogic.gdx.Net$HttpRequest, com.badlogic.gdx.Net$HttpResponseListener):void");
    }

    public void cancelHttpRequest(Net.HttpRequest httpRequest) {
        Net.HttpResponseListener httpResponseListener = getFromListeners(httpRequest);
        if (httpResponseListener != null) {
            httpResponseListener.cancelled();
            removeFromConnectionsAndListeners(httpRequest);
        }
    }

    synchronized void removeFromConnectionsAndListeners(Net.HttpRequest httpRequest) {
        this.connections.remove(httpRequest);
        this.listeners.remove(httpRequest);
    }

    synchronized void putIntoConnectionsAndListeners(Net.HttpRequest httpRequest, Net.HttpResponseListener httpResponseListener, HttpURLConnection connection) {
        this.connections.put(httpRequest, connection);
        this.listeners.put(httpRequest, httpResponseListener);
    }

    synchronized Net.HttpResponseListener getFromListeners(Net.HttpRequest httpRequest) {
        Net.HttpResponseListener httpResponseListener;
        httpResponseListener = this.listeners.get(httpRequest);
        return httpResponseListener;
    }
}
