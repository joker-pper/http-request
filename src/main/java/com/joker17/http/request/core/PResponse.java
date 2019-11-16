package com.joker17.http.request.core;

import com.joker17.http.request.config.BaseRequestConfig;
import com.joker17.http.request.support.ResolveUtils;
import lombok.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Locale;

@Data
@NoArgsConstructor
public class PResponse implements Serializable {

    @ToString.Exclude
    private HttpEntity entity;

    @ToString.Exclude
    private HttpResponse httpResponse;

    private ContentType contentType;

    private String contentEncoding;

    @Getter(value = AccessLevel.NONE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private byte[] body;

    private URI uri;

    private Locale locale;

    private int statusCode;
    private long contentLength;
    private boolean isRepeatable;
    private boolean isChunked;
    private boolean isStreaming;


    public String getText() throws IOException {
        return getText(ResolveUtils.getCharset(getContentType()));
    }

    public String getText(String charset) throws IOException {
        return getText(Charset.forName(charset));
    }

    public String getText(Charset charset) throws IOException {
        return ResolveUtils.toString(getContent(), (int)getContentLength(), charset);
    }

    public byte[] getBody() throws IOException {
        if (body == null) {
            body = ResolveUtils.copyToByteArray(entity.getContent());
        }
        return body;
    }


    public InputStream getContent() throws IOException {
        if (body != null) {
            return new ByteArrayInputStream(getBody());
        }
        return entity.getContent();
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        ResolveUtils.copy(getContent(), outputStream);
    }




}
