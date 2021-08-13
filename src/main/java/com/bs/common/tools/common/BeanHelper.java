package com.bs.common.tools.common;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName BeanHelper
 * @Description bean拷贝工具
 * @date 2021/4/25 14:35
 */
public class BeanHelper extends BeanUtils {

    /**
     * 转换list中的bean的类型
     * @param source 复制对象
     * @param vClass 最终转换对象
     * @param <T> 原类型
     * @param <V> 新类型
     * @return
     */
    public static <T,V> List<V> copyList(List<T> source, Class<V> vClass){
        if(source == null || source.isEmpty() || vClass == null){
            return new ArrayList<V>();
        }
        return source.stream().map(s -> {
            V obj = null;
            try {
                obj = vClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            copyProperties(s,obj);
            return obj;
        }).collect(Collectors.toList());
    }

    /**
     * 转换PageInfo
     * @param source 复制对象
     * @param vClass 最终转换对象
     * @param <T> 原类型
     * @param <V> 新类型
     * @return
     */
    public static <T,V> PageInfo<V> copyPage(PageInfo<T> source, Class<V> vClass){
        PageInfo<V> target = new PageInfo<>();
        copyProperties(source,target,"list");
        target.setList(copyList(source.getList(),vClass));
        return target;
    }


    /**
     * 增强复制对象，使用方法引用获取忽略的属性
     * @param source 复制对象
     * @param target 粘贴对象
     * @param ignoreProperties 忽略source中的属性方法引用
     * @param <T>
     */
    @Deprecated
    public static <T> void copyObj(Object source, Object target, Function<T, Object>... ignoreProperties){
        if (null != ignoreProperties) {
            List<String> collect = Arrays.asList(ignoreProperties).stream().map(ReflectUtils::getFieldName).collect(Collectors.toList());
            String[] strings = collect.toArray(new String[collect.size()]);
            copyProperties(source, target, strings);
        } else {
            copyProperties(source, target);
        }
    }

    /**
     * 增强复制对象，仅拷贝不为null的属性
     * @param source 复制对象
     * @param target 粘贴对象
     */
    public static void copyObj(Object source, Object target){
        copyPropertiesNotNull(source, target,null,(String[])null);
    }

    /**
     * 转换PageInfo，并且转换BaseEnum
     * @param source
     * @param vClass
     * @param <T>
     * @param <V>
     * @return
     */
    public static  <T,V> PageInfo<V> copyPageByEnum(PageInfo<T> source, Class<V> vClass){
        PageInfo<V> target = new PageInfo<>();
        copyProperties(source,target,"list");
        target.setList(copyListByEnum(source.getList(),vClass));
        return target;
    }

    /**
     * 转换list中的bean的类型，并且转换BaseEnum
     * @param source
     * @param vClass
     * @param <T>
     * @param <V>
     * @return
     */
    public static  <T,V> List<V> copyListByEnum(List<T> source, Class<V> vClass){
        if(source == null || source.isEmpty() || vClass == null){
            return new ArrayList<V>();
        }
        return source.stream().map(s -> {
            V obj = null;
            try {
                obj = vClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            copyObjByEnum(s,obj);
            return obj;
        }).collect(Collectors.toList());
    }

    /**
     * 复制对象，并且转换BaseEnum
     * @param source
     * @param target
     * @param ignoreProperties
     */
    public static void copyObjByEnum(Object source, Object target, String... ignoreProperties){
        copyPropertiesByEnum(source, target,null, ignoreProperties);
    }

    /**
     * 复制对象，并且转换BaseEnum
     * @param source
     * @param target
     */
    public static void copyObjByEnum(Object source, Object target){
        copyPropertiesByEnum(source, target,null, null);
    }

    /**
     * org.springframework.beans.BeanUtils 不为null才复制 转换BaseEnum(源属性为值，目标属性为BaseEnum时)
     *
     * @param source
     * @param target
     * @param editable
     * @param ignoreProperties
     * @throws BeansException
     */
    private static void copyPropertiesByEnum(Object source, Object target, Class<?> editable, String... ignoreProperties)
            throws BeansException {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
                        "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null && (ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())
                           // || Arrays.asList(writeMethod.getParameterTypes()[0].getInterfaces()).contains(BaseEnum.class)
                    )) {
                        try {

                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            // 判断被复制的属性是否为null, 如果不为null才复制
                            if (value != null) {
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }
                                Class<?> propertyType = targetPd.getPropertyType();
                                // 将source值转为枚举BaseEnum给target
//                                if (Arrays.asList(propertyType.getInterfaces()).contains(BaseEnum.class) &&
//                                        !(value instanceof BaseEnum)){
//                                    value = BaseEnum.allOf(propertyType,value.toString());
//                                }
                                writeMethod.invoke(target, value);
                            }
                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "不能拷贝属性 '" + targetPd.getName() + "' 从原对象给目标对象,详细原因见:", ex);
                        }
                    }
                }
            }
        }
    }


    /**
     * org.springframework.beans.BeanUtils 不为null才复制
     *
     * @param source
     * @param target
     * @param editable
     * @param ignoreProperties
     * @throws BeansException
     */
    private static void copyPropertiesNotNull(Object source, Object target, Class<?> editable, String... ignoreProperties)
            throws BeansException {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
                        "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {

                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            // 判断被复制的属性是否为null, 如果不为null才复制
                            if (value != null) {
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }
                                writeMethod.invoke(target, value);
                            }
                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "不能拷贝属性 '" + targetPd.getName() + "' 从原对象给目标对象,详细原因见:", ex);
                        }
                    }
                }
            }
        }
    }

}
