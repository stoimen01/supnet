package com.supnet.entry.register

import com.supnet.entry.ConnectionAwareViewModel

interface RegisterViewModel : ConnectionAwareViewModel {

    fun onRegisterClicked(email: String, username: String, password: String)

    fun onBackFromRegisterClicked()
}