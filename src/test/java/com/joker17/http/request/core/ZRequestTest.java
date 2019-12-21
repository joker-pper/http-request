package com.joker17.http.request.core;

import com.joker17.http.request.config.*;
import com.joker17.http.request.support.ResolveUtils;
import org.apache.http.Header;
import org.apache.http.entity.ContentType;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class ZRequestTest {

    private String userDir = System.getProperty("user.dir");

    @Test
    public void testConfig() throws IOException {
        System.out.println(GetRequestConfig.of().setContentType(HttpConstants.APPLICATION_JSON)
                .setUrl("https://baidu.com").addHeader("token", "123456")
                .addQueryParameter("key1", "key1.1", "key1.2"));

        System.out.println(PostRequestConfig.of().setUrl("https://baidu.com").setRequestBody("{\"username\": \"test\"}"));
        System.out.println(PostRequestConfig.of().setUrl("https://baidu.com").addFile(new File("")));

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

    }


    @Test
    public void testRequestBody() throws IOException {
        String baseUrl = "http://localhost:8080/api/body/test";
        String requestBody = "{\"username\": \"test\"}";
        ZRequest request = ZRequest.of();
        PResponse response = request.doPost(PostRequestConfig.of().setUrl(baseUrl + "/post")
                .setRequestBody(requestBody));
        System.out.println(response);
        System.out.println(response.getText());

        response = request.doPut(PutRequestConfig.of().setUrl(baseUrl + "/put")
                .setRequestBody(requestBody));
        System.out.println(response);
        System.out.println(response.getText());

        response = request.doPatch(PatchRequestConfig.of().setUrl(baseUrl + "/patch")
                .setRequestBody(requestBody));
        System.out.println(response);
        System.out.println(response.getText());

        response = request.doDelete(DeleteRequestConfig.of().setUrl(baseUrl + "/delete")
                .setRequestBody(requestBody));
        System.out.println(response);
        System.out.println(response.getText());
    }


    @Test
    public void testFormParameter() throws IOException {
        String baseUrl = "http://localhost:8080/api/form/parameter/test";
        String body = "{\"username\": \"test\"}";
        ZRequest request = ZRequest.of();
        PResponse response = request.doPost(PostRequestConfig.of()
                .setContentType(HttpConstants.APPLICATION_FORM_URLENCODED)
                .setUrl(baseUrl + "/post")
                .setFormParameter("body", body));
        System.out.println(response);
        System.out.println(response.getText());

        response = request.doPut(PutRequestConfig.of()
                .setContentType(HttpConstants.APPLICATION_FORM_URLENCODED)
                .setUrl(baseUrl + "/put")
                .setFormParameter("body", body));
        System.out.println(response);
        System.out.println(response.getText());

        response = request.doPatch(PatchRequestConfig.of()
                .setContentType(HttpConstants.APPLICATION_FORM_URLENCODED)
                .setUrl(baseUrl + "/patch")
                .setFormParameter("body", body));
        System.out.println(response);
        System.out.println(response.getText());

    }


    @Test
    public void testFileAndFormParameter() throws IOException {
        String baseUrl = "http://localhost:8080/api/file/and/form/parameter/test";
        String body = "{\"username\": \"test\"}";
        File file = new File(String.format("%s/src/test/java/%s/20161452.jpg", userDir, this.getClass().getPackage().getName().replace(".", "/")));
        ZRequest request = ZRequest.of();
        PResponse response = request.doPost(PostRequestConfig.of()
                .setUrl(baseUrl + "/post")
                .addFile(file)
                .setFormParameter("body", body));
        System.out.println(response);
        System.out.println(response.getText());

        response = request.doPut(PutRequestConfig.of()
                .setUrl(baseUrl + "/put")
                .addFile(file)
                .setFormParameter("body", body));
        System.out.println(response);
        System.out.println(response.getText());

        response = request.doPatch(PatchRequestConfig.of()
                .setUrl(baseUrl + "/patch")
                .addFile(file)
                .setFormParameter("body", body));
        System.out.println(response);
        System.out.println(response.getText());

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

        int count = contentLength == -1 ? 1 : 4;
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
            byteArrayOutputStream.write(response.getBody());
            byteArrayOutputStream.flush();

            for (Header header : response.getHttpResponse().getAllHeaders()) {
                System.out.println(String.format("[Header] %s: %s", header.getName(), header.getValue()));
            }

            System.out.println("=======================================================");

            if (response.getStatusCode() == 200) {
                System.err.println("当前资源不支持分片");
                break;
            }
        }


        File file = new File(String.format("%s/src/test/java/%s/%s", userDir, this.getClass().getPackage().getName().replace(".", "/"), filename));

        ResolveUtils.copy(byteArrayOutputStream.toByteArray(), new FileOutputStream(file));

        //System.out.println(ResolveUtils.toString(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), byteArrayOutputStream.size(), Charset.forName("UTF-8")));

    }

}