# http-request

通过链式进行请求参数的设置,获取http方法对应的请求执行结果(使用httpclient对部分常用方式进行封装
,无需关注过多细节.)

##函数

`e.g: PResponse response = ZRequest.of().doGet(requestConfig)`

`PResponse doGet(GetRequestConfig requestConfig)`

`PResponse doPost(PostRequestConfig requestConfig)`

`PResponse doPut(PutRequestConfig requestConfig)`

`PResponse doPatch(PatchRequestConfig requestConfig)`

`PResponse doDelete(DeleteRequestConfig requestConfig)`

###备注

queryParameter -> 拼接到url后面(All)

formParameter  -> 非requestBody时添加(Post,Put,Patch)

requestBody    -> requestBody时添加(Post,Put,Patch,Delete)

fileParameter  -> 非requestBody时添加(Post,Put,Patch)

优先级         -> requestBody > fileParameter > formParameter

存在requestBody时fileParameter及formParameter会被忽略
