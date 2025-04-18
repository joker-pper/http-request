package com.joker17.http.request.core;

import org.junit.Assert;
import org.junit.function.ThrowingRunnable;

/**
 * @author joker-pper 2025年04月05日 下午14:02:17
 */
public class AssertUtils {
    private AssertUtils() {
    }

    /**
     * 验证抛出指定异常及错误提示内容
     *
     * @param expectedThrowable
     * @param expectedErrorMsg
     * @param runnable
     * @param <T>
     */
    public static <T extends Throwable> void assertThrows(Class<T> expectedThrowable, final String expectedErrorMsg, final ThrowingRunnable runnable) {
        Assert.assertThrows(expectedThrowable, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                try {
                    runnable.run();
                } catch (Exception e) {
                    Assert.assertEquals(expectedErrorMsg, e.getMessage());
                    throw e;
                }
            }
        });
    }
}
