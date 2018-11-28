package com.supnet.common

interface StateReducer<S: Any, E: Any, F: Any> {

    fun reduce(lastResult: ReduceResult<S, F>, event: E): ReduceResult<S, F>

    fun S.repeat(): ReduceResult<S, F> = ReduceResult(this)

    fun S.effectOnly(effect: F): ReduceResult<S, F> =
        ReduceResult(this, effect)

    fun stateOnly(newState: S): ReduceResult<S, F> =
        ReduceResult(newState)

    fun resultOf(newState: S, effect: F) = ReduceResult(newState, effect)

    fun resultOf(newState: S, effects: List<F>) = ReduceResult(newState, effects)

    fun resultOf(newState: S): ReduceResult<S, F> = ReduceResult(newState)

    fun throwInvalid(state: S, event: E): Nothing = throw IllegalEventException(state, event)

    class IllegalEventException(state: Any, event: Any) : Exception("Illegal event: $event happened on state: $state")

}