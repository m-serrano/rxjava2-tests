package com.test.mserrano;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import junit.framework.TestCase;

import java.util.concurrent.TimeUnit;

public class BlockingSubscribeTests extends TestCase {


    //https://stackoverflow.com/questions/54128079/rxjava2-blockingsubscribe-vs-subscribe
    public void test1() {
        System.out.println("Before blockingSubscribe");
        System.out.println("Before Thread: " + Thread.currentThread());

        Observable.interval(1, TimeUnit.SECONDS)
                .take(5)
                .blockingSubscribe(t -> {
                    System.out.println("Thread: " + Thread.currentThread());
                    System.out.println("Value:  " + t);
                });

        System.out.println("After blockingSubscribe");
        System.out.println("After Thread: " + Thread.currentThread());
    }

    public void test2() throws InterruptedException{
        System.out.println("Before subscribe");
        System.out.println("Before Thread: " + Thread.currentThread());

        Observable.timer(1, TimeUnit.SECONDS, Schedulers.io())
                .concatWith(Observable.timer(1, TimeUnit.SECONDS, Schedulers.single()))
                .subscribe(t -> {
                    System.out.println("Thread: " + Thread.currentThread());
                    System.out.println("Value:  " + t);
                });


        System.out.println("After subscribe");
        System.out.println("After Thread: " + Thread.currentThread());

        // RxJava uses daemon threads, without this, the app would quit immediately
        Thread.sleep(3000);

        System.out.println("Done");
    }

}
