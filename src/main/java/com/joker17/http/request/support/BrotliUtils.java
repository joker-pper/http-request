package com.joker17.http.request.support;

import org.brotli.dec.BrotliInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class BrotliUtils {

    private BrotliUtils() {
    }

    public static String toString(InputStream inputStream, long contentLength, Charset charset) throws IOException {
        try {
            BrotliInputStream stream = new BrotliInputStream(inputStream, 4096);
            return ResolveUtils.toString(stream, contentLength, charset);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
    }

}
