package com.test.mserrano;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import junit.framework.TestCase;

public class Tests extends TestCase {

    public void testA(){
        StringBuilder sb = new StringBuilder();
        Observable<String> observable = Observable.just("Hello")
                .observeOn(Schedulers.computation())
        /*.subscribe(s -> {
            sb.append(s);
            System.out.println(Thread.currentThread().getName() + ": " + s);
        })*/;

        String result = sb.toString();
        assertEquals("Hello", result);
    }

    public void testB(){
        StringBuilder sb = new StringBuilder();
        Observable<String> observable = Observable.just("A","B","C")
                .subscribeOn(Schedulers.computation());

        observable.subscribe(getObserver());
    }

    public void testC(){
        StringBuilder sb = new StringBuilder();
        Single.just("A")
                .subscribeOn(Schedulers.computation())
                .subscribe(getSingleObserver());
    }

    public void testD(){
        Observable.just("Some String")              // Computation
                //.subscribeOn(Schedulers.computation()) // -- changing the thread
                .map(str -> str.length())                // Computation
                .map(length -> 2 * length)              // Computation
                .subscribeOn(Schedulers.computation()) // -- changing the thread
                .subscribe(number -> System.out.println( "Number " + number + " - " + Thread.currentThread().getName()));// Computation
    }

    private Observer<String> getObserver() {
        io.reactivex.Observer<String> observer = new Observer<String>() {

            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe: " + Thread.currentThread().getName());
            }

            @Override
            public void onNext(String value) {
                System.out.println("onNext: " + value + Thread.currentThread().getName());
            }


            @Override
            public void onError(Throwable e) {
                System.out.println("onError: ");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete: All Done!" + Thread.currentThread().getName());
            }

        };
        return observer;
    }

    private SingleObserver<String> getSingleObserver() {
        return new SingleObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println(" onSubscribe " + Thread.currentThread().getName());
            }

            @Override
            public void onSuccess(String value) {
                System.out.println( " onSuccess : " + value + " - "+ Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError: ");
            }

        };
    }
}
