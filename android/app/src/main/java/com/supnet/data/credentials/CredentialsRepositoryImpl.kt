package com.supnet.data.credentials

import com.supnet.data.credentials.api.CredentialsClient
import com.supnet.data.credentials.store.CredentialsStore

class CredentialsRepositoryImpl(
    private val credentialsStore: CredentialsStore,
    private val credentialsClient: CredentialsClient
) : CredentialsRepository {

    override fun signIn(email: String, password: String) =
        credentialsClient.loginUser(email, password)

    override fun signUp(email: String, userName: String, password: String) =
        credentialsClient.registerUser(email, userName, password)

    override fun signOut() = credentialsClient.logoutUser()



}