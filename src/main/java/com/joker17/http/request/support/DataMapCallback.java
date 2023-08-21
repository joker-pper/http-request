package com.joker17.http.request.support;

public interface DataMapCallback<B> {

    void run(String key, B valueList);
}
