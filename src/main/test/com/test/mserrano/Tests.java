package com.test.mserrano;

import io.reactivex.Observable;
import junit.framework.TestCase;

public class Tests extends TestCase {

    public void testA(){
        StringBuilder sb = new StringBuilder();
        Observable<String> observable = Observable.just("Hello");
        observable.subscribe(s -> sb.append(s));

        String result = sb.toString();
        assertEquals("Hello", result);
    }

}
