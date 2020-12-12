package com.codahale.metrics;

import java.util.concurrent.atomic.LongAdder;

public class Counter implements Metric, Counting {

    private final LongAdder count = new LongAdder();

    public Counter() {
    }

    public void inc() {
        this.inc(1L);
    }

    public void inc(long n) {
        this.count.add(n);
    }

    public void dec() {
        this.dec(1L);
    }

    public void dec(long n) {
        this.count.add(-n);
    }

    public long getCount() {
        return this.count.sum();
    }
}
