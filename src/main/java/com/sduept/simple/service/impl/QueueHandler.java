package com.sduept.simple.service.impl;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

@Component
public class QueueHandler {

    private final BlockingQueue<String> queues = new ArrayBlockingQueue<String>(10);

    public void reciveMsg(String msg) {
        try {
            queues.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void handleQueue() {
        new Thread(()->{
            try {
                while (true) {
                    String take = queues.take();
                    System.out.println(take);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
