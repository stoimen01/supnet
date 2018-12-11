package com.supnet.domain.friends

import com.supnet.domain.entities.Friend
import com.supnet.domain.entities.Invitation
import io.reactivex.Observable

interface FriendsManager {

    fun sendIntent(intent: FriendsManagerIntent)

    fun results(): Observable<FriendsManagerResult>

    fun friends(): Observable<List<Friend>>

    fun invitations(): Observable<List<Invitation>>

}