package com.supnet.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.rxkotlin.plusAssign

abstract class BaseViewModel <S : Any, E : Any, VE : E, F : Any> (
    initialState: S,
    initialEffects: List<F>? = null,
    protected val schedulersProvider: SchedulersProvider,
    private val reducer: StateReducer <S, E, F>
) : AutoDisposableViewModel() {

    constructor(
        initialState: S,
        initialEffect: F,
        schedulersProvider: SchedulersProvider,
        reducer: StateReducer<S, E, F>
    ) : this(initialState, listOf(initialEffect), schedulersProvider, reducer)

    private val initialResult = ReduceResult(initialState, initialEffects)

    private val events = PublishRelay.create<E>()

    val liveState : LiveData<S> by lazy {
        val ls = MutableLiveData<S>()
        disposables += events
            .observeOn(schedulersProvider.computation())
            .scan(initialResult, reducer::reduce)
            .distinctUntilChanged()
            .observeOn(schedulersProvider.ui())
            .subscribe { (state, effects) ->
                effects?.forEach(this::onEffect)
                ls.value = state
            }
        return@lazy ls
    }

    protected abstract fun onEffect(effect: F)

    protected fun onEvent(event: E) = events.accept(event)

    fun onViewEvent(event: VE) = events.accept(event)

}