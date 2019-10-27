package com.test.mserrano;

import io.reactivex.Observable;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class AnotherExample extends TestCase {


    public static class ApiTest {

        public Observable<String> apiCall() throws InterruptedException{
            List<String> list = Arrays.asList("Lunes", "Martes", "Miercoles", "Jueves", "Viernes");

            return Observable.create(emitter -> {
                if(!emitter.isDisposed()){
                    Thread.sleep(1000);

                    list.forEach(a -> {
                        System.out.println("In apiCall - " + a + " - " + Thread.currentThread());
                        emitter.onNext(a);
                    });
                    emitter.onComplete();
                }

            });
        }

        public Observable<String> anotherApiCall(String val){

            return Observable.create(emitter -> {
                if(!emitter.isDisposed()) {
                    Thread.sleep(1000);
                    System.out.println("In anotherApiCall - " + Thread.currentThread());
                    emitter.onNext("8-"+val);
                    emitter.onComplete();
                }
            });

        }

    }

    public void test1() throws InterruptedException{
        ApiTest service = new ApiTest();
        service.apiCall().
                flatMap(m -> service.anotherApiCall(m))
        .blockingSubscribe(m -> System.out.println( "En el Subscriber: " + m + ": " + Thread.currentThread().getName()), Throwable::printStackTrace);
    }

}
