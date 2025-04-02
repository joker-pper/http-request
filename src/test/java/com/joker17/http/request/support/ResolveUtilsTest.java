package com.joker17.http.request.support;

import com.joker17.http.request.core.HttpConstants;
import com.joker17.http.request.core.ZRequestTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class ResolveUtilsTest {
    private String userDir = System.getProperty("user.dir");

    @Test
    public void testResolveUrl() {
        assertEquals("https://baidu.com?username=xxx&step=2", ResolveUtils.resolveUrl("https://baidu.com", "username=xxx&step=2"));
        assertEquals("https://baidu.com?username=xxx&step=2", ResolveUtils.resolveUrl("https://baidu.com?", "username=xxx&step=2"));
        assertEquals("https://baidu.com?age=xxx&step=2&username=xxx&step=2", ResolveUtils.resolveUrl("https://baidu.com?age=xxx&step=2", "username=xxx&step=2"));
    }

    @Test
    public void testGetURI() {
        Map<String, List<String>> parameterMap = new LinkedHashMap<>(8);

        parameterMap.put("username", Collections.singletonList("xxx"));
        parameterMap.put("step", Collections.singletonList("2"));

        assertEquals(URI.create("https://baidu.com?username=xxx&step=2"), ResolveUtils.getURI("https://baidu.com", ResolveUtils.getNameValuePairList(parameterMap), true, HttpConstants.UTF_8));
        assertEquals(URI.create("https://baidu.com?username=xxx&step=2"), ResolveUtils.getURI("https://baidu.com?", ResolveUtils.getNameValuePairList(parameterMap), true, HttpConstants.UTF_8));
        assertEquals(URI.create("https://baidu.com?age=xxx&step=2&username=xxx&step=2"), ResolveUtils.getURI("https://baidu.com?age=xxx&step=2", ResolveUtils.getNameValuePairList(parameterMap), true, HttpConstants.UTF_8));
    }


    @Test
    public void transferSimplifyStringMap() {
        Map<String, List<String>> dataMap = new LinkedHashMap<>(16);
        dataMap.put(":method", Collections.singletonList("GET"));
        dataMap.put(":scheme", Collections.singletonList("https"));
        dataMap.put(":path", Collections.singletonList("/api/json/list"));
        dataMap.put(":token", Collections.singletonList("123456"));
        dataMap.put(":data", Arrays.asList("111", "222"));
        dataMap.put(":emptyData", Collections.<String>emptyList());
        dataMap.put("Accept", Collections.singletonList("*/*"));
        dataMap.put("Accept-Encoding", Collections.singletonList("gzip, deflate, br"));
        dataMap.put("Accept-Language", Collections.singletonList("zh-HK,zh-CN;q=0.9,zh;q=0.8"));

        Map<String, String> expectedMap = new LinkedHashMap<>(16);
        expectedMap.put(":method", "GET");
        expectedMap.put(":scheme", "https");
        expectedMap.put(":path", "/api/json/list");
        expectedMap.put(":token", "123456");
        expectedMap.put(":data", "111");
        expectedMap.put(":emptyData", null);
        expectedMap.put("Accept", "*/*");
        expectedMap.put("Accept-Encoding", "gzip, deflate, br");
        expectedMap.put("Accept-Language", "zh-HK,zh-CN;q=0.9,zh;q=0.8");
        Assert.assertEquals(expectedMap, ResolveUtils.transferSimplifyStringMap(dataMap, true));

        expectedMap.put(":data", "222");
        Assert.assertEquals(expectedMap, ResolveUtils.transferSimplifyStringMap(dataMap, false));
    }

    @Test
    public void convertBulkDataMap() {
        String dataText = ":method:GET\n" +
                "//:method:GET\n" +
                ":scheme: https\n" +
                ":path:\n" +
                "/api/json/list\n" +
                ":token:\n" +
                " 123456\n" +
                ":data:\n" +
                "111\n" +
                ":data:\n" +
                "222\n" +
                "Accept:*/*\n" +
                "Accept-Encoding:gzip, deflate, br\n" +
                "//Content-Length:11648\n" +
                "Accept-Language:\n" +
                "zh-HK,zh-CN;q=0.9,zh;q=0.8\n" +
                "Content-Type:\n" +
                "application/x-www-form-urlencoded; charset=utf-8";

        Map<String, List<String>> dataMap = ResolveUtils.convertBulkDataMap(dataText, ":", true, "Content-Type");

        Map<String, List<String>> expectedMap = new LinkedHashMap<>(16);
        expectedMap.put(":method", Collections.singletonList("GET"));
        expectedMap.put(":scheme", Collections.singletonList("https"));
        expectedMap.put(":path", Collections.singletonList("/api/json/list"));
        expectedMap.put(":token", Collections.singletonList("123456"));
        expectedMap.put(":data", Arrays.asList("111", "222"));
        expectedMap.put("Accept", Collections.singletonList("*/*"));
        expectedMap.put("Accept-Encoding", Collections.singletonList("gzip, deflate, br"));
        expectedMap.put("Accept-Language", Collections.singletonList("zh-HK,zh-CN;q=0.9,zh;q=0.8"));
        Assert.assertEquals(expectedMap, dataMap);
        System.out.println(dataMap);
    }

    @Test
    public void validateWithAnnotateConvertBulkDataMap() {
        String dataText = "//:method:GET\n" +
                "//:scheme: https\n" +
                "//:path:\n" +
                "/api/json/list\n" +
                ":\n" +
                "//:\n" +
                "//:path:\n" +
                " /api/json/list\n";

        Map<String, List<String>> dataMap = ResolveUtils.convertBulkDataMap(dataText, ":", true);

        Map<String, List<String>> expectedMap = new LinkedHashMap<>(16);
        Assert.assertEquals(expectedMap, dataMap);
        System.out.println(dataMap);
    }

    @Test
    public void parseByTemplate() {
        Assert.assertEquals("${key}:${value}", ResolveUtils.parseByTemplate("${key}:${value}", Collections.singletonMap("s", "s")));
        Assert.assertEquals("${key}:${value}", ResolveUtils.parseByTemplate("${key}:${value}", Collections.singletonMap("Key", "mykey")));
        Assert.assertEquals("${key}:${value}", ResolveUtils.parseByTemplate("${key}:${value}", Collections.singletonMap("Value", "myvalue")));

        Assert.assertEquals("mykey:${value}", ResolveUtils.parseByTemplate("${key}:${value}", Collections.singletonMap("key", "mykey")));
        Assert.assertEquals("${key}:myvalue", ResolveUtils.parseByTemplate("${key}:${value}", Collections.singletonMap("value", "myvalue")));

        Assert.assertEquals("${key}:1", ResolveUtils.parseByTemplate("${key}:${value}", Collections.singletonMap("value", 1)));
        Assert.assertEquals("${key}:null", ResolveUtils.parseByTemplate("${key}:${value}", Collections.singletonMap("value", null)));

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("key", "mykey");
        dataMap.put("value", "myvalue");

        Assert.assertEquals("mykey:myvalue", ResolveUtils.parseByTemplate("${key}:${value}", dataMap));
        Assert.assertEquals("mykey:myvalue, result: myvalue", ResolveUtils.parseByTemplate("${key}:${value}, result: ${value}", dataMap));
    }

    @Test
    public void testToString() throws IOException {
        String javaFilePath = ZRequestTest.class.getName().replace(".", "/") + ".java";
        final File file = new File(String.format("%s/src/test/java/%s", userDir, javaFilePath));
        String result = ResolveUtils.toString(new FileInputStream(file), -1, HttpConstants.UTF_8);
        Assert.assertNotNull(result);
        System.out.println(result);
    }
}