package com.joker17.http.request.support;

import com.joker17.http.request.core.AssertUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import static org.junit.Assert.assertEquals;

public class ArgsUtilsTest {

    @Test
    public void testNotNull() {
        assertEquals("aaa", ArgsUtils.notNull("aaa", "参数不能为null"));
        AssertUtils.assertThrows(IllegalArgumentException.class, "参数不能为null", new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                ArgsUtils.notNull(null, "参数不能为null");
            }
        });
    }

    @Test
    public void testNotNullWithName() {
        assertEquals("aaa", ArgsUtils.notNullWithName("aaa", "参数"));

        AssertUtils.assertThrows(IllegalArgumentException.class, "参数 may not be null", new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                ArgsUtils.notNullWithName(null, "参数");
            }
        });
    }


    @Test
    public void testNotEmpty() {
        assertEquals("aaa", ArgsUtils.notEmpty("aaa", "参数不能为空"));
        AssertUtils.assertThrows(IllegalArgumentException.class, "参数不能为空", new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                ArgsUtils.notEmpty(null, "参数不能为空");
            }
        });

        AssertUtils.assertThrows(IllegalArgumentException.class, "参数不能为空", new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                ArgsUtils.notEmpty("", "参数不能为空");
            }
        });

        assertEquals(" ", ArgsUtils.notEmpty(" ", "参数不能为空"));

    }

    @Test
    public void testNotEmptyWithName() {
        assertEquals("aaa", ArgsUtils.notEmptyWithName("aaa", "参数"));
        AssertUtils.assertThrows(IllegalArgumentException.class, "参数 may not be null", new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                ArgsUtils.notEmptyWithName(null, "参数");
            }
        });

        AssertUtils.assertThrows(IllegalArgumentException.class, "参数 may not be empty", new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                ArgsUtils.notEmptyWithName("", "参数");
            }
        });

        assertEquals(" ", ArgsUtils.notEmptyWithName(" ", "参数"));

    }

    @Test
    public void testNotBlank() {
        assertEquals("aaa", ArgsUtils.notBlank("aaa", "参数不能为blank"));
        AssertUtils.assertThrows(IllegalArgumentException.class, "参数不能为blank", new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                ArgsUtils.notBlank(null, "参数不能为blank");
            }
        });

        AssertUtils.assertThrows(IllegalArgumentException.class, "参数不能为blank", new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                ArgsUtils.notBlank("", "参数不能为blank");
            }
        });
        AssertUtils.assertThrows(IllegalArgumentException.class, "参数不能为blank", new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                ArgsUtils.notBlank(" ", "参数不能为blank");
            }
        });
    }

    @Test
    public void testNotBlankWithName() {
        assertEquals("aaa", ArgsUtils.notBlankWithName("aaa", "参数"));
        AssertUtils.assertThrows(IllegalArgumentException.class, "参数 may not be null", new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                ArgsUtils.notBlankWithName(null, "参数");
            }
        });

        AssertUtils.assertThrows(IllegalArgumentException.class, "参数 may not be blank", new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                ArgsUtils.notBlankWithName("", "参数");
            }
        });

        AssertUtils.assertThrows(IllegalArgumentException.class, "参数 may not be blank", new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                ArgsUtils.notBlankWithName(" ", "参数");
            }
        });

    }


}
