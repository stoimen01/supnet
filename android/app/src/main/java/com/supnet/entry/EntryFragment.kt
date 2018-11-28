package com.supnet.entry

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.Supnet
import com.supnet.common.BaseFragment
import com.supnet.common.observeCommands
import com.supnet.model.connection.AndroidConnectionAgent
import com.supnet.common.BackPressHandler
import com.supnet.entry.EntryCommand.*
import com.supnet.entry.login.LoginFragment
import com.supnet.entry.register.RegisterFragment

class EntryFragment : BaseFragment(), BackPressHandler {

    private val viewModel by lazy {
        ViewModelProviders
            .of(this, EntryViewModelFactory(
                AndroidConnectionAgent(context!!.applicationContext),
                Supnet.credentialsManager
            ))
            .get(EntryViewModel::class.java)
    }

    override fun provideViewId() = R.layout.fragment_user_navigation

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeCommands(viewModel.getCommands()) {
            return@observeCommands when (it) {
                ShowLogin -> {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.childFragmentContainer, LoginFragment(), null)
                        .commit()
                    Unit
                }
                ShowRegister -> {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.childFragmentContainer, RegisterFragment(), null)
                        .addToBackStack(null)
                        .commit()
                    Unit
                }
                BackFromRegister -> {
                    childFragmentManager.popBackStack()
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return if (childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStack()
            true
        } else false
    }
}