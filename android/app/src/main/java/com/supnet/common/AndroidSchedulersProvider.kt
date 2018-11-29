package com.supnet.common

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AndroidSchedulersProvider : SchedulersProvider {

    override fun ui(): Scheduler = AndroidSchedulers.mainThread()

    override fun io() = Schedulers.io()

    override fun computation() = Schedulers.computation()

}