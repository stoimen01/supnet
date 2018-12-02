package com.supnet.data.local

import com.supnet.common.Nullable
import io.reactivex.Completable
import io.reactivex.Single

class AndroidSupnetStore : SupnetStore {

    private var userInfo: UserInfo? = null

    override fun getUserInfo(): Single<Nullable<UserInfo>> {
        return Single.just(Nullable(userInfo))
    }

    override fun saveUserInfo(info: UserInfo): Completable {
        userInfo = info
        return Completable.complete()
    }

    override fun removeUserInfo(): Completable {
        userInfo = null
        return Completable.complete()
    }

}