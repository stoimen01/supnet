package com.supnet.entry.signin

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.Supnet
import com.supnet.common.*
import com.supnet.entry.EntryViewModel
import com.supnet.entry.signin.SignInEvent.SignInViewEvent.*
import com.supnet.entry.signin.SignInState.Idle
import com.supnet.entry.signin.SignInState.Loading
import kotlinx.android.synthetic.main.fragment_login.*

class SignInFragment : BaseFragment() {

    private val viewModel by lazy {

        val navigator = ViewModelProviders
            .of(parentFragment!!)
            .get(EntryViewModel::class.java)

        ViewModelProviders
            .of(this, SignInViewModelFactory(Supnet.app.connectionAgent, Supnet.app.supnetRepository, navigator))
            .get(SignInViewModel::class.java)
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

    private fun onLiveState(state: SignInState) = when (state) {
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