package com.supnet.indoor.settings

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.Supnet
import com.supnet.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment() {

    private val viewModel by lazy {
        ViewModelProviders
            .of(this, SettingsViewModelFactory(Supnet.supnetRepository))
            .get(SettingsViewModel::class.java)
    }

    override fun provideViewId() = R.layout.fragment_settings

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnSignOut.setOnClickListener {
            viewModel.signOut()
        }

    }


}