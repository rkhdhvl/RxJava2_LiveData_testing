package com.practice.rxjava2_livedata_testing

import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class RxImmediateSchedulerRule : TestRule {

    private val immediate = object : Scheduler()
    {

        override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
            // this prevents StackOverflowErrors when scheduling with a delay
            return super.scheduleDirect(run, 0, unit)
        }

        override fun createWorker(): Scheduler.Worker {
            return ExecutorScheduler.ExecutorWorker(Executor { it.run() },true)
        }

    }

    override fun apply(base: Statement?, description: Description?): Statement {
        return object :Statement(){
            @Throws(Throwable::class)
            override fun evaluate() {
                /*
                * RxAndroid's RxAndroidPlugins class provides hooks for overriding RxAndroid's Schedulers */
                RxAndroidPlugins.setInitMainThreadSchedulerHandler{ scheduler -> immediate}
                RxJavaPlugins.setInitIoSchedulerHandler { scheduler -> immediate}
                RxJavaPlugins.setInitComputationSchedulerHandler { scheduler -> immediate }
                RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler -> immediate }
                RxJavaPlugins.setInitSingleSchedulerHandler { scheduler -> immediate }


                try {
                    base!!.evaluate()
                } finally {
                    RxJavaPlugins.reset()
                    RxAndroidPlugins.reset()
                }

            }
        }
    }
}