package com.joker17.http.request.core;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class BaseRequestApi {

    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final Charset ASCII = Charset.forName("US-ASCII");
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

    @Setter(value = AccessLevel.NONE)
    private List<String> headerKeyList = new ArrayList<>(16);
    @Setter(value = AccessLevel.NONE)
    private List<String> headerValueList = new ArrayList<>(16);

    private int connectTimeout = -1;

    private int socketTimeout = -1;

    private Charset charset = UTF_8;

    private String url;

    public BaseRequestApi addHeaders(String headerKeys[], String headerValues[]) {
        if (headerKeys != null && headerValues != null) {
            for (int i = 0; i < headerKeys.length; i ++) {
                addHeader(headerKeys[i], headerValues[i]);
            }
        }
        return this;
    }

    public BaseRequestApi addHeader(String headerKey, String headerValue) {
        headerKeyList.add(headerKey);
        headerValueList.add(headerValue);
        return this;
    }

    public BaseRequestApi addHeaders(Map<String, List<String>> headersValueMap) {
        if (headersValueMap != null) {
            for (String key : headersValueMap.keySet()) {
                List<String> values = headersValueMap.get(key);
                if (values != null) {
                    for (String value : values) {
                        addHeader(key, value);
                    }
                }
            }
        }
        return this;
    }


    public BaseRequestApi clearHeaders() {
        headerKeyList.clear();
        headerValueList.clear();
        return this;
    }


}
