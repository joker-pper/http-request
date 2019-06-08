package com.joker17.http.request.core;

import com.joker17.http.request.config.GetRequestConfig;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ZRequestTest {

    @Test
    public void test() throws IOException {
        ZRequest request = ZRequest.of();
        PResponse response = request.doGet(GetRequestConfig.of().of().setContentType("text/plain").setUrl("http://www.baidu.com").addFormParameter("key", 1, 2, 3));
        System.out.println(response);
        System.out.println(response.getText(HttpConstants.UTF_8));
        System.out.println(request.getCookieStore());
    }

}