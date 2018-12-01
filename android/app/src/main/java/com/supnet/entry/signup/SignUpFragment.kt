package com.supnet.entry.signup

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.Supnet
import com.supnet.common.*
import com.supnet.entry.EntryViewModel
import com.supnet.entry.signup.SignUpEvent.SignUpViewEvent.*
import com.supnet.entry.signup.SignUpState.Idle
import com.supnet.entry.signup.SignUpState.Loading
import kotlinx.android.synthetic.main.fragment_register.*

class SignUpFragment : BaseFragment() {

    private val viewModel: SignUpViewModel by lazy {

        val navigator = ViewModelProviders
            .of(parentFragment!!)
            .get(EntryViewModel::class.java)

        ViewModelProviders
            .of(this, SignUpViewModelFactory(Supnet.app.connectionAgent, Supnet.supnetRepository, navigator))
            .get(SignUpViewModel::class.java)
    }

    override fun provideViewId() = R.layout.fragment_register

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        txtUserName.addTextChangedListener(SimpleTextWatcher {
            viewModel.onViewEvent(UsernameChanged(it))
        })

        txtFriend.addTextChangedListener(SimpleTextWatcher {
            viewModel.onViewEvent(EmailChanged(it))
        })

        txtPassword.addTextChangedListener(SimpleTextWatcher {
            viewModel.onViewEvent(PasswordChanged(it))
        })

        btnCreateAccount.setOnClickListener {
            viewModel.onViewEvent(SignUpClicked)
            hideKeyboard()
        }

        btnBackRegister.setOnClickListener {
            viewModel.onViewEvent(SignUpBackClicked)
        }

        observe(viewModel.liveState, this::onLiveState)

    }

    private fun onLiveState(state: SignUpState) = when (state) {
        is Idle -> {
            btnCreateAccount.isEnabled = state.isCreateEnabled
            progressBar.hide()
        }
        is Loading -> {
            btnCreateAccount.isEnabled = false
            progressBar.show()
        }
    }

}