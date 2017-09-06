package com.hou.lock.utils;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by Zxl on 2017/9/6
 */

public class LockBus {

    private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    public void send(Object o) {
        bus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return bus;
    }

    private static final LockBus instance = new LockBus();

    public static LockBus getDefault() {
        return instance;
    }
}
