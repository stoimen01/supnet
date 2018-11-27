package com.supnet.user.register

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.common.BaseFragment
import com.supnet.user.UserNavigationViewModel
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : BaseFragment() {

    private val viewModel by lazy {
        ViewModelProviders
            .of(parentFragment!!)
            .get(UserNavigationViewModel::class.java)
    }

    override fun provideViewId() = R.layout.fragment_register

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnCreateAccount.setOnClickListener {
            viewModel.onCreateAccountClicked()
        }

        btnBackRegister.setOnClickListener {
            viewModel.onBackFromRegisterClicked()
        }

    }

}