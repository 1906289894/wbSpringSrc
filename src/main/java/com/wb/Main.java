package com.wb;

import com.wb.service.OrderService;
import com.wb.service.UserService;
import com.wb.spring.AppConfig;
import com.wb.spring.ClassPathApplicationContext;

/**
 * ${desc}
 *
 * @author wb
 * @date 2024/8/20 10:26
 **/
public class Main {
    public static void main(String[] args) {
        ClassPathApplicationContext context = new ClassPathApplicationContext(AppConfig.class);
        UserService userService = (UserService)context.getBean("userService");
        UserService userService2 = (UserService)context.getBean("userService");
        OrderService orderService = userService.getOrderService();
        OrderService orderService2 = userService2.getOrderService();
        System.out.println(orderService2);
        System.out.println(orderService);
    }
}