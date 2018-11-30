package com.supnet.entry.register

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.Supnet
import com.supnet.common.*
import com.supnet.entry.EntryViewModel
import com.supnet.entry.register.RegisterEvent.RegisterViewEvent.*
import com.supnet.entry.register.RegisterState.Idle
import com.supnet.entry.register.RegisterState.Loading
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : BaseFragment() {

    private val viewModel: RegisterViewModel by lazy {

        val navigator = ViewModelProviders
            .of(parentFragment!!)
            .get(EntryViewModel::class.java)

        ViewModelProviders
            .of(this, RegisterViewModelFactory(Supnet.app.connectionAgent, Supnet.credentialsManager, navigator))
            .get(RegisterViewModel::class.java)
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
            viewModel.onViewEvent(RegisterClicked)
            hideKeyboard()
        }

        btnBackRegister.setOnClickListener {
            viewModel.onViewEvent(RegisterBackClicked)
        }

        observe(viewModel.liveState, this::onLiveState)

    }

    private fun onLiveState(state: RegisterState) = when (state) {
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