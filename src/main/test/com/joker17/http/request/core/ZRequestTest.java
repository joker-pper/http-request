package com.joker17.http.request.core;

import com.joker17.http.request.config.GetRequestConfig;
import com.joker17.http.request.config.PostRequestConfig;
import org.apache.http.Header;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class ZRequestTest {

    @Test
    public void testConfig() throws IOException {
        System.out.println(GetRequestConfig.of().setContentType(HttpConstants.APPLICATION_JSON)
                .setUrl("https://baidu.com").addHeader("token", "123456")
                .addFormParameter("key1", "key1.1", "key1.2"));

        System.out.println(PostRequestConfig.of().setUrl("https://baidu.com").setRequestBody("{\"username\": \"test\"}"));
        System.out.println(PostRequestConfig.of().setUrl("https://baidu.com").addFile(new File("")));

    }

    @Test
    public void test() throws IOException {
        ZRequest request = ZRequest.of();
        PResponse response = request.doGet(GetRequestConfig.of().setContentType("text/plain").setUrl("http://www.baidu.com").addFormParameter("key", 1, 2, 3));
        System.out.println(response);
        System.out.println(response.getText(HttpConstants.UTF_8));
        System.out.println(request.getCookieStore());

        System.out.println(response.getHttpResponse());
        for (Header header : response.getHttpResponse().getAllHeaders()) {
            System.out.println(String.format("[Header] %s: %s", header.getName(), header.getValue()));
        }

    }

}