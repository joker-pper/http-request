package com.joker17.http.request.support;

import org.apache.http.util.Args;
import org.apache.http.util.TextUtils;

public class ArgsUtils {

    private ArgsUtils() {
    }

    /**
     * 参数不能为null
     *
     * @param argument 参数
     * @param message  为null时的错误信息
     * @param <T>
     * @return
     */
    public static <T> T notNull(final T argument, final String message) {
        Args.check(argument != null, message);
        return argument;
    }

    /**
     * 参数不能为null
     *
     * @param argument 参数
     * @param name     参数名称 （不符合时作为标识抛出错误信息）
     * @param <T>
     * @return
     */
    public static <T> T notNullWithName(final T argument, final String name) {
        return Args.notNull(argument, name);
    }

    /**
     * 参数不能为empty
     *
     * @param argument 参数
     * @param message  为empty时的错误信息
     * @param <T>
     * @return
     */
    public static <T extends CharSequence> T notEmpty(final T argument, final String message) {
        Args.check(argument != null && !TextUtils.isEmpty(argument), message);
        return argument;
    }

    /**
     * 参数不能为empty
     *
     * @param argument 参数
     * @param name     参数名称 （不符合时作为标识抛出错误信息）
     * @param <T>
     * @return
     */
    public static <T extends CharSequence> T notEmptyWithName(final T argument, final String name) {
        return Args.notEmpty(argument, name);
    }

    /**
     * 参数不能为blank
     *
     * @param argument 参数
     * @param message  为blank时的错误信息
     * @param <T>
     * @return
     */
    public static <T extends CharSequence> T notBlank(final T argument, final String message) {
        Args.check(argument != null && !TextUtils.isBlank(argument), message);
        return argument;
    }

    /**
     * 参数不能为blank
     *
     * @param argument 参数
     * @param name     参数名称 （不符合时作为标识抛出错误信息）
     * @param <T>
     * @return
     */
    public static <T extends CharSequence> T notBlankWithName(final T argument, final String name) {
        return Args.notBlank(argument, name);
    }
}
