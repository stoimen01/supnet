package com.supnet.domain.friends

import com.jakewharton.rxrelay2.PublishRelay
import com.supnet.data.local.SupnetStore
import com.supnet.data.remote.rest.SupnetRestClient
import com.supnet.data.remote.ws.WsClient
import com.supnet.data.remote.ws.WsEvent
import com.supnet.domain.AcceptInvitationRequest
import com.supnet.domain.InvitationRequest
import com.supnet.domain.RejectInvitationRequest
import com.supnet.domain.entities.Friend
import com.supnet.domain.entities.Invitation
import com.supnet.domain.friends.FriendsManagerIntent.*
import com.supnet.domain.friends.FriendsManagerResult.InvitationResult
import com.supnet.domain.friends.FriendsManagerResult.InvitationResult.*
import com.supnet.domain.friends.FriendsManagerResult.*
import com.supnet.domain.friends.FriendsManagerResult.AcceptInvitationResult.*
import com.supnet.domain.friends.FriendsManagerResult.RejectInvitationResult.*
import io.reactivex.Observable

class FriendsManagerImpl(
    private val supnetClient: SupnetRestClient,
    private val wsClient: WsClient,
    private val store: SupnetStore
): FriendsManager {

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

    private val intents = PublishRelay.create<FriendsManagerIntent>()

    private val results: Observable<FriendsManagerResult> by lazy {
        return@lazy intents
            .flatMap<FriendsManagerResult> { intent ->
                return@flatMap when (intent) {
                    is InvitationIntent -> onInvitation(intent)
                    is AcceptInvitation -> onAcceptInvitation(intent)
                    is RejectInvitation -> onRejectInvitation(intent)
                    is Connect -> TODO()
                }
            }
            .publish()
            .refCount()
    }

    private fun onInvitation(intent: InvitationIntent): Observable<InvitationResult> {
        return store
            .getToken()
            .flatMapObservable { (token) ->
                return@flatMapObservable if (token == null) {
                    Observable.just(InvitationFailure)
                } else {
                    val invitation = InvitationRequest(intent.recipient, intent.message)
                    supnetClient
                        .sendInvitation(token, invitation)
                        .andThen(store.removeUserData())
                        .andThen(Observable.just<InvitationResult>(InvitationSend))
                }
            }
            .onErrorReturn {
                it.printStackTrace()
                InvitationFailure
            }
    }

    private fun onAcceptInvitation(intent: AcceptInvitation): Observable<AcceptInvitationResult> {
        return store
            .getToken()
            .flatMapObservable { (token) ->
                return@flatMapObservable if (token == null) {
                    Observable.just(AcceptInvitationFailure)
                } else {
                    val request = AcceptInvitationRequest(intent.id)
                    supnetClient
                        .acceptInvitation(token, request)
                        .flatMapCompletable {
                            store.removeInvitation(intent.id)
                        }
                        .andThen(Observable.just<AcceptInvitationResult>(InvitationAccepted))
                }
            }
            .onErrorReturn {
                it.printStackTrace()
                AcceptInvitationFailure
            }
    }

    private fun onRejectInvitation(intent: RejectInvitation): Observable<RejectInvitationResult> {
        return store
            .getToken()
            .flatMapObservable { (token) ->
                return@flatMapObservable if (token == null) {
                    Observable.just(RejectInvitationFailute)
                } else {
                    val request = RejectInvitationRequest(intent.id)
                    supnetClient
                        .rejectInvitation(token, request)
                        .andThen(store.removeInvitation(intent.id))
                        .andThen(Observable.just<RejectInvitationResult>(InvitationRejected))
                }
            }
            .onErrorReturn {
                it.printStackTrace()
                RejectInvitationFailute
            }
    }

    override fun sendIntent(intent: FriendsManagerIntent) = intents.accept(intent)

    override fun results(): Observable<FriendsManagerResult> = results

    override fun friends(): Observable<List<Friend>> = store.friends()

    override fun invitations(): Observable<List<Invitation>> = store.invitations()

}