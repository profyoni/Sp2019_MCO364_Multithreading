package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
class Outer
{
    static int x;
    static class Inner{
        void foo()
        {
            Outer.x = 8;
        }
    }
}

class SharedObject{
    public static List<String> list = Collections.synchronizedList(new ArrayList<String>());
}

class ListAdder extends Thread
{
    ListAdder(String name)
    {
        this.name = name;
    }
    static Object lock = new Object();
    static int count = 0;
    static final int MAX_COUNT = 10;
    private String name;

    public ListAdder() {

    }

    @Override
    public void run()
    {
        for (int i=0;i<MAX_COUNT;i++)
        {
            // synchronized ( lock )
            {
                SharedObject.list.add(i+"");
                System.out.printf( "%10s %10d : %10d%n",
                        name, Thread.currentThread().getId(), i);
                try {
                    Thread.sleep( (int) (Math.random()* 5));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // System.out.println(i);
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
    static JButton button;
    static ExecutorService ex;
    public static void main(String[] args) {
        JFrame app = new JFrame();
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        app.setSize(500,500);
        button = new JButton("Press Me");
        app.add(button);

        ex = Executors.newFixedThreadPool(4);


        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        slowTask();
                    }
                };
                ex.execute(runnable);
            }
        });

        app.setVisible(true);
    }

    private static void  slowTask() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (((ThreadPoolExecutor) ex).getActiveCount() >= 1)
                button.setText("Started");
            }
        });


        for (int i=0;i<1_000_000;i++)
        {
            System.out.println(i);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (((ThreadPoolExecutor) ex).getActiveCount() < 1)
                    button.setText("Completed");
            }
        });
    }

    public static void main3(String[] args) {
        final int MaxRunnables = 5;
        ExecutorService ex = Executors.newFixedThreadPool(4);
        for (int i = 0; i < MaxRunnables; i++) {
            Runnable runnable1 = new ListAdder("" + (char)('A' + i) );
            ex.execute(runnable1);
        }
        ex.shutdown();
//        try {
//            ex.awaitTermination(5, TimeUnit.MINUTES);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        while (! ex.isTerminated()) {
        try{
            Thread.sleep(100);
        }catch (Exception e){}
        }

        System.out.println(SharedObject.list.size());
    }

    public static void main2(String[] args) {

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
