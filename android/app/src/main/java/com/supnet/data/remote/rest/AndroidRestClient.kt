package com.supnet.data.remote.rest

import com.supnet.domain.*
import io.reactivex.Completable
import io.reactivex.Single

class AndroidRestClient(
    private val supnetRestApi: SupnetRestApi
): SupnetRestClient {

    private val tokenPrefix = "Bearer "

    override fun signUp(email: String, userName: String, password: String): Single<SignUpResponse> =
        supnetRestApi.signUp(SignUpRequest(userName, email, password))
            .map {
                it.body()!!
            }

    override fun signOff(token: String): Completable {
        return supnetRestApi.signOff(tokenPrefix + token)
    }

    override fun signIn(email: String, password: String): Single<SignInResponse>  =
        supnetRestApi.signIn(SignInRequest(email, password))
            .map {
                it.body()!!
            }

    override fun signOut(token: String): Completable {
        return supnetRestApi.signOut(tokenPrefix + token)
    }

    override fun sendInvitation(token: String, invitation: InvitationRequest): Completable =
        supnetRestApi.sendInvitation(tokenPrefix + token, invitation)

    override fun acceptInvitation(token: String, request: AcceptInvitationRequest): Single<AcceptInvitationResponse> {
        return supnetRestApi.acceptInvitation(tokenPrefix + token, request)
            .map {
                it.body()!!
            }
    }

    override fun rejectInvitation(token: String, request: RejectInvitationRequest): Completable {
        return supnetRestApi.rejectInvitation(tokenPrefix + token, request)
    }
}