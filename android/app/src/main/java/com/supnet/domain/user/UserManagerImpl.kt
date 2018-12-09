package com.supnet.domain.user

import com.jakewharton.rxrelay2.PublishRelay
import com.supnet.domain.user.UserManagerIntent.*
import com.supnet.domain.user.UserManagerResult.*
import com.supnet.domain.user.UserManagerResult.InvitationResult.*
import com.supnet.domain.user.UserManagerResult.SignInResult.*
import com.supnet.domain.user.UserManagerResult.SignOffResult.*
import com.supnet.domain.user.UserManagerResult.SignOutResult.*
import com.supnet.domain.user.UserManagerResult.SignUpResult.*
import com.supnet.domain.user.UserState.*
import com.supnet.data.local.SupnetStore
import com.supnet.data.remote.rest.SupnetRestClient
import com.supnet.domain.InvitationRequest
import io.reactivex.Observable

class UserManagerImpl(
    private val supnetStore: SupnetStore,
    private val supnetClient: SupnetRestClient
) : UserManager {

    private val supnetIntents = PublishRelay.create<UserManagerIntent>()

    override fun sendIntent(intent: UserManagerIntent) = supnetIntents.accept(intent)

    override fun results(): Observable<UserManagerResult> = results

    private val results: Observable<UserManagerResult> by lazy {
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

    private fun onSignUp(intent: SignUpIntent): Observable<UserManagerResult.SignUpResult> {
        val (email, name, password) = intent
        return supnetClient
            .signUp(email, name, password)
            .flatMapObservable { result ->
                supnetStore
                    .saveUserData(result.id, name, email, password)
                    .andThen(supnetStore.saveToken(result.token))
                    .andThen(Observable.just<SignUpResult>(SignUpSuccess(result.token)))
            }
            .onErrorReturn {
                it.printStackTrace()
                SignUpFailure
            }
    }

    private fun onSignOff(): Observable<SignOffResult> {
        return supnetStore
            .getToken()
            .flatMapObservable { (token) ->
                return@flatMapObservable if (token == null) {
                    Observable.just(SignOffFailure)
                } else {
                    supnetClient
                        .signOff(token)
                        .andThen(supnetStore.removeUserData())
                        .andThen(Observable.just<SignOffResult>(SignOffSuccess))
                }
            }
            .onErrorReturn {
                it.printStackTrace()
                SignOffFailure
            }
    }

    private fun onSignIn(intent: SignInIntent): Observable<SignInResult> {
        val (email, password) = intent
        return supnetClient
            .signIn(email, password)
            .flatMapObservable { result ->
                supnetStore
                    .saveUserData(result.id, result.username, email, password)
                    .andThen(supnetStore.saveFriends(result.friends))
                    .andThen(supnetStore.saveToken(result.token))
                    .andThen(Observable.just<SignInResult>(SignInSuccess(result.token)))
            }
            .onErrorReturn {
                it.printStackTrace()
                SignInFailure
            }
    }

    private fun onSignOut(): Observable<SignOutResult> {
        return supnetStore
            .getToken()
            .flatMapObservable { (token) ->
                return@flatMapObservable if (token == null) {
                    Observable.just(SignOutFailure)
                } else {
                    supnetClient
                        .signOut(token)
                        .andThen(supnetStore.removeUserData())
                        .andThen(Observable.just<SignOutResult>(SignOutSuccess))
                }
            }
            .onErrorReturn {
                it.printStackTrace()
                SignOutFailure
            }
    }

    private fun onInvitation(intent: InvitationIntent): Observable<InvitationResult> {
        return supnetStore
            .getToken()
            .flatMapObservable { (token) ->
                return@flatMapObservable if (token == null) {
                    Observable.just(InvitationFailure)
                } else {
                    val invitation = InvitationRequest(intent.recipient, intent.message)
                    supnetClient
                        .sendInvitation(token, invitation)
                        .andThen(supnetStore.removeUserData())
                        .andThen(Observable.just<InvitationResult>(InvitationSend))
                }
            }
            .onErrorReturn {
                it.printStackTrace()
                InvitationFailure
            }
    }

    private val userStates: Observable<UserState> by lazy {
        val storageStream = supnetStore
            .getToken()
            .map<UserState> { (token) ->
                return@map if (token == null) SignedOut
                else SignedIn(token)
            }
            .toObservable()

        return@lazy results
            .flatMap { return@flatMap when (it) {
                is SignUpSuccess -> Observable.just(SignedIn(it.token))
                is SignInSuccess -> Observable.just(SignedIn(it.token))
                SignOutSuccess, SignOffSuccess -> Observable.just(SignedOut)
                else -> Observable.empty()
            }}
            .startWith(storageStream)
            .replay(1)
            .refCount()
    }

    override fun userStates() = userStates

}
