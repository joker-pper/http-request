package com.joker17.http.request.core;

import com.joker17.http.request.config.*;
import org.apache.http.Header;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

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

}