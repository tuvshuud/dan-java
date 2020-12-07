package com.example.dan;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;

import java.io.OutputStream;

public class DanApi extends DefaultApi20 {
    private static class InstanceHolder {
        private static final DanApi INSTANCE = new DanApi();
    }

    public static DanApi instance() {
        return DanApi.InstanceHolder.INSTANCE;
    }
    @Override
    public String getAccessTokenEndpoint() {
        return "https://sso.gov.mn/oauth2/token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://sso.gov.mn/oauth2/authorize";
    }

    @Override
    public DanService createService(String apiKey, String apiSecret, String callback, String defaultScope,
                                           String responseType, OutputStream debugStream, String userAgent, HttpClientConfig httpClientConfig,
                                           HttpClient httpClient) {

        return new DanService(this, apiKey, apiSecret, callback, defaultScope, responseType, debugStream,
                userAgent, httpClientConfig, httpClient);
    }
}
