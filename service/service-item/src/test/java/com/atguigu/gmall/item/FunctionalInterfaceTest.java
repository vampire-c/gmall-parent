package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FunctionalInterfaceTest {
    @Test
    public void testFunction() {
        Function<String, String> function = item -> {
            return item + "_suffix";
        };

        System.out.println(function.apply("555")); // 555_suffix
    }

    @Test
    public void testPredicate() {
        Predicate<String> predicate = item -> {
            return item.isEmpty();
        };

        System.out.println(predicate.test("")); // true
    }

    @Test
    public void testConsumer() {
        Consumer<String> consumer = item -> {
            System.out.println(item);
        };

        consumer.accept("test"); // test
    }

    @Test
    public void testSupplier() {
        Supplier<String> supplier  = () -> {
            return "Supplier";
        };

        System.out.println(supplier.get());
    }




}