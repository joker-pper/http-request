package com.joker17.http.request.core;

import com.joker17.http.request.support.BrotliUtils;
import com.joker17.http.request.support.ResolveUtils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * contentEncoding为br时获取text是否进行解码 (来自request config)
     */
    private Boolean brotliContentDecoderTextEnabled;


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


    /**
     * 获取默认编码的文本内容
     *
     * @return
     */
    public String getText() throws IOException {
        return getText(ResolveUtils.getCharset(getContentType()));
    }


    /**
     * 获取文本内容
     *
     * @param charset
     * @return
     * @throws IOException
     */
    public String getText(String charset) throws IOException {
        return getText(Charset.forName(charset));
    }

    /**
     * 获取文本内容
     *
     * @param charset
     * @return
     * @throws IOException
     */
    public String getText(Charset charset) throws IOException {
        if (Boolean.TRUE.equals(brotliContentDecoderTextEnabled) && StringUtils.equals(HttpConstants.BR_CONTENT_ENCODING, contentEncoding)) {
            return BrotliUtils.toString(getContent(), getContentLength(), charset);
        }
        return ResolveUtils.toString(getContent(), getContentLength(), charset);
    }

    /**
     * 将数据全部放到body中，调用后会关闭entity原InputStream流（不适合大文件的处理）
     *
     * @return
     * @throws IOException
     */
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

    /**
     * 获取InputStream流，如果先调用了getBody()，则从body转换为ByteArrayInputStream，反之则为原InputStream流。
     * <p>
     * 无自定义handler时，默认会被调用填充数据到body中。
     *
     * @return
     * @throws IOException
     */
    public InputStream getContent() throws IOException {
        if (body != null) {
            return new ByteArrayInputStream(body);
        }

        if (isNullEntityAndCheck()) {
            return new ByteArrayInputStream(HttpConstants.EMPTY_BYTES);
        }

        return entity.getContent();
    }

    /**
     * 写入输出流
     *
     * @param outputStream
     * @throws IOException
     */
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
        brotliContentDecoderTextEnabled = null;
        body = null;
        uri = null;
        locale = null;
    }

}
