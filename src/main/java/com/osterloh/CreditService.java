package com.osterloh;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class CreditService {

    private int fullCredit;
    private Map<Long, Integer> orderValue = new HashMap<>();

    public CreditService() {
        this.fullCredit = 100;
    }

    public void NewOrderValue(Long orderId, int value) {
        if(value > fullCredit) {
            throw  new IllegalStateException("Insufficient balance!");
        }

        fullCredit = fullCredit - value;
        orderValue.put(orderId, value);
    }

    public void cancelOrderValue(Long id) {
        fullCredit = fullCredit + orderValue.get(id);
        orderValue.remove(id);
    }

    public int getFullCredit() {
        return fullCredit;
    }
}
