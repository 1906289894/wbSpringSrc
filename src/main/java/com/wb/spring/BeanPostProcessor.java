package com.wb.spring;

/**
 * @author wb
 * @date 2024/8/20 14:44
 **/
public interface BeanPostProcessor {
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
