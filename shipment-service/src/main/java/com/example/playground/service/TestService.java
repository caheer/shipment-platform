package com.example.playground.service;


import com.example.shipment.model.Shipment;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

@Service
public class TestService {

    public void playWithReflection() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = Shipment.class;
        Field[] fields = Shipment.class.getDeclaredFields();

        for (Field field : fields) {
            System.out.println(field.getName());
        }

        Method[] methods = Shipment.class.getDeclaredMethods();

        for (Method method : methods) {
            System.out.println(method.getName());
        }

        Constructor<?>[] constructors =
                Shipment.class.getDeclaredConstructors();

        Constructor<?> constructor =
                clazz.getDeclaredConstructor();

        Object obj = constructor.newInstance();

        Shipment shipment =
                Shipment.class
                        .getDeclaredConstructor()
                        .newInstance();

        shipment.setShipmentName("TTTTADASDASDT");
        System.out.println(shipment.getShipmentName());

        System.out.println("Reflection pause");
    }

    public void useIt(){


        Function<String, Integer> stringLength =
                text -> text.length();

        Integer length = stringLength.apply("shipment");

        List<Number> numbers = new ArrayList<>();
        List<Integer> integers = new ArrayList<>();
        List<Shipment> shipments = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        Number numberResult = first(numbers);
        Integer integerResult = first(integers);

        Number numberResult2 = second(numbers);
        Number integerResult2 = second(integers);
        Integer integerResult2b = second(integers);
        //String stringResult2 = second(strings);

        HashMap<String, String> stringMap = new HashMap<>();
        stringMap.put("ASD","ASDA");


        Set<String> stringSet =     Set.of("asd","ASDF");
        Shipment shipment = new Shipment(UUID.randomUUID());
/*
        ShipmentValidator shipmentValidatorLiteral = s -> s.getShipmentName() != null;
        validateShipment(shipmentValidatorLiteral,shipment);

        validateShipment(s -> s.getShipmentName() != null,shipment);*/

        String shipmentSummary = prepareShipmentSummary(s -> s.getShipmentName() + s.getShipmentDescription(),shipment);

        String shipmentSummaryByMethodsReference = prepareShipmentSummary(Shipment::getSummary,shipment);
    }

    private String prepareShipmentSummary(Function<Shipment,String> summaryCreator,Shipment s){
        return summaryCreator.apply(s);
    }
/*
    private void validateShipment(ShipmentValidator sv, Shipment s){
        boolean validationResult = sv.validate(s);
    }*/

    public <T> T first(List<T> list) {
        return list.get(0);
    }

    public <T extends Number> T second(List<T> list) {
        return list.get(0);
    }

    public <T extends Shipment> void process(List<? extends T> list) {
    }

}

