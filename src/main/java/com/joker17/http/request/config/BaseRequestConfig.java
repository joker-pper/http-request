package com.joker17.http.request.config;

import com.joker17.http.request.core.HttpConstants;
import com.joker17.http.request.support.ValidateUtils;
import lombok.*;
import org.apache.http.entity.ContentType;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;

@Data
public class BaseRequestConfig<T>  implements Serializable {

    @Setter(value = AccessLevel.NONE)
    private List<String> headerKeyList = new ArrayList<>(16);
    @Setter(value = AccessLevel.NONE)
    private List<String> headerValueList = new ArrayList<>(16);

    /**
     * 表单请求参数
     */
    @Setter(value = AccessLevel.NONE)
    private Map<String, List<String>> formParameterMap = new LinkedHashMap<>(16);

    @Setter(value = AccessLevel.NONE)
    private int connectTimeout = -1;

    @Setter(value = AccessLevel.NONE)
    private int socketTimeout = -1;

    @Setter(value = AccessLevel.NONE)
    private String url;

    @Setter(value = AccessLevel.NONE)
    private ContentType contentType = ContentType.create(HttpConstants.APPLICATION_JSON, HttpConstants.UTF_8);

    public T setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return (T)this;
    }

    public T setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return (T)this;
    }

    public T setContentType(ContentType contentType) {
        this.contentType = contentType;
        return (T)this;
    }

    public T setContentType(String mimeType) {
       return setContentType(mimeType, HttpConstants.UTF_8);
    }

    public T setContentType(String mimeType, String charset) {
        return setContentType(mimeType, Charset.forName(charset));
    }

    public T setContentType(String mimeType, Charset charset) {
        return setContentType(ContentType.create(mimeType, charset));
    }

    public T setUrl(String url) {
        this.url = url;
        return (T)this;
    }



    public T addHeaders(String headerKeys[], String headerValues[]) {
        if (headerKeys != null && headerValues != null) {
            for (int i = 0; i < headerKeys.length; i++) {
                addHeader(headerKeys[i], headerValues[i]);
            }
        }
        return (T)this;
    }

    public T addHeader(String headerKey, String headerValue) {
        ValidateUtils.checkKeyNameNotEmpty(headerKey);
        if (headerKey.equalsIgnoreCase("Content-Type")) {
            throw new IllegalArgumentException("not support add header Content-Type!");
        }
        headerKeyList.add(headerKey);
        headerValueList.add(headerValue);
        return (T)this;
    }

    public T addHeaders(Map<String, String> headersValueMap) {
        if (headersValueMap != null) {
            for (String key : headersValueMap.keySet()) {
                String value = headersValueMap.get(key);
                addHeader(key, value);
            }
        }
        return (T)this;
    }


    public T clearHeaders() {
        headerKeyList.clear();
        headerValueList.clear();
        return (T)this;
    }

    public T clearHeaders(String... headerKeys) {
        if (headerKeys != null) {
            Set<Integer> indexList = new HashSet<>(16);
            for (String headerKey : headerKeys) {
                int index = 0;
                for (String key : headerKeyList) {
                    if (key.equals(headerKey)) {
                        indexList.add(index);
                        break;
                    }
                    index++;
                }
            }
            for (Integer index : indexList) {
                headerKeyList.remove(index);
                headerValueList.remove(index);
            }
        }
        return (T)this;
    }




    private T resolveFormParameter(String name, boolean add, Object... values) {
        ValidateUtils.checkKeyNameNotEmpty(name);
        List<String> formParameterValueList = formParameterMap.get(name);
        if (formParameterValueList == null) {
            formParameterValueList = new ArrayList<>(16);
            formParameterMap.put(name, formParameterValueList);
        } else {
            if (!add) {
                formParameterValueList.clear();
            }
        }
        if (values != null) {
            for (Object value : values) {
                formParameterValueList.add(value == null ? null : value.toString());
            }
        }
        return (T)this;
    }

    public T addFormParameter(String name, Object... values) {
        return resolveFormParameter(name, true, values);
    }

    public T setFormParameter(String name, Object... values) {
        resolveFormParameter(name, false, values);
        return (T)this;
    }

    public T addFormParameters(String[] names, Object[] values) {
        if (names != null && values != null) {
            for (int i = 0; i < names.length; i++) {
                addFormParameter(names[i], values[i]);
            }
        }
        return (T)this;
    }

    public T setFormParameters(String[] names, Object[] values) {
        if (names != null && values != null) {
            for (int i = 0; i < names.length; i++) {
                setFormParameter(names[i], values[i]);
            }
        }
        return (T)this;
    }

    public T addFormParameters(Map<String, List<Object>> formParameterMap) {
        if (formParameterMap != null) {
            for (String name : formParameterMap.keySet()) {
                List<Object> valueList = formParameterMap.get(name);
                if (valueList != null && !valueList.isEmpty()) {
                    addFormParameter(name, valueList.toArray(new Object[valueList.size()]));
                }
            }
        }
        return (T)this;
    }

    public T setFormParameters(Map<String, List<Object>> formParameterMap) {
        if (formParameterMap != null) {
            for (String name : formParameterMap.keySet()) {
                List<Object> valueList = formParameterMap.get(name);
                if (valueList != null && !valueList.isEmpty()) {
                    setFormParameter(name, valueList.toArray(new Object[valueList.size()]));
                }
            }
        }
        return (T)this;
    }

    public T clearFormParameters(String... names) {
        if (names != null) {
            for (String name : names) {
                formParameterMap.remove(name);
            }
        }
        return (T)this;
    }

    public T clearFormParameters() {
        formParameterMap.clear();
        return (T)this;
    }


}
