package com.joyent.manta.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Class that allows you to fake a HTTP client by preemptively inserting a
 * response object.
 */
public class FakeCloseableHttpClient extends CloseableHttpClient {
    final CloseableHttpResponse response;

    public FakeCloseableHttpClient(final CloseableHttpResponse response) {
        this.response = response;
    }

    @Override
    protected CloseableHttpResponse doExecute(HttpHost target,
                                              HttpRequest request,
                                              HttpContext context)
            throws IOException {
        return response;
    }

    @Override
    public void close() throws IOException {
    }

    @Deprecated
    @Override
    public org.apache.http.params.HttpParams getParams() {
        return null;
    }

    @Deprecated
    @Override
    public org.apache.http.conn.ClientConnectionManager getConnectionManager() {
        return null;
    }
}
