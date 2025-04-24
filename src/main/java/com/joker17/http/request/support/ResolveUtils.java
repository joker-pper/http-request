package com.joker17.http.request.support;

import com.joker17.http.request.config.BaseRequestConfig;
import com.joker17.http.request.core.HttpConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
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
     * @param url    url 不能为null  e.g: http://localhost  http://localhost?   http://localhost?a=1 http://localhost?a=1&  http://localhost?a=1&b=2
     * @param params 参数 可为null e.g: username=xxx&step=2  ?username=xxx&step=2 &username=xxx&step=2
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


    /**
     * 获取修正后的api前缀 （移除后面多余的/，如有多个也会被移除多次）,如果最终为空白字符将返回空字符串
     *
     * @param apiPrefix api前缀  e.g: https://localhost  https://localhost:8080 https://localhost:8080/ https://localhost:8080/user
     * @return
     */
    public static String getCorrectedApiPrefix(String apiPrefix) {
        if (StringUtils.isBlank(apiPrefix)) {
            return "";
        }

        String result = apiPrefix;
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result.trim();

    }

    /**
     * 获取修正后的url
     *
     * @param apiPrefix api前缀  e.g: https://localhost  https://localhost:8080 https://localhost:8080/ https://localhost:8080/user
     * @param apiPath   api路径 e.g /api/v1/list
     * @return
     */
    public static String getCorrectedUrl(String apiPrefix, String apiPath) {
        ArgsUtils.notBlank(apiPrefix, "apiPrefix must be not blank");
        ArgsUtils.notBlank(apiPath, "apiPath must be not blank");
        String fixApiPrefix = getCorrectedApiPrefix(apiPrefix);
        ArgsUtils.notBlank(fixApiPrefix, "fixApiPrefix must be not blank");
        String fixApiPath = StringUtils.startsWith(apiPath, "/") ? apiPath : "/" + apiPath;
        return fixApiPrefix + fixApiPath;
    }

    /**
     * copy
     *
     * @param in
     * @param out
     * @return
     * @throws IOException
     */
    public static int copy(Reader in, Writer out) throws IOException {
        ArgsUtils.notNull(in, "No Reader specified");
        ArgsUtils.notNull(out, "No Writer specified");
        try {
            int byteCount = 0;
            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return byteCount;
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                //ignore
            }
            try {
                out.close();
            } catch (IOException ex) {
                //ignore
            }
        }
    }

    /**
     * copy
     *
     * @param in
     * @param out
     * @return
     * @throws IOException
     */
    public static int copy(InputStream in, OutputStream out) throws IOException {
        ArgsUtils.notNull(in, "No InputStream specified");
        ArgsUtils.notNull(out, "No OutputStream specified");
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
                //ignore
            }

            try {
                out.close();
            } catch (IOException e) {
                //ignore
            }

        }
    }

    /**
     * copy
     *
     * @param in
     * @param out
     * @throws IOException
     */
    public static void copy(byte[] in, OutputStream out) throws IOException {
        ArgsUtils.notNull(in, "No input byte array specified");
        ArgsUtils.notNull(out, "No OutputStream specified");
        try {
            out.write(in);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                //ignore
            }

        }
    }

    /**
     * copyToByteArray
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static byte[] copyToByteArray(InputStream in) throws IOException {
        if (in == null) {
            return new byte[0];
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
            copy(in, out);
            return out.toByteArray();
        }
    }

    public static String toString(final HttpEntity entity, final Charset defaultCharset) throws IOException, ParseException {
        return EntityUtils.toString(entity, defaultCharset);
    }

    public static String toString(final InputStream inStream, final long contentLength, final ContentType contentType) throws IOException {
        return toString(inStream, contentLength, getCharset(contentType));
    }


    /**
     * toString
     *
     * @param inStream
     * @param contentLength 小于0时会为默认buffer size，注意：不能过大
     * @param charset       字符集
     * @return
     * @throws IOException
     */
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

    /**
     * 格式化url params (注：不使用通过UrlEncoded的方式进行格式化一般是已经确认无误的要使用的数据，无需再次UrlEncoded)
     *
     * @param params               参数列表
     * @param charset              编码
     * @param formatWithUrlEncoded 是否通过UrlEncoded的方式进行格式化
     * @return
     */
    public static String formatUrlParams(final List<NameValuePair> params, final Charset charset, boolean formatWithUrlEncoded) throws IOException {
        if (params == null || params.isEmpty()) {
            return "";
        }

        if (formatWithUrlEncoded) {
            return EntityUtils.toString(new UrlEncodedFormEntity(params, charset));
        }

        StringBuilder builder = new StringBuilder();
        for (NameValuePair nameValuePair : params) {
            builder.append(nameValuePair.getName());
            if (nameValuePair.getValue() != null) {
                //存在值时 进行拼接 =xxx
                builder.append(HttpConstants.EQUAL_SIGN_STR).append(nameValuePair.getValue());
            }
            //拼接 &
            builder.append(HttpConstants.QP_SEP_A_CHAR);
        }

        int len = builder.length();
        if (len > 0 && builder.charAt(len - 1) == HttpConstants.QP_SEP_A_CHAR) {
            builder.deleteCharAt(len - 1);
        }

        return builder.toString();
    }

    /**
     * 当拼接参数时，将params进行放到url上，并返回URI对象 （默认使用UrlEncoded的方式格式化参数）
     *
     * @param url          url
     * @param params       参数列表
     * @param appendParams 是否拼接参数
     * @param charset      编码
     * @return
     */
    public static URI getURI(String url, List<NameValuePair> params, boolean appendParams, Charset charset) {
        return getURI(url, params, appendParams, true, charset);
    }

    /**
     * 当拼接参数时，将params进行放到url上，并返回URI对象 （可控制是否使用UrlEncoded的方式格式化参数）
     *
     * @param url                        url
     * @param params                     参数列表
     * @param appendParams               是否拼接参数
     * @param paramsFormatWithUrlEncoded 参数列表格式化时是否通过UrlEncoded的方式
     * @param charset                    编码
     * @return
     */
    public static URI getURI(String url, List<NameValuePair> params, boolean appendParams, boolean paramsFormatWithUrlEncoded, Charset charset) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("uri must not be empty.");
        }
        if (appendParams && params != null && !params.isEmpty()) {
            try {
                String formParamsStr = formatUrlParams(params, charset, paramsFormatWithUrlEncoded);
                url = resolveUrl(url, formParamsStr);
            } catch (IOException e) {
                //ignore
            }
        }
        return URI.create(url);
    }

    /**
     * 获取表单参数列表 （结果一定不为null，但数据不存在时会为Collections.emptyList()）
     *
     * @param requestConfig
     * @param <T>
     * @return
     */
    public static <T> List<NameValuePair> getFormParamList(BaseRequestConfig<T> requestConfig) {
        return getNameValuePairList(requestConfig != null ? requestConfig.getFormParameterMap() : null);
    }

    /**
     * 获取查询参数列表 （结果一定不为null，但数据不存在时会为Collections.emptyList()）
     *
     * @param requestConfig
     * @param <T>
     * @return
     */
    public static <T> List<NameValuePair> getQueryParamList(BaseRequestConfig<T> requestConfig) {
        return getNameValuePairList(requestConfig != null ? requestConfig.getQueryParameterMap() : null);
    }

    /**
     * 转换参数map为nameValuePairList （结果一定不为null，但数据不存在时会为Collections.emptyList()）
     *
     * @param parameterMap 参数map
     * @return 不为null的结果列表
     */
    public static List<NameValuePair> getNameValuePairList(Map<String, List<String>> parameterMap) {
        if (parameterMap == null || parameterMap.isEmpty()) {
            return Collections.emptyList();
        }

        List<NameValuePair> resultList = new ArrayList<>(16);

        for (Map.Entry<String, List<String>> entry : parameterMap.entrySet()) {
            String paramKey = entry.getKey();
            List<String> paramValueList = entry.getValue();
            if (paramValueList != null) {
                for (String paramValue : paramValueList) {
                    resultList.add(new BasicNameValuePair(paramKey, paramValue));
                }
            }
        }

        return resultList;
    }

    /**
     * 获取cookieStore
     *
     * @param httpClient
     * @return
     */
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
     * 获取contentType （若未从entity中解析出结果，则兜底为text/plain和默认字符集的结果）
     *
     * @param entity
     * @param defaultCharset
     * @return
     * @throws IOException
     * @throws ParseException
     */
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

    /**
     * 获取字符集
     *
     * @param contentType
     * @return
     */
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

    /**
     * 解析contentLength值
     *
     * @param contentLengthHeader
     * @return
     */
    public static Long getContentLength(Header contentLengthHeader) {
        if (contentLengthHeader == null) {
            return null;
        }

        HeaderElement[] elements = contentLengthHeader.getElements();
        if (elements == null) {
            return null;
        }

        for (HeaderElement element : elements) {
            String name = element.getName();
            if (name != null && name.length() > 0) {
                try {
                    return Long.parseLong(element.getName());
                } catch (NumberFormatException e) {
                    //ignore
                }
            }
        }
        return null;
    }

    /**
     * 解析contentEncoding值
     *
     * @param contentEncodingHeader
     * @return
     */
    public static String getContentEncoding(Header contentEncodingHeader) {
        if (contentEncodingHeader == null) {
            return null;
        }

        HeaderElement[] elements = contentEncodingHeader.getElements();
        if (elements == null) {
            return null;
        }

        for (HeaderElement element : elements) {
            String name = element.getName();
            if (name != null && name.length() > 0) {
                return name;
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
     * 解析多组数据 （支持注释数据，及key对应多个参数值）
     *
     * @param data                       支持postman bulk edit data / chrome复制的请求头内容
     *
     *                                   <p>
     *                                   e.g:
     *                                   key1:value1  【常规，推荐】
     *                                   <p>
     *                                   key2:\\n
     *                                   value2   【换行】
     *                                   <p>
     *                                   //key3:value3  【注释】
     * @param separator                  分割符, 常规':', 也可进行自定义
     * @param removeValueFirstBlankSpace 是否移除value的第一个空格
     * @param continueKeys               跳过keys，将不解析对应key的数据到结果中
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

    /**
     * 简单解析模板进行获取替换参数后的结果
     *
     * @param template 模板  （参数占位符为 ${key}，即参数map中数据的key名）
     * @param paramMap 参数map map的value支持各种类型（为了支持int/long等无需转换使用Object），但推荐值是String，如果值为null也会被替换为null
     * @return
     */
    public static String parseByTemplate(String template, Map<String, ?> paramMap) {
        String result = template;
        if (paramMap != null && !paramMap.isEmpty()) {
            //进行替换参数map中的变量值
            for (Map.Entry<String, ?> paramMapEntry : paramMap.entrySet()) {
                String key = paramMapEntry.getKey();
                Object value = paramMapEntry.getValue();
                //将 ${key} 替换为对应的值
                result = StringUtils.replace(result, String.format("${%s}", key), Objects.toString(value));
            }
        }
        return result;
    }
}
