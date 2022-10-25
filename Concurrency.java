package main.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Java의 built-in 아토믹 인크레멘터를 사용 -> thread safe
 * 일반 primitive 사용 -> thread unsafe (lock이 없음)
 */
public class Concurrency {

    private static class Counter extends Thread {

        public static int items = 0;
        public static AtomicInteger aItems = new AtomicInteger(0);

        public void run() {
            for (int i = 0; i < 1_000_000; i++) {
                items++;
                aItems.incrementAndGet();
            }
        }
    }

    public static void main(String args[]) throws InterruptedException {

        List<Counter> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            list.add(new Counter());
        }

        for (Counter c : list) c.start();
        for (Counter c : list) c.join();

        System.out.println("Total sum int (not correct): " + Counter.items);
        System.out.println("Total sum AtomicInteger (correct): " + Counter.aItems);
    }
}
