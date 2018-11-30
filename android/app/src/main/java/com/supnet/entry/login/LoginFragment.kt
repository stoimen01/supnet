package com.supnet.entry.login

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.Supnet
import com.supnet.common.*
import com.supnet.entry.EntryViewModel
import com.supnet.entry.login.LoginEvent.LoginViewEvent.*
import com.supnet.entry.login.LoginState.Idle
import com.supnet.entry.login.LoginState.Loading
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment() {

    private val viewModel by lazy {

        val navigator = ViewModelProviders
            .of(parentFragment!!)
            .get(EntryViewModel::class.java)

        ViewModelProviders
            .of(this, LoginViewModelFactory(Supnet.app.connectionAgent, Supnet.credentialsManager, navigator))
            .get(LoginViewModel::class.java)
    }

    override fun provideViewId() = R.layout.fragment_login

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        txtFriend.addTextChangedListener(SimpleTextWatcher {
            viewModel.onViewEvent(EmailChanged(it))
        })

        txtPassword.addTextChangedListener(SimpleTextWatcher {
            viewModel.onViewEvent(PasswordChanged(it))
        })

        btnRegister.setOnClickListener {
            viewModel.onViewEvent(CreateAccountClicked)
        }

        btnSignIn.setOnClickListener {
            viewModel.onViewEvent(SignInClicked)
            hideKeyboard()
        }

        observe(viewModel.liveState, this::onLiveState)
    }


    private fun onLiveState(state: LoginState) = when (state) {
        is Idle -> {
            btnSignIn.isEnabled = state.isSignInEnabled
            progressBar.hide()
        }
        is Loading -> {
            btnSignIn.isEnabled = false
            progressBar.show()
        }
    }

}