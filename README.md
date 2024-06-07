
> http-request  

[![Java support](https://img.shields.io/badge/Java-7+-green?logo=java&logoColor=white)](https://openjdk.java.net/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.joker-pper/http-request.svg?label=Maven%20Central)](https://central.sonatype.com/search?q=io.github.joker-pper:http-request)
[![Last SNAPSHOT](https://img.shields.io/nexus/snapshots/https/s01.oss.sonatype.org/io.github.joker-pper/http-request?label=latest%20snapshot)](https://s01.oss.sonatype.org/content/repositories/snapshots/io/github/joker-pper/http-request/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


 - 使用httpclient对部分常用方式进行封装,无需关注过多细节
 - 通过链式进行请求参数的设置,获取http方法对应的请求执行结果
 - v1.0.1: 提供handler进行自定义解析结果(存在时不再默认填充body数据)或用于大文件的处理
 - v1.0.2: 提供callback可进行自定义增加或覆写client request config；支持postman bulk data数据解析
 - v1.0.3: Delete方法增加支持 formParameter 和 fileParameter
 - v1.0.4: PResponse增加clear方法、ZRequest增加close方法；提供全局设置default request config；提供全局设置default httpClient config callback；

> 引入方式
 
    <dependencies>
        <dependency>
            <groupId>io.github.joker-pper</groupId>
            <artifactId>http-request</artifactId>
            <version>TAG</version>
        </dependency>
    </dependencies>       


----------


> 函数


`v1.0.1: PResponse doHead(HeadRequestConfig requestConfig)`

`PResponse doGet(GetRequestConfig requestConfig)`

`PResponse doPost(PostRequestConfig requestConfig)`

`PResponse doPut(PutRequestConfig requestConfig)`

`PResponse doPatch(PatchRequestConfig requestConfig)`

`PResponse doDelete(DeleteRequestConfig requestConfig)`

> 使用方式


    PResponse response = ZRequest.of().doGet(requestConfig);
    int statusCode = response.getStatusCode();
    String text = response.getText();
    String utf8Text = response.getText("utf-8");
    InputStream inputStream = response.getContent();
    response.writeTo(new FileOutputStream("xxx"));
    byte[] body = response.getBody();


> 备注

queryParameter   -> 拼接到url后面(All)

formParameter    -> 非requestBody时添加(Post,Put,Patch,Delete)

requestBody      -> requestBody时添加(Post,Put,Patch,Delete)

fileParameter    -> 非requestBody时添加(Post,Put,Patch,Delete)

优先级            -> requestBody > fileParameter > formParameter

存在requestBody时fileParameter及formParameter会被忽略

> 拓展

  提供增加或覆写client request config（v1.0.2）
  
```
        PostRequestConfig.of().setUrl("https://xxxx.com").setConfigCallback(new RequestConfigCallback<RequestConfig.Builder>() {
            @Override
            public void execute(RequestConfig.Builder builder) {
                builder.setCircularRedirectsAllowed(true).setMaxRedirects(10);
            }
        });
```

  提供全局设置default request config（v1.0.4）

```
   
        HttpClientUtils.setDefaultRequestConfig(RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build());

```

  提供全局设置default httpClientSupport config callback（v1.0.4）

```
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
   
```

> 相关文档

[HttpClientBuilder使用](docs/HttpClientBuilder使用.md)

> 其他

[http-request-server-demo](https://github.com/joker-pper/http-request-server-demo.git)

[test文件](https://github.com/joker-pper/http-request/blob/master/src/test/java/com/joker17/http/request/core/ZRequestTest.java)


