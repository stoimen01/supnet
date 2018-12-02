package com.supnet.data

import com.jakewharton.rxrelay2.PublishRelay
import com.supnet.data.SupnetIntent.*
import com.supnet.data.SupnetResult.*
import com.supnet.data.SupnetResult.InvitationResult.*
import com.supnet.data.SupnetResult.SignInResult.*
import com.supnet.data.SupnetResult.SignOffResult.*
import com.supnet.data.SupnetResult.SignOutResult.*
import com.supnet.data.SupnetResult.SignUpResult.*
import com.supnet.data.UserState.*
import com.supnet.data.local.SupnetStore
import com.supnet.data.local.UserInfo
import com.supnet.data.remote.rest.SupnetRestClient
import io.reactivex.Observable

class SupnetRepositoryImpl(
    private val supnetStore: SupnetStore,
    private val supnetClient: SupnetRestClient
) : SupnetRepository {

    private val supnetIntents = PublishRelay.create<SupnetIntent>()

    override fun sendIntent(intent: SupnetIntent) = supnetIntents.accept(intent)

    override fun results() = results

    private val results: Observable<SupnetResult> by lazy {
        return@lazy supnetIntents
            .flatMap { intent ->
                return@flatMap when (intent) {
                    is SignUpIntent -> onSignUp(intent)
                    SignOffIntent -> onSignOff()
                    is SignInIntent -> onSignIn(intent)
                    SignOutIntent -> onSignOut()
                    is InvitationIntent -> onInvitation(intent)
                }
            }
            .publish()
            .refCount()
    }

    private fun onSignUp(intent: SignUpIntent): Observable<SupnetResult.SignUpResult> {
        val (email, name, password) = intent
        return supnetClient
            .signUp(email, name, password)
            .flatMapObservable { result ->

                val userInfo = UserInfo(
                    id = result.id,
                    token = result.token,
                    name = result.username,
                    email = email,
                    password = password,
                    friends = result.friends,
                    gadgets = result.gadgets
                )

                return@flatMapObservable supnetStore
                    .saveUserInfo(userInfo)
                    .andThen(Observable.just<SignUpResult>(SignUpSuccess(result.token)))
                    .onErrorReturn { SignUpFailure }
            }
    }

    private fun onSignOff(): Observable<SignOffResult> {
        return supnetStore
            .getUserInfo()
            .flatMapObservable { (usrInfo) ->
                return@flatMapObservable if (usrInfo == null) {
                    Observable.just(SignOffFailure)
                } else {
                    supnetClient
                        .signOff(usrInfo.token)
                        .andThen(supnetStore.removeUserInfo())
                        .andThen(Observable.just<SignOffResult>(SignOffSuccess))
                        .onErrorReturn { SignOffFailure }
                }
            }
    }

    private fun onSignIn(intent: SignInIntent): Observable<SignInResult> {
        val (email, password) = intent
        return supnetClient
            .signIn(email, password)
            .flatMapObservable { result ->

                val userInfo = UserInfo(
                    id = result.id,
                    token = result.token,
                    name = result.username,
                    email = email,
                    password = password,
                    friends = result.friends,
                    gadgets = result.gadgets
                )

                return@flatMapObservable supnetStore
                    .saveUserInfo(userInfo)
                    .andThen(Observable.just<SignInResult>(SignInSuccess(result.token)))
                    .onErrorReturn { SignInFailure }
            }
    }

    private fun onSignOut(): Observable<SignOutResult> {
        return supnetStore
            .getUserInfo()
            .flatMapObservable { (usrInfo) ->
                return@flatMapObservable if (usrInfo == null) {
                    Observable.just(SignOutFailure)
                } else {
                    supnetClient
                        .signOut(usrInfo.token)
                        .andThen(supnetStore.removeUserInfo())
                        .andThen(Observable.just<SignOutResult>(SignOutSuccess))
                        .onErrorReturn { SignOutFailure }
                }
            }
    }

    private fun onInvitation(intent: InvitationIntent): Observable<InvitationResult> {
        return supnetStore
            .getUserInfo()
            .flatMapObservable { (usrInfo) ->
                return@flatMapObservable if (usrInfo == null) {
                    Observable.just(InvitationFailure)
                } else {
                    val invitation = FriendshipInvitation(usrInfo.name, intent.recipient, intent.message)
                    supnetClient
                        .sendInvitation(usrInfo.token, invitation)
                        .andThen(supnetStore.removeUserInfo())
                        .andThen(Observable.just<InvitationResult>(InvitationSend))
                        .onErrorReturn { InvitationFailure }
                }
            }
    }

    private val userStates: Observable<UserState> by lazy {
        val storageStream = supnetStore
            .getUserInfo()
            .map<UserState> { (usrInfo) ->
                return@map if (usrInfo == null) SignedOut
                else SignedIn(usrInfo.token)
            }
            .toObservable()

        return@lazy results
            .flatMap { return@flatMap when (it) {
                is SignUpSuccess -> Observable.just(SignedIn(it.token))
                is SignInSuccess -> Observable.just(SignedIn(it.token))
                SignOutSuccess, SignOffSuccess -> Observable.just(SignedOut)
                else -> Observable.empty<UserState>()
            } }
            .startWith(storageStream)
            .replay(1)
            .refCount()
    }

    override fun userStates() = userStates
}
