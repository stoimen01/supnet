package com.supnet.data.remote.rest

import com.supnet.domain.*
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SupnetRestApi {

    @POST("/signup")
    fun signUp(@Body value: SignUpRequest): Single<Response<SignUpResponse>>

    @POST("/signoff")
    fun signOff(@Header("Authorization") token: String): Completable

    @POST("/signin")
    fun signIn(@Body value: SignInRequest): Single<Response<SignInResponse>>

    @POST("/signout")
    fun signOut(@Header("Authorization") token: String): Completable

    @POST("/invitation/send")
    fun sendInvitation(
        @Header("Authorization") token: String,
        @Body value: InvitationRequest
    ): Completable

    @POST("/invitation/accept")
    fun acceptInvitation(
        @Header("Authorization") token: String,
        @Body value: AcceptInvitationRequest
    ): Single<Response<AcceptInvitationResponse>>

    @POST("/invitation/reject")
    fun rejectInvitation(
        @Header("Authorization") token: String,
        @Body value: RejectInvitationRequest
    ): Completable

}