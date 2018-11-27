package com.supnet.user.login

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.common.BaseFragment
import com.supnet.user.UserNavigationViewModel
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment() {

    private val viewModel by lazy {
        ViewModelProviders
            .of(parentFragment!!)
            .get(UserNavigationViewModel::class.java)
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
    }

}