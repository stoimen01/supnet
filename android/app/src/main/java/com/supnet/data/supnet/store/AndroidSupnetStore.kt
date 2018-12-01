package com.supnet.data.supnet.store

import io.reactivex.Completable
import io.reactivex.Single

class AndroidSupnetStore : SupnetStore {

    private var userInfo: UserInfo? = null

    override fun getUserInfo(): Single<UserInfo> {
        return if (userInfo != null) {
            Single.just(userInfo)
        } else {
            Single.error(Throwable("User info not available"))
        }
    }

    override fun saveUserInfo(info: UserInfo): Completable {
        userInfo = info
        return Completable.complete()
    }

}