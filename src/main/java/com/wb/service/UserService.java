package com.wb.service;

import com.wb.spring.Autowired;
import com.wb.spring.Component;
import com.wb.spring.Scope;

/**
 * @author wb
 * @date 2024/8/20 10:35
 **/
@Component("userService")
@Scope
public class UserService{

    @Autowired
    private OrderService orderService;

    public OrderService getOrderService() {
        return orderService;
    }
}
