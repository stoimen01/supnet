package com.supnet.entry.login

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.common.BaseFragment
import com.supnet.common.observe
import com.supnet.entry.EntryViewModel
import com.supnet.model.connection.ConnectionState.*
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment() {

    private val viewModel: LoginViewModel by lazy {
        ViewModelProviders
            .of(parentFragment!!)
            .get(EntryViewModel::class.java)
    }

    override fun provideViewId() = R.layout.fragment_login

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnRegister.setOnClickListener {
            viewModel.onCreateAccountClicked()
        }

        btnLogin.setOnClickListener {
            viewModel.onLoginClicked("", "")
        }

        observe(viewModel.getConnectionState()) {
            val isEnabled = when (it) {
                CONNECTED -> true
                DISCONNECTED -> false
            }
            btnLogin.isEnabled = isEnabled
        }

    }

}