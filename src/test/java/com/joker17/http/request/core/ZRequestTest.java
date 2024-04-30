package com.joker17.http.request.core;

import com.joker17.http.request.config.GetRequestConfig;
import com.joker17.http.request.config.HeadRequestConfig;
import com.joker17.http.request.config.PostRequestConfig;
import com.joker17.http.request.config.RequestConfigCallback;
import com.joker17.http.request.support.HttpClientSupportConfigCallback;
import com.joker17.http.request.support.HttpClientUtils;
import com.joker17.http.request.support.ResolveUtils;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZRequestTest {

    private String userDir = System.getProperty("user.dir");

    @BeforeClass
    public static void beforeClass() {

        HttpClientUtils.setDefaultRequestConfig(RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build());

        HttpClientUtils.setDefaultHttpClientSupportConfigCallback(new HttpClientSupportConfigCallback() {

            @Override
            public void executeDefaultHttp(HttpClientBuilder builder) {
                builder.setUserAgent(HttpConstants.USER_AGENTS[2]);
                builder.setMaxConnTotal(500);
                builder.setMaxConnPerRoute(60);
            }

            @Override
            public void executeIgnoreVerifySSLHttp(HttpClientBuilder builder) {
                builder.setUserAgent(HttpConstants.USER_AGENTS[2]);
                builder.setMaxConnTotal(500);
                builder.setMaxConnPerRoute(60);
            }
        });

    }

    @Test
    public void testConfig() throws IOException {
        System.out.println(GetRequestConfig.of().setContentType(HttpConstants.APPLICATION_JSON)
                .setUrl("https://baidu.com").addHeader("token", "123456")
                .addQueryParameter("key1", "key1.1", "key1.2"));

        System.out.println(PostRequestConfig.of().setUrl("https://baidu.com").setRequestBody("{\"username\": \"test\"}"));
        System.out.println(PostRequestConfig.of().setUrl("https://baidu.com").addFile(new File("")));

        Map<String, List<String>> parameterMap1 = new HashMap<>();
        Map<String, List<Object>> parameterMap2 = new HashMap<>();

        String[] parameterNames = new String[]{"1", "2"};
        Object[] parameterValues1 = new Object[]{"111", 222};
        String[] parameterValues2 = new String[]{"222", "333"};

        System.out.println(PostRequestConfig.of().setUrl("https://baidu.com")
                .setFormParameters(parameterMap1)
                .setFormParameters(parameterMap2)
                .addFormParameters(parameterMap1)
                .addFormParameters(parameterMap2)

                .setFormParameters(parameterNames, parameterValues1)
                .setFormParameters(parameterNames, parameterValues2)
                .addFormParameters(parameterNames, parameterValues1)
                .addFormParameters(parameterNames, parameterValues2)

                .setQueryParameters(parameterMap1)
                .setQueryParameters(parameterMap2)
                .addQueryParameters(parameterMap1)
                .addQueryParameters(parameterMap2)

                .setQueryParameters(parameterNames, parameterValues1)
                .setQueryParameters(parameterNames, parameterValues2)
                .addQueryParameters(parameterNames, parameterValues1)
                .addQueryParameters(parameterNames, parameterValues2)

                .addFile(new File("")));
    }

    @Test
    public void testConfigWithCallback() {
        System.out.println(PostRequestConfig.of().setUrl("https://baidu.com").setConfigCallback(new RequestConfigCallback<RequestConfig.Builder>() {
            @Override
            public void execute(RequestConfig.Builder builder) {
                builder.setCircularRedirectsAllowed(true).setMaxRedirects(10);
            }
        }));

    }

    @Test
    public void test() throws IOException {
        ZRequest request = ZRequest.of();
        PResponse response = request.doGet(GetRequestConfig.of().setContentType("text/plain").setUrl("http://www.baidu.com")
                .addQueryParameter("key", 1, 2, 3));
        System.out.println(response);
        System.out.println(response.getText(HttpConstants.UTF_8));
        System.out.println(request.getCookieStore());

        System.out.println(response.getHttpResponse());
        for (Header header : response.getHttpResponse().getAllHeaders()) {
            System.out.println(String.format("[Header] %s: %s", header.getName(), header.getValue()));
        }

        //清理资源
        response.clear();
    }

    @Test
    public void testDownloadWithRange() throws IOException {

        ZRequest request = ZRequest.of();

        String url = "https://dl.360safe.com/360zip_setup_4.0.0.1200.exe";

        PResponse headResponse = request.doHead(HeadRequestConfig.of().setUrl(url));

        long contentLength = headResponse.getContentLength();

        System.out.println("=======================================================");

        for (Header header : headResponse.getHttpResponse().getAllHeaders()) {
            System.out.println(String.format("[Header] %s: %s", header.getName(), header.getValue()));
        }

        System.out.println("=======================================================");

        String contentDisposition = null;
        try {
            contentDisposition = headResponse.getHttpResponse().getLastHeader("Content-Disposition").getValue();
        } catch (Exception e) {
        }

        String filename = ResolveUtils.getFilenameByContentDisposition(contentDisposition, ResolveUtils.getFilenameByUrl(url));

        System.out.println(String.format("[file name]: %s ", filename));
        System.out.println("=======================================================");

        //清理资源
        headResponse.clear();

        int count = contentLength == -1 || contentLength <= 1024 * 10 ? 1 : 4;
        long avgLength = contentLength / count;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        for (int i = 0; i < count; i++) {
            long start;
            long end;

            if (i == 0) {
                start = 0;
            } else {
                start = i * avgLength;
            }

            if (i != count - 1) {
                end = (i + 1) * avgLength - 1;
            } else {
                end = contentLength - 1;
            }

            PResponse response = request.doGet(GetRequestConfig.of().setContentType((ContentType) null).setUrl(url)
                    .addHeader("Range", String.format("bytes=%s-%s", start, end)));

            for (Header header : response.getHttpResponse().getAllHeaders()) {
                System.out.println(String.format("[Header] %s: %s", header.getName(), header.getValue()));
            }

            int statusCode = response.getStatusCode();
            if (statusCode == 200) {
                System.out.println("=======================================================");
                System.err.println("当前资源不支持分片");
                System.out.println("=======================================================");
            } else {
                System.out.println("=======================================================");
            }

            byteArrayOutputStream.write(response.getBody());
            byteArrayOutputStream.flush();

            //清理资源
            response.clear();
            if (statusCode == 200) {
                break;
            }
        }


        final File file = buildDownloadFile(filename);
        ResolveUtils.copy(byteArrayOutputStream.toByteArray(), new FileOutputStream(file));

        System.out.println(String.format("[file name]: %s download.", filename));
    }

    @Test
    public void testDownloadBigFileWithRange() throws IOException {

        ZRequest request = ZRequest.of();

        //String url = "https://download.jetbrains.com.cn/idea/ideaIC-2023.1.2.dmg";
        String url = "https://dl.360safe.com/360zip_setup_4.0.0.1200.exe";

        PResponse headResponse = request.doHead(HeadRequestConfig.of().setUrl(url));

        long contentLength = headResponse.getContentLength();

        System.out.println("=======================================================");

        for (Header header : headResponse.getHttpResponse().getAllHeaders()) {
            System.out.println(String.format("[Header] %s: %s", header.getName(), header.getValue()));
        }

        System.out.println("=======================================================");

        String contentDisposition = null;
        try {
            contentDisposition = headResponse.getHttpResponse().getLastHeader("Content-Disposition").getValue();
        } catch (Exception e) {
        }

        String filename = ResolveUtils.getFilenameByContentDisposition(contentDisposition, ResolveUtils.getFilenameByUrl(url));

        System.out.println(String.format("[file name]: %s ", filename));
        System.out.println("=======================================================");

        final File file = buildDownloadFile(filename);

        int onceContentMinLength = 1024 * 1024;

        int count;
        if (contentLength == -1 || contentLength <= onceContentMinLength) {
            count = 1;
        } else {
            int appointTimesContentLimitedLength = 1024 * 1024 * 100;
            if (contentLength <= appointTimesContentLimitedLength) {
                //小于等于100M时分3次
                count = 3;
            } else {
                //每次50M
                int onceContentMaxLength = 1024 * 1024 * 50;
                count = (int) (contentLength / onceContentMaxLength);
            }
        }

        long avgLength = contentLength / count;

        for (int i = 0; i < count; i++) {
            long start;
            long end;

            if (i == 0) {
                start = 0;
            } else {
                start = i * avgLength;
            }

            if (i != count - 1) {
                end = (i + 1) * avgLength - 1;
            } else {
                end = contentLength - 1;
            }

            PResponseHandler<PResponse, GetRequestConfig> handler = new PResponseHandler<PResponse, GetRequestConfig>() {
                @Override
                public void beforeExecute(HttpRequestBase request, GetRequestConfig requestConfig) {
                }

                @Override
                public PResponse handleResponse(HttpRequestBase request, PResponse response) throws IOException {
                    response.writeTo(new FileOutputStream(file, true));
                    return response;
                }
            };
            PResponse response = request.doGet(GetRequestConfig.of().setContentType((ContentType) null).setUrl(url)
                    .addHeader("Range", String.format("bytes=%s-%s", start, end)), handler);

            for (Header header : response.getHttpResponse().getAllHeaders()) {
                System.out.println(String.format("[Header] %s: %s", header.getName(), header.getValue()));
            }

            int statusCode = response.getStatusCode();
            if (statusCode == 200) {
                System.out.println("=======================================================");
                System.err.println("当前资源不支持分片");
                System.out.println("=======================================================");
                System.out.println(String.format("[file name]: %s download.", filename));
                break;
            } else {
                System.out.println("=======================================================");
            }

            //清理资源
            response.clear();
        }

        System.out.println(String.format("[file name]: %s download.", filename));

    }

    private File buildDownloadFile(String filename) {
        File file = new File(String.format("%s/src/test/download/%s", userDir, filename));
        if (!file.getParentFile().isDirectory()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }

}