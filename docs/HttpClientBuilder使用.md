

## 设置未指定连接管理器时，默认连接管理器的总连接数

```

    builder.setMaxConnTotal(500);
    
    //poolingmgr.setMaxTotal(maxConnTotal);

```

## 设置未指定连接管理器时，默认连接管理器每个路由的默认最大连接数

```

    builder.setMaxConnPerRoute(60);
    
    //poolingmgr.setDefaultMaxPerRoute(maxConnPerRoute);

```
## 禁用重试

```
    
    builder.disableAutomaticRetries();

```

## 设置重试处理器 (未禁用重试时生效)

```

    builder.setRetryHandler(new DefaultHttpRequestRetryHandler(3, false));

```