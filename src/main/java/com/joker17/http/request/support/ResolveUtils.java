package com.joker17.http.request.support;

import com.joker17.http.request.config.BaseRequestConfig;
import com.joker17.http.request.core.HttpConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.*;

public class ResolveUtils {

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private ResolveUtils() {
    }

    /**
     * 获取处理的url,参数拼接在url后面
     *
     * @param url
     * @param params 参数 e.g: username=xxx&step=2
     * @return
     */
    public static String resolveUrl(String url, String params) {
        StringBuilder builder = new StringBuilder();
        int index = url.lastIndexOf("?");
        String prev = url;
        String[] afterUrlParamArray = null;
        if (index > -1) {
            //存在?时
            //前缀截止到?
            prev = url.substring(0, index);
            //数组, [参数=值]
            afterUrlParamArray = url.substring(index + 1).split("&");
        }
        builder.append(prev);
        index = 0;
        if (afterUrlParamArray != null && afterUrlParamArray.length > 0) {
            for (String afterUrlParam : afterUrlParamArray) {
                if (afterUrlParam == null || "".equals(afterUrlParam.trim())) {
                    continue;
                }
                builder.append(index == 0 ? "?" : "&").append(afterUrlParam);
                index++;
            }
        }
        if (params != null && !params.isEmpty()) {
            builder.append(index == 0 ? "?" : "&");
            builder.append(params.startsWith("?") || params.startsWith("&") ? params.substring(1) : params);
        }
        return builder.toString();
    }

