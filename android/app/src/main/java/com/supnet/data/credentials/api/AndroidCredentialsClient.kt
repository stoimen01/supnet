package com.supnet.data.credentials.api

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

class AndroidCredentialsClient : CredentialsClient {

    interface CredentialsApi {

        @POST("/signin")
        fun login(@Body value: LoginCredentials): Single<Response<SignResult>>

        @POST("/signup")
        fun register(@Body value: RegisterCredentials): Single<Response<SignResult>>

    }

    private val client: CredentialsApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CredentialsApi::class.java)
    }

    override fun loginUser(email: String, password: String): Single<SignResult>  =
        client.login(LoginCredentials(email, password))
            .map {
                it.body()!!
            }

    override fun registerUser(email: String, userName: String, password: String): Single<SignResult> =
        client.register(RegisterCredentials(userName, email, password))
            .map {
                it.body()!!
            }

    override fun logoutUser(): Completable =
        Completable.timer(3, TimeUnit.SECONDS)
            .andThen(Completable.complete())

}