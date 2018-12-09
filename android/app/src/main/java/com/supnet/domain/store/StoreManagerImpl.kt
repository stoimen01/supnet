package com.supnet.domain.store

import com.supnet.data.local.SupnetStore
import com.supnet.data.remote.ws.WsClient
import com.supnet.data.remote.ws.WsEvent
import com.supnet.domain.entities.Friend
import com.supnet.domain.entities.Invitation
import io.reactivex.Observable

class StoreManagerImpl(
    wsClient: WsClient,
    private val store: SupnetStore
): StoreManager {

    init {
        wsClient
            .socketMessages()
            .flatMapCompletable {
                return@flatMapCompletable when (it) {
                    is WsEvent.WsMessageEvent.InvitationAccepted -> {
                        store.addFriend(Friend(it.friendId, it.friendName))
                    }
                    is WsEvent.WsMessageEvent.InvitationReceived -> {
                        store.addInvitation(
                            Invitation(
                                id = it.invitationId,
                                senderName = it.senderName,
                                message = it.message
                            )
                        )
                    }
                }
            }
            .subscribe()
    }

    override fun friends(): Observable<List<Friend>> = store.friends()

    override fun invitations(): Observable<List<Invitation>> = store.invitations()

}