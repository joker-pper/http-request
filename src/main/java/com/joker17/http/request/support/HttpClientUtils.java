package com.joker17.http.request.support;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpClientUtils {

    private static RequestConfig DEFAULT_REQUEST_CONFIG = null;

    private static HttpClientSupportConfigCallback DEFAULT_HTTP_CLIENT_SUPPORT_CONFIG_CALLBACK = null;

    private HttpClientUtils() {
    }

    public static void setDefaultRequestConfig(RequestConfig defaultRequestConfig) {
        DEFAULT_REQUEST_CONFIG = defaultRequestConfig;
    }

    public static RequestConfig getDefaultRequestConfig() {
        return DEFAULT_REQUEST_CONFIG;
    }

    public static HttpClientSupportConfigCallback getDefaultHttpClientSupportConfigCallback() {
        return DEFAULT_HTTP_CLIENT_SUPPORT_CONFIG_CALLBACK;
    }

    public static void setDefaultHttpClientSupportConfigCallback(HttpClientSupportConfigCallback defaultHttpClientSupportConfigCallback) {
        DEFAULT_HTTP_CLIENT_SUPPORT_CONFIG_CALLBACK = defaultHttpClientSupportConfigCallback;
    }

    /**
     * 获取忽略证书验证的httpClient
     * https://blog.csdn.net/u010430495/article/details/72967591
     *
     * @return
     */
    public static CloseableHttpClient getIgnoreVerifySSLHttpClient() {
        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            }).build();
            HttpClientBuilder builder = HttpClients.custom().setSSLContext(sslContext).
                    setSSLHostnameVerifier(new NoopHostnameVerifier())
                    .setDefaultRequestConfig(getDefaultRequestConfig());
            HttpClientSupportConfigCallback clientConfigCallback = getDefaultHttpClientSupportConfigCallback();
            if (clientConfigCallback != null) {
                clientConfigCallback.executeIgnoreVerifySSLHttp(builder);
            }
            return builder.build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new RuntimeException("get ignore verify ssl http client error.", e);
        }
    }

    public static CloseableHttpClient getDefaultHttpClient() {
        HttpClientBuilder builder = HttpClients.custom().setDefaultRequestConfig(getDefaultRequestConfig());
        HttpClientSupportConfigCallback clientConfigCallback = getDefaultHttpClientSupportConfigCallback();
        if (clientConfigCallback != null) {
            clientConfigCallback.executeDefaultHttp(builder);
        }
        return builder.build();
    }

}
