package com.supnet.data

import io.reactivex.Observable

interface SupnetRepository {

    fun sendIntent(intent: SupnetIntent)

    fun userStates(): Observable<UserState>

    fun results(): Observable<SupnetResult>

}