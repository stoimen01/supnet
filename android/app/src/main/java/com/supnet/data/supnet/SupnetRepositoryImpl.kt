package com.supnet.data.supnet

import com.supnet.data.supnet.client.SupnetClient
import com.supnet.data.supnet.client.socket.SocketEvent
import com.supnet.data.supnet.store.SupnetStore
import com.supnet.data.supnet.store.UserInfo
import io.reactivex.Completable
import io.reactivex.Observable

class SupnetRepositoryImpl(
    private val supnetStore: SupnetStore,
    private val supnetClient: SupnetClient
) : SupnetRepository {

    override fun signIn(email: String, password: String): Completable {
        return supnetClient
            .signIn(email, password)
            .flatMapCompletable { result ->
                supnetStore.saveUserInfo(UserInfo(
                    id = result.id,
                    token = result.token,
                    name = result.username,
                    email = email,
                    password = password,
                    friends = result.friends,
                    gadgets = result.gadgets
                ))
            }
    }

    override fun signUp(email: String, userName: String, password: String): Completable {
        return supnetClient
            .signUp(email, userName, password)
            .flatMapCompletable { result ->
                supnetStore.saveUserInfo(UserInfo(
                    id = result.id,
                    token = result.token,
                    name = result.username,
                    email = email,
                    password = password,
                    friends = result.friends,
                    gadgets = result.gadgets
                ))
            }
    }

    override fun openSocket(): Observable<SocketState> {
        return supnetStore
            .getUserInfo()
            .flatMapObservable {
                supnetClient.openSocket(it.token)
                    .flatMap { socketEvent ->
                        return@flatMap when(socketEvent) {
                            is SocketEvent.MessageEvent -> Observable.empty()
                            is SocketEvent.StateEvent -> Observable.just(socketEvent.state)
                        }
                    }
            }
    }

    override fun sendInvitation(recipient: String, message: String): Completable {
        return supnetStore
            .getUserInfo()
            .flatMapCompletable { userInfo ->
                supnetClient.sendInvitation(FriendshipInvitation(
                    initiatorName = userInfo.name,
                    recipientName = recipient,
                    message = message
                ))
            }
    }

    override fun signOut() = supnetClient.signOut()

    override fun removeAccount(): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}