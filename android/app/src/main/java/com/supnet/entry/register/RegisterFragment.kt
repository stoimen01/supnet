package com.supnet.entry.register

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.common.BaseFragment
import com.supnet.common.observe
import com.supnet.entry.EntryViewModel
import com.supnet.model.connection.ConnectionState
import com.supnet.model.connection.ConnectionState.*
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : BaseFragment() {

    private val viewModel: RegisterViewModel by lazy {
        ViewModelProviders
            .of(parentFragment!!)
            .get(EntryViewModel::class.java)
    }

    override fun provideViewId() = R.layout.fragment_register

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnCreateAccount.setOnClickListener {
            viewModel.onRegisterClicked("", "", "")
        }

        btnBackRegister.setOnClickListener {
            viewModel.onBackFromRegisterClicked()
        }

        observe(viewModel.getConnectionState()) {
            val isEnabled = when (it) {
                CONNECTED -> true
                DISCONNECTED -> false
            }
            btnCreateAccount.isEnabled = isEnabled
        }

    }

}