/**
 * 방향성
 *
 * Task1, 2(sub thread), 3(main thread) 가 parrelel 하게 run 됨
 * shceduling 되어서 번갈아가면서 실행됨
 * Thread 만드는 방법: extends Thread / new Thread()
 * 
 * Thread 라이프사이클
 *
 * New
 * Runnable
 * Running
 * Blocked / Waiting
 * Terminated / Dead
 *
 * 추가 Method
 *
 * setPriority() / 말 그대로
 * join() / 해당 thread 끝날 때까지 기다림
 *
 * */


class Task1 extends Thread {
  public void run() {
    System.out.println("Task1 start");

    for (int i = 101;i < 199;i++) {
      System.out.print(i + " ");
    }

    System.out.println("Task1 end");
  }
}

class Task2 implements Runnable {
  @Override
  public void run() {
    System.out.println("Task2 start");

    for (int i = 201;i < 299;i++) {
      System.out.print(i + " ");
    }

    System.out.println("Task2 end");
  }
}

public class ThreadRun {

  public static void main(String[] args) {
    // Task1
    System.out.println("Task1 Kickoff");
    Task1 task1 = new Task1();
    task1.start();

    // Task2
    System.out.println("Task2 Kickoff");
    Task2 task2 = new Task2();
    Thread task2Thread = new Thread(task2);
    task2Thread.start();

    task1.join();
    task2Thread.join();

    // Task3
    System.out.println("Task3 Kickoff");

    for (int i = 301;i < 399;i++) {
      System.out.print(i + " ");
    }

    System.out.println("Task3 end");

  }
}
