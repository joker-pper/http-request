package com.joker17.http.request.core;

import com.joker17.http.request.config.DeleteRequestConfig;
import com.joker17.http.request.config.PatchRequestConfig;
import com.joker17.http.request.config.PostRequestConfig;
import com.joker17.http.request.config.PutRequestConfig;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * server demo地址：
 * <p>
 * https://github.com/joker-pper/http-request-server-demo.git
 */
public class ZRequestWithServerDemoTests {

    private String userDir = System.getProperty("user.dir");

    @Test
    public void testRequestBody() throws IOException {
        String baseUrl = "http://localhost:8080/api/body/test";
        String requestBody = "{\"username\": \"test\", \"remark\": \"测试测试1234\"}";
        String expected = "{\"data\":{\"username\":\"test\",\"remark\":\"测试测试1234\"},\"info\":\"ok\"}";
        ZRequest request = ZRequest.of();
        PResponse response = request.doPost(PostRequestConfig.of().setUrl(baseUrl + "/post")
                .setRequestBody(requestBody));
        System.out.println(response);
        System.out.println("response text: " + response.getText());
        Assert.assertEquals(expected, response.getText());

        response = request.doPut(PutRequestConfig.of().setUrl(baseUrl + "/put")
                .setRequestBody(requestBody));
        System.out.println(response);
        System.out.println("response text: " + response.getText());
        Assert.assertEquals(expected, response.getText());

        response = request.doPatch(PatchRequestConfig.of().setUrl(baseUrl + "/patch")
                .setRequestBody(requestBody));
        System.out.println(response);
        System.out.println("response text: " + response.getText());
        Assert.assertEquals(expected, response.getText());

        response = request.doDelete(DeleteRequestConfig.of().setUrl(baseUrl + "/delete")
                .setRequestBody(requestBody));
        System.out.println(response);
        System.out.println("response text: " + response.getText());
        Assert.assertEquals(expected, response.getText());

    }


    @Test
    public void testFormParameter() throws IOException {
        String baseUrl = "http://localhost:8080/api/form/parameter/test";
        String body = "{\"username\": \"test\", \"remark\": \"测试测试1234\"}";
        String expected = "{\"data\":\"{\\\"username\\\": \\\"test\\\", \\\"remark\\\": \\\"测试测试1234\\\"}\",\"info\":\"ok\"}";

        ZRequest request = ZRequest.of();
        PResponse response = request.doPost(PostRequestConfig.of()
                .setContentType(HttpConstants.APPLICATION_FORM_URLENCODED)
                .setUrl(baseUrl + "/post")
                .setFormParameter("body", body));
        System.out.println(response);
        System.out.println("response text: " + response.getText());
        Assert.assertEquals(expected, response.getText());

        response = request.doPut(PutRequestConfig.of()
                .setContentType(HttpConstants.APPLICATION_FORM_URLENCODED)
                .setUrl(baseUrl + "/put")
                .setFormParameter("body", body));
        System.out.println(response);
        System.out.println("response text: " + response.getText());
        Assert.assertEquals(expected, response.getText());

        response = request.doPatch(PatchRequestConfig.of()
                .setContentType(HttpConstants.APPLICATION_FORM_URLENCODED)
                .setUrl(baseUrl + "/patch")
                .setFormParameter("body", body));
        System.out.println(response);
        System.out.println("response text: " + response.getText());
        Assert.assertEquals(expected, response.getText());

        response = request.doDelete(DeleteRequestConfig.of()
                .setContentType(HttpConstants.APPLICATION_FORM_URLENCODED)
                .setUrl(baseUrl + "/delete")
                .setFormParameter("body", body)
        );
        System.out.println(response);
        System.out.println("response text: " + response.getText());
        Assert.assertEquals(expected, response.getText());

    }


    @Test
    public void testFileAndFormParameter() throws IOException {
        //multipart/form-data类型

        //  无需传contentType
        //  需要指定对应的字符集，避免文件名称和字符乱码

        String baseUrl = "http://localhost:8080/api/file/and/form/parameter/test";
        String body = "{\"username\": \"test\", \"remark\": \"测试测试1234\"}";
        String expected = "{\"data\":\"{\\\"username\\\": \\\"test\\\", \\\"remark\\\": \\\"测试测试1234\\\"}\",\"info\":\"图片-20161452.jpg\"}";

        File file = new File(String.format("%s/src/test/resources/图片-20161452.jpg", userDir));
        ZRequest request = ZRequest.of();
        PResponse response = request.doPost(PostRequestConfig.of()
                .setUrl(baseUrl + "/post")
                .setCharset(HttpConstants.UTF_8)
                .clearContentType()
                .addFile(file)
                .setFormParameter("body", body));
        System.out.println(response);
        System.out.println("response text: " + response.getText());
        Assert.assertEquals(expected, response.getText());

        response = request.doPut(PutRequestConfig.of()
                .setUrl(baseUrl + "/put")
                .setCharset(HttpConstants.UTF_8)
                .clearContentType()
                .addFile(file)
                .setFormParameter("body", body));
        System.out.println(response);
        System.out.println("response text: " + response.getText());
        Assert.assertEquals(expected, response.getText());

        response = request.doPatch(PatchRequestConfig.of()
                .setUrl(baseUrl + "/patch")
                .setCharset(HttpConstants.UTF_8)
                .clearContentType()
                .addFile(file)
                .setFormParameter("body", body));
        System.out.println(response);
        System.out.println("response text: " + response.getText());
        Assert.assertEquals(expected, response.getText());

        response = request.doDelete(DeleteRequestConfig.of()
                .setUrl(baseUrl + "/delete")
                .setCharset(HttpConstants.UTF_8)
                .clearContentType()
                .addFile(file)
                .setFormParameter("body", body));
        System.out.println(response);
        System.out.println("response text: " + response.getText());
        Assert.assertEquals(expected, response.getText());

    }


}