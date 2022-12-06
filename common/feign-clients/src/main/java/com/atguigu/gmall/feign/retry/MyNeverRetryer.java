package com.atguigu.gmall.feign.retry;

import feign.RetryableException;
import feign.Retryer;

public class MyNeverRetryer implements Retryer {

    int start = 1;
    int end = 10;

    @Override
    public void continueOrPropagate(RetryableException e) {
        // 不重试
        throw e;

        // System.out.println("自定义重试器....");
        // if (start++ > end) {
        //     throw e;
        // }
    }

    @Override
    public Retryer clone() {
        return new MyNeverRetryer();
    }
}
