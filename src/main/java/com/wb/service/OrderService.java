package com.wb.service;

import com.wb.spring.Component;
import com.wb.spring.InitializingBean;
import com.wb.spring.Scope;

/**
 * @author wb
 * @date 2024/8/20 11:18
 **/
@Component
@Scope("prototype")
public class OrderService implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        System.out.println("init order service");
    }
}
