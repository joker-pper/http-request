package com.joker17.http.request.core;

import com.joker17.http.request.config.*;
import com.joker17.http.request.support.HttpClientUtils;
import com.joker17.http.request.support.ResolveUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class ZRequest {

    private CloseableHttpClient httpClient;
    private CookieStore cookieStore;

    private ZRequest(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * 自定义httpClient
     *
     * @param httpClient
     * @return
     */
    public static ZRequest of(CloseableHttpClient httpClient) {
        return new ZRequest(httpClient);
    }

    /**
     * 获取ZRequest对象
     *
     * @param isIgnoreVerifySSL 是否忽略验证ssl
     * @return
     */
    public static ZRequest of(boolean isIgnoreVerifySSL) {
        if (isIgnoreVerifySSL) {
            return of(HttpClientUtils.getIgnoreVerifySSLHttpClient());
        }
        return of(HttpClientUtils.getDefaultHttpClient());
    }

    /**
     * 获取ZRequest对象
     *
     * @return
     */
    public static ZRequest of() {
        return of(true);
    }

    /**
     * head请求
     *
     * @param requestConfig
     * @return
     * @throws IOException
     */
    public PResponse doHead(HeadRequestConfig requestConfig) throws IOException {
        return doHead(requestConfig, null);
    }

    /**
     * head请求
     *
     * @param requestConfig
     * @param handler
     * @param <P>
     * @param <Z>
     * @return
     * @throws IOException
     */
    public <P, Z extends HeadRequestConfig> P doHead(Z requestConfig, PResponseHandler<P, Z> handler) throws IOException {
        HttpHead httpHead = new HttpHead();
        resolveBaseRequestConfig(httpHead, requestConfig);
        return resolveResponse(httpHead, requestConfig, handler);
    }

    /**
     * get请求
     *
     * @param requestConfig
     * @return
     * @throws IOException
     */
    public PResponse doGet(GetRequestConfig requestConfig) throws IOException {
        return doGet(requestConfig, null);
    }

    /**
     * get请求
     *
     * @param requestConfig
     * @param handler
     * @param <P>
     * @param <Z>
     * @return
     * @throws IOException
     */
    public <P, Z extends GetRequestConfig> P doGet(Z requestConfig, PResponseHandler<P, Z> handler) throws IOException {
        HttpGet httpGet = new HttpGet();
        resolveBaseRequestConfig(httpGet, requestConfig);
        return resolveResponse(httpGet, requestConfig, handler);
    }

    /**
     * post请求
     *
     * @param requestConfig
     * @return
     * @throws IOException
     */
    public PResponse doPost(PostRequestConfig requestConfig) throws IOException {
        return doPost(requestConfig, null);
    }

    /**
     * post请求
     *
     * @param requestConfig
     * @param handler
     * @param <P>
     * @param <Z>
     * @return
     * @throws IOException
     */
    public <P, Z extends PostRequestConfig> P doPost(Z requestConfig, PResponseHandler<P, Z> handler) throws IOException {
        HttpPost httpPost = new HttpPost();
        resolveRequestBodyAndFileConfig(httpPost, requestConfig);
        return resolveResponse(httpPost, requestConfig, handler);
    }

    public PResponse doPut(PutRequestConfig requestConfig) throws IOException {
        return doPut(requestConfig, null);
    }

    public <P, Z extends PutRequestConfig> P doPut(Z requestConfig, PResponseHandler<P, Z> handler) throws IOException {
        HttpPut httpPut = new HttpPut();
        resolveRequestBodyAndFileConfig(httpPut, requestConfig);
        return resolveResponse(httpPut, requestConfig, handler);
    }

    public PResponse doPatch(PatchRequestConfig requestConfig) throws IOException {
        return doPatch(requestConfig, null);
    }

    public <P, Z extends PatchRequestConfig> P doPatch(Z requestConfig, PResponseHandler<P, Z> handler) throws IOException {
        HttpPatch httpPatch = new HttpPatch();
        resolveRequestBodyAndFileConfig(httpPatch, requestConfig);
        return resolveResponse(httpPatch, requestConfig, handler);
    }

    public PResponse doDelete(DeleteRequestConfig requestConfig) throws IOException {
        return doDelete(requestConfig, null);
    }

    public <P, Z extends DeleteRequestConfig> P doDelete(Z requestConfig, PResponseHandler<P, Z> handler) throws IOException {
        HttpDelete httpDelete = new HttpDelete();
        resolveRequestBodyAndFileConfig(httpDelete, requestConfig);
        return resolveResponse(httpDelete, requestConfig, handler);
    }

    /**
     * 获取request字符集
     *
     * @param requestConfig
     * @param defaultCharset
     * @return
     */
    protected <T> Charset getRequestCharset(BaseRequestConfig<T> requestConfig, Charset defaultCharset) {
        Charset requestCharset = requestConfig.getCharset();
        if (requestCharset != null) {
            return requestCharset;
        }
        Charset requestContentTypeCharset = ResolveUtils.getCharset(requestConfig.getContentType());
        return requestContentTypeCharset != null ? requestContentTypeCharset : defaultCharset;
    }

    /**
     * 进行设置基本请求信息配置
     *
     * @param requestBase
     * @param requestConfig
     */
    protected <T> void resolveBaseRequestConfig(HttpRequestBase requestBase, BaseRequestConfig<T> requestConfig) {
        String url = requestConfig.getUrl();
        Charset requestCharset = getRequestCharset(requestConfig, HttpConstants.UTF_8);
        List<NameValuePair> queryParamList = ResolveUtils.getQueryParamList(requestConfig);
        //设置请求url
        requestBase.setURI(ResolveUtils.getURI(url, queryParamList, true, requestCharset));
        //设置请求头和超时时间等
        setHeaderAndRequestConfig(requestBase, requestConfig);
    }

    protected <T> void resolveRequestBodyConfig(HttpEntityEnclosingRequestBase requestBase, RequestBodyConfig<T> requestConfig) {
        String requestBody = requestConfig.getRequestBody();
        Charset requestCharset = getRequestCharset(requestConfig, HttpConstants.UTF_8);
        boolean hasRequestBody = requestBody != null;
        resolveBaseRequestConfig(requestBase, requestConfig);
        if (!hasRequestBody) {
            List<NameValuePair> formParamList = ResolveUtils.getFormParamList(requestConfig);
            if (!formParamList.isEmpty()) {
                UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParamList, requestCharset);
                requestBase.setEntity(uefEntity);
            }
        } else {
            StringEntity stringEntity = new StringEntity(requestBody, requestCharset);
            requestBase.setEntity(stringEntity);
        }
    }

    protected <T> void resolveRequestBodyAndFileConfig(HttpEntityEnclosingRequestBase requestBase, RequestBodyAndFileConfig<T> requestConfig) {
        Map<String, List<File>> fileParameterMap = requestConfig.getFileParameterMap();
        boolean hasFileParameterMap = !fileParameterMap.isEmpty();
        String requestBody = requestConfig.getRequestBody();
        boolean hasRequestBody = requestBody != null;
        if (hasRequestBody || !hasFileParameterMap) {
            //存在request body 或 不存在file参数时
            resolveRequestBodyConfig(requestBase, requestConfig);
            return;
        }

        resolveBaseRequestConfig(requestBase, requestConfig);
        Charset requestCharset = getRequestCharset(requestConfig, HttpConstants.UTF_8);

        //移除Content-Type header
        requestBase.removeHeaders(HttpHeaders.CONTENT_TYPE);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(requestCharset);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        for (Map.Entry<String, List<File>> entry : fileParameterMap.entrySet()) {
            String fileKey = entry.getKey();
            List<File> fileList = entry.getValue();
            for (File file : fileList) {
                //相当于<input type="file" name="fileKey"/>
                FileBody fileBody = new FileBody(file);
                builder.addPart(fileKey, fileBody);
            }
        }

        Map<String, List<String>> formParameterMap = requestConfig.getFormParameterMap();
        boolean hasFormParameter = !formParameterMap.isEmpty();

        if (hasFormParameter) {
            for (Map.Entry<String, List<String>> entry : formParameterMap.entrySet()) {
                String paramKey = entry.getKey();
                List<String> paramValueList = entry.getValue();
                for (String paramValue : paramValueList) {
                    //相当于<input type="text" name="paramKey"/>
                    StringBody stringBody = new StringBody(paramValue, ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), requestCharset));
                    builder.addPart(paramKey, stringBody);
                }
            }
        }

        requestBase.setEntity(builder.build());
    }

    /**
     * 设置请求头和requestConfig
     *
     * @param requestBase
     * @param requestConfig
     */
    protected <T> void setHeaderAndRequestConfig(HttpRequestBase requestBase, BaseRequestConfig<T> requestConfig) {
        //设置config
        requestBase.setConfig(getRequestConfig(requestConfig));
        //设置请求头
        ContentType contentType = requestConfig.getContentType();
        if (contentType != null) {
            requestBase.setHeader(HttpHeaders.CONTENT_TYPE, contentType.toString());
        }
        if (requestConfig.getUserAgent() != null) {
            requestBase.setHeader(HttpHeaders.USER_AGENT, requestConfig.getUserAgent());
        }

        Map<String, List<String>> headerParameterMap = requestConfig.getHeaderParameterMap();
        for (Map.Entry<String, List<String>> entry : headerParameterMap.entrySet()) {
            String headerKey = entry.getKey();
            List<String> headerValues = entry.getValue();
            for (String headerValue : headerValues) {
                requestBase.addHeader(new BasicHeader(headerKey, headerValue));
            }
        }
    }

    /**
     * 获取requestConfig
     *
     * @param baseRequestConfig
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected <T> RequestConfig getRequestConfig(BaseRequestConfig<T> baseRequestConfig) {
        RequestConfig defaultRequestConfig = HttpClientUtils.getDefaultRequestConfig();

        RequestConfig.Builder builder;
        if (defaultRequestConfig != null) {
            //支持从全局默认配置中copy
            builder = RequestConfig.copy(defaultRequestConfig);
        } else {
            builder = RequestConfig.custom();
        }

        //构建requestConfig
        buildRequestConfig(builder, baseRequestConfig);

        RequestConfigCallback configCallback = baseRequestConfig.getConfigCallback();
        if (configCallback != null) {
            configCallback.execute(builder);
        }

        return builder.build();
    }

    /**
     * 构建requestConfig
     *
     * @param builder
     * @param baseRequestConfig
     */
    protected <T> void buildRequestConfig(RequestConfig.Builder builder, BaseRequestConfig<T> baseRequestConfig) {

        //connectTimeout、socketTimeout、connectionRequestTimeout存在时进行赋值
        if (baseRequestConfig.getConnectTimeout() != null) {
            builder.setConnectTimeout(baseRequestConfig.getConnectTimeout());
        }

        if (baseRequestConfig.getSocketTimeout() != null) {
            builder.setSocketTimeout(baseRequestConfig.getSocketTimeout());
        }

        if (baseRequestConfig.getConnectionRequestTimeout() != null) {
            builder.setConnectionRequestTimeout(baseRequestConfig.getConnectionRequestTimeout());
        }

        //其他参数存在时进行赋值
        if (baseRequestConfig.getCookieSpec() != null) {
            builder.setCookieSpec(baseRequestConfig.getCookieSpec());
        }

        if (baseRequestConfig.getRedirectsEnabled() != null) {
            builder.setRedirectsEnabled(baseRequestConfig.getRedirectsEnabled());
        }

        if (baseRequestConfig.getMaxRedirects() != null) {
            builder.setMaxRedirects(baseRequestConfig.getMaxRedirects());
        }

    }

    protected <P, Z extends BaseRequestConfig<T>, T> P resolveResponse(final HttpRequestBase request, final Z requestConfig, PResponseHandler<P, Z> handler) throws IOException {
        CloseableHttpResponse response = null;
        PResponse presponse;
        try {
            boolean hasHandler = handler != null;
            if (hasHandler) {
                handler.beforeExecute(request, requestConfig);
            }

            response = httpClient.execute(request);

            presponse = new PResponse();
            presponse.setUri(request.getURI());
            presponse.setStatusCode(response.getStatusLine().getStatusCode());
            presponse.setHttpResponse(response);

            HttpEntity entity = response.getEntity();
            Header contentEncodingHeader = null;
            Long contentLength = null;
            if (entity != null) {
                presponse.setEntity(entity);
                if (!hasHandler) {
                    //不存在handler时默认设置body内容（此种方式会关闭流，提前放到body中，但不适合大文件的处理）
                    presponse.setBody(presponse.getBody());
                }

                Header contentTypeHeader = entity.getContentType();
                if (contentTypeHeader != null) {
                    presponse.setContentType(ContentType.get(entity));
                }
                contentEncodingHeader = entity.getContentEncoding();
                contentLength = entity.getContentLength();
                presponse.setChunked(entity.isChunked());
                presponse.setRepeatable(entity.isRepeatable());
                presponse.setStreaming(entity.isStreaming());
            } else {
                Header contentTypeHeader = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);

                if (contentTypeHeader != null) {
                    presponse.setContentType(ContentType.parse(contentTypeHeader.getValue()));
                }

                contentEncodingHeader = response.getFirstHeader(HttpHeaders.CONTENT_ENCODING);
                contentLength = ResolveUtils.getContentLength(response.getFirstHeader(HttpHeaders.CONTENT_LENGTH));
            }

            presponse.setContentEncoding(ResolveUtils.getContentEncoding(contentEncodingHeader));
            presponse.setContentLength(contentLength == null ? -1L : contentLength);
            presponse.setLocale(response.getLocale());

            if (hasHandler) {
                return handler.handleResponse(request, presponse);
            }
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                //ignore
            }
        }
        //不存在handler时默认返回PResponse对象
        return (P) presponse;
    }

    public CookieStore getCookieStore() {
        if (cookieStore == null) {
            cookieStore = ResolveUtils.getCookieStore(httpClient);
        }
        return cookieStore;
    }

    /**
     * 关闭
     *
     * @throws IOException
     */
    public void close() throws IOException {
        cookieStore = null;
        httpClient.close();
    }

}
