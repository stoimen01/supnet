package com.supnet.data.local

import com.supnet.common.Nullable
import io.reactivex.Completable
import io.reactivex.Single

interface SupnetStore {

    fun saveUserInfo(info: UserInfo): Completable

    fun getUserInfo(): Single<Nullable<UserInfo>>

    fun removeUserInfo(): Completable

}