package com.zb.lib_base.adapter;

@FunctionalInterface
public interface IntConsumer {

    void accept(int position);

    default void delete(int i) {

    }
}