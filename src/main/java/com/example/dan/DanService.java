package com.example.dan;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.pkce.PKCE;

import java.io.OutputStream;

public class DanService extends OAuth20Service {
    private static final String VERSION = "2.0";
    private final DefaultApi20 api;
    private final String defaultScope;
    private final String apiKey;
    private final String apiSecret;

    public DanService(DefaultApi20 api, String apiKey, String apiSecret, String callback, String defaultScope,
                          String responseType, OutputStream debugStream, String userAgent, HttpClientConfig httpClientConfig,
                          HttpClient httpClient) {
        super(api, apiKey, apiSecret, callback, defaultScope, responseType, debugStream, userAgent, httpClientConfig, httpClient);
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.api = api;
        this.defaultScope = defaultScope;
    }

    protected OAuthRequest createAccessTokenRequest(AccessTokenRequestParams params) {
        final OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());

        api.getClientAuthentication().addClientAuthentication(request, getApiKey(), getApiSecret());

        request.addParameter(OAuthConstants.CODE, params.getCode());
        final String callback = getCallback();
        if (callback != null) {
            request.addParameter(OAuthConstants.REDIRECT_URI, callback);
        }
        final String scope = params.getScope();
        if (scope != null) {
            request.addParameter(OAuthConstants.SCOPE, scope);
        } else if (defaultScope != null) {
            request.addParameter(OAuthConstants.SCOPE, defaultScope);
        }
        request.addParameter(OAuthConstants.GRANT_TYPE, OAuthConstants.AUTHORIZATION_CODE);
        request.addParameter(OAuthConstants.CLIENT_ID, apiKey);
        request.addParameter(OAuthConstants.CLIENT_SECRET, apiSecret);

        final String pkceCodeVerifier = params.getPkceCodeVerifier();
        if (pkceCodeVerifier != null) {
            request.addParameter(PKCE.PKCE_CODE_VERIFIER_PARAM, pkceCodeVerifier);
        }
        if (isDebug()) {
            log("created access token request with body params [%s], query string params [%s]",
                    request.getBodyParams().asFormUrlEncodedString(),
                    request.getQueryStringParams().asFormUrlEncodedString());
        }
        return request;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }
}
