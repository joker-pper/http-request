package com.joker17.http.request.config;

import com.joker17.http.request.core.HttpConstants;
import com.joker17.http.request.support.DataMapCallback;
import com.joker17.http.request.support.ValidateUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.entity.ContentType;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class BaseRequestConfig<T> implements Serializable {

    @Setter(value = AccessLevel.NONE)
    protected String method = "UNKNOWN";

    @Setter(value = AccessLevel.NONE)
    private Map<String, List<String>> headerParameterMap = new LinkedHashMap<>(16);

    /**
     * 查询参数
     */
    @Setter(value = AccessLevel.NONE)
    private Map<String, List<String>> queryParameterMap = new LinkedHashMap<>(16);

    /**
     * 表单参数
     */
    @Setter(value = AccessLevel.NONE)
    private Map<String, List<String>> formParameterMap = new LinkedHashMap<>(16);

    @Setter(value = AccessLevel.NONE)
    /**
     * 字符集，优先级最高
     */
    private Charset charset = HttpConstants.UTF_8;

    @Setter(value = AccessLevel.NONE)
    /**
     * 连接目标服务器超时时间 *
     */
    private Integer connectTimeout;

    @Setter(value = AccessLevel.NONE)
    /**
     * 读取目标服务器数据超时时间 *
     */
    private Integer socketTimeout;

    @Setter(value = AccessLevel.NONE)
    /**
     * 从连接池中取连接的超时时间 *
     */
    private Integer connectionRequestTimeout;


    @Setter(value = AccessLevel.NONE)
    /**
     * @see CookieSpecs
     * e.g:  IGNORE_COOKIES 可用来忽略控制台中异常cookie提示
     */
    private String cookieSpec;

    /**
     * 查询参数设置不使用url encoded (未设置为true时，则默认对queryParameterMap数据进行url encoded )
     */
    @Setter(value = AccessLevel.NONE)
    private Boolean queryParameterNotWithUrlEncoded;

    @Setter(value = AccessLevel.NONE)
    private Boolean redirectsEnabled;

    @Setter(value = AccessLevel.NONE)
    private Integer maxRedirects;

    @Setter(value = AccessLevel.NONE)
    private RequestConfigCallback configCallback;

    @Setter(value = AccessLevel.NONE)
    private ContentType contentType = HttpConstants.DEFAULT_CONTENT_TYPE;

    @Setter(value = AccessLevel.NONE)
    private String userAgent;

    @Setter(value = AccessLevel.NONE)
    private String url;

    public T setCharset(Charset charset) {
        this.charset = charset;
        return (T) this;
    }

    public T setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return (T) this;
    }

    public T setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return (T) this;
    }

    public T setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        return (T) this;
    }

    public T setCookieSpec(String cookieSpec) {
        this.cookieSpec = cookieSpec;
        return (T) this;
    }

    public T setRedirectsEnabled(boolean redirectsEnabled) {
        this.redirectsEnabled = redirectsEnabled;
        return (T) this;
    }

    public T setMaxRedirects(int maxRedirects) {
        this.maxRedirects = maxRedirects;
        return (T) this;
    }

    public T setConfigCallback(RequestConfigCallback configCallback) {
        this.configCallback = configCallback;
        return (T) this;
    }

    public T setContentType(ContentType contentType) {
        this.contentType = contentType;
        return (T) this;
    }

    /**
     * 设置contentType （支持仅mimeType及携带charset的固定格式）
     *
     * @param mimeType e.g: text/plain  text/html; charset=utf-8
     * @return
     */
    public T setContentType(String mimeType) {
        if (mimeType.contains(HttpConstants.SEMICOLON_STR)) {
            String toUpperCaseMimeType = mimeType.toUpperCase();
            int index = toUpperCaseMimeType.indexOf(HttpConstants.CHARSET_STR);
            String charsetStr = null;
            if (index != -1) {
                toUpperCaseMimeType = toUpperCaseMimeType.substring(index + HttpConstants.CHARSET_STR.length());
                charsetStr = toUpperCaseMimeType.substring(toUpperCaseMimeType.indexOf(HttpConstants.EQUAL_SIGN_STR) + 1).trim();
            }
            String removeSemicolonMimeType = mimeType.substring(0, mimeType.indexOf(HttpConstants.SEMICOLON_STR));
            if (StringUtils.isNotEmpty(charsetStr)) {
                return setContentType(removeSemicolonMimeType, charsetStr);
            } else {
                return setContentType(removeSemicolonMimeType, charset);
            }
        }
        return setContentType(mimeType, charset);
    }

    public T setContentType(String mimeType, String charset) {
        return setContentType(mimeType, Charset.forName(charset));
    }

    public T setContentType(String mimeType, Charset charset) {
        return setContentType(ContentType.create(mimeType, charset));
    }

    public T clearContentType() {
        this.contentType = null;
        return (T) this;
    }

    public T setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return (T) this;
    }

    public T setUrl(String url) {
        this.url = url;
        return (T) this;
    }

    public T addHeaders(String headerKeys[], String headerValues[]) {
        if (headerKeys != null && headerValues != null) {
            for (int i = 0; i < headerKeys.length; i++) {
                addHeader(headerKeys[i], headerValues[i]);
            }
        }
        return (T) this;
    }

    public T addHeader(String headerKey, String... headerValue) {
        ValidateUtils.checkKeyNameNotEmpty(headerKey);
        if (headerKey.equalsIgnoreCase("Content-Type")) {
            throw new IllegalArgumentException("not support add header Content-Type!");
        }

        List<String> headerValues = headerParameterMap.get(headerKey);
        if (headerValues == null) {
            headerValues = new ArrayList<>(4);
            headerParameterMap.put(headerKey, headerValues);
        }

        if (headerValue != null) {
            for (String currentValue : headerValue) {
                headerValues.add(currentValue);
            }
        }

        return (T) this;
    }

    public T addHeaders(Map<String, String> headersValueMap) {
        if (headersValueMap != null) {
            for (Map.Entry<String, String> entry : headersValueMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                addHeader(key, value);
            }
        }
        return (T) this;
    }

    public T setHeaders(String headerKeys[], String headerValues[]) {
        if (headerKeys != null && headerValues != null) {
            for (int i = 0; i < headerKeys.length; i++) {
                setHeader(headerKeys[i], headerValues[i]);
            }
        }
        return (T) this;
    }

    public T setHeader(String headerKey, String... headerValue) {
        ValidateUtils.checkKeyNameNotEmpty(headerKey);
        if (headerKey.equalsIgnoreCase("Content-Type")) {
            throw new IllegalArgumentException("not support set header Content-Type!");
        }

        List<String> headerValues = headerParameterMap.get(headerKey);
        if (headerValues == null) {
            headerValues = new ArrayList<>(4);
            headerParameterMap.put(headerKey, headerValues);
        }

        headerValues.clear();

        if (headerValue != null) {
            for (String currentValue : headerValue) {
                headerValues.add(currentValue);
            }
        }

        return (T) this;
    }

    public T setHeaders(Map<String, String> headersValueMap) {
        if (headersValueMap != null) {
            for (Map.Entry<String, String> entry : headersValueMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                setHeader(key, value);
            }
        }
        return (T) this;
    }

    public T clearHeaders() {
        headerParameterMap.clear();
        return (T) this;
    }

    public T clearHeaders(String... headerKeys) {
        if (headerKeys != null) {
            for (String headerKey : headerKeys) {
                headerParameterMap.remove(headerKey);
            }
        }
        return (T) this;
    }


    private T resolveQueryOrFormParameter(String name, boolean add, boolean isQuery, Object... values) {
        ValidateUtils.checkKeyNameNotEmpty(name);
        Map<String, List<String>> parameterMap = isQuery ? queryParameterMap : formParameterMap;
        List<String> parameterValueList = parameterMap.get(name);
        if (parameterValueList == null) {
            parameterValueList = new ArrayList<>(16);
            parameterMap.put(name, parameterValueList);
        } else {
            if (!add) {
                parameterValueList.clear();
            }
        }
        if (values != null) {
            for (Object value : values) {
                parameterValueList.add(value == null ? null : value.toString());
            }
        }
        return (T) this;
    }

    private T resolveFormParameter(String name, boolean add, Object... values) {
        return resolveQueryOrFormParameter(name, add, false, values);
    }

    public T addFormParameter(String name, Object... values) {
        return resolveFormParameter(name, true, values);
    }

    public T setFormParameter(String name, Object... values) {
        resolveFormParameter(name, false, values);
        return (T) this;
    }

    public T addFormParameters(String[] names, Object[] values) {
        if (names != null && values != null) {
            for (int i = 0; i < names.length; i++) {
                addFormParameter(names[i], values[i]);
            }
        }
        return (T) this;
    }

    public T setFormParameters(String[] names, Object[] values) {
        if (names != null && values != null) {
            for (int i = 0; i < names.length; i++) {
                setFormParameter(names[i], values[i]);
            }
        }
        return (T) this;
    }


    /**
     * 进行遍历dataMap，当key存在不为空的列表值时，进行执行回调函数
     *
     * @param dataMap  数据Map
     * @param callback 回调函数
     * @param <E>      泛型，可使用String / Object类型 （最终值会转换为String）
     */
    protected <E> void forEachWithListMap(Map<String, List<E>> dataMap, DataMapCallback<List<E>> callback) {
        if (dataMap != null) {
            for (Map.Entry<String, List<E>> entry : dataMap.entrySet()) {
                String name = entry.getKey();
                List<E> valueList = entry.getValue();
                if (valueList != null && !valueList.isEmpty()) {
                    callback.run(name, valueList);
                }
            }
        }
    }

    /**
     * 添加formParameters
     *
     * @param formParameterMap 参数Map
     * @param <E>              泛型，可使用String / Object类型 （最终值会转换为String）
     * @return
     */
    public <E> T addFormParameters(Map<String, List<E>> formParameterMap) {
        forEachWithListMap(formParameterMap, new DataMapCallback<List<E>>() {
            @Override
            public void run(String key, List<E> valueList) {
                addFormParameter(key, valueList.toArray(new Object[valueList.size()]));
            }
        });
        return (T) this;
    }

    /**
     * 设置formParameters
     *
     * @param formParameterMap 参数Map
     * @param <E>              泛型，可使用String / Object类型 （最终值会转换为String）
     * @return
     */
    public <E> T setFormParameters(Map<String, List<E>> formParameterMap) {
        forEachWithListMap(formParameterMap, new DataMapCallback<List<E>>() {
            @Override
            public void run(String key, List<E> valueList) {
                setFormParameter(key, valueList.toArray(new Object[valueList.size()]));
            }
        });
        return (T) this;
    }

    /**
     * 清除指定formParameters
     *
     * @param names 要清除的names
     * @return
     */
    public T clearFormParameters(String... names) {
        if (names != null) {
            for (String name : names) {
                formParameterMap.remove(name);
            }
        }
        return (T) this;
    }

    /**
     * 清除所有formParameters
     *
     * @return
     */
    public T clearFormParameters() {
        formParameterMap.clear();
        return (T) this;
    }

    private T resolveQueryParameter(String name, boolean add, Object... values) {
        return resolveQueryOrFormParameter(name, add, true, values);
    }

    public T addQueryParameter(String name, Object... values) {
        return resolveQueryParameter(name, true, values);
    }

    public T setQueryParameter(String name, Object... values) {
        resolveQueryParameter(name, false, values);
        return (T) this;
    }

    public T addQueryParameters(String[] names, Object[] values) {
        if (names != null && values != null) {
            for (int i = 0; i < names.length; i++) {
                addQueryParameter(names[i], values[i]);
            }
        }
        return (T) this;
    }

    public T setQueryParameters(String[] names, Object[] values) {
        if (names != null && values != null) {
            for (int i = 0; i < names.length; i++) {
                setQueryParameter(names[i], values[i]);
            }
        }
        return (T) this;
    }

    /**
     * 添加queryParameters
     *
     * @param queryParameterMap 参数Map
     * @param <E>               泛型，可使用String / Object类型 （最终值会转换为String）
     * @return
     */
    public <E> T addQueryParameters(Map<String, List<E>> queryParameterMap) {
        forEachWithListMap(queryParameterMap, new DataMapCallback<List<E>>() {
            @Override
            public void run(String key, List<E> valueList) {
                addQueryParameter(key, valueList.toArray(new Object[valueList.size()]));
            }
        });
        return (T) this;
    }

    /**
     * 设置queryParameters
     *
     * @param queryParameterMap 参数Map
     * @param <E>               泛型，可使用String / Object类型 （最终值会转换为String）
     * @return
     */
    public <E> T setQueryParameters(Map<String, List<E>> queryParameterMap) {
        forEachWithListMap(queryParameterMap, new DataMapCallback<List<E>>() {
            @Override
            public void run(String key, List<E> valueList) {
                setQueryParameter(key, valueList.toArray(new Object[valueList.size()]));
            }
        });
        return (T) this;
    }

    /**
     * 清除指定queryParameters
     *
     * @param names 要清除的names
     * @return
     */
    public T clearQueryParameters(String... names) {
        if (names != null) {
            for (String name : names) {
                queryParameterMap.remove(name);
            }
        }
        return (T) this;
    }


    /**
     * 清除所有queryParameters
     *
     * @return
     */
    public T clearQueryParameters() {
        queryParameterMap.clear();
        return (T) this;
    }


}
