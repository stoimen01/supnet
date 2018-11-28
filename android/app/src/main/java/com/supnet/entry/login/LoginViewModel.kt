package com.supnet.entry.login

import com.supnet.entry.ConnectionAwareViewModel

interface LoginViewModel : ConnectionAwareViewModel {

    fun onLoginClicked(email: String, password: String)

    fun onCreateAccountClicked()
}