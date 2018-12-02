package com.supnet.data.remote.rest

import com.supnet.data.FriendshipInvitation
import com.supnet.data.SignResult
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SupnetRestApi {

    @POST("/signup")
    fun signUp(@Body value: RegisterCredentials): Single<Response<SignResult>>

    @POST("/signin")
    fun signIn(@Body value: LoginCredentials): Single<Response<SignResult>>

    @POST("/invitation")
    fun sendInvitation(@Body value: FriendshipInvitation): Completable

}