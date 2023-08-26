package com.joker17.http.request.support;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class ResolveUtilsTest {

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
}