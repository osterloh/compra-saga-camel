package com.osterloh;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Header;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class CreditService {

    private int fullCredit;
    private Map<Long, Integer> orderValue = new HashMap<>();

    public CreditService() {
        this.fullCredit = 100;
    }

    public void newOrderValue(@Header("id") Long orderId, @Header("value") int value) {
        if(value > fullCredit) {
            throw  new IllegalStateException("Insufficient balance!");
        }

        fullCredit = fullCredit - value;
        orderValue.put(orderId, value);
    }

    public void cancelOrderValue(Long id) {
    System.out.println("OrderValue failed. Starting the canceling of order.");
    }

    public int getFullCredit() {
        return fullCredit;
    }
}
