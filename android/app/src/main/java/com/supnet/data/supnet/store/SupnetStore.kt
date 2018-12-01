package com.supnet.data.supnet.store

import io.reactivex.Completable
import io.reactivex.Single

interface SupnetStore {

    fun saveUserInfo(info: UserInfo): Completable

    fun getUserInfo(): Single<UserInfo>

}