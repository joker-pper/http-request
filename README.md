
> http-request  

[![](https://jitpack.io/v/joker-pper/http-request.svg)](https://jitpack.io/#joker-pper/http-request)



 - 使用httpclient对部分常用方式进行封装,无需关注过多细节
 - 通过链式进行请求参数的设置,获取http方法对应的请求执行结果
 - v1.0.1: 提供handler进行自定义解析结果(存在时不再默认填充body数据)

> 引入方式



    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    
 
    <dependencies>
        <dependency>
            <groupId>com.github.joker-pper</groupId>
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

formParameter    -> 非requestBody时添加(Post,Put,Patch)

requestBody      -> requestBody时添加(Post,Put,Patch,Delete)

fileParameter    -> 非requestBody时添加(Post,Put,Patch)

优先级            -> requestBody > fileParameter > formParameter

存在requestBody时fileParameter及formParameter会被忽略

> 其他

[http-request-server-demo](https://github.com/joker-pper/http-request-server-demo.git)

[test文件](https://github.com/joker-pper/http-request/blob/master/src/test/java/com/joker17/http/request/core/ZRequestTest.java)


