package com.supnet.user.login

interface LoginViewModel {

    fun onLoginClicked(name: String, password: String)

    fun onCreateAccountClicked()
}