package com.edgn.api;

public interface EventAccess {
    <L> void add(Class<L> listenerType, L listener);
    <L> void remove(Class<L> listenerType, L listener);
}