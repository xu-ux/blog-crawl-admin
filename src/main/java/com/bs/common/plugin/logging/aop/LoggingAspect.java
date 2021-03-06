package com.bs.common.plugin.logging.aop;

import com.bs.common.plugin.logging.aop.annotation.Logging;
import com.bs.common.plugin.system.domain.SysBaseLog;
import com.bs.common.plugin.logging.aop.enums.LoggingType;
import com.bs.common.plugin.logging.async.LoggingFactory;
import com.bs.common.tools.sequence.SequenceUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * Describe: 日 志 切 面 实 现
 * Author: test-admin
 * CreateTime: 2019/10/23
 * */
@Aspect
@Component
public class LoggingAspect {

    @Resource
    private LoggingFactory loggingFactory;

    /**
     * 切 面 编 程
     * */
    @Pointcut("@annotation(com.bs.common.plugin.logging.aop.annotation.Logging) || @within(com.bs.common.plugin.logging.aop.annotation.Logging)")
    public void dsPointCut() { }

    /**
     * 处 理 系 统 日 志
     * */
    @Around("dsPointCut()")
    private Object around(ProceedingJoinPoint joinPoint) throws Throwable
    {
        SysBaseLog sysLog = new SysBaseLog();
        Object result;
        try {
            Logging loggingAnnotation = getLogging(joinPoint);
            sysLog.setId(SequenceUtil.makeStringId());
            sysLog.setTitle(loggingAnnotation.value());
            sysLog.setTitle(loggingAnnotation.title());
            sysLog.setDescription(loggingAnnotation.describe());
            sysLog.setBusinessType(loggingAnnotation.type());
            sysLog.setSuccess(true);
            sysLog.setLoggingType(LoggingType.OPERATE);
            result = joinPoint.proceed();
        }catch (Exception exception){
            sysLog.setSuccess(false);
            sysLog.setErrorMsg(exception.getMessage());
            throw exception;
        }finally {
            loggingFactory.record(sysLog);
        }
        return result;
    }

    /**
     * 获 取 注 解
     * */
    public com.bs.common.plugin.logging.aop.annotation.Logging getLogging(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Class<? extends Object> targetClass = point.getTarget().getClass();
        com.bs.common.plugin.logging.aop.annotation.Logging targetLogging = targetClass.getAnnotation(com.bs.common.plugin.logging.aop.annotation.Logging.class);
        if ( targetLogging != null) {
            return targetLogging;
        } else {
            Method method = signature.getMethod();
            com.bs.common.plugin.logging.aop.annotation.Logging logging = method.getAnnotation(com.bs.common.plugin.logging.aop.annotation.Logging.class);
            return logging;
        }
    }
}
