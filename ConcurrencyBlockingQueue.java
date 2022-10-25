import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 프로듀서, 컨슈머 모델 (BlockingQueue 기반으로 Threadsafe)
 * 최대 CAPACITY 만큼 queue에 채울 수 있고, 가득찰 시 프로듀서는 500ms sleep, 
 * 컨슈머는 500ms 마다 queue 소비, 총 CONSUMERS 개
 * queue에는 총 20개 만들어지고 -> 물론 그 도중에 계속 생산, 소비 반복되고,
 * 결국 0이 되고, 컨슈머 멈춤
 *
 * BlockingQueue는 자동으로 threadsafe 기능을 제공하기 때문에,
 * queue가 꽉 차면(CAPACITY만큼) 생산을 500ms 멈춤 
 */
public class ConcurrencyBlockingQueue{

    private static final String STOP = "producer stopped";
    private static final int CONSUMERS = 3;
    private static final int CAPACITY = 5;

    static class Producer extends Thread {
        BlockingQueue queue;

        Producer(BlockingQueue queue) {
            this.queue = queue;
        }

        // 총 20개를 queue에 넣을 때까지 반복함 (그 동안 소비, 충전 반복)
        public void run() {
            int nItems = 1;
            while (nItems <= 20) {
                try {
                    if (queue.remainingCapacity() > 0) {
                        String item = "item" + nItems;
                        queue.add(item);
                        String capacity = String.format(" [%d/%d]", queue.size(), CAPACITY);
                        System.out.println("Producer is adding " + item + capacity );
                        nItems++;
                        Thread.sleep(100);
                    } else {
                        System.out.println("Producer queue is full");
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 이제 queue가 비워질 때까지 기다림
            while (queue.remainingCapacity() != CAPACITY) {
                try {
                    System.out.println(queue.remainingCapacity());
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 컨슈머의 아래 while loop를 break시킴
            for (int i = 0; i < CONSUMERS; i++) {
                queue.add(STOP);
            }
        }
    }

    static class Consumer extends Thread {
        BlockingQueue queue;
        String name;

        Consumer(String name, BlockingQueue queue) {
            this.name = name;
            this.queue = queue;
        }

        // 500ms 마다 queue에서 가져감 (각 컨슈머 쓰레드 당)
        public void run() {
            while (true) {
                try {
                    String item = (String)queue.take();
                    if (item.equals(STOP)) {
                        break;
                    }
                    System.out.println(name + " took " + item);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 프로듀서, 컨슈머 공통 queue
        BlockingQueue queue = new ArrayBlockingQueue<String>(CAPACITY);
        // 프로듀서 생성
        new Producer(queue).start();

        Thread.sleep(100);

        // 컨슈머 생성
        for (int i = 0; i < CONSUMERS; i++) {
            new Consumer("Consumer" + i, queue).start();
        }
    }
}
