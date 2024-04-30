package com.joker17.http.request.support;

import org.apache.http.impl.client.HttpClientBuilder;


/**
 * 用于HttpClientUtils获取的httpClient设置其他属性
 */
public interface HttpClientSupportConfigCallback {

    /**
     * HttpClientUtils.getDefaultHttpClient方法使用
     *
     * @param builder
     */
    void executeDefaultHttp(HttpClientBuilder builder);

    /**
     * HttpClientUtils.getIgnoreVerifySSLHttpClient方法使用
     *
     * @param builder
     */
    void executeIgnoreVerifySSLHttp(HttpClientBuilder builder);
}
