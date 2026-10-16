package com.example.playground.proxies;

import java.lang.reflect.Proxy;

public class ReflectionProxyExample {

    public void testProxy(){
        ShipmentService target =
                new ShipmentServiceImpl();

        ShipmentService proxy =
                (ShipmentService) Proxy.newProxyInstance(
                        ShipmentService.class.getClassLoader(),
                        new Class<?>[]{ShipmentService.class},
                        (proxyObject, method, args) -> {

                            System.out.println("Before");

                            Object result =
                                    method.invoke(target, args);

                            System.out.println("After");

                            return result;
                        }
                );

        proxy.updateShipment();
    }
}
