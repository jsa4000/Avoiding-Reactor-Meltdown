package com.example.demo.threading;

import org.junit.jupiter.api.Test;

public class FunctionalInterfaceIT {

    /**
     * A functional interface is an interface that contains only one abstract method.
     * They can have only one functionality to exhibit. From Java 8 onwards, lambda
     * expressions can be used to represent the instance of a functional interface. A
     * functional interface can have any number of default methods. Runnable,
     * ActionListener, Comparable are some of the examples of functional interfaces.
     */

    @FunctionalInterface
    interface Greeter {
        void sayHello(String name);
    }

    @Test
    void createFunctionalInterfaceWithImplementationTest() {
        Greeter greeter = new Greeter() {
            @Override
            public void sayHello(String name) {
                System.out.printf("Hello %s%n", name);
            }
        };
        greeter.sayHello("Javier");
    }

    @Test
    void implementingFunctionalInterfaceWithLambdaTest() {
        Greeter greeter = name -> System.out.printf("Hello %s%n", name);
        greeter.sayHello("Javier");
    }

}
