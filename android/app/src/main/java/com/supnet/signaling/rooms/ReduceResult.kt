package com.supnet.signaling.rooms

data class ReduceResult<out S, out F>(
    val state: S,
    val effects: List<F>?
) {
    constructor(state: S): this(state, null)
    constructor(state: S, effect: F): this(state, listOf(effect))
}