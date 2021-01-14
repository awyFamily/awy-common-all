package com.awy.common.util.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * jdk8 通用 Builder 模式构建器
 * @param <T>
 */
public class Jdk8Builder<T> {

    private final Supplier<T> instance;

    private List<Consumer<T>> modifiers = new ArrayList<>();

    public Jdk8Builder(Supplier<T> instance){
        this.instance = instance;
    }

    public static <T> Jdk8Builder<T> of(Supplier<T> instance){
        return new Jdk8Builder<>(instance);
    }

    public <P1> Jdk8Builder<T> with(Consumer1<T,P1> consumer, P1 p1){
        Consumer<T> c = instance -> consumer.accept(instance,p1);
        modifiers.add(c);
        return this;
    }

    public <P1,P2> Jdk8Builder<T> with(Consumer2<T,P1,P2> consumer, P1 p1, P2 p2){
        Consumer<T> c = instance -> consumer.accept(instance,p1,p2);
        modifiers.add(c);
        return this;
    }

    public <P1,P2,P3> Jdk8Builder<T> with(Consumer3<T,P1,P2,P3> consumer, P1 p1, P2 p2, P3 p3){
        Consumer<T> c = instance -> consumer.accept(instance,p1,p2,p3);
        modifiers.add(c);
        return this;
    }

    public T build(){
        T value = instance.get();
        modifiers.forEach(modifier -> modifier.accept(value));
        modifiers.clear();
        return value;
    }


    @FunctionalInterface
    public interface Consumer1<T,P1>{
        void accept(T t, P1 p1);
    }

    @FunctionalInterface
    public interface Consumer2<T,P1,P2>{
        void accept(T t, P1 p1, P2 p2);
    }

    @FunctionalInterface
    public interface Consumer3<T,P1,P2,P3>{
        void accept(T t, P1 p1, P2 p2, P3 p3);
    }
}
