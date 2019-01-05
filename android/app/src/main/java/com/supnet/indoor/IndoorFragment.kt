package com.supnet.indoor

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.common.BaseFragment
import com.supnet.common.hide
import com.supnet.common.observeCommands
import com.supnet.indoor.IndoorCommand.*
import com.supnet.indoor.connection.ConnectionFragment
import com.supnet.indoor.friends.FriendsFragment
import com.supnet.indoor.gadgets.GadgetsFragment
import com.supnet.indoor.settings.SettingsFragment
import kotlinx.android.synthetic.main.fragment_indoor.*

class IndoorFragment : BaseFragment() {

    private val viewModel by lazy {
        ViewModelProviders
            .of(this)
            .get(IndoorViewModel::class.java)
    }

    override fun provideViewId() = R.layout.fragment_indoor

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navBottom.setOnNavigationItemSelectedListener(this::onNavItemSelected)
        observeCommands(viewModel.getLiveCommands(), this::onLiveCommand)
    }

    private fun onLiveCommand(cmd: IndoorCommand) = when (cmd) {
        ShowFriends -> showFragment(FriendsFragment())
        ShowGadgets -> showFragment(GadgetsFragment())
        ShowSettings -> showFragment(SettingsFragment())
        ShowConnection -> {
            navBottom.hide()
            showFragment(ConnectionFragment())
        }
    }

    private fun onNavItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_friends -> {
                viewModel.onFriendsSelected()
                true
            }
            R.id.action_gadgets -> {
                viewModel.onGadgetsSelected()
                true
            }
            R.id.action_settings -> {
                viewModel.onSettingsSelected()
                true
            }
            else -> false
        }
    }

    private fun showFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.containerIndoor, fragment)
            .commit()
    }
}