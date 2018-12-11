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

class RootActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProviders
            .of(this, RootViewModelFactory(Supnet.app.userManager))
            .get(RootViewModel::class.java)
    }

    private val currentFragment
    get() = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        observeCommands(viewModel.getCommands(), this::onNavigationCommand)
    }

    private fun onNavigationCommand(cmd: RootCommand) = when (cmd) {
        ShowEntryFlow -> showFragment(EntryFragment())
        ShowIndoorFlow -> showFragment(IndoorFragment())
    }

    override fun onBackPressed() {
        val fragment = currentFragment
        if (fragment is BackPressHandler && fragment.onBackPressed()) return
        super.onBackPressed()
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment, null)
            .commit()
    }

}
