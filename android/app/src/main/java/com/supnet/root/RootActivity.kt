package com.supnet.root

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.Supnet
import com.supnet.common.*
import com.supnet.root.RootCommand.*
import com.supnet.entry.EntryFragment
import com.supnet.indoor.IndoorFragment
import kotlinx.android.synthetic.main.activity_navigation.*

class RootActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProviders
            .of(this, RootViewModelFactory(Supnet.credentialsManager))
            .get(RootViewModel::class.java)
    }

    private val currentFragment
    get() = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        observeCommands(viewModel.getCommands(), this::onNavigationCommand)
        observe(viewModel.getLiveState(), this::onLiveState)
    }

    private fun onLiveState(state: RootViewState) {
        if (state.isLoading) progressBar.show()
        else progressBar.hide()
    }

    private fun onNavigationCommand(cmd: RootCommand) = when (cmd) {

        ShowEntryFlow -> showFragment(EntryFragment())

        ShowIndoorFlow -> showFragment(IndoorFragment())

        is ShowErrorMessage -> showToast(cmd.msg)

    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (fragment is BackPressHandler && fragment.onBackPressed()) return
        super.onBackPressed()
    }

    private fun showFragment(fragment: Fragment) {

        // don't show the same fragment multiple times
        if (currentFragment?.javaClass?.canonicalName ==
            fragment.javaClass.canonicalName) return

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment, null)
            .commit()
    }

}
