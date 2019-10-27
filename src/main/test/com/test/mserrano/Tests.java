package com.test.mserrano;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.subscribers.BlockingSubscriber;
import io.reactivex.schedulers.Schedulers;
import junit.framework.TestCase;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

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

    //https://stackoverflow.com/questions/44984730/rxandroid-whats-the-difference-between-subscribeon-and-observeon
    public void testD1(){
        Observable.just("Some String")              // Computation
                .subscribeOn(Schedulers.newThread()) // -- changing the thread
                .map(str -> str.length())                // Computation
                .map(length -> 2 * length)              // Computation
                //.subscribeOn(Schedulers.computation()) // -- changing the thread
                //.observeOn(Schedulers.computation())
                //.subscribe(number -> System.out.println( "result: " + number + " - " + Thread.currentThread().getName()));// Computation
                .subscribe(getIntObserver());
    }

    //https://stackoverflow.com/questions/44984730/rxandroid-whats-the-difference-between-subscribeon-and-observeon
    public void testD2(){
        Observable.range(1, 10)              // Computation
                .subscribeOn(Schedulers.newThread()) // -- changing the thread
                .map(length -> length * 2 )              // Computation
                //.subscribeOn(Schedulers.computation()) // -- changing the thread
                //.observeOn(Schedulers.computation())
                //.subscribe(number -> System.out.println( "result: " + number + " - " + Thread.currentThread().getName()));// Computation
                .subscribe(getIntObserver());
    }

    //https://stackoverflow.com/questions/44984730/rxandroid-whats-the-difference-between-subscribeon-and-observeon
    public void testD3(){
        Observable.range(1, 10)              // Computation
                .subscribeOn(Schedulers.newThread()) // -- changing the thread
                .map(length -> length * 2 )              // Computation
                //.subscribeOn(Schedulers.computation()) // -- changing the thread
                //.observeOn(Schedulers.computation())
                //.subscribe(number -> System.out.println( "result: " + number + " - " + Thread.currentThread().getName()));// Computation
                .blockingSubscribe(getIntObserver());
    }


    //https://github.com/ReactiveX/RxJava/tree/2.x
    public void testE(){
        Observable.create(emitter -> {
            while (!emitter.isDisposed()) {
                long time = System.currentTimeMillis();
                emitter.onNext(time);
                if (time % 2 != 0) {
                    emitter.onError(new IllegalStateException("Odd millisecond!"));
                    break;
                }
            }
        }).subscribe(System.out::println, Throwable::printStackTrace);
    }

    public void testF() throws InterruptedException{
        Flowable.fromCallable(() -> {
            Thread.sleep(1000); //  imitate expensive computation
            System.out.println( "En el Observable: " + Thread.currentThread().getName());
            return "Done";
        }).map(m -> 200)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())

                .subscribe(m -> System.out.println( "En el Subscriber: " + m + ": " + Thread.currentThread().getName()), Throwable::printStackTrace);

        Thread.sleep(2000);
    }

    //Concurrency within a flow
    public void testG(){
        Flowable.range(1, 10)
                .observeOn(Schedulers.computation())
                .map(v -> v * v)
                .blockingSubscribe(m -> System.out.println( "En el Subscriber: " + m + ": " + Thread.currentThread().getName()));
    }

    //Parallel processing
    public void testI(){
        Flowable.range(1, 10)
                .flatMap(v ->
                        Flowable.just(v)
                                .subscribeOn(Schedulers.computation())
                                .map(w -> w * w)
                )
                .blockingSubscribe(m -> System.out.println( "En el Subscriber: " + m + ": " + Thread.currentThread().getName()));
    }

    //Parallel processing
    public void testI1() {
        Flowable.range(1, 10)
                .flatMap(v ->
                        Flowable.just(v)
                                .subscribeOn(Schedulers.computation())
                                .map(w -> {
                                    int x = w * w;
                                    System.out.println("En el Observable: " + x + " - " + Thread.currentThread().getName());
                                    return x;
                                })
                )
                //.subscribeOn(Schedulers.computation())
                .blockingSubscribe(m -> System.out.println("En el Subscriber: " + m + ": " + Thread.currentThread().getName()));
    }

    //Parallel processing
    public void testI2() {
        Flowable.range(1, 10)
                .flatMap(v ->
                        Flowable.just(v)
                                .subscribeOn(Schedulers.computation())
                                .map(w -> {
                                    int x = w * w;
                                    System.out.println("En el Observable: " + x + " - " + Thread.currentThread().getName());
                                    return x;
                                })
                )
                //.subscribeOn(Schedulers.computation())
                .subscribe(m -> System.out.println("En el Subscriber: " + m + ": " + Thread.currentThread().getName()));
    }

    //Parallel processing
    public void testI3() {
        Flowable.range(1, 10)
                .parallel()
                .runOn(Schedulers.computation())
                .map(w -> {
                            int x = w * w;
                            System.out.println("En el Observable: " + x + " - " + Thread.currentThread().getName());
                            return x;
                        }
                ).sequential()
                //.observeOn(Schedulers.newThread())
                .blockingSubscribe(m -> System.out.println("En el Subscriber: " + m + ": " + Thread.currentThread().getName()));
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

    private Observer<Integer> getIntObserver() {
        io.reactivex.Observer<Integer> observer = new Observer<Integer>() {

            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe: " + Thread.currentThread().getName());
            }

            @Override
            public void onNext(Integer value) {
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
