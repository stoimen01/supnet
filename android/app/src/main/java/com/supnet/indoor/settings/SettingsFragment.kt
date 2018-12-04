package com.supnet.indoor.settings

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.common.*
import com.supnet.indoor.settings.SettingsCommand.*
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment() {

    private val viewModel by lazy {
        ViewModelProviders
            .of(this, SettingsViewModelFactory())
            .get(SettingsViewModel::class.java)
    }

    override fun provideViewId() = R.layout.fragment_settings

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        progressBar.hide()

        btnSignOut.setOnClickListener { viewModel.signOut() }
        btnSignOff.setOnClickListener { viewModel.signOff() }

        observe(viewModel.getLiveState()) {
            btnSignOff.isEnabled = it.isSignOffEnabled
            if (it.isLoading) {
                progressBar.show()
            } else {
                progressBar.hide()
            }
        }

        observeCommands(viewModel.getLiveCommand()) {
            when (it) {
                SHOW_ERROR -> {
                    showToast("Error occurred")
                }
            }
        }

    }
}