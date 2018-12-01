package com.supnet.entry

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.common.BaseFragment
import com.supnet.common.observeCommands
import com.supnet.common.BackPressHandler
import com.supnet.entry.EntryCommand.*
import com.supnet.entry.signin.SignInFragment
import com.supnet.entry.signup.SignUpFragment

class EntryFragment : BaseFragment(), BackPressHandler {

    private val viewModel by lazy {
        return@lazy ViewModelProviders
            .of(this)
            .get(EntryViewModel::class.java)
    }

    override fun provideViewId() = R.layout.fragment_user_navigation

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeCommands(viewModel.getCommands(), this::onCommand)
    }

    private fun onCommand(cmd: EntryCommand) = when (cmd) {
        ShowSignIn -> {
            childFragmentManager.beginTransaction()
                .replace(R.id.childFragmentContainer, SignInFragment(), null)
                .commit()
            Unit
        }
        ShowSignUp -> {
            childFragmentManager.beginTransaction()
                .replace(R.id.childFragmentContainer, SignUpFragment(), null)
                .addToBackStack(null)
                .commit()
            Unit
        }
        ShowBack -> {
            childFragmentManager.popBackStack()
        }
    }

    override fun onBackPressed(): Boolean {
        return if (childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStack()
            true
        } else false
    }

}