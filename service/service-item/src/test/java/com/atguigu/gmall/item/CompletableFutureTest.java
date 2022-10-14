package com.atguigu.gmall.item;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
CompletableFuture： 异步编排
1、怎么启动一个异步任务：
     1)、X 继承 Thread; new Haha().start()
     2)、X 实现 Runnable；   new Thread(new Test()).start();
     3)、Callable：  有返回值，有异常
                     FutureTask<Integer> task = new FutureTask<>(new Heihei());
                     new Thread(task).start();
     4)、线程池: ExecutorService
         submit():  提交一个带返回值Future的任务； future.get();//等待任务执行完
         execute(): 提交一个返回void任
     5)、异步编排: CompletableFuture
         1）、启动一个异步的方式：
           runAsync(runnable)：  CompletableFuture<Void>            以异步方式启动一个任务并在默认的线程池(ForkJoinPool)执行。 返回 void
           runAsync(runnable,executor):CompletableFuture<Void>    以异步方式启动一个任务并在指定的线程池(executor)执行。 返回 void
                                                             可以调用future.get()等待异步结束
           supplyAsync(supplier): CompletableFuture<U>   以异步方式启动一个任务并在默认的线程池(ForkJoinPool)执行。返回异步结果
           supplyAsync(supplier,executor):CompletableFuture<U>  以异步方式启动一个任务并在指定的线程池(executor)执行。返回异步结
         2）、编排 then
             1)、future.get();//等待异步任务执行完，并获取结果
             2)、future.whenComplete(); 当异步结束后做一件事情.
                     正常完成、异常完成
             3)、future = future.exceptionally()； 异常兜底：
             4)、thenXXX：接下来干什么
                   1）、thenRun(runnable):                 接下来跑一个任务，以当前线程作为跑任务的线程，不开额外的异步线程
                   2）、thenRunAsync(runnable):            接下来跑一个任务，用默认线程池新开一个异步线程，
                   3）、thenRunAsync(runnable,executor):   接下来跑一个任务，用指定线程池新开一个异步线程，
                   ===============================================
                   1）、thenAccept(consumer):              接下来跑一个任务，接受到上次的结果，以当前线程作为跑任务的线程，没有返回值
                   2）、thenAcceptAsync(consumer)
                   3）、thenAcceptAsync(consumer,executor)
                   ===============================================
                   1）、thenApply(function):
                   2）、thenApplyAsync(function):
                   3）、thenApplyAsync(function,executor):
                   =====================================
                   thenCombine: 结合一个任务
       3)、组合多任务
             allOf();
             anyOf();
             thenCombine
             runAfterBot
启动一个异步任务，开始编写它的编排逻辑；
*/
public class CompletableFutureTest {
    static ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {
        // 1）、thenApply(function):       接下来跑一个任务，接受到上次的结果，以当前线程作为跑任务的线程，有返回值
        // 2）、thenApplyAsync(function):
        // 3）、thenApplyAsync(function,executor):
        CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> {
            return "-1-supplyAsync";
        }).thenApply(t -> {
            return t + "-2-thenApply";
        }).thenAcceptAsync(t -> {
            System.out.println(t + "-3-thenAcceptAsync");
        }, executorService).whenComplete((t, e) -> {
            System.out.println("结束.... t:" + t + "   异常:" + e);
        });

    }

    public static void main5thenAcceptAsync(String[] args) {
        // 1）、thenAccept(consumer):              接下来跑一个任务，接受到上次的结果，以当前线程作为跑任务的线程，没有返回值
        // 2）、thenAcceptAsync(consumer)
        // 3）、thenAcceptAsync(consumer,executor)
        CompletableFuture.supplyAsync(() -> {
            return "-1-supplyAsync";
        }).thenAccept(t -> {
            System.out.println(t + "-2-thenAccept");
        }).thenAcceptAsync(t -> {
            System.out.println(t + "-3-thenAcceptAsync");
        }, executorService);
    }

    public static void main4thenRunAsync(String[] args) throws InterruptedException {
        // 1）、thenRun(runnable):                 接下来跑一个任务，以当前线程作为跑任务的线程，不开额外的异步线程
        // 2）、thenRunAsync(runnable):            接下来跑一个任务，用默认线程池新开一个异步线程，
        // 3）、thenRunAsync(runnable,executor):   接下来跑一个任务，用指定线程池新开一个异步线程，
        CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread() + "-1-runAsync");
        }, executorService).thenRun(() -> {
            System.out.println(Thread.currentThread() + "-2-thenRun");
        }).thenRunAsync(() -> {
            System.out.println(Thread.currentThread() + "-3-thenRunAsync");
        }, executorService).thenRunAsync(() -> {
            System.out.println(Thread.currentThread() + "-4-thenRunAsync");
        });

        Thread.sleep(100000);
    }


    public static void main3(String[] args) throws Exception {

        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread() + "CompletableFuture.supplyAsync");
            return 1 + 2 / 0;
        }, executorService).exceptionally((e) -> {
            System.out.println("异常" + e);
            return 100;
        });

        System.out.println(completableFuture.get());

        //     .whenComplete((t, e) -> {
        // if (!StringUtils.isEmpty(e)) {
        //     System.out.println("异常" + e);
        // } else {
        //     System.out.println("正常执行" + t);
        //     System.out.println(t * 2);
        // }
    }

    public static void main2(String[] args) throws InterruptedException, ExecutionException {
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread() + "runAsync(runnable,executor)");
        }, executorService);
        completableFuture.get();

        System.out.println(Thread.currentThread() + "System.out.println();");

        Thread.sleep(100000000);
    }

    public static void main1(String[] args) throws ExecutionException, InterruptedException {
        // new Test1().start();
        // new Thread(new Test2()).start();
        // new Thread(new FutureTask<>(new Test3())).start();
        executorService.execute(() -> {
            System.out.println("Executors.newFixedThreadPool");
        });

        Future<String> submit = executorService.submit(() -> {
            return "executorService.submit";
        });
        System.out.println(submit.get());

        System.out.println(3);
    }

    public static class Test1 extends Thread {
        @Override
        public void run() {
            System.out.println("extends Thread");
        }
    }

    public static class Test2 implements Runnable {
        @Override
        public void run() {
            System.out.println("implements Runnable");
        }
    }

    public static class Test3 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("implements Callable<Integer>");
            return 1 / 1;
        }
    }
}
