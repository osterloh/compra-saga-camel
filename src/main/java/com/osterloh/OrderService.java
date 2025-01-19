package com.osterloh;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class OrderService {

    private Set<Long> orders = new HashSet<>();

    public void newOrder(Long id) {
        orders.add(id);
    }

    public void cancelOrder(Long id) {
        orders.remove(id);
    }
}
