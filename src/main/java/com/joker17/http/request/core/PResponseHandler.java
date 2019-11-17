package com.joker17.http.request.core;

import com.joker17.http.request.config.BaseRequestConfig;
import org.apache.http.client.methods.HttpRequestBase;

public abstract class PResponseHandler<P, Z extends BaseRequestConfig> {

    public abstract void beforeExecute(HttpRequestBase request, Z requestConfig);

    public abstract P handleResponse(PResponse response);

}
