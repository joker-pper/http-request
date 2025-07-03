package com.joker17.http.request.config;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RequestBodyConfig<T> extends BaseRequestConfig<T> {

    @Setter(value = AccessLevel.NONE)
    private String requestBody = null;

    public T setRequestBody(String body) {
        requestBody = body;
        return (T) this;
    }

}
