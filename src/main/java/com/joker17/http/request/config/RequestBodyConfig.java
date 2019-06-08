package com.joker17.http.request.config;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class RequestBodyConfig<T> extends BaseRequestConfig<T> {

    private String requestBody = null;

    public T setRequestBody(String body) {
        requestBody = body;
        return (T) this;
    }

}
