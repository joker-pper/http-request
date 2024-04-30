package com.joker17.http.request.core;

import com.joker17.http.request.support.ResolveUtils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Locale;

@Slf4j
@Data
@NoArgsConstructor
@Setter(AccessLevel.PACKAGE)
public class PResponse implements Serializable {

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private HttpEntity entity;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
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
        return ResolveUtils.toString(getContent(), getContentLength(), charset);
    }

    public byte[] getBody() throws IOException {
        if (body == null) {
            if (isNullEntityAndCheck()) {
                body = HttpConstants.EMPTY_BYTES;
            } else {
                body = ResolveUtils.copyToByteArray(getContent());
            }
        }
        return body;
    }

    public InputStream getContent() throws IOException {
        if (body != null) {
            return new ByteArrayInputStream(body);
        }

        if (isNullEntityAndCheck()) {
            return new ByteArrayInputStream(HttpConstants.EMPTY_BYTES);
        }

        return entity.getContent();
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        ResolveUtils.copy(getContent(), outputStream);
    }

    /**
     * 获取entity是否为空并检查
     *
     * @return
     */
    protected boolean isNullEntityAndCheck() {
        if (entity != null) {
            return false;
        }

        log.warn("entity is null, uri: {}.", uri);

        return true;
    }

    /**
     * 清理资源
     */
    public void clear() {
        if (entity != null) {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                //ignore
            }
            entity = null;
        }

        httpResponse = null;
        contentType = null;
        contentEncoding = null;
        body = null;
        uri = null;
        locale = null;
    }

}
