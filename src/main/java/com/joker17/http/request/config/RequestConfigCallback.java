package com.joker17.http.request.config;

/**
 * 用于设置其他属性，e.g：HttpHost proxy
 *
 * @param <T> RequestConfig.Builder
 */
public interface RequestConfigCallback<T> {

    void execute(T builder);

}
