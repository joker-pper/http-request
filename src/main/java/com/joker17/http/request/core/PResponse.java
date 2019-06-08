package com.joker17.http.request.core;

import com.joker17.http.request.support.ResolveUtils;
import lombok.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;

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
    private byte[] body;

    private URI uri;

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

    public byte[] getBody() {
        return body == null ? new byte[0] : body;
    }

    public InputStream getContent() {
        return new ByteArrayInputStream(getBody());
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        ResolveUtils.copy(getBody(), outputStream);
    }

}
