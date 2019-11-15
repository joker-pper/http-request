package com.joker17.http.request.core;

import java.nio.charset.Charset;

public class HttpConstants {

    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final Charset GBK = Charset.forName("GBK");
    public static final Charset ASCII = Charset.forName("US-ASCII");
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

    public static final String WILDCARD = "*/*";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_XML = "text/xml";
    public static final String TEXT_HTML = "text/html";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String APPLICATION_XHTML_XML = "application/xhtml+xml";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";


    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";
    public static final String PUT_METHOD = "PUT";
    public static final String PATCH_METHOD = "PATCH";
    public static final String DELETE_METHOD = "DELETE";

}
