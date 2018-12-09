package com.supnet.domain.store

import com.supnet.domain.entities.Friend
import com.supnet.domain.entities.Invitation
import io.reactivex.Observable

interface StoreManager {

    fun friends(): Observable<List<Friend>>

    fun invitations(): Observable<List<Invitation>>

}