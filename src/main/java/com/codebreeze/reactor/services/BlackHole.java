package com.codebreeze.reactor.services;

public interface BlackHole<T> {
    void consume(T t);
}
