package com.supnet.domain.user

import io.reactivex.Observable

interface UserManager {

    fun sendIntent(intent: UserManagerIntent)

    fun results(): Observable<UserManagerResult>

    fun userStates(): Observable<UserState>

}