package com.company;

import java.util.*;

class SharedObject{
    public static List<String> list = Collections.synchronizedList(new ArrayList<String>());
}

class ListAdder extends Thread
{
    static Object lock = new Object();
    static int count = 0;
    static final int MAX_COUNT = 1_000_000;
    @Override
    public void run()
    {
        for (int i=0;i<MAX_COUNT;i++)
        {
            // synchronized ( lock )
            {
                SharedObject.list.add(i+"");
            }
        }
    }
}
class Adder extends Thread
{
    static Object lock = new Object();
    static int count = 0;
    static final int MAX_COUNT = 1_000_000;
    @Override
    public void run()
    {
        for (int i=0;i<MAX_COUNT;i++)
        {
           // synchronized ( lock )
            {
                count++;
            }
        }
    }
}


class Adder2 implements Runnable {
    static Object lock = new Object();
    static int count = 0;
    static final int MAX_COUNT = 1_000;

    @Override
    public void run() {
        {
            for (int i = 0; i < MAX_COUNT; i++) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock) {
                    count++;
                    System.out.println(Thread.currentThread().getId() + ":" + count);
                }
            }
        }
    }
}

class MyArrayList<T> {
    private T[] backingStore = (T[]) new Object[10];
    private int ip;

    public synchronized void add(T elt) {
        backingStore[ip++] = elt;
    }

    public T get(int index) {
        return backingStore[index];
    }

    public synchronized T set(int index, T elt) {
        T old = backingStore[index];
        backingStore[index] = elt;
        return old;
    }
}

public class Main {
    public static void main(String[] args) {

        Runnable runnable1 = new ListAdder();
        Thread thread1 = new Thread(runnable1);
        thread1.start();

        Runnable runnable2 = new ListAdder();
        Thread thread2 = new Thread(runnable2);
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
        }

        System.out.println(SharedObject.list.size());

	
    }
}
