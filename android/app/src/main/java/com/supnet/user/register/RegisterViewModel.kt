package com.supnet.user.register

import android.provider.ContactsContract

interface RegisterViewModel {

    fun onRegisterClicked(email: String, username: String, password: String)

    fun onBackFromRegisterClicked()
}