    public static int copy(InputStream in, OutputStream out) throws IOException {
        Args.notNull(in, "No InputStream specified");
        Args.notNull(out, "No OutputStream specified");
        try {
            int byteCount = 0;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

            int bytesRead;
            for (; (bytesRead = in.read(buffer)) != -1; byteCount += bytesRead) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            return byteCount;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }

            try {
                out.close();
            } catch (IOException e) {
            }

        }
    }

    public static void copy(byte[] in, OutputStream out) throws IOException {
        Args.notNull(in, "No input byte array specified");
        Args.notNull(out, "No OutputStream specified");
        try {
            out.write(in);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }

        }
    }

    public static byte[] copyToByteArray(InputStream in) throws IOException {
        if (in == null) {
            return new byte[0];
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
            copy(in, out);
            return out.toByteArray();
        }
    }

    public static ContentType getContentType(final HttpEntity entity, final Charset defaultCharset) throws IOException, ParseException {

        ContentType contentType = null;
        try {
            contentType = ContentType.get(entity);
        } catch (final UnsupportedCharsetException ex) {
            if (defaultCharset == null) {
                throw new UnsupportedEncodingException(ex.getMessage());
            }
        }
        if (contentType != null) {
            if (contentType.getCharset() == null) {
                contentType = contentType.withCharset(defaultCharset);
            }
        } else {
            contentType = ContentType.DEFAULT_TEXT.withCharset(defaultCharset);
        }
        return contentType;
    }

    public static String toString(final HttpEntity entity, final Charset defaultCharset) throws IOException, ParseException {
        return EntityUtils.toString(entity, defaultCharset);
    }

    public static String toString(final InputStream inStream, final long contentLength, final ContentType contentType) throws IOException {
        return toString(inStream, contentLength, getCharset(contentType));
    }

    public static Charset getCharset(final ContentType contentType) {
        Charset charset = null;
        if (contentType != null) {
            charset = contentType.getCharset();
            if (charset == null) {
                final ContentType defaultContentType = ContentType.getByMimeType(contentType.getMimeType());
                charset = defaultContentType != null ? defaultContentType.getCharset() : null;
            }
        }
        return charset;
    }

    public static String toString(final InputStream inStream, final long contentLength, Charset charset) throws IOException {
        if (inStream == null) {
            return null;
        }
        try {
            Args.check(contentLength <= Integer.MAX_VALUE, "HTTP data too large to be buffered in memory");
            int capacity = (int) contentLength;
            if (capacity < 0) {
                capacity = DEFAULT_BUFFER_SIZE;
            }

            if (charset == null) {
                charset = HTTP.DEF_CONTENT_CHARSET;
            }
            final Reader reader = new InputStreamReader(inStream, charset);
            final CharArrayBuffer buffer = new CharArrayBuffer(capacity);
            final char[] tmp = new char[1024];
            int l;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            return buffer.toString();
        } finally {
            inStream.close();
        }
    }

    public static URI getURI(String url, List<NameValuePair> paramList, boolean appendParams, Charset charset) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("uri must not be empty.");
        }
        if (appendParams && paramList != null && !paramList.isEmpty()) {
            try {
                String formParamsStr = EntityUtils.toString(new UrlEncodedFormEntity(paramList, charset));
                url = resolveUrl(url, formParamsStr);
            } catch (IOException e) {
            }
        }
        return URI.create(url);
    }

    public static List<NameValuePair> getFormParamList(BaseRequestConfig requestConfig) {
        return getNameValuePairList(requestConfig != null ? requestConfig.getFormParameterMap() : null);
    }

    public static List<NameValuePair> getQueryParamList(BaseRequestConfig requestConfig) {
        return getNameValuePairList(requestConfig != null ? requestConfig.getQueryParameterMap() : null);
    }

    public static List<NameValuePair> getNameValuePairList(Map<String, List<String>> parameterMap) {
        List<NameValuePair> resultList = new ArrayList<>(16);
        if (parameterMap != null) {
            for (Map.Entry<String, List<String>> entry : parameterMap.entrySet()) {
                String paramKey = entry.getKey();
                List<String> paramValueList = entry.getValue();
                if (paramValueList != null) {
                    for (String paramValue : paramValueList) {
                        resultList.add(new BasicNameValuePair(paramKey, paramValue));
                    }
                }
            }
        }
        return resultList;
    }

    public static CookieStore getCookieStore(CloseableHttpClient httpClient) {
        if (httpClient != null) {
            try {
                Field field = httpClient.getClass().getDeclaredField("cookieStore");
                field.setAccessible(true);

                Object result = field.get(httpClient);
                if (result != null) {
                    if (result instanceof CookieStore) {
                        return (CookieStore) result;
                    }
                }
            } catch (Exception e) {
                //获取cookieStore失败
                throw new RuntimeException("get cookie store error", e);
            }
        }
        return null;
    }


    /**
     * 获取文件名(通过返回的请求头信息中获取)
     *
     * @param text
     * @param defaultValue
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getFilenameByContentDisposition(String text, String defaultValue) {
        String result = null;
        if (text != null && text.length() > 0) {
            try {
                text = new String(text.getBytes(HttpConstants.ISO_8859_1), HttpConstants.UTF_8);
                String symbol = "filename=";
                int index = text.indexOf(symbol);
                if (index != -1) {
                    //result = URLDecoder.decode(text.substring(index + symbol.length()),"UTF-8");
                    result = text.substring(index + symbol.length());
                    if (result.startsWith("\"")) {
                        result = result.replace("\"", "");
                    }
                    if (result.endsWith(";")) {
                        result = result.substring(0, result.length() - 1);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("parse file name error.", e);
            }
        }
        return result != null && result.length() > 0 ? result : defaultValue;
    }

    /**
     * 通过url获取文件名
     * e.g:
     * download/a.txt -> a.txt
     * download/a.txt?random=xxx -> a.txt
     *
     * @param text
     * @return
     */
    public static String getFilenameByUrl(String text) {
        String result = null;
        if (text != null && text.length() > 0) {
            int index = text.lastIndexOf('/');
            if (index != -1) {
                //获取/后面的部分
                result = text.substring(index + 1);
                index = result.indexOf('?');
                if (index != -1) {
                    //存在?时截取前面的内容
                    result = result.substring(0, index);
                }
            }
        }
        return result;
    }

    /**
     * 解析多组数据
     *
     * @param data                       支持postman bulk edit data / chrome复制的请求头内容
     *
     *                                   <p>
     *                                   e.g:
     *                                   key1:value1
     *                                   key2:\\n
     *                                   value2
     *                                   //key3:value3
     * @param separator                  分割符, e.g:
     * @param removeValueFirstBlankSpace 是否移除value的第一个空格
     * @param continueKeys               跳过keys
     * @return
     */
    public static Map<String, List<String>> convertBulkDataMap(String data, String separator, boolean removeValueFirstBlankSpace, String... continueKeys) {
        if (StringUtils.isBlank(data)) {
            return Collections.emptyMap();
        }

        String[] results = StringUtils.split(data, "\n");
        if (results == null || results.length == 0) {
            return Collections.emptyMap();
        }

        Set<String> continueKeySet;
        if (continueKeys != null && continueKeys.length > 0) {
            continueKeySet = new HashSet<>(Arrays.asList(continueKeys));
        } else {
            continueKeySet = Collections.emptySet();
        }

        Map<String, List<String>> resultMap = new LinkedHashMap<>(32);

        String annotateSymbol = "//";
        String key = null;
        String value = null;
        boolean isWillContainsKey = true;
        boolean isStartWithAnnotateSymbol = false;
        boolean isToAddValue = false;
        int separatorLen = StringUtils.length(separator);
        for (final String result : results) {
            if (isWillContainsKey) {
                int count = StringUtils.countMatches(result, separator);
                if (count == 0) {
                    //不存在时跳过
                    continue;
                }

                isStartWithAnnotateSymbol = StringUtils.startsWith(result, annotateSymbol);
                String currentStr = result;
                if (isStartWithAnnotateSymbol) {
                    //被注释时
                    currentStr = StringUtils.removeStart(currentStr, annotateSymbol);
                }

                //key: e.g:  :method  Accept-Encoding
                boolean startWithSeparator = StringUtils.startsWith(currentStr, separator);
                if (startWithSeparator && StringUtils.length(currentStr) == separatorLen) {
                    //只存在分割符时
                    continue;
                }

                if (startWithSeparator) {
                    key = separator + StringUtils.substringBefore(currentStr.substring(separatorLen), separator);
                } else {
                    key = StringUtils.substringBefore(currentStr, separator);
                }

                if (count == (startWithSeparator ? 2 : 1) && StringUtils.endsWith(currentStr, separator)) {
                    //数量匹配并以分割符号结尾时 -- 值和key不在同一行
                    isWillContainsKey = false;
                } else {
                    value = StringUtils.substringAfter(currentStr, key + separator);
                    //需要设置值
                    isToAddValue = true;
                }
            } else {
                //当前是value时
                value = result;
                //需要设置值
                isToAddValue = true;
                //下一个包含key
                isWillContainsKey = true;
            }

            if (isToAddValue) {
                isToAddValue = false;
                if (isStartWithAnnotateSymbol) {
                    //是被注释时
                    continue;
                }

                if (continueKeySet.contains(key)) {
                    //是被跳过的key时
                    continue;
                }

                if (removeValueFirstBlankSpace) {
                    value = StringUtils.removeStart(value, " ");
                }

                List<String> valueList = resultMap.get(key);
                if (valueList == null) {
                    valueList = new ArrayList<>(4);
                    resultMap.put(key, valueList);
                }
                valueList.add(value);
            }
        }
        return resultMap;
    }

    /**
     * 转换成简单的key value map
     *
     * @param dataMap
     * @param takeFirstValue 是否取第一个值
     * @return
     */
    public static Map<String, String> transferSimplifyStringMap(final Map<String, List<String>> dataMap, boolean takeFirstValue) {
        if (dataMap == null || dataMap.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> resultMap = new LinkedHashMap<>(32);
        for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            List<String> valueList = entry.getValue();
            String value;
            if (valueList == null || valueList.isEmpty()) {
                value = null;
            } else {
                if (takeFirstValue) {
                    value = valueList.get(0);
                } else {
                    value = valueList.get(valueList.size() - 1);
                }
            }
            resultMap.put(key, value);
        }
        return resultMap;
    }
}
