package com.bs.common.tools.common;

import lombok.extern.slf4j.Slf4j;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName ReflectUtils
 * @Description 反射类
 * @date 2021/7/9 17:17
 */
@Slf4j
public class ReflectUtils {

    private static final Pattern GET_PATTERN = Pattern.compile("^get[A-Z].*");

    private static final Pattern IS_PATTERN = Pattern.compile("^is[A-Z].*");

    private static final String LAMBDA = "lambda$";

    /**
     * 获取属性名称
     *
     * @param fn
     * @return
     */
    public static String getFieldName(Function fn) {
        try {
            Method method = fn.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda)method.invoke(fn);
            String getter = serializedLambda.getImplMethodName();
            if (GET_PATTERN.matcher(getter).matches()) {
                getter = getter.substring(3);
            } else if (IS_PATTERN.matcher(getter).matches()) {
                getter = getter.substring(2);
            } else if (getter.startsWith(LAMBDA)) {
                throw new IllegalArgumentException("Function不能传递lambda表达式,只能使用方法引用");
            } else {
                throw new IllegalArgumentException(getter + "不是Getter方法引用");
            }
            return Introspector.decapitalize(getter);
        } catch (Exception var4) {
            log.error("通过方法引用获取属性名失败",var4);
            throw new RuntimeException(var4);
        }
    }
}
