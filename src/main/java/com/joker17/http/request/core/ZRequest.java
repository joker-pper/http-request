package com.joker17.http.request.core;

import com.joker17.http.request.config.*;
import com.joker17.http.request.support.ResolveUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(staticName = "of")
public class ZRequest {

    private CloseableHttpClient httpClient = HttpClients.createDefault();

    private CookieStore cookieStore = null;

    public PResponse doGet(GetRequestConfig requestConfig) throws IOException {
        HttpGet httpGet = new HttpGet();
        resolveBaseRequestConfig(httpGet, requestConfig);
        return getPResponse(httpGet);
    }

    public PResponse doPost(PostRequestConfig requestConfig) throws IOException {
        HttpPost httpPost = new HttpPost();
        resolveRequestBodyAndFileConfig(httpPost, requestConfig);
        return getPResponse(httpPost);
    }

    public PResponse doPut(PutRequestConfig requestConfig) throws IOException {
        HttpPut httpPut = new HttpPut();
        resolveRequestBodyAndFileConfig(httpPut, requestConfig);
        return getPResponse(httpPut);
    }

    public PResponse doPatch(PatchRequestConfig requestConfig) throws IOException {
        HttpPatch httpPatch = new HttpPatch();
        resolveRequestBodyAndFileConfig(httpPatch, requestConfig);
        return getPResponse(httpPatch);
    }

    public PResponse doDelete(DeleteRequestConfig requestConfig) throws IOException  {
        HttpDelete httpDelete = new HttpDelete();
        resolveRequestBodyConfig(httpDelete, requestConfig);
        return getPResponse(httpDelete);
    }

    protected void resolveBaseRequestConfig(HttpRequestBase requestBase, BaseRequestConfig requestConfig) {
        String url = requestConfig.getUrl();
        ContentType requestContentType = requestConfig.getContentType();
        Charset requestCharset = requestContentType.getCharset();

        List<NameValuePair> queryParamList = ResolveUtils.getQueryParamList(requestConfig);
        //设置请求url
        requestBase.setURI(ResolveUtils.getURI(url, queryParamList, true, requestCharset));
        //设置请求头和超时时间
        setHeaderAndTimeoutRequestConfig(requestBase, requestConfig);
    }

    protected void resolveRequestBodyConfig(HttpEntityEnclosingRequestBase requestBase, RequestBodyConfig requestConfig) {
        String requestBody = requestConfig.getRequestBody();
        ContentType requestContentType = requestConfig.getContentType();
        Charset requestCharset = requestContentType.getCharset();

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

    protected void resolveRequestBodyAndFileConfig(HttpEntityEnclosingRequestBase requestBase, RequestBodyAndFileConfig requestConfig) {
        Map<String, List<String>> formParameterMap = requestConfig.getFormParameterMap();
        String requestBody = requestConfig.getRequestBody();
        Map<String, List<File>> fileParameterMap = requestConfig.getFileParameterMap();

        boolean hasRequestBody = requestBody != null;

        boolean hasFormParameter = !formParameterMap.isEmpty();
        boolean hasFileParameterMap = !fileParameterMap.isEmpty();

        ContentType requestContentType = requestConfig.getContentType();
        Charset requestCharset = requestContentType.getCharset();

        resolveBaseRequestConfig(requestBase, requestConfig);

        if (!hasFileParameterMap && !hasRequestBody) {
            List<NameValuePair> formParamList = ResolveUtils.getFormParamList(requestConfig);
            if (!formParamList.isEmpty()) {
                UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParamList, requestCharset);
                requestBase.setEntity(uefEntity);
            }
        } else {
            if (hasRequestBody) {
                StringEntity stringEntity = new StringEntity(requestBody, requestCharset);
                requestBase.setEntity(stringEntity);
            } else {
                if (hasFileParameterMap) {
                    requestBase.removeHeaders("Content-Type");
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    for (String fileKey : fileParameterMap.keySet()) {
                        List<File> fileList = fileParameterMap.get(fileKey);
                        for (File file : fileList) {
                            //相当于<input type="file" name="fileKey"/>
                            FileBody fileBody = new FileBody(file);
                            builder.addPart(fileKey, fileBody);
                        }
                    }
                    if (hasFormParameter) {
                        for (String paramKey : formParameterMap.keySet()) {
                            List<String> paramValueList = formParameterMap.get(paramKey);
                            for (String paramValue : paramValueList) {
                                //相当于<input type="text" name="paramKey"/>
                                StringBody stringBody = new StringBody(paramValue, ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), requestCharset));
                                builder.addPart(paramKey, stringBody);
                            }
                        }
                    }
                    requestBase.setEntity(builder.build());
                }
            }
        }
    }

    protected void setHeaderAndTimeoutRequestConfig(HttpRequestBase requestBase, BaseRequestConfig requestConfig) {
        requestBase.setConfig(getTimeoutRequestConfig(requestConfig.getSocketTimeout(), requestConfig.getConnectTimeout()));
        //设置请求头
        List<String> headerKeyList = requestConfig.getHeaderKeyList();
        List<String> headerValueList = requestConfig.getHeaderValueList();
        if (!headerKeyList.isEmpty()) {
            for (int i = 0; i < headerKeyList.size(); i++) {
                requestBase.addHeader(new BasicHeader(headerKeyList.get(i), headerValueList.get(i)));
            }
        }
        requestBase.setHeader("Content-Type", requestConfig.getContentType().toString());
    }

    protected RequestConfig getTimeoutRequestConfig(int socketTimeout, int connectTimeout) {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
        return requestConfig;
    }


    protected PResponse getPResponse(HttpUriRequest request) throws IOException {
        CloseableHttpResponse response = null;
        PResponse presponse = new PResponse();
        try {
            response = httpClient.execute(request);

            presponse.setUri(request.getURI());
            presponse.setStatusCode(response.getStatusLine().getStatusCode());
            presponse.setHttpResponse(response);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                presponse.setEntity(entity);
                presponse.setBody(ResolveUtils.copyToByteArray(entity.getContent()));

                Header contentType = entity.getContentType();
                Header contentEncoding = entity.getContentEncoding();

                if (contentType != null) {
                    presponse.setContentType(ContentType.get(entity));
                }
                if (contentEncoding != null) {
                    presponse.setContentEncoding(contentEncoding.toString());
                }

                presponse.setContentLength(entity.getContentLength());
                presponse.setChunked(entity.isChunked());
                presponse.setRepeatable(entity.isRepeatable());
                presponse.setStreaming(entity.isStreaming());
            }

        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
            }
        }
        return presponse;
    }

    public CookieStore getCookieStore() {
        if (cookieStore == null) {
            cookieStore = ResolveUtils.getCookieStore(httpClient);
        }
        return cookieStore;
    }

}
