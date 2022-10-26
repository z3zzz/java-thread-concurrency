import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Java의 built-in 아토믹 인크레멘터를 사용 -> thread safe
 * 일반 primitive 사용 -> thread unsafe (lock이 없음)
 *
 * 각 thread는 join 하지 않으면 프로그램 조기 종료됨
 *
 * 단순히 lock을 사용하는 것만으로도 부족함
 *
 *
 */
public class ConcurrencyLock {

    private static class Counter extends Thread {

        public static int items = 0;
        public static int itemsWithLock = 0;
        public static AtomicInteger aItems = new AtomicInteger(0);
        public ReentrantLock lock = new ReentrantLock();

        public Scanner sc = new Scanner(System.in);
        public String userInput = sc.nextLine();
        public static HashMap<Character, Integer> map = new HashMap<>();
        public static HashMap<Character, LongAdder> mapWithLock = new HashMap<>();
        public char[] characters = userInput.toCharArray();


        public void run() {
            for (char character: characters) {
                Integer count = map.get(character);

                if (count == null) {
                    map.put(character, 1);
                }
                else {
                    map.put(character, count + 1);
                }
            }

            for (char character: characters) {
                LongAdder count = mapWithLock.get(character);

                if (count == null)  {
                    count = new LongAdder();
                }

                count.increment();
                mapWithLock.put(character, count);
            }

            for (int i = 0; i < 1_000_000; i++) {
                items++;
                aItems.incrementAndGet();
            }

            for (int i = 0; i < 1_000_000; i++) {
                lock.lock();
                itemsWithLock++;
                lock.unlock();
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
        System.out.println("Total sum int (with lock): " + Counter.itemsWithLock);
        System.out.println("Total sum AtomicInteger (correct): " + Counter.aItems);
        System.out.println("Total character count (not correct)): " + Counter.map);
        System.out.println("Total character count (with lock)): " + Counter.mapWithLock);
    }
}
