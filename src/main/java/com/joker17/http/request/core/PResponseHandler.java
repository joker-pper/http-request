package com.joker17.http.request.core;

import com.joker17.http.request.config.BaseRequestConfig;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

/**
 * response处理器
 * <p>
 * 注: 大文件时需使用
 *
 * @param <P>
 * @param <Z>
 */
public abstract class PResponseHandler<P, Z extends BaseRequestConfig> {

    /**
     * 发送请求前执行
     *
     * @param request
     * @param requestConfig
     */
    public abstract void beforeExecute(HttpRequestBase request, Z requestConfig);

    /**
     * 处理返回结果
     *
     * @param response
     * @return
     * @throws IOException
     */
    public abstract P handleResponse(PResponse response) throws IOException;

}
