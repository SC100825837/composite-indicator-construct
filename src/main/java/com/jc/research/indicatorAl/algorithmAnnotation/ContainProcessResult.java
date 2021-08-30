package com.jc.research.indicatorAl.algorithmAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 带有过程结果的算法用该注解标识
 * 在后续算法链的执行过程中通过此注解判断方法的返回值
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContainProcessResult {
}